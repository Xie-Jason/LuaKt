package org.luakt.lexer

import org.luakt.util.log.Log
import org.luakt.util.log.Loggable

class LexerException(
    private val line : Int,
    // span
    private val offset : Int,
    private val len : Int,
    private val reason : String,
    private val help : String = "",
    private val contextBefore : String = "",
    private val contextAfter : String = "",
    private val file : String = "",
) : Exception(), Loggable {

    override fun toLog() = Log.from {
        red("Compile Error -> Lexer Error  ").normal(reason).ln()
        blue("Line $line , Offset $offset")
        if(file.isNotEmpty()) blue(" of $file")
        ln()
        putLn(contextBefore)
        yellow(StringBuilder().apply{
            repeat(offset){ append('_') }
            repeat(len){ append('↑') }
        }.toString()).ln()
        putLn(contextAfter)
        if(help.isNotBlank()) blue("Help : ").normal(help).ln()
    }

    companion object{
        fun invalidIdHeadChar(line : Int, offset : Int, len : Int, invalid : Char, ctxBefore : String, ctxAfter : String, file: String) : LexerException{
            return LexerException(
                line,offset,len,
                "invalid first char in identifier : '$invalid'",
                "you could use '_' or any non-special-symbol and non-number character as the begin of identifier in your lua program",
                ctxBefore,ctxAfter,file
            )
        }
        fun unexpectChar(line : Int, offset : Int, len : Int, invalid : Char, ctxBefore : String, ctxAfter : String, file: String) : LexerException{
            fun chineseChar(e : Char) = "You should use half-width character '$e' rather than full-width character '$invalid'"
            val help = when(invalid){
                '，' -> chineseChar(',')
                '：' -> chineseChar(':')
                '“' -> chineseChar('“')
                '”' -> chineseChar('”')
                '’' -> chineseChar('’')
                '‘' -> chineseChar('‘')
                else -> ""
            }
            return LexerException(line,offset,len, "unexpected character : '$invalid'", help, ctxBefore,ctxAfter,file)
        }
        fun unresolvedNumber(line : Int, offset : Int, len : Int, invalid : String, ctxBefore : String, ctxAfter : String, file: String) : LexerException{
            return LexerException(line,offset,len,
                "Unresolved Number : '$invalid'", "",
                ctxBefore,ctxAfter,file
            )
        }
    }
}