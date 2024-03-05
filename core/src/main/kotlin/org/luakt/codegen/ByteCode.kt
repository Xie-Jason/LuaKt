package org.luakt.codegen

object ByteCode {
    val NOP : UByte = 0.toUByte()
    // ======== Const -> OpStack ========
    val AConstNull : UByte = 1.toUByte()
    val IConstM1 : UByte = 2.toUByte()
    val IConst0 : UByte = 3.toUByte()
    val IConst1 : UByte = 4.toUByte()
    val IConst2 : UByte = 5.toUByte()
    val IConst3 : UByte = 6.toUByte()
    val IConst4 : UByte = 7.toUByte()
    val IConst5 : UByte = 8.toUByte()
    val LConst0 : UByte = 9.toUByte()
    val LConst1 : UByte = 10.toUByte()
    val FConst0 : UByte = 11.toUByte()
    val FConst1 : UByte = 12.toUByte()
    val FConst2 : UByte = 13.toUByte()
    val DConst0 : UByte = 14.toUByte()
    val DConst1 : UByte = 15.toUByte()
    val BIPush : UByte = 16.toUByte()
    val SIPush : UByte = 17.toUByte()
    val LDC : UByte = 18.toUByte()
    val LDC_W : UByte = 19.toUByte()
    val LDC2W : UByte = 20.toUByte()
    // ======== Slot -> OpStack ========
    val ILoad : UByte = 21.toUByte()
    val LLoad : UByte = 22.toUByte()
    val FLoad : UByte = 23.toUByte()
    val DLoad : UByte = 24.toUByte()
    val ALoad : UByte = 25.toUByte()
    /* ====== Auto replaced by JVM =======
    val ILoad0 : UByte = 26.toUByte()
    val ILoad1 : UByte = 27.toUByte()
    val ILoad2 : UByte = 28.toUByte()
    val ILoad3 : UByte = 29.toUByte()
    val LLoad0 : UByte = 30.toUByte()
    val LLoad1 : UByte = 31.toUByte()
    val LLoad2 : UByte = 32.toUByte()
    val LLoad3 : UByte = 33.toUByte()
    val FLoad0 : UByte = 34.toUByte()
    val FLoad1 : UByte = 35.toUByte()
    val FLoad2 : UByte = 36.toUByte()
    val FLoad3 : UByte = 37.toUByte()
    val DLoad0 : UByte = 38.toUByte()
    val DLoad1 : UByte = 39.toUByte()
    val DLoad2 : UByte = 40.toUByte()
    val DLoad3 : UByte = 41.toUByte()
    val ALoad0 : UByte = 42.toUByte()
    val ALoad1 : UByte = 43.toUByte()
    val ALoad2 : UByte = 44.toUByte()
    val ALoad3 : UByte = 45.toUByte()*/
    // ====== Slot Array -> OpStack ======
    val IALoad : UByte = 46.toUByte()
    val LALoad : UByte = 47.toUByte()
    val FALoad : UByte = 48.toUByte()
    val DALoad : UByte = 49.toUByte()
    val AALoad : UByte = 50.toUByte()
    val BALoad : UByte = 51.toUByte()
    val CALoad : UByte = 52.toUByte()
    val SALoad : UByte = 53.toUByte()
    // ======== OpStack -> Slot ========
    val IStore : UByte = 54.toUByte()
    val LStore : UByte = 55.toUByte()
    val FStore : UByte = 56.toUByte()
    val DStore : UByte = 57.toUByte()
    val AStore : UByte = 58.toUByte()
    /* ====== Auto replaced by JVM =======
    val IStore0 : UByte = 59.toUByte()
    val IStore1 : UByte = 60.toUByte()
    val IStore2 : UByte = 61.toUByte()
    val IStore3 : UByte = 62.toUByte()
    val LStore0 : UByte = 63.toUByte()
    val LStore1 : UByte = 64.toUByte()
    val LStore2 : UByte = 65.toUByte()
    val LStore3 : UByte = 66.toUByte()
    val FStore0 : UByte = 67.toUByte()
    val FStore1 : UByte = 68.toUByte()
    val FStore2 : UByte = 69.toUByte()
    val FStore3 : UByte = 70.toUByte()
    val DStore0 : UByte = 71.toUByte()
    val DStore1 : UByte = 72.toUByte()
    val DStore2 : UByte = 73.toUByte()
    val DStore3 : UByte = 74.toUByte()
    val AStore0 : UByte = 75.toUByte()
    val AStore1 : UByte = 76.toUByte()
    val AStore2 : UByte = 77.toUByte()
    val AStore3 : UByte = 78.toUByte()*/
    // ====== OpStack -> Slot Array ======
    val IAStore : UByte = 79.toUByte()
    val LAStore : UByte = 80.toUByte()
    val FAStore : UByte = 81.toUByte()
    val DAStore : UByte = 82.toUByte()
    val AAStore : UByte = 83.toUByte()
    val BAStore : UByte = 84.toUByte()
    val CAStore : UByte = 85.toUByte()
    val SAStore : UByte = 86.toUByte()
    // ========== OpStack Op ==========
    val Pop : UByte = 87.toUByte()
    val Pop2 : UByte = 88.toUByte()
    val Dup : UByte = 89.toUByte()
    val DupX1 : UByte = 90.toUByte()
    val DupX2 : UByte = 91.toUByte()
    val Dup2 : UByte = 92.toUByte()
    val Dup2X1 : UByte = 93.toUByte()
    val Dup2X2 : UByte = 94.toUByte()
    val Swap : UByte = 95.toUByte()
    // ========= OpStack Calc =========
    val IAdd : UByte = 96.toUByte()
    val LAdd : UByte = 97.toUByte()
    val FAdd : UByte = 98.toUByte()
    val DAdd : UByte = 99.toUByte()
    val ISub : UByte = 100.toUByte()
    val LSub : UByte = 101.toUByte()
    val FSub : UByte = 102.toUByte()
    val DSub : UByte = 103.toUByte()
    val IMul : UByte = 104.toUByte()
    val LMul : UByte = 105.toUByte()
    val FMul : UByte = 106.toUByte()
    val DMul : UByte = 107.toUByte()
    val IDiv : UByte = 108.toUByte()
    val LDiv : UByte = 109.toUByte()
    val FDiv : UByte = 110.toUByte()
    val DDiv : UByte = 111.toUByte()
    val IRem : UByte = 112.toUByte()
    val LRem : UByte = 113.toUByte()
    val FRem : UByte = 114.toUByte()
    val DRem : UByte = 115.toUByte()
    val INeg : UByte = 116.toUByte()
    val LNeg : UByte = 117.toUByte()
    val FNeg : UByte = 118.toUByte()
    val DNeg : UByte = 119.toUByte()
    val IShl : UByte = 120.toUByte()
    val LShl : UByte = 121.toUByte()
    val IShr : UByte = 122.toUByte()
    val LShr : UByte = 123.toUByte()
    val IUShr : UByte = 124.toUByte()
    val LUShr : UByte = 125.toUByte()
    val IAnd : UByte = 126.toUByte()
    val LAnd : UByte = 127.toUByte()
    val IOr : UByte = 128.toUByte()
    val LOr : UByte = 129.toUByte()
    val IXor : UByte = 130.toUByte()
    val LXor : UByte = 131.toUByte()
    val IInc : UByte = 132.toUByte()
    // ========== Type Cast ==========
    val I2L : UByte = 133.toUByte()
    val I2F : UByte = 134.toUByte()
    val I2D : UByte = 135.toUByte()
    val L2I : UByte = 136.toUByte()
    val L2F : UByte = 137.toUByte()
    val L2D : UByte = 138.toUByte()
    val F2I : UByte = 139.toUByte()
    val F2L : UByte = 140.toUByte()
    val F2D : UByte = 141.toUByte()
    val D2I : UByte = 142.toUByte()
    val D2L : UByte = 143.toUByte()
    val D2F : UByte = 144.toUByte()
    val I2B : UByte = 145.toUByte()
    val I2C : UByte = 146.toUByte()
    val I2S : UByte = 147.toUByte()
    // ==========  Compare  ==========
    val LCmp : UByte = 148.toUByte()
    val FCmpL : UByte = 149.toUByte()
    val FCmpG : UByte = 150.toUByte()
    val DCmpL : UByte = 151.toUByte()
    val DCmpG : UByte = 152.toUByte()
    // ======== Zero Cond Jump ========
    val IfEq : UByte = 153.toUByte()
    val IfNe : UByte = 154.toUByte()
    val IfLt : UByte = 155.toUByte()
    val IfGe : UByte = 156.toUByte()
    val IfGt : UByte = 157.toUByte()
    val IfLe : UByte = 158.toUByte()
    // ======= Operand Cond Jump =======
    val IfICmpEq : UByte = 159.toUByte()
    val IfICmpNe : UByte = 160.toUByte()
    val IfICmpLt : UByte = 161.toUByte()
    val IfICmpGe : UByte = 162.toUByte()
    val IfICmpGt : UByte = 163.toUByte()
    val IfICmpLe : UByte = 164.toUByte()
    val IfACmpEq : UByte = 165.toUByte()
    val IfACmpNe : UByte = 166.toUByte()
    // =========== No-Cond Jump ===========
    val Goto : UByte = 167.toUByte()
    val JSR : UByte = 168.toUByte()
    val Ret : UByte = 169.toUByte()
    val TableSwitch : UByte = 170.toUByte()
    val LookupSwitch : UByte = 171.toUByte()
    val IReturn : UByte = 172.toUByte()
    val LReturn : UByte = 173.toUByte()
    val FReturn : UByte = 174.toUByte()
    val DReturn : UByte = 175.toUByte()
    val AReturn : UByte = 176.toUByte()
    val Return : UByte = 177.toUByte()
    // ========== Field Access ==========
    val GetStatic : UByte = 178.toUByte()
    val PutStatic : UByte = 179.toUByte()
    val GetField : UByte = 180.toUByte()
    val PutField : UByte = 181.toUByte()
    // ============  Invoke  ============
    val InvokeVirtual : UByte = 182.toUByte()
    val InvokeSpecial : UByte = 183.toUByte()
    val InvokeStatic : UByte = 184.toUByte()
    val InvokeInterface : UByte = 185.toUByte()
    // val UNUSED : UByte = 186.toUByte()
    val New : UByte = 187.toUByte()
    val NewArray : UByte = 188.toUByte()
    val ANewArray : UByte = 189.toUByte()
    val ArrayLength : UByte = 190.toUByte()
    val AThrow : UByte = 191.toUByte()
    val CheckCast : UByte = 192.toUByte()
    val InstanceOf : UByte = 193.toUByte()
    val MonitorEnter : UByte = 194.toUByte()
    val MonitorExit : UByte = 195.toUByte()
    val Wide : UByte = 196.toUByte()
    val MultiANewArray : UByte = 197.toUByte()
    val IfNull : UByte = 198.toUByte()
    val IfNonNull : UByte = 199.toUByte()
     val GotoW : UByte = 200.toUByte()
    // val JsrW : UByte = 201.toUByte()

