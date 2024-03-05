package org.luakt.parse

import org.luakt.lexer.CodeContext
import org.luakt.lexer.Token
import org.luakt.syntax.SyntaxNode
import org.luakt.util.log.Log
import org.luakt.util.log.Loggable

open class StaticCheckException(
    private val errorName : String,
    private val line : Int,
    private val offset : Int,
    private val len : Int,
    private val reason : String,
    private val help : String = "",
    private val contextBefore : String = "",
    private val contextAfter : String = "",
    private val file : String = "",
    private val lines : List<String>? = null,
    private val beginOffset : Int = -1,
    private val endOffset : Int = Int.MAX_VALUE,
    private val innerError : Exception? = null
) : Exception(), Loggable {

    override fun toLog() = Log.from {
        red("Compile Error -> ").red(errorName).red(" : ").normal(reason).ln()
        if(len > 0) blue("Line $line , Offset $offset ")
        if(file.isNotEmpty()) blue("In file : $file")
        ln()
        putLn(contextBefore)
        if (lines.isNullOrEmpty()){
            // single line mark
            yellow(StringBuilder().apply{
                repeat(offset){ append('_') }
                repeat(len){ append('↑') }
            }.toString()).ln()
        }else{
            // multi lines mark
            val firstNotBlank = lines.indexOfFirst(String::isNotBlank)
            val lastNotBlank = lines.indexOfLast(String::isNotBlank)
            lines.forEachIndexed { idx, ln ->
                putLn(ln)
                if(ln.isNotBlank()){
                    var (off,end) = rangeOfLn(ln)
                    if(idx == firstNotBlank) off = maxOf(off, beginOffset)
                    if(idx == lastNotBlank) end = minOf(end, endOffset)
                    yellow(StringBuilder().apply{
                        repeat(off){ append('_') }
                        repeat(end - off){ append('↑') }
                    }.toString()).ln()
                }
            }
        }
        putLn(contextAfter)
        if(help.isNotBlank()) blue("Help : ").normal(help).ln()
    }

    private fun rangeOfLn(s : String) : Pair<Int,Int>{
        var off = 0
        var end = s.length - 1
        while (s[off] == ' ') off++
        while (s[end] == ' ') end--
        return Pair(off,end)
    }

    companion object{
        fun make(
            errorName: String,
            token: Token,
            reason: String,
            help: String,
            codeCtx : CodeContext,
            filepath : String,
            error : Exception? = null
        ) : StaticCheckException {
            return StaticCheckException(
                errorName,
                token.line,
                token.offset,
                token.len,
                reason,
                help,
                codeCtx.before(token.idx),
                codeCtx.after(token.idx),
                filepath,
                innerError = error
            )
        }

        fun make(
            errorName: String,
            node : SyntaxNode,
            reason : String,
            help: String,
            codeCtx : CodeContext,
            filepath : String,
            error : Exception? = null
        ) : StaticCheckException{
            return StaticCheckException(
                errorName,
                node.beginLine,
                node.beginOffset,
                node.endIdx - node.beginIdx,
                reason,
                help,
                codeCtx.before(node.beginIdx, false),
                codeCtx.after(node.endIdx),
                filepath,
                codeCtx.lines(node.beginIdx, node.endLine - node.beginLine + 1),
                node.beginOffset,
                node.endOffset,
                innerError = error
            )
        }
    }

}