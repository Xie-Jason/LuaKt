package org.luakt.syntax

import org.luakt.lexer.Token
import java.lang.Appendable

sealed class Statement (
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

    override fun toSrcCode(buf: Appendable, depth: Int) {
        for (i in 0 until depth) buf.append("    ")
        this.stmtToSrc(buf, depth)
        buf.append('\n')
    }

    abstract fun stmtToSrc(buf: Appendable, depth: Int)

    companion object{
        fun padding(buf: Appendable, depth: Int){
            for (i in 0 until depth) buf.append("    ")
        }

        fun displayLocal(body: Body, buf: Appendable, depth: Int){
            body.locals.asSequence()
                .windowed(8,8,true)
                .forEach { list ->
                    padding(buf, depth)
                    buf.append("-- ")
                    list.forEach{ entry ->
                        buf.append(entry.key)
                            .append(":")
                            .append(if(entry.value.area == VarType.Local) "lv" else "uv")
                            .append(entry.value.idx.toString())
                            .append("  ")
                    }
                    buf.append("\n")
                }
        }

        fun joinToSrc(exprList : List<Expression>, buf: Appendable, depth: Int){
            for (i in 0..exprList.lastIndex){
                exprList[i].toSrcCode(buf,depth)
                if(i < exprList.lastIndex){
                    buf.append(", ")
                }
            }
        }
    }

    override fun toString(): String = "${super.toString()} ${macros().joinToString()}"
}
/*class GlobalVarDeclare(
    begin: Token,
    val name : String,
    val expr : Expression
) : Statement(begin, expr){

}*/

class LocalDeclare(
    local : Token,
    val left : MutableList<String>,
    val right : MutableList<Expression>,
    val leftTokens : MutableList<Token> = mutableListOf()
) : Statement(local, right.last()){
    val locations = mutableListOf<Location>()

    constructor(vars: MutableList<String>, exprList: MutableList<Expression>)
            : this(Token.empty,vars, exprList)

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("local ").append(left.joinToString()).append(" = ")
        joinToSrc(right, buf, depth)
    }

    override fun toString(): String = "${super.toString()} $left = $right"
}

class IfElse(
    begin : Token,
    end : Token,
    val branches : MutableList<Pair<Expression,Body>>
) : Statement(begin,end){
    constructor(branches: MutableList<Pair<Expression, Body>>)
            : this(Token.empty,Token.empty,branches)

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        branches.forEachIndexed { idx, branch ->
            if (idx == 0){
                buf.append("if ")
                branch.first.toSrcCode(buf, depth)
                buf.append(" then\n")
            }else if(idx == branches.lastIndex && branch.first is LiteralTrue) {
                padding(buf, depth)
                buf.append("else\n")
            }else{
                padding(buf, depth)
                buf.append("elseif ")
                branch.first.toSrcCode(buf, depth)
                buf.append(" then\n")
            }
            displayLocal(branch.second, buf, depth+1)
            branch.second.forEach { it.toSrcCode(buf,depth+1) }
        }
        padding(buf, depth)
        buf.append("end")
    }

    override fun toString(): String = "${super.toString()} ${branches.joinToString(" else ", " if ")}"
}

class WhileLoop(
    begin : Token,
    end : Token,
    val cond : Expression,
    val body : Body
) : Statement(begin,end) {
    constructor(cond: Expression, body: MutableList<Statement>)
            : this(Token.empty,Token.empty,cond, Body(body))

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("while ")
        cond.toSrcCode(buf, depth)
        buf.append(" do\n")
        displayLocal(body, buf, depth+1)
        body.forEach { it.toSrcCode(buf,depth+1) }
        padding(buf, depth)
        buf.append("end")
    }

    override fun toString(): String = "${super.toString()} while $cond { ${body.joinToString("\n  ")} }"
}

class RepeatLoop(
    begin : Token,
    val cond : Expression,
    val body : Body
) : Statement(begin, cond) {
    constructor(cond: Expression, body: MutableList<Statement>)
            : this(Token.empty,cond, Body(body))

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("repeat\n")
        displayLocal(body, buf, depth+1)
        body.forEach { it.toSrcCode(buf, depth+1) }
        padding(buf, depth)
        buf.append("until ")
        cond.toSrcCode(buf, depth)
    }

    override fun toString(): String = "${super.toString()} repeat { ${body.joinToString("\n  ")} until $cond }"
}