    fun stackSizeEffect(code : UByte, argSize : Short = 0, retSize : Int = 1) : Short{
        val effect = when(code){
            // <-> stack <-> slot
            in AConstNull..LDC2W -> 1
            in ILoad..ALoad -> 1
            in IStore..AStore -> -1
            // [array][idx] -> [elem]
            in IALoad..SALoad -> -1
            // [array][idx][elem] -> []
            in IAStore..SAStore -> -3
            // calculate
            // [op1][op2] -> [ans]
            in IAdd..DRem -> -1
            in IShl..LXor -> -1
            in LCmp..DCmpG -> -1
            // [op] -> [ans]
            in INeg..DNeg -> 0
            // type cast
//            in I2L..I2S -> 0
            I2L, F2D, I2D, F2L -> 1
            L2I, D2F, D2I, L2F -> -1
            // jump
            // [op] -> []
            in IfEq..IfLe -> -1
            IfNull, IfNonNull -> -1
            // [op1][op2] -> []
            in IfICmpEq..IfACmpNe -> -2
            // [] -> [obj]
            New -> 1
            // [size] -> [array]
            NewArray,ANewArray -> 0
            // field
            GetStatic -> 1
            PutStatic -> -1
            GetField -> 0   // [obj] -> [field]
            PutField -> -2  // [obj][val] -> []
            // method
            InvokeVirtual,InvokeInterface, InvokeSpecial -> retSize - argSize - 1
            InvokeStatic -> retSize - argSize
            // switch
            TableSwitch, LookupSwitch -> -1
            // stack op
            Pop -> -1
            Pop2 -> -2
            in Dup..DupX2 -> 1
            in Dup2..Dup2X2 -> 2
            Swap -> 0
            else -> 0
        }
        return effect.toShort()
    }
}