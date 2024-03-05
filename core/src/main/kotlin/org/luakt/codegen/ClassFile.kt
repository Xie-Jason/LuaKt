package org.luakt.codegen

import org.luakt.util.Four
import org.luakt.util.Path
import org.luakt.util.buf.Writer
import kotlin.experimental.or

class ClassFile(
    name : String,
    parent : String
){
    private val constantPool = mutableListOf<ConstantInfo>()
    private var accessFlag : Short = Flag.Public
    private val thisClass : UShort
    private val superClass : UShort
    private val interfaces = mutableListOf<UShort>()
    private val fields = mutableListOf<FieldInfo>()
    private val methods = mutableListOf<MethodInfo>()
    private val attributes = mutableListOf<AttrInfo>()
    // ================ Serializable ================

    // ============== ConstantPool Assist ==============
    // Int,Long,String,Double,Float, -> Index
    private val cpLiteralMap = mutableMapOf<Any, UShort>()
    private val cpUtf8Map = mutableMapOf<String, UShort>()
    private val cpClassMap = mutableMapOf<String, UShort>()
    private val cpFieldMap = mutableMapOf<Triple<String,String,String>, UShort>()
    // If the first boolean of tuple is true means is an interface method
    private val cpMethodMap = mutableMapOf<Four<Boolean, String, String, String>, UShort>()
    private val cpNameAndTypeMap = mutableMapOf<Pair<String,String>, UShort>()
    // ================== Other Assist ==================
    val selfClass : String = name
    private val parentClass : String = parent
    var outputPath : String = ""
        private set

    init {
        thisClass = useClass(name)
        superClass = useClass(parent)
        addInitFn()

    }

    companion object{
        private const val Magic : Int = 0xcafebabe.toInt()
        private const val MinorVersion : Short = 0
        private const val MajorVersion : Short = 50
    }

    fun setPath(name : String, output : String){
        outputPath = output
        addSourceFile(Path.fileNameOfPath(name))
    }

    fun addInterface(itfName : String){
        val itf = useClass(itfName)
        interfaces.add(itf)
    }

    private fun addInitFn(){
        val flag = Flag.Public
        val name = "<init>"
        val codes = Codes(this)
        val typ = "()V"
        codes.put(ByteCode.ALoad, 0.toByte())
        codes.put(ByteCode.InvokeSpecial, useClsMethod(parentClass, name, typ))
        codes.put(ByteCode.Return)
        addFn(name, flag, 1U, useUtf8(typ), codes)
    }

    fun addClinitFn(codes: Codes, maxLocals: UShort){
        val flag = Flag.Private or Flag.Static
        val name = "<clinit>"
        val typ = "()V"
        addFn(name, flag, maxLocals, useUtf8(typ), codes)
    }

    fun addField(name: String, type : String = Type.obj, isPub : Boolean = false){
        val flag = if(isPub) Flag.Public else Flag.Private
        val nameIdx = useUtf8(name)
        val descriptorIdx = useUtf8(type)
        val fieldInfo = FieldInfo(
            flag,
            nameIdx,
            descriptorIdx,
            0,
            emptyList()
        )
        fields.add(fieldInfo)
    }

    fun addStaticField(name: String) {
        val flag = Flag.Public or Flag.Static
        val nameIdx = useUtf8(name)
        val descriptorIdx = useUtf8(Type.ref(Type.Cls.OBJ))
        val fieldInfo = FieldInfo(
            flag,
            nameIdx,
            descriptorIdx,
            0,
            emptyList()
        )
        fields.add(fieldInfo)
    }

    fun addLuaFn(name : String, maxLocals : UShort, codes: Codes, isPub : Boolean = false){
        val flag = if (isPub) Flag.Public else Flag.Private
        val descriptorIdx = useUtf8(Type.luaFn)
//        println("$name local size $maxLocals")
        addFn(name, flag, maxLocals, descriptorIdx, codes)
    }

    fun addFn(name : String, flag : Short, maxLocals : UShort, descriptor : String, codes: Codes){
        val descriptorIdx = useUtf8(descriptor)
        addFn(name, flag, maxLocals, descriptorIdx, codes)
    }

    private fun addFn(name : String, flag : Short, maxLocals : UShort, descriptorIdx : UShort, codes: Codes){
        val codeAdditionalAttr = mutableListOf<AttrInfo>()
        if(codes.lineNumTable != null && codes.lineNumTable!!.size > 1){
            codeAdditionalAttr.add(LineNumTableAttr(
                useUtf8(AttrInfo.Name.LineNumberTable),
                codes.lineNumTable!!
            ))
        }
        val codeAttr = CodeAttr(
            useUtf8(AttrInfo.Name.Code),
            codes.maxStack().toUShort(),
            maxLocals,
            codes.len(),
            codes,
            0,
            codeAdditionalAttr.size.toShort(),
            codeAdditionalAttr
        )
        val methodInfo = MethodInfo(
            flag,
            useUtf8(name),
            descriptorIdx,
            1,
            listOf(codeAttr)
        )
        methods.add(methodInfo)
    }

    private fun useUtf8(s : String) : UShort{
        val idx = cpUtf8Map[s]
        if(idx != null) return idx
        // the start Index of ConstantPool is 1
        constantPool.add(ConUtf8(s))
        val newIdx = constantPool.size.toUShort()
        cpUtf8Map[s] = newIdx
        return newIdx
    }

    fun useString(s : String) : UShort{
        val strIdx = cpLiteralMap[s]
        if(strIdx != null) return strIdx
        // new utf8 and string ConstantInfo
        val utf8Idx = useUtf8(s)
        constantPool.add(ConString(utf8Idx))
        val idx = constantPool.size.toUShort()
        cpLiteralMap[s] = idx
        return idx
    }

    fun useClass(cls : String) : UShort{
        val clsIdx = cpClassMap[cls]
        if(clsIdx != null) return clsIdx
        val utf8Idx = useUtf8(cls)
        constantPool.add(ConClass(utf8Idx))
        val idx = constantPool.size.toUShort()
        cpClassMap[cls] = idx
//        println("$cls -> $idx")
        return idx
    }

    fun useField(cls : String, name : String, type : String) : UShort{
        val triple = Triple(cls, name, type)
        val existIdx = cpFieldMap[triple]
        if(existIdx != null) return existIdx
        val clsIdx = useClass(cls)
        val nameAndTypeIdx = useNameAndType(name, type)
        constantPool.add(ConFieldRef(clsIdx, nameAndTypeIdx))
        val idx = constantPool.size.toUShort()
        cpFieldMap[triple] = idx
        return idx
    }

    fun useClsMethod(cls: String, name: String, type: String) : UShort{
        return useMethodRef(false, cls, name, type)
    }

    fun useItfMethod(cls: String, name: String, type: String) : UShort{
        return useMethodRef(true, cls, name, type)
    }

    fun useInt(n : Int) : UShort{
        val existIdx = cpLiteralMap[n]
        if(existIdx != null) return existIdx
        constantPool.add(ConInt(n))
        val idx = constantPool.size.toUShort()
        cpLiteralMap[n] = idx
        return idx
    }

    fun useFloat(n : Float) : UShort{
        val existIdx = cpLiteralMap[n]
        if(existIdx != null) return existIdx
        constantPool.add(ConFloat(n))
        val idx = constantPool.size.toUShort()
        cpLiteralMap[n] = idx
        return idx
    }

    fun useLong(n : Long) : UShort{
        val existIdx = cpLiteralMap[n]
        if(existIdx != null) return existIdx
        constantPool.add(ConLong(n))
        val idx = constantPool.size.toUShort()
        constantPool.add(ConPadding())
        cpLiteralMap[n] = idx
        return idx
    }

    fun useDouble(n : Double) : UShort{
        val existIdx = cpLiteralMap[n]
        if(existIdx != null) return existIdx
        constantPool.add(ConDouble(n))
        val idx = constantPool.size.toUShort()
        constantPool.add(ConPadding())
        cpLiteralMap[n] = idx
        return idx
    }

    private fun useMethodRef(fromInterface : Boolean, cls: String, name : String, type : String) : UShort{
        val tuple = Four(fromInterface, cls, name, type)
        val existIdx = cpMethodMap[tuple]
        if(existIdx != null) return existIdx
        val clsIdx = useClass(cls)
        val nameAndTypeIdx = useNameAndType(name, type)
        val constant = if(fromInterface){
            ConInterMethodRef(clsIdx, nameAndTypeIdx)
        }else{
            ConMethodRef(clsIdx, nameAndTypeIdx)
        }
        constantPool.add(constant)
        val idx = constantPool.size.toUShort()
        cpMethodMap[tuple] = idx
        return idx
    }

    private fun useNameAndType(name : String, type : String) : UShort{
        val pair = Pair(name, type)
        val existIdx = cpNameAndTypeMap[pair]
        if(existIdx != null) return existIdx
        val nameIdx = useUtf8(name)
        val typeIdx = useUtf8(type)
        constantPool.add(ConNameAndType(nameIdx, typeIdx))
        val idx = constantPool.size.toUShort()
        cpNameAndTypeMap[pair] = idx
        return idx
    }

    private fun addSourceFile(name : String){
        attributes.add(SourceFileAttr(
            useUtf8(AttrInfo.Name.SourceFile),
            useUtf8(name)
        ))
    }

    object Flag{
        const val Public    : Short = 0x0001
        const val Private   : Short = 0x0002
        const val Protected : Short = 0x0004
        const val Static    : Short = 0x0008
        const val Final     : Short = 0x0010
    }

    class FieldInfo(
        private val accessFlag : Short,
        private val nameIndex : UShort,
        private val descriptorIndex : UShort,
        private val attrCount : Short,
        private val attrList : List<AttrInfo>
    ){
        fun write(w : Writer){
            w.write(accessFlag)
            w.write(nameIndex)
            w.write(descriptorIndex)
            w.write(attrCount)
            attrList.forEach { it.write(w) }
        }
    }

    class MethodInfo(
        private val accessFlag : Short,
        private val nameIndex : UShort,
        private val descriptorIndex : UShort,
        private val attrCount : Short,
        private val attrList : List<AttrInfo>
    ){
        fun write(w : Writer){
            w.write(accessFlag)
            w.write(nameIndex)
            w.write(descriptorIndex)
            w.write(attrCount)
            attrList.forEach { it.write(w) }
        }
    }

    fun write(w : Writer){
        w.write(Magic)
        w.write(MinorVersion)
        w.write(MajorVersion)
        // constant pool
        w.write((constantPool.size + 1).toShort())
        constantPool.forEach {
            it.write(w)
        }
        // meta
        w.write(accessFlag)
        w.write(thisClass)
        w.write(superClass)
        // interfaces
        w.write(interfaces.size.toShort())
        interfaces.forEach(w::write)
        // fields
        w.write(fields.size.toShort())
        fields.forEach { it.write(w) }
        // methods
        w.write(methods.size.toShort())
        methods.forEach { it.write(w) }
        // attributes
        w.write(attributes.size.toShort())
        attributes.forEach { it.write(w) }
        w.complete()
    }
    
}