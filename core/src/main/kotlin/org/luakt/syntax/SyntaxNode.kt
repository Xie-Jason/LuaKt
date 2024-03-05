package org.luakt.syntax

import java.lang.Appendable

abstract class SyntaxNode(
    beginLine : Int,
    beginOffset : Int,
    beginIdx : Int,
    endLine : Int,
    endOffset : Int,
    endIdx : Int
) : ToSourceCode {
    private val pos : Array<Int> = arrayOf(beginLine,beginOffset,beginIdx,endLine,endOffset,endIdx)
    private var macros : MutableList<UsedAttrMacro>? = null

    val beginLine
        get() = pos[0]
    val beginOffset
        get() = pos[1]
    val beginIdx
        get() = pos[2]
    var endLine
        get() = pos[3]
        set(value) {
            pos[3] = value
        }
    var endOffset: Int
        get() = pos[4]
        set(value) {
            pos[4] = value
        }
    var endIdx : Int
        get() = pos[5]
        set(value){
            pos[5] = value
        }

    fun addMacro(macro : UsedAttrMacro){
        if(this.macros == null) this.macros = mutableListOf()
        this.macros!!.add(macro)
    }
    fun addMacro(macros : List<UsedAttrMacro>){
        if(this.macros == null) this.macros = mutableListOf()
        macros.forEach{
            this.macros!!.add(it)
        }
    }
    fun hasAttrMacro() : Boolean = this.macros?.isNotEmpty() ?: false
    fun macros() : Sequence<UsedAttrMacro> = this.macros?.asSequence() ?: sequenceOf()

//    override fun toString() = "${this.javaClass.simpleName} ${if(this is Statement) pos.contentToString() else ""}"
    override fun toString(): String = this.javaClass.simpleName
}


interface ToSourceCode {
    fun toSrcCode(buf : Appendable, depth : Int)
}