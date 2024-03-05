package org.luakt.macro.parser

import org.luakt.lexer.Token
import org.luakt.lexer.TokenType
import org.luakt.parse.AbsParser
import org.luakt.syntax.*
import org.luakt.util.log.Log

class TypeParser(
    tokenList: List<Token>
) : AbsParser(tokenList) {

    fun parse() : List<DeclaredType>{
        val types = mutableListOf<DeclaredType>()
        while (hasNext){
            types.add(parseType())
            if(hasNext && ahead().type == TokenType.Comma){
                next()
            }
        }
        return types
    }

    /*
    type         number, function, string, boolean, thread, any, customized, java.*.*
    nullable     type?
    union        type | type
    table.array  {type}
    table.map    {type:type}
    table.tuple  {type,type ...}
    table.object {'k':type,'k':type ... }
    paren        (type)
    */
    private fun parseType() : DeclaredType {
        val tk = next()
        var typ = when(tk.type){
            TokenType.Id -> {
                when(tk.str){
                    "any" -> DeclaredType.anyNotNull
                    "number" -> DeclaredType.numNotNull
                    "string" -> DeclaredType.strNotNull
                    "boolean" -> DeclaredType.boolNotNull
                    "thread" -> DeclaredType.threadNotNull
                    "table" -> DeclaredType.tableNotNull
                    "java" -> {
                        val buf = StringBuilder(tk.str)
                        while (hasNext && ahead().type == TokenType.Dot){
                            next()
                            buf.append('.').append(next().str)
                        }
                        DeclaredJavaType(false,buf.toString())
                    }
                    else -> DeclaredCustomType(false,tk.str)
                }
            }
            // (
            TokenType.LParen -> {
                val type = parseType()
                assertTk(next(), TokenType.RParen)
                type
            }
            else -> throw Exception()
        }
        // ?
        if (hasNext && ahead().type == TokenType.Ques) {
            next()
            typ.nullable = true
        }
        // |
        while (hasNext && ahead().type == TokenType.BitOr) {
            next()
            val another = parseType()
            if(typ is DeclaredUnionType && another is DeclaredUnionType){
                typ.types.addAll(another.types)
            }else if(typ is DeclaredUnionType){
                typ.types.add(another)
            }else{
                typ = DeclaredUnionType(false, mutableListOf(typ,another))
            }
        }
        return typ
    }

    private fun assertTk(tk : Token, type : TokenType){
        throw Log.from {

        }
    }
}