package org.luakt.util.buf

interface Writer {
    fun write(b : Byte)
    fun write(s : Short)
    fun write(i : Int)
    fun write(l : Long)
    fun complete()

    fun write(d : Double) = write(d.toRawBits())
    fun write(f : Float)  = write(f.toRawBits())
    fun write(s : UShort) = write(s.toShort())
    fun write(b : UByte)  = write(b.toByte())

    fun write(array : ByteArray, start : Int = 0, len : Int = array.size){
        for(idx in start until start+len){
            write(array[idx])
        }
    }
}