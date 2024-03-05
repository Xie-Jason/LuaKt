package org.luakt.util.buf

@OptIn(ExperimentalUnsignedTypes::class)
class UShorts {

    private var buf = UShortArray(64)
    private var len : Int = 0

    fun add(s : UShort){
        if(len >= buf.size){
            resize(1)
        }
        buf[len++] = s
    }

    fun add(s1 : UShort, s2 : UShort){
        if(len >= buf.size){
            resize(2)
        }
        buf[len++] = s1
        buf[len++] = s2
    }

    operator fun get(idx : Int) = buf[idx]

    operator fun set(idx : Int, value : UShort){
        buf[idx] = value
    }

    fun array() = this.buf
    fun len() = this.len

    private fun resize(size : Int){
        val newBuf = UShortArray(if (size > len) size + len else len * 2)
        for(idx in 0 until len){
            newBuf[idx] = buf[idx]
        }
        buf = newBuf
    }

    override fun toString(): String {
        return buf.slice(0 until len).toUShortArray().contentToString()
    }
}