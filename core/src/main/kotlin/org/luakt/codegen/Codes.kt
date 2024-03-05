package org.luakt.codegen

import org.luakt.util.buf.Bytes
import kotlin.math.max

class Codes(
    private val file: ClassFile
) : Bytes(){
    private var currStackTop : Int = 0
    private var maxStackSize : Int = 0
    var lineNumTable : FnGenContext.LineNumTable? = null
        private set

    fun maxStack() = maxStackSize + 1

    fun putConst(int : Int){
        when(int){
            -1 -> put(ByteCode.IConstM1)
            0  -> put(ByteCode.IConst0)
            1  -> put(ByteCode.IConst1)
            2  -> put(ByteCode.IConst2)
            3  -> put(ByteCode.IConst3)
            4  -> put(ByteCode.IConst4)
            5  -> put(ByteCode.IConst5)
            in Byte.MIN_VALUE..Byte.MAX_VALUE -> put(ByteCode.BIPush, int.toByte())
            in Short.MIN_VALUE..Short.MAX_VALUE -> put(ByteCode.SIPush, int.toShort())
            else -> {
                val idx = file.useInt(int)
                if(idx < UByte.MAX_VALUE){
                    put(ByteCode.LDC, idx.toUByte())
                }else{
                    put(ByteCode.LDC_W, idx)
                }
            }
        }
    }

    fun putConst(bool : Boolean){

    }

    fun put(code : UByte, b1 : Byte){
        currStackTop += ByteCode.stackSizeEffect(code)
        maxStackSize = max(maxStackSize, currStackTop)
        // printCode(code)
        add(code)
        add(b1)
    }

    fun put(code : UByte, s : UByte){
        currStackTop += ByteCode.stackSizeEffect(code)
        maxStackSize = max(maxStackSize, currStackTop)
        // printCode(code)
        add(code)
        add(s)
    }

    fun put(code: UByte, argSize : Short, retVal : Boolean, conIdx : UShort){
        assert(code in ByteCode.InvokeVirtual..ByteCode.InvokeInterface)
        currStackTop += ByteCode.stackSizeEffect(code, argSize, if(retVal) 1 else 0)
        maxStackSize = max(maxStackSize, currStackTop)
        // printCode(code)
        add(code)
        add(conIdx.toShort())
    }

    fun put(code : UByte, s : Short){
        currStackTop += ByteCode.stackSizeEffect(code)
        maxStackSize = max(maxStackSize, currStackTop)
        // printCode(code)
        add(code)
        add(s)
    }

    fun put(code : UByte, s : UShort){
        currStackTop += ByteCode.stackSizeEffect(code)
        maxStackSize = max(maxStackSize, currStackTop)
        // printCode(code)
        add(code)
        add(s.toShort())
    }

    fun put(code: UByte, b1: Byte, b2 : Byte){
        currStackTop += ByteCode.stackSizeEffect(code)
        maxStackSize = max(maxStackSize, currStackTop)
        // printCode(code)
        add(code)
        add(b1)
        add(b2)
    }

    fun put(code: UByte): Codes {
        currStackTop += ByteCode.stackSizeEffect(code)
        maxStackSize = max(maxStackSize, currStackTop)
        // printCode(code)
        add(code)
        return this
    }

    fun setLineNumTable(lineNumTable: FnGenContext.LineNumTable){
        this.lineNumTable = lineNumTable
    }

    private fun printCode(code : UByte){
        println("[$code] $currStackTop, $maxStackSize")
    }
}