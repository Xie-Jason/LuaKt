package org.luakt.util

object Print {
    fun Number.padding(len : Int = 4) : String {
        val s = this.toString()
        if(s.length >= len) return s
        return " ".repeat(len - s.length) + s
    }
}