package org.luakt.codegen

object Type{
    const val byte = "B"
    const val char = "C"
    const val double = "D"
    const val float = "F"
    const val short = "S"
    const val bool = "Z"
    const val int = "I"
    const val long = "J"
    // only used in method descriptors
    const val void = "V"

    fun ref(cls : String) = "L$cls;"
    fun array(type : String) = "[$type"
    fun method(params : List<String>?, ret : String) : String{
        if (params == null) return "()$ret"
        return "(${params.joinToString(separator = "")})$ret"
    }

    val str = ref(Cls.STR)
    val table = ref(Cls.TABLE)
    val obj = ref(Cls.OBJ)
    val objArray = array(ref(Cls.OBJ))
    val mod = ref(Cls.MOD)
    val luaIter = ref(Cls.ITER)
    val state = ref(Cls.STATE)
    // all user-defined lua function have same type descriptor
    val luaFn = method(listOf(objArray, objArray), objArray)
    val binaryOpFn = method(listOf(ref(Cls.OBJ), ref(Cls.OBJ)), ref(Cls.OBJ))
    val unaryOpFn = method(listOf(ref(Cls.OBJ)), ref(Cls.OBJ))
    val luaModCallFn = method(listOf(int, objArray, objArray), objArray)

    object Cls{
        private const val LIB_PATH = "org/luakt/lib"
        const val OBJ    = "java/lang/Object"
        const val STR    = "java/lang/String"
        const val LONG   = "java/lang/Long"
        const val DOUBLE = "java/lang/Double"
        const val BOOL   = "java/lang/Boolean"
        const val LUA    = "$LIB_PATH/Lua"
        const val LIB    = "$LIB_PATH/Lib"
        const val MOD    = "$LIB_PATH/LuaModule"
        const val MOD_FN = "$LIB_PATH/LuaModFunc"
        const val ITER   = "$LIB_PATH/LuaIter"
        const val TABLE  = "$LIB_PATH/LuaTable"
        const val STATE  = "LuaState"
        const val INT_ITER = "$LIB_PATH/LuaIntIter"
    }
}