package org.luakt.codegen

import org.luakt.util.buf.Writer

sealed interface ConstantInfo{
    fun tag() : Byte
    fun write(w : Writer)
}

class ConUtf8(
    val value : String
) : ConstantInfo{
    override fun tag(): Byte = 1

    override fun write(w: Writer) {
        w.write(tag())
        val bytes = value.toByteArray()
        w.write(bytes.size.toShort())
        w.write(bytes)
    }
}

class ConInt(
    val value : Int
) : ConstantInfo{
    override fun tag(): Byte = 3
    override fun write(w: Writer) {
        w.write(tag())
        w.write(value)
    }
}

class ConFloat(
    val value : Float
) : ConstantInfo{
    override fun tag(): Byte = 4
    override fun write(w: Writer) {
        w.write(tag())
        w.write(value)
    }
}

class ConLong(
    val value : Long
) : ConstantInfo{
    override fun tag(): Byte = 5
    override fun write(w: Writer) {
        w.write(tag())
        w.write(value)
    }
}

class ConDouble(
    val value : Double
) : ConstantInfo{
    override fun tag(): Byte = 6
    override fun write(w: Writer) {
        w.write(tag())
        w.write(value)
    }
}

class ConClass(
    val index: UShort
) : ConstantInfo{
    override fun tag(): Byte = 7
    override fun write(w: Writer) {
        w.write(tag())
        w.write(index)
    }
}

class ConString(
    val index: UShort
) : ConstantInfo{
    override fun tag(): Byte = 8
    override fun write(w: Writer) {
        w.write(tag())
        w.write(index)
    }
}

class ConFieldRef(
    val classIndex: UShort,
    val nameAndTypeIndex: UShort
)  : ConstantInfo{
    override fun tag(): Byte = 9
    override fun write(w: Writer) {
        w.write(tag())
        w.write(classIndex)
        w.write(nameAndTypeIndex)
    }
}

class ConMethodRef(
    val classIndex: UShort,
    val nameAndTypeIndex: UShort
)  : ConstantInfo{
    override fun tag(): Byte = 10
    override fun write(w: Writer) {
        w.write(tag())
        w.write(classIndex)
        w.write(nameAndTypeIndex)
    }
}

class ConInterMethodRef(
    val classIndex: UShort,
    val nameAndTypeIndex: UShort
) : ConstantInfo{
    override fun tag(): Byte = 11
    override fun write(w: Writer) {
        w.write(tag())
        w.write(classIndex)
        w.write(nameAndTypeIndex)
    }
}

class ConNameAndType(
    val nameIndex: UShort,
    val descriptorIndex: UShort
) : ConstantInfo{
    override fun tag(): Byte = 12
    override fun write(w: Writer) {
        w.write(tag())
        w.write(nameIndex)
        w.write(descriptorIndex)
    }
}

class ConMethodHandle(
    val refKind : Byte,
    val refIndex: UShort
) : ConstantInfo{
    override fun tag(): Byte = 15
    override fun write(w: Writer) {
        w.write(tag())
        w.write(refKind)
        w.write(refIndex)
    }
}

class ConMethodType(
    val index: UShort
) : ConstantInfo{
    override fun tag(): Byte = 16
    override fun write(w: Writer) {
        w.write(tag())
        w.write(index)
    }
}

class ConInvokeDyn(
    val bootMethodAttrIndex: UShort,
    val nameAndTypeIndex: UShort
) : ConstantInfo{
    override fun tag(): Byte = 18
    override fun write(w: Writer) {
        w.write(tag())
        w.write(bootMethodAttrIndex)
        w.write(nameAndTypeIndex)
    }
}

// fill ConPadding object after Double and Long ConstantInfo
class ConPadding(
    val _unused : Int = -1
) : ConstantInfo{
    override fun tag(): Byte = _unused.toByte()
    override fun write(w: Writer) {}
}