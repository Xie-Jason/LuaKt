package org.luakt.macro

import org.luakt.lexer.Token
import org.luakt.parse.ParseContext
import org.luakt.parse.LuaScript
import org.luakt.parse.StaticCheckException
import org.luakt.syntax.*

class MacroProcessor(
    private val rootScript : LuaScript,
    private val parseContext: ParseContext,
){
    private val attrMacroMap = mutableMapOf<String, AttrMacro>()
    private val procMacroMap = mutableMapOf<String, ProcMacro>()
    private val ctx = MacroContext(false, attrMacroMap, procMacroMap, parseContext)

    fun registerAttrMacro(macro : AttrMacro) = attrMacroMap.put(macro.name, macro)
    fun registerProcMacro(macro : ProcMacro) = procMacroMap.put(macro.name, macro)

    fun process() {
        registerAttrMacro(BuiltinMacros.Type)
        registerAttrMacro(BuiltinMacros.Decorator)
        rootScript.requires.values.asSequence()
            .plusElement(rootScript)
            .forEach { script ->
                ctx.currFile = script.file
                ctx.codeCtx = script.codeContext
                handleStmtList(script.body)
            }
    }

    private fun handleStmtList(list: MutableList<Statement>){
        val newList = list.flatMap { handleStmt(it) ?: listOf(it) }
        list.clear()
        list.addAll(newList)
    }

    private fun handleStmt(stmt : Statement) : List<Statement>?{
        when(stmt){
            // contains expression
            is LocalDeclare -> handleExprList(stmt.right)
            is Assign -> handleExprList(stmt.right)
            is Return -> handleExprList(stmt.list)
            is Discard -> handleExpr(stmt.expr)

            // contains sub statements
            is ForEqLoop -> {
                handleExpr(stmt.start)
                handleExpr(stmt.limit)
                if (stmt.step != null) handleExpr(stmt.step)
                handleStmtList(stmt.body)
            }
            is ForInLoop -> {
                handleExpr(stmt.iter)
                handleStmtList(stmt.body)
            }
            is IfElse -> {
                stmt.branches.forEach{
                    handleExpr(it.first)
                    handleStmtList(it.second)
                }
            }
            is RepeatLoop -> {
                handleExpr(stmt.cond)
                handleStmtList(stmt.body)
            }
            is Block -> {
                handleStmtList(stmt.body)
            }
            is WhileLoop -> {
                handleExpr(stmt.cond)
                handleStmtList(stmt.body)
            }
            // do nothing
            is None  -> {}
            is Goto  -> {}
            is Label -> {}
            is Break -> {}
        }
        // process attr
        return if(stmt.hasAttrMacro()){
            val result = mutableListOf(stmt)
            stmt.macros().forEach { attr ->
                println(attr.name)
                println(attrMacroMap)
                val macro = attrMacroMap[attr.name]
                if(macro == null || macro.attach.contains(stmt::class.java).not()){
                    throw errMacroNotFound(attr)
                }
                val expanded : List<SyntaxNode>
                try{
                    expanded = macro.expand(ctx, attr.tokens, result.first())
                }catch (e : Exception){
                    throw errMacroExpand(attr, e)
                }
                result[0] = expanded[0] as Statement
                result.addAll(expanded.slice(1..expanded.lastIndex).map { it as Statement })
            }
            result
        } else null
    }

    private fun handleExprList(exprList : MutableList<Expression>){
        for (idx in 0..exprList.lastIndex){
            exprList[idx] = handleExpr(exprList[idx]) ?: exprList[idx]
        }
    }

    private fun handleExpr(expr : Expression) : Expression? {
        var replacedExpr : Expression? = null
        when(expr){
            is DeclaredFunc -> handleStmtList(expr.body)
            is FieldAccess -> {
                expr.obj = handleExpr(expr.obj) ?: expr.obj
                expr.fields.forEachIndexed { idx, field ->
                    expr.fields[idx] = handleExpr(field) ?: field
                }
            }
            is FuncInvoke -> {
                expr.func = handleExpr(expr.func) ?: expr.func
                for (idx in 0..expr.args.lastIndex){
                    expr.args[idx] = handleExpr(expr.args[idx]) ?: expr.args[idx]
                }
            }
            is BinOp -> {
                expr.left = handleExpr(expr.left) ?: expr.left
                expr.then.forEachIndexed { idx, pair ->
                    val repl = handleExpr(pair.second)
                    if(repl != null) expr.then[idx] = Pair(pair.first, repl)
                }
            }
            is UnaryOp -> {
                expr.inner = handleExpr(expr.inner) ?: expr.inner
            }
            is LiteralTable -> {
                for (idx in 0..expr.pairs.lastIndex){
                    val pair = expr.pairs[idx]
                    val tmpK = handleExpr(pair.first)
                    val tmpV = handleExpr(pair.second)
                    if(tmpK != null || tmpV != null){
                        expr.pairs[idx] = Pair(tmpK ?: pair.first, tmpV ?: pair.second)
                    }
                }
            }
            is UsedProcMacro -> {
                val macro = procMacroMap[expr.name]
                macro ?: throw errMacroNotFound(expr)
                try {
                    replacedExpr = macro.expand(expr.args)
                }catch (e : Exception){
                    throw errMacroExpand(expr, e)
                }
            }
            else -> {}
        }
        if(expr.hasAttrMacro()){
            if(replacedExpr == null) {
                replacedExpr = expr
            }
            expr.macros().forEach { attr ->
                val macro = attrMacroMap[attr.name]
                if(macro == null || macro.attach.contains(expr::class.java).not()){
                    throw errMacroNotFound(attr)
                }
                try {
                    replacedExpr = macro.expand(ctx, attr.tokens, replacedExpr!!).first() as Expression
                }catch (e : Exception){
                    throw errMacroExpand(attr, e)
                }
            }
        }
        return replacedExpr
    }

    companion object{
        private const val Err : String = "Macro Error"
    }

    private fun errMacroNotFound(macro : UsedAttrMacro) : StaticCheckException{
        return StaticCheckException.make(Err, macro.nameTk, "attribute macro not found : ${macro.name}", "", ctx.codeCtx!!, ctx.currFile)
    }

    private fun errMacroNotFound(macro : UsedProcMacro) : StaticCheckException{
        return StaticCheckException.make(Err, macro, "procedure macro not found : ${macro.name}", "", ctx.codeCtx!!, ctx.currFile)
    }

    private fun errMacroExpand(macro : UsedAttrMacro, e: Exception): Throwable {
        return StaticCheckException.make(
            Err, macro.nameTk,
            "error happened during attribute macro expand : ${e.message}",
            "", ctx.codeCtx!!, ctx.currFile, e
        )
    }

    private fun errMacroExpand(proc: UsedProcMacro, e: Exception): Throwable {
        return StaticCheckException.make(
            Err, proc,
            "error happened during procedure macro expand : ${e.message}",
            "", ctx.codeCtx!!, ctx.currFile, e
        )
    }
}