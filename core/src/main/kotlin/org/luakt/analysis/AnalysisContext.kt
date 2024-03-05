package org.luakt.analysis

import org.luakt.lexer.Token
import org.luakt.parse.LuaScript
import org.luakt.parse.StaticCheckException
import org.luakt.syntax.*
import org.luakt.util.Lambda
import kotlin.math.max

class AnalysisContext(
    private val script: LuaScript,
    // push and pop when enter and exit a function
    private val funcLocalStack : MutableList<FuncAnalysisContext> = mutableListOf()
){
    val fnCtx get() = funcLocalStack.last()
    var funcIndex = 0
        private set

    fun enterFunc(){
        funcLocalStack.add(FuncAnalysisContext())
    }

    fun exitFunc() : FuncMeta {
        val goto = fnCtx.checkAllGoto()
        if(goto != null){
            errorUnknownSymbol(goto.labelName, goto)
        }

        fnCtx.refreshLocalSize()
        val poppedFnCtx = funcLocalStack.removeLast()
        val maxLocals = poppedFnCtx.maxLocalSize()
        return FuncMeta(
            maxLocals,
            poppedFnCtx.upValues
        )
    }

    fun nextFnIdx(): Int {
        val idx = funcIndex
        funcIndex++
        return idx
    }

    fun findGlobal(name : String) = script.globals[name]

    fun isDefined(sym : String): Boolean {
        return fnCtx.isDefinedInFunc(sym) // local
                || script.globals.containsKey(sym) // global
                || funcLocalStack.any { fn -> fn.isDefinedInFunc(sym) } // capture
    }

    fun isDefinedInOutFn(sym: String) : Boolean{
        return funcLocalStack.size > 1
                && funcLocalStack[funcLocalStack.lastIndex-1].findLocal(sym) != null
//            && funcLocalStack.slice(0 until funcLocalStack.lastIndex)
//            .reversed()
//            .any { it.findLocal(sym) != null }
    }

    fun newGlobal(name : String): Location {
//            println("put gv $name ${script.globals.size}")
        val location = Location.asGlobal(script.globals.size, name)
        script.globals[name] = location
        return location
    }

    fun newUpValue(name : String, fromOutsideLocal : Boolean): Location {
        // allocate space for variable in current function
        val toLoc = fnCtx.newLocal(name,true)
        if(fromOutsideLocal){
            // find original index in first outside function local
            val fromIdx = funcLocalStack[funcLocalStack.lastIndex-1].findLocal(name)!!
            val captureInfo = CaptureInfo(
                toLoc,
                fromIdx
            )
            fnCtx.upValues.add(captureInfo)
            return toLoc
        }
        val fromLocation = funcLocalStack.slice(0 until funcLocalStack.lastIndex)
            .reversed()
            .firstNotNullOf{ it.findLocal(name)  }
//            funcLocalStack.reversed()
//            .first{ fn -> fn.isDefinedInFunc(name) }
//            .findLocal(name)!!
        if(fromLocation.area == VarType.FieldUpValue){
            val captureInfo = CaptureInfo(
                toLoc,
                fromLocation
            )
            fnCtx.upValues.add(captureInfo)
            return toLoc
        }
        val newFieldIdx = script.fields.size
        val captureInfo = CaptureInfo(
            toLoc,
            Location.asField(newFieldIdx, name)
        )
        fnCtx.upValues.add(captureInfo)
        script.fields.add(Pair(newFieldIdx, name))
        // make the captured original variable could also be accessed normally
        fromLocation.area = VarType.FieldUpValue
        fromLocation.idx = newFieldIdx
        return toLoc
    }

    fun errorRepeatSymbol(tk : Token){
        throw StaticCheckException.make(
            ErrorName,
            tk,
            "unexpected repeated symbol '${tk.str}'",
            "",
            script.codeContext,
            script.file
        )
    }

    fun errorRepeatSymbol(sym : String, node : SyntaxNode){
        throw StaticCheckException.make(
            ErrorName,
            node,
            "unexpected repeated symbol '${sym}'",
            "",
            script.codeContext,
            script.file
        )
    }

/*    fun errorUnknownSymbol(tk : Token){
        throw StaticCheckException.make(
            ErrorName, tk,
            "unexpected unknown symbol '${tk.str}'", "",
            script.codeContext, script.file
        )
    }*/

    fun errorUnknownSymbol(sym : String, node : SyntaxNode){
        throw StaticCheckException.make(
            ErrorName,
            node,
            "unexpected unknown symbol '${sym}'",
            "",
            script.codeContext,
            script.file
        )
    }

    fun errorInvalidBreak(brk : Break){
        throw StaticCheckException.make(
            ErrorName,
            brk,
            "invalid 'break' which not located in loop code structure",
            "",
            script.codeContext,
            script.file
        )
    }

    companion object{
        private const val ErrorName = "Analysis Error"
    }

    class FuncAnalysisContext{
        private var localIdx : Int = 0
        private var maxLocalSize : Int = 0
        val upValues : MutableList<CaptureInfo> = mutableListOf()
        private val localStack : MutableList<MutableMap<String, Location>> = mutableListOf()
        // if current block (at last) is a loop block
        private val loopMarkStack : MutableList<Boolean> = mutableListOf()
        // follows are used very different from 'localStack'
        private val rootScope = LabelScope(null)
        private var scope = rootScope
        private var labelCounter : Int = 0

        fun maxLocalSize() = maxLocalSize

        override fun toString(): String {
            return "$localStack \n"
        }

        fun enterBlock(isLoop : Boolean){
            loopMarkStack.add(isLoop)

            val prevScope = scope
            val newScope = LabelScope(prevScope)
            prevScope.subScopes.add(newScope)
            scope = newScope

            localStack.add(mutableMapOf())
            if(localStack.size == 1){
                // the first slot in lua module function is 'this' reference
                // use special name to avoid conflict
                newLocal("<ThisModule>")
                newLocal("<Arguments>")
                newLocal("<UpValues>")
            }
        }

        fun refreshLocalSize(){
            maxLocalSize = max(localIdx, maxLocalSize)
        }

        fun exitBlock() : MutableMap<String, Location> {
            scope = scope.prevScope!!
            loopMarkStack.removeLast()
            val pop = localStack.removeLast()
            maxLocalSize = max(localIdx, maxLocalSize)
            localIdx -= pop.size
            return pop
        }

        fun isDefinedInBlock(name : String) : Boolean = localStack.last().containsKey(name)

        fun isDefinedInFunc(name : String) : Boolean{
            return localStack.any { it.containsKey(name) }
        }

        fun newLocal(name : String, inRootScope : Boolean = false): Location {
            val idx = localIdx
            val location = Location.asLocal(idx, name)
            if(inRootScope){
                localStack.first()[name] = location
            }else{
                localStack.last()[name] = location
            }
            localIdx++
            maxLocalSize = max(localIdx, maxLocalSize)
            return location
        }

        fun findLocal(name: String) : Location? {
            for(idx in localStack.lastIndex downTo 0){
                if(localStack[idx].containsKey(name)){
                    return localStack[idx][name]
                }
            }
            return null
        }

        fun couldNewLabel(label: Label) : Boolean{
            return scope.labels.containsKey(label.name).not()
        }

        fun newLabel(label: Label){
            label.index = labelCounter++
            scope.labels[label.name] = label
        }

        fun addGoto(goto: Goto){
            if(scope.labels.containsKey(goto.labelName)){
                goto.label = scope.labels[goto.labelName]!!
            }else{
                // need further check and fill
                scope.gotos.add(goto)
            }
        }

        // search target label from rootScope to each leafScope
        private fun findLabel(name: String) : Label? {
            fun find(curr : LabelScope, name : String) : Label? {
                var label = curr.labels[name]
                if(label != null) return label
                for (subScope in curr.subScopes) {
                    label = find(subScope, name)
                    if(label != null) return label
                }
                return null
            }
            return find(rootScope, name)
        }

        // search target label from currScope to rootScope
        private fun findLabel(scope: LabelScope, name : String) : Label? {
            var currScope : LabelScope? = scope
            while (currScope != null){
                val label = currScope.labels[name]
                if(label != null) return label
                currScope = currScope.prevScope
            }
            return null
        }

        fun checkAllGoto() : Goto?{
            fun checkGoto(currScope : LabelScope) : Goto? {
                for (goto in currScope.gotos) {
                    val label = if(goto.strictScope){
                        findLabel(currScope, goto.labelName)
                    }else{
                        findLabel(goto.labelName)
                    }
                    if(label != null){
                        goto.label = label
                    }else{
                        return goto
                    }
                }
                return null
            }
            fun check(currScope: LabelScope) : Goto? {
                var goto = checkGoto(currScope)
                if (goto != null) return goto
                for (subScope in currScope.subScopes) {
                    goto = check(subScope)
                    if (goto != null) return goto
                }
                return null
            }

            return check(rootScope)
        }

        fun couldPutBreak() : Boolean = loopMarkStack.any(Lambda::self)
    }

    private class LabelScope(
        val prevScope : LabelScope?,
    ){
        val labels: MutableMap<String, Label> = mutableMapOf()
        val gotos: MutableList<Goto> = mutableListOf()
        val subScopes: MutableList<LabelScope> = mutableListOf()
    }
}