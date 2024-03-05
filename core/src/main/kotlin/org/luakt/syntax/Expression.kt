package org.luakt.syntax

import org.luakt.lexer.Token
import org.luakt.lexer.TokenType
import org.luakt.syntax.VarType.*
import java.lang.Appendable

sealed class Expression (
    beginLine : Int,
    beginOffset : Int,
    beginIdx : Int,
    endLine : Int,
    endOffset : Int,
    endIdx : Int
) : SyntaxNode(beginLine, beginOffset, beginIdx, endLine, endOffset, endIdx){
    constructor(first : Token, last : Token) : this(
        first.line,
        first.offset,
        first.idx,
        last.line,
        last.offset+last.len,
        last.idx+last.len
    )
    constructor(node: SyntaxNode) : this(
        node.beginLine,
        node.beginOffset,
        node.beginIdx,
        node.endLine,
        node.endOffset,
        node.endIdx
    )
    constructor(first: SyntaxNode, last : SyntaxNode) : this(
        first.beginLine,
        first.beginOffset,
        first.beginIdx,
        last.endLine,
        last.endOffset,
        last.endIdx
    )
    constructor(tk: Token, last : SyntaxNode) : this(
        tk.line,
        tk.offset,
        tk.idx,
        last.endLine,
        last.endOffset,
        last.endIdx
    )
    constructor(first: SyntaxNode, last: Token) : this(
        first.beginLine,
        first.beginOffset,
        first.beginIdx,
        last.line,
        last.offset+last.len,
        last.idx+last.len
    )
}

class Var(
    token : Token
) : Expression(token,token){
    val name = token.str
    var location : Location? = null

    constructor(name: String) : this(Token(0,0,0,0,TokenType.Id,name))
    override fun toString(): String = "${super.toString()}<$name>"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append(name)
    }

    fun toToken() : Token{
        return Token(beginLine, beginOffset, endOffset-beginOffset, beginIdx, TokenType.Id, name)
    }
}

class FieldAccess(
    var obj : Expression,
    val fields : MutableList<Expression>
) : Expression(obj,fields.last()){
    override fun toString(): String = "${super.toString()} $obj -> ${fields.joinToString(separator = " -> ")}"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        obj.toSrcCode(buf, depth)
        fields.forEach {
            buf.append("[")
            it.toSrcCode(buf, depth)
            buf.append("]")
        }
    }
}

class BinOp(
    var left : Expression,
    val then : MutableList<Pair<BinOpType,Expression>>
) : Expression(left, then.last().second){
    override fun toString(): String = "${super.toString()}<$left ${then.map { "${it.first} ${it.second}" }.joinToString(" ")}>"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("(")
        left.toSrcCode(buf, depth)
        then.forEach {
            buf.append(" ").append(it.first.toCode()).append(" ")
            it.second.toSrcCode(buf, depth)
        }
        buf.append(")")
    }
}

enum class BinOpType{
    And,
    Or,

    Add, // +
    Sub, // -
    Mul, // *
    Div, // /
    Mod, // %
    Pow, // ^
    Gt,  // >
    Lt,  // <
    Eqs,   // ==
    NotEq, // ~=
    GtEq,  // >=
    LtEq,  // <=
    Concat,// ..
    DivNoRem, // //
    LShift, // <<
    RShift, // >>
    BitXor, // ~
    BitAnd, // &
    BitOr;  // |

    fun toCode() : String = when(this){
        And -> "and"
        Or -> "or"
        Add -> "+"
        Sub -> "-"
        Mul -> "*"
        Div -> "/"
        Mod -> "%"
        Pow -> "^"
        Gt -> ">"
        Lt -> "<"
        Eqs -> "=="
        NotEq -> "~="
        GtEq -> ">="
        LtEq -> "<="
        Concat -> ".."
        DivNoRem -> ""
        LShift -> "<<"
        RShift -> ">>"
        BitAnd -> "&"
        BitOr -> "|"
        BitXor -> "~"
    }
}

class UnaryOp(
    opToken : Token,
    var inner : Expression,
    val op : UniOpType,
) : Expression(opToken,inner){
    constructor(expr: Expression, op: UniOpType) : this(Token.empty, expr, op)
    override fun toString(): String = "${super.toString()} $op $inner"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("(").append(op.toCode()).append(" ")
        inner.toSrcCode(buf, depth)
        buf.append(")")
    }
}

enum class UniOpType{
    Neg,
    Not,
    GetLen,
    BitNot;
    fun toCode() : Char {
        return when(this){
            Neg -> '-'
            Not -> '!'
            GetLen -> '#'
            BitNot -> '~'
        }
    }
}

class Require(
    req : Token,
    end : Token,
    val path : String
) : Expression(req, end){
    constructor(path: String, out : String)
            : this(Token.empty, Token.empty, path) {
        this.outPath = out
    }

    var outPath : String = ""
    var loadPath : String = ""
    var isJavaLib = false

    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("require($path)")
    }

    override fun toString(): String = "Require '$path'"
}

class FuncInvoke(
    close : Token,
    var func : Expression,
    val args : MutableList<Expression>
) : Expression(func,close){
    constructor(func: Expression, args: MutableList<Expression>) : this(Token.empty, func, args)
    override fun toString(): String = "${super.toString()} $func (${args.joinToString()})"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        func.toSrcCode(buf,depth)
        buf.append("(")
        for (idx in 0..args.lastIndex){
            args[idx].toSrcCode(buf, depth)
            if(idx < args.lastIndex) buf.append(", ")
        }
        buf.append(")")
    }
}
/*
class MethodInvoke(
    open : Token,
    close : Token,
    val self : Expression,
    val method : String,
    val args : List<Expression>
) : Expression(open,close){
    constructor(self: Expression, method: String, args: List<Expression>) :
            this(Token.empty,Token.empty,self, method, args)
    override fun toString(): String = "${super.toString()} $self : $method (${args.joinToString()})"
}*/