class ForEqLoop(
    begin : Token,
    end : Token,
    val name : String,
    val start : Expression,
    val limit : Expression,
    val step : Expression?,
    val body : Body
) : Statement(begin,end) {
    constructor(name: String, start: Expression, limit: Expression, step: Expression?, body: MutableList<Statement>) :
            this(Token.empty,Token.empty,name, start, limit, step, Body(body))

    var location : Location? = null

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("for $name = ")
        start.toSrcCode(buf, depth)
        buf.append(", ")
        limit.toSrcCode(buf, depth)
        buf.append(", ")
        step?.toSrcCode(buf, depth)
        buf.append(" do\n")
        displayLocal(body, buf, depth+1)
        body.forEach { it.toSrcCode(buf, depth+1) }
        padding(buf, depth)
        buf.append("end")
    }

    override fun toString(): String = "${super.toString()} for $name = $start,$limit,$step { ${body.joinToString("\n  ")} }"
}

class ForInLoop(
    begin : Token,
    end : Token,
    val names : MutableList<String>,
    val iter : Expression,
    val body : Body,
    val nameTokens : MutableList<Token> = mutableListOf<Token>()
) : Statement(begin,end) {
    constructor(names: MutableList<String>, iter: Expression, body: MutableList<Statement>) :
            this(Token.empty, Token.empty, names, iter, Body(body))

    val locations : MutableList<Location> = mutableListOf()

    override fun toString(): String = "${super.toString()} for $names in $iter { ${body.joinToString("\n  ")} }"
    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("for ${names.joinToString()} in ")
        iter.toSrcCode(buf, depth)
        buf.append(" do\n")
        displayLocal(body, buf, depth+1)
        body.forEach { it.toSrcCode(buf, depth+1) }
        padding(buf, depth)
        buf.append("end")
    }
}

class Discard(
    val expr : Expression
) : Statement(expr){
    override fun toString(): String = "${super.toString()} $expr"
    override fun stmtToSrc(buf: Appendable, depth: Int) = expr.toSrcCode(buf, depth)
}

class Return(
    ret : Token,
    val list : MutableList<Expression>
) : Statement(
    ret.line,
    ret.offset,
    ret.idx,
    (list.lastOrNull()?.endLine  ?: ret.line),
    ((list.lastOrNull()?.endOffset ?: (ret.offset + ret.len))),
    (list.lastOrNull()?.endIdx  ?: ret.idx),
){
    constructor(list: MutableList<Expression>) : this(Token.empty, list)
    override fun toString(): String = "${super.toString()} $list"
    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("return ")
        joinToSrc(list, buf, depth)
    }
}

class Assign(
    val left : MutableList<Expression>,
    val right : MutableList<Expression>
) : Statement(left.first(), right.last()) {
    val locations = mutableListOf<Location>()

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        joinToSrc(left, buf, depth)
        buf.append(" = ")
        joinToSrc(right, buf, depth)
    }

    override fun toString(): String = "${super.toString()} $left = $right"
}

class Block(
    begin : Token,
    end : Token,
    val body : Body
) : Statement(begin,end){
    constructor(list: MutableList<Statement>) : this(Token.empty,Token.empty, Body(list))

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("do\n")
        displayLocal(body, buf, depth+1)
        body.forEach { it.toSrcCode(buf, depth+1) }
        padding(buf, depth)
        buf.append("end")
    }

    override fun toString(): String = "${super.toString()} { ${body.joinToString("\n  ")} }"
}

class Body(
    list: MutableList<Statement>,
    val locals : MutableMap<String, Location> = mutableMapOf(),
) : ArrayList<Statement>(list)

class Break(brk : Token) : Statement(brk,brk) {
    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("break")
    }
}

class Label(
    begin : Token,
    end : Token,
    val name : String
) : Statement(begin,end){
    var index : Int = -1
    var offset : Int = 0

    constructor(name : String) : this(Token.empty,Token.empty,name)

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("::").append(name).append("::")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Label
        if (name != other.name) return false
        if (index != other.index) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + index
        return result
    }

    companion object{
        val none = Label("")
    }

    override fun toString(): String = "::$name\$$index ln$beginLine::"
}

class Goto(
    begin : Token,
    end : Token,
    val labelName : String
) : Statement(begin,end){
    constructor(label : String) : this(Token.empty,Token.empty,label)

    var label : Label = Label.none
    // if false, will not be limited by the definition scope of target label, usually used by macro
    var strictScope : Boolean = true

    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append("goto ").append(labelName)
    }

    override fun toString(): String = "goto ln${beginLine} => $label"
}

class None(semi : Token) : Statement(semi,semi){
    override fun stmtToSrc(buf: Appendable, depth: Int) {
        buf.append(";")
    }
}