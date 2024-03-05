package org.luakt.util.buf

class BufWriter : Bytes(), Writer {
    override fun write(b: Byte) {
        super.add(b)
    }

    override fun write(s: Short) {
        super.add(s)
    }

    override fun write(i: Int) {
        super.add(i)
    }

    override fun write(l: Long) {
        super.add(l)
    }

    override fun complete() {}
}