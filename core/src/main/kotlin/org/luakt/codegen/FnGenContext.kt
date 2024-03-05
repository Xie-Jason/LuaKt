package org.luakt.codegen

import org.luakt.syntax.*
import org.luakt.util.buf.UShorts

class FnGenContext(
    val name : String,
    val fnMeta : FuncMeta,
    val body : Body,
    val globals : Map<String, Location>,
    val file: ClassFile,
    val params : List<Location> = listOf(),
    val codes : Codes = Codes(file),
    val hasVarArg : Boolean = false,
    private val blockStack : MutableList<BlkGenCtx> = mutableListOf(),
){
    val gotos = mutableListOf<Pair<Goto, Int>>()
    private val lineNumTable = LineNumTable()

    init {
        codes.setLineNumTable(lineNumTable)
    }

    fun enterLoop(){
        blockStack.add(BlkGenCtx())
    }

    fun exitLoop(labelEnd : Int){
        val blk = blockStack.removeLast()
        blk.labelBreakIndices.forEach { labelIdx ->
            codes.set(labelIdx, (labelEnd - labelIdx + 1).toShort())
        }
    }

    fun completeFunc(){
        gotos.forEach {
            val (goto, idx) = it
            val target = goto.label.offset
            val from = idx - 1
            codes.set(idx, target - from)
        }
    }

    fun addLabelBreakIdx(labelIdx: Int) {
        blockStack.last().labelBreakIndices.add(labelIdx)
    }

    fun markLine(ln : Int){
        val line = ln.toUInt()
        if(lineNumTable.size == 0 || (line <= UShort.MAX_VALUE && line > lineNumTable.lastLine())){
            lineNumTable.add(codes.len().toUShort(), ln.toUShort())
        }
    }

    fun markJumpTarget() {
        // TODO used for stackMapTable
    }

    companion object{
        const val ArgsIdx : Byte = 1
        const val UpValuesIdx : Byte = 2
    }

    class BlkGenCtx(
        val labelBreakIndices : MutableList<Int> = mutableListOf()
    )

    class LineNumTable{
        private val vec : UShorts = UShorts()

        val size get() = vec.len() / 2

        fun lastLine() : UShort = vec[size * 2 - 1]

        fun add(pc : UShort, ln : UShort){
            vec.add(pc, ln)
        }

        fun getPc(idx : Int) : UShort = vec[idx * 2]

        fun getLine(idx : Int) = vec[idx * 2 + 1]

        override fun toString(): String {
            return vec.toString()
        }
    }
}