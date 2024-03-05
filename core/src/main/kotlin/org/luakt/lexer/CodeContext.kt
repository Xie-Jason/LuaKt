package org.luakt.lexer

import kotlin.math.max
import kotlin.math.min

class CodeContext(
    private val src : CharArray
) {
    fun before(idx : Int, withLn : Boolean = true) : String{
        var end = idx
        if(withLn){
            // end backward
            while (end < src.size && src[end] != '\n'){
                end++
            }
        }else{
            // end forward
            while (end > 0 && src[end] != '\n'){
                end--
            }
        }
        var begin = idx
        for (c in 0..2){
            while (begin > 0 && src[begin] != '\n'){
                begin--
            }
            begin--
        }
        begin++
        return String(src, max(0,begin), min(src.size,end) - max(0,begin))
//        return String(src, begin, end - begin)
    }

    fun lines(idx: Int, count: Int): List<String>? {
        if (count <= 0) return null
        var begin = idx
        var count = count
        while (begin >= 0 && src[begin] != '\n') {
            begin--
        }
        if(begin >= 0 && src[begin] == '\n') begin++
        var end = idx
        while (end < src.size && count > 0) {
            if (src[end] == '\n') count--
            end++
        }
        if(end < src.size && src[end] == '\n') end--
        //        return String(src, begin, end - begin).split("\n")
        return String(src, max(0, begin), min(src.size, end) - max(0, begin)).split("\n")
    }

    fun after(idx : Int, line : Int = 1) : String{
        var begin = idx
        var line = line
        while (begin < src.size && line > 0){
            if(src[begin] == '\n') line--
            begin++
        }
//        begin++
        var end = begin
        for (c in 0..2){
            while (end < src.size && src[end] != '\n'){
                end++
            }
            end++
        }
        return String(src, min(src.size,begin),min(end,src.size) - min(src.size,begin))
//        return String(src, begin, end - begin)
    }
}