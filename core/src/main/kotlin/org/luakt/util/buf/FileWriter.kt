package org.luakt.util.buf

import java.io.BufferedOutputStream
import java.io.OutputStream

class FileWriter(
    out : OutputStream
) : BufferedOutputStream(out), Writer {

    override fun write(b : Byte) = super<BufferedOutputStream>.write(b.toInt())

    override fun write(s : Short){
        super<BufferedOutputStream>.write((s.toInt() shr 8) and 0xff)
        super<BufferedOutputStream>.write(s.toInt() and 0xff)
    }

    override fun write(int : Int){
        super<BufferedOutputStream>.write((int shr 24) and 0xff)
        super<BufferedOutputStream>.write((int shr 16) and 0xff)
        super<BufferedOutputStream>.write((int shr 8) and 0xff)
        super<BufferedOutputStream>.write(int and 0xff)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        super<Writer>.write(b, off, len)
    }

    override fun write(l : Long){
        write(((l shr 56) and 0xff).toByte())
        write(((l shr 48) and 0xff).toByte())
        write(((l shr 40) and 0xff).toByte())
        write(((l shr 32) and 0xff).toByte())
        write(((l shr 24) and 0xff).toByte())
        write(((l shr 16) and 0xff).toByte())
        write(((l shr 8) and 0xff).toByte())
        write((l and 0xff).toByte())
    }

    override fun complete() {
        super.flush()
    }
}