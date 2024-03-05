package org.luakt.parse

import org.luakt.lexer.CodeContext
import org.luakt.lib.Lib
import org.luakt.syntax.*
class LuaScript (
    val file : String,
    val body : Body,
    // key : path
    val requires : Map<String,LuaScript>,
    val codeContext: CodeContext,
    val globals : MutableMap<String, Location>,
    val outputFile : String = ""
){
    var meta = FuncMeta()
    val fields = mutableSetOf<Pair<Int,String>>()
    var fnCount : Int = 0

    fun fillGlobalSymbol(){
        var idx = 0
        globals["arg"] = Location.asGlobal(idx++, "arg")
        Lib.global.forEach{ it ->
            val key = it.key as String
            globals[key] = Location.asGlobal(idx++, key)
        }
    }

    fun dumpSource(bufSupplier : () -> Appendable) : List<Pair<String, Appendable>>{
        val list = mutableListOf<Pair<String, Appendable>>()
        dumpSource(bufSupplier, list)
        return list
    }

    private fun dumpSource(bufSupplier : () -> Appendable, list : MutableList<Pair<String, Appendable>>) {
        for (require in this.requires) {
            val buf = bufSupplier()
            require.value.dumpSource(buf)
            list.add(Pair(require.value.file, buf))
        }
        val buf = bufSupplier()
        this.dumpSource(buf)
        list.add(Pair(this.file, buf))
    }

    private fun dumpSource(buf : Appendable){
        for (imported in this.requires.keys) {
            buf.append("-- import $imported \n")
        }
        Statement.displayLocal(body, buf, 0)
        body.forEach {
            it.toSrcCode(buf, 0)
        }
    }

    companion object{
        val empty = LuaScript("", Body(mutableListOf()), mapOf(), CodeContext("".toCharArray()), mutableMapOf())
    }
}

