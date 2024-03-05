package org.luakt.macro

import org.luakt.lexer.CodeContext
import org.luakt.lexer.Token
import org.luakt.macro.parser.ExprParser
import org.luakt.parse.ParseContext
import org.luakt.parse.Parser
import org.luakt.util.config.BuildConfig
import kotlin.math.abs
import kotlin.random.Random

class MacroContext(
    val release : Boolean,
    val attrMacroMap : Map<String, AttrMacro>,
    val procMacroMap : Map<String, ProcMacro>,
    val parseContext: ParseContext
) {
    val storage = mutableMapOf<String,MutableMap<String,Any>>()

    var currFile : String = ""
    var codeCtx : CodeContext? = null

    fun newParser(args : List<Token>) : Parser{
        return Parser(args, currFile, codeCtx!!, parseContext, BuildConfig.empty)
    }
    fun newExprParser(args : List<Token>) : ExprParser{
        return ExprParser(args, currFile, codeCtx!!, parseContext)
    }
    private val random = Random(System.currentTimeMillis())
    fun randName(prefix : String) : String {
        return StringBuilder().append(prefix)
            .append('_')
            .append(System.currentTimeMillis().toString(16))
            .append(abs(random.nextInt()).toString(16))
            .toString()
    }
}