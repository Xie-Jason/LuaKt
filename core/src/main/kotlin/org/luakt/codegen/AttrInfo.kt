package org.luakt.codegen

import org.luakt.util.buf.Bytes
import org.luakt.util.buf.Writer

sealed class AttrInfo(
    val nameIndex : UShort
){
    abstract fun len() : Int
    abstract fun write(w : Writer)

    object Name{
        const val Code = "Code"
        const val LineNumberTable = "LineNumberTable"
        const val SourceFile = "SourceFile"
    }
}

/*
Code_attribute {
	u2 attribute_name_index;
	u4 attribute_length;
	u2 max_stack;
	u2 max_locals;
	u4 code_length;
	u1 code[code_length];
	u2 exception_table_length;
	{  u2 start_pc;
	   u2 end_pc;
	   u2 handler_pc;
	   u2 catch_type;
	} exception_table[exception_table_length];
	u2 attributes_count;
	attribute_info attributes[attributes_count];
}
*/
class CodeAttr(
    nameIndex: UShort,
    val maxStack : UShort,
    val maxLocals : UShort,
    val codeLength : Int,
    val code : Bytes,
    val exceptionTabLen : Short, // 0
    val attrCount : Short,
    val attrInfo : List<AttrInfo>
) : AttrInfo(nameIndex){

    override fun len(): Int {
        return (2 * 2) + 4 + codeLength + 2 + 2 + attrInfo.sumOf{ it.len() + 6 }
    }

    override fun write(w: Writer) {
//        println("code attr ${len()} ${attrInfo.sumOf{ it.len() + 6}}")
        w.write(nameIndex)
        w.write(len())
        w.write(maxStack)
        w.write(maxLocals)
        w.write(codeLength)
        w.write(code.array(), 0, code.len())
        w.write(exceptionTabLen)
        w.write(attrCount)
        attrInfo.forEach { it.write(w) }
    }
}

class LineNumTableAttr(
    nameIndex: UShort,
    val table : FnGenContext.LineNumTable
) : AttrInfo(nameIndex){
    override fun len(): Int = 2 + table.size * 4

    override fun write(w: Writer) {
//        println("    line num table ${len()}")
//        println("    $table")
        w.write(nameIndex)
        w.write(len())
        w.write(table.size.toUShort())
        for (i in 0 until table.size){
            w.write(table.getPc(i))
            w.write(table.getLine(i))
        }
    }
}

class SourceFileAttr(
    attrNameIndex : UShort,
    val fileNameIndex : UShort
) : AttrInfo(attrNameIndex){
    override fun len(): Int = 2

    override fun write(w: Writer) {
        w.write(nameIndex)
        w.write(len())
        w.write(fileNameIndex)
    }
}

class StackMapTableAttr(
    attrNameIndex : UShort,
    val entries : List<StackMapEntry>
) : AttrInfo(attrNameIndex){
    override fun len(): Int = 2 + entries.sumOf(StackMapEntry::len)

    override fun write(w: Writer) {
        w.write(nameIndex)
        w.write(len())
        assert(entries.size < Short.MAX_VALUE)
        w.write(entries.size.toShort())
        entries.forEach { it.write(w) }
    }

}

class StackMapEntry(
    val type : UByte,
    val offset : Int,
    val localTypes : List<StackMapType> = emptyList(),
    val stackTypes : List<StackMapType> = emptyList()
){
    var offsetDelta : Short = 0

    fun setPrevEntryOffset(prevOffset : Int){
        offsetDelta = (offset - prevOffset).toShort()
    }

    fun len() : Int {
        return when(type.toInt()){
            in FrameType.Same -> 1
            in FrameType.SameLocalsOneStackItemFrame -> 1 + stackTypes.first().len()
            FrameType.SameLocalsOneStackItemFrameExtended -> 3 + stackTypes.first().len()
            in FrameType.Chop -> 3
            FrameType.SameFrameExtended -> 3
            in FrameType.Append -> 3 + localTypes.sumOf(StackMapType::len)
            FrameType.Full -> 7 + localTypes.sumOf(StackMapType::len) + stackTypes.sumOf(StackMapType::len)
            else -> throw Exception()
        }
    }

    fun write(w : Writer){
        w.write(type)
        when(type.toInt()){
            in FrameType.Same -> {}
            in FrameType.SameLocalsOneStackItemFrame -> {
                stackTypes.first().write(w)
            }
            FrameType.SameLocalsOneStackItemFrameExtended -> {
                w.write(offsetDelta)
                stackTypes.first().write(w)
            }
            in FrameType.Chop -> {
                w.write(offsetDelta)
            }
            FrameType.SameFrameExtended -> {
                w.write(offsetDelta)
            }
            in FrameType.Append -> {
                w.write(offsetDelta)
                localTypes.forEach { it.write(w) }
            }
            FrameType.Full -> {
                w.write(offsetDelta)
                w.write(localTypes.size.toShort())
                localTypes.forEach { it.write(w) }
                w.write(stackTypes.size.toShort())
                stackTypes.forEach { it.write(w) }
            }
            else -> throw Exception()
        }
    }

    object FrameType{
        val Same = 0..63
        val SameLocalsOneStackItemFrame = 64..127
        const val SameLocalsOneStackItemFrameExtended = 247
        val Chop = 248..250
        const val SameFrameExtended = 251
        val Append = 252..254
        const val Full = 255
    }
}


class StackMapType(
    val tag : Byte,
    val index : UShort = UShort.MAX_VALUE
){
    fun len() : Int{
        return when(tag){
            Tag.Object, Tag.UnInit -> 3
            else -> 1
        }
    }

    fun write(w : Writer){
        w.write(tag)
        when(tag){
            Tag.Object, Tag.UnInit -> w.write(index)
            else -> {}
        }
    }

    object Tag{
        const val Top : Byte = 0
        const val Int : Byte = 1
        const val Float : Byte = 2
        const val Double : Byte = 3
        const val Long : Byte = 4
        const val Null : Byte = 5
        const val UnInitThis : Byte = 6
        const val Object : Byte = 7
        const val UnInit : Byte = 8
    }
}