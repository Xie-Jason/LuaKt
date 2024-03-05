package org.luakt.lexer

import org.luakt.util.Print.padding

class Token(
    val line : Int,
    val offset: Int,
    val len: Int,
    val idx: Int,
    val type : TokenType,
    private val data : Any? = null
)
{
//    constructor(line : Int, offset : Int, len : Int, idx : Int, type: TokenType, data: Any? = null) :
//            this(arrayOf(line,offset,len,idx),type, data)
    constructor(token: Token) : this(token.line, token.offset, token.len, token.idx, token.type, token.data)

    constructor(pos: Array<Int>, type : TokenType, data: Any? = null)
            : this(pos[0], pos[1], pos[2], pos[3], type, data)

    val num
        get() = this.data!! as Double
//        set(value) { this.data = value }

    val int
        get() = this.data!! as Long
//        set(value) { this.data = value }

    val str
        get() = this.data!! as String
//        set(value) { this.data = value }

    companion object{
        val empty = Token(0,0,0,0,TokenType.Semi)

        val identifiers = mapOf(
            "true" to TokenType.True,
            "false" to TokenType.False,
            "local" to TokenType.Local,
            "global" to TokenType.Global,
            "function" to TokenType.Function,
            "return" to TokenType.Return,
            "end" to TokenType.End,
            "if" to TokenType.If,
            "else" to TokenType.Else,
            "elseif" to TokenType.ElseIf,
            "then" to TokenType.Then,
            "while" to TokenType.While,
            "do" to TokenType.Do,
            "for" to TokenType.For,
            "break" to TokenType.Break,
            "repeat" to TokenType.Repeat,
            "until" to TokenType.Until,
            "and" to TokenType.And,
            "not" to TokenType.Not,
            "or" to TokenType.Or,
            "nil" to TokenType.Nil,
            "in" to TokenType.In,
            "goto" to TokenType.Goto
        )
    }

//    override fun toString(): String = "($type${if(data != null) " " + data.toString() else ""})${pos.contentToString()}"
    override fun toString() : String =
        "${line.padding()} ${offset.padding()} ${len.padding()} ${idx.padding()}   $type ${if(data != null) "$data" else ""}"
}