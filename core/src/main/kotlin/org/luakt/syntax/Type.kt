package org.luakt.syntax

sealed class DeclaredType(
    nullable : Boolean
){
    var nullable : Boolean = nullable

    companion object{
        val anyNullable = DeclaredBuiltinType(true,"any")
        val anyNotNull = DeclaredBuiltinType(false,"any")
        val numNotNull = DeclaredBuiltinType(false, "number")
        val strNotNull = DeclaredBuiltinType(false, "string")
        val boolNotNull = DeclaredBuiltinType(false, "bool")
        val threadNotNull = DeclaredBuiltinType(false, "thread")
        val tableNotNull = DeclaredBuiltinType(false, "table")
    }
}

class DeclaredCustomType(
    nullable: Boolean,
    val name : String
) : DeclaredType(nullable)

class DeclaredBuiltinType(
    nullable: Boolean,
    val name : String
) : DeclaredType(nullable)

class DeclaredUnionType(
    nullable: Boolean,
    val types : MutableList<DeclaredType>
) : DeclaredType(nullable)

class DeclaredJavaType(
    nullable: Boolean,
    val name : String
) : DeclaredType(nullable)