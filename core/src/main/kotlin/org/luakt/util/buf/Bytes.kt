package org.luakt.util.buf

open class Bytes {
    private var buf : ByteArray = ByteArray(64)
    private var len : Int = 0

    fun add(byte : Byte): Bytes {
        if(len >= buf.size){
            resize(1)
        }
        buf[len++] = byte
        return this
    }

    fun add(b : UByte) : Bytes {
        if(len >= buf.size){
            resize(1)
        }
        buf[len++] = b.toByte()
        return this
    }

    fun add(s : Short): Bytes {
        add(((s.toInt() shr 8) and 0xff).toByte())
        add((s.toInt() and 0xff).toByte())
        return this
    }

    fun add(i : Int): Bytes {
        add(((i shr 24) and 0xff).toByte())
        add(((i shr 16) and 0xff).toByte())
        add(((i shr 8) and 0xff).toByte())
        add(((i shr 0) and 0xff).toByte())
        return this
    }

    fun add(l : Long): Bytes {
        add(((l shr 56) and 0xff).toByte())
        add(((l shr 48) and 0xff).toByte())
        add(((l shr 40) and 0xff).toByte())
        add(((l shr 32) and 0xff).toByte())
        add(((l shr 24) and 0xff).toByte())
        add(((l shr 16) and 0xff).toByte())
        add(((l shr 8) and 0xff).toByte())
        add(((l shr 0) and 0xff).toByte())
        return this
    }

    operator fun get(idx : Int) = buf[idx]

    fun set(idx : Int, byte: Byte){
        buf[idx] = byte
    }

    fun set(idx : Int, s : Short){
        buf[idx] = s.toInt().shr(8).and(0xff).toByte()
        buf[idx+1] = s.toInt().and(0xff).toByte()
    }

    fun set(idx : Int, int : Int){
        buf[idx]   = int.shr(24).and(0xff).toByte()
        buf[idx+1] = int.shr(16).and(0xff).toByte()
        buf[idx+2] = int.shr(8).and(0xff).toByte()
        buf[idx+3] = int.and(0xff).toByte()
    }

    fun array() : ByteArray = this.buf
    fun len() = this.len

    private fun resize(size : Int){
        val newBuf = ByteArray(if (size > len) size + len else len * 2)
        System.arraycopy(buf, 0, newBuf, 0, len)
        buf = newBuf
    }
}