class DeclaredFunc(
    fn : Token,
    end : Token,
    val params : MutableList<String>,
    val body : Body,
    val paramTokens : MutableList<Token> = mutableListOf()
) : Expression(fn, end) {
    constructor(params: MutableList<String>, body: MutableList<Statement>) :
            this(Token.empty,Token.empty,params,Body(body))

    val paramsLocations = mutableListOf<Location>()
    var meta = FuncMeta()
    var fnIdx = 0

    fun hasVarArg() = params.isNotEmpty() && params.last() == "..."

    override fun toString(): String {
        return "${super.toString()} (${params.joinToString()}) { ${body.joinToString(separator = "\n    ")} })"
    }

    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("function")
            .append(params.joinToString(prefix = "(", postfix = ")"))
            .append("\n")
        // add meta info
        Statement.padding(buf, depth+1)
        buf.append("-- fnId : $fnIdx")
        if(meta.captures.isNotEmpty()) buf.append("  UpValues : ${meta.captures.joinToString()}")
        buf.append(" \n")
        Statement.padding(buf, depth+1)
        Statement.displayLocal(body, buf, 0)
        body.forEach {
            it.toSrcCode(buf, depth+1)
        }
        Statement.padding(buf, depth)
        buf.append("end")
    }
}

class FuncMeta(
    var maxLocals : Int = -1,
    val captures : List<CaptureInfo> = emptyList(),
//    val hasVarArg : Boolean = false
//    val localArrayRef : Int = -1,
//    val localArrayLen : Int = -1,
//    val localArrayIdx : Int = -1
)

data class CaptureInfo(
    val toLoc : Location,
    val fromLoc : Location,
    // is captured from a local variable of outside function
//    val isLocal : Boolean
){
    override fun toString(): String = "$toLoc <- $fromLoc"
}

data class Location(
    var area : VarType,
    var idx  : Int,
    var name : String
){
    override fun toString(): String {
        return when (area) {
            Global -> "gv$idx\$$name"
            FieldUpValue -> "uv$idx\$$name"
            Local -> "lv$idx\$$name"
        }
    }

    companion object{
        fun asGlobal(idx : Int, name : String): Location {
            return Location(Global, idx, name)
        }

        fun asField(idx : Int, name : String): Location {
            return Location(FieldUpValue, idx, name)
        }

        fun asLocal(idx : Int, name: String) : Location{
            return Location(Local, idx, name)
        }
    }
}

enum class VarType{
    Global,
    FieldUpValue,
    Local,
//    FuncUpValue,
//    None,
}

class UsedProcMacro(
    begin : Token,
    end : Token,
    val name : String,
    val args : List<Token>
) : Expression(begin, end) {
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("$name!()")
    }
}

sealed interface LiteralValue

class LiteralInt(
    token : Token
) : Expression(token,token), LiteralValue{
    val int : Long = token.int
    constructor(n : Long) : this(Token(0,0,0,0, TokenType.Int, n))
    constructor(n : Int) : this(Token(0,0,0,0, TokenType.Int, n.toLong()))

    override fun toString(): String = "$int"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append(int.toString())
    }
}

class LiteralFalse(
    token : Token
) : Expression(token,token), LiteralValue{
    override fun toString(): String = "false"
    companion object{
        @JvmField
        val value = LiteralFalse(Token.empty)
    }
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("false")
    }
}

class LiteralTrue(
    token : Token
) : Expression(token,token), LiteralValue{
    override fun toString(): String = "true"
    companion object{
        @JvmField
        val value = LiteralTrue(Token.empty)
    }
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("true")
    }
}

class LiteralNum(
    token : Token
) : Expression(token,token), LiteralValue{
    constructor(num : Double) : this(Token(0,0,0,0,TokenType.Num, num))
    val num : Double = token.num
    override fun toString(): String = "$num"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("$num")
    }
}

class LiteralStr(
    token: Token
) : Expression(token,token), LiteralValue{
    constructor(v : String) : this(Token(0,0,0,0,TokenType.Str, v))
    val str : String = token.str
    override fun toString(): String = "\"$str\""
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append('"').append(str).append('"')
    }
}

class LiteralNil(
    token: Token
) : Expression(token,token), LiteralValue {
    override fun toString(): String = "nil"
    companion object{
        @JvmField
        val value = LiteralNil(Token.empty)
    }
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("nil")
    }
}

class LiteralTable(
    open : Token,
    close : Token,
    val pairs : MutableList<Pair<Expression,Expression>>
) : Expression(open,close), LiteralValue {
    constructor(list: MutableList<Pair<Expression, Expression>>) : this(Token.empty,Token.empty,list)
    override fun toString(): String = "${super.toString()} $pairs"
    override fun toSrcCode(buf: Appendable, depth: Int) {
        buf.append("{")
        pairs.forEach {
            buf.append("[")
            it.first.toSrcCode(buf, depth)
            buf.append("]=")
            it.second.toSrcCode(buf, depth)
            buf.append(';')
        }
        buf.append("}")
    }
}
/*
class LiteralArray(
    open : Token,
    close : Token,
    val list : List<Expression>
) : Expression(open,close), LiteralValue {
    constructor(list: List<Expression>) : this(Token.empty,Token.empty,list)
    override fun toString(): String = "${super.toString()} $list"
}*/
