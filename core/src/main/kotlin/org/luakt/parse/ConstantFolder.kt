package org.luakt.parse

import org.luakt.syntax.*
import org.luakt.syntax.UniOpType.*

object ConstantFolder {
    fun foldUnaryOp(unaryOp : UnaryOp) : Expression? {
        return when(unaryOp.op){
            Neg -> when(val inner = unaryOp.inner){
                is LiteralInt -> LiteralInt(- inner.int)
                is LiteralNum -> LiteralNum(- inner.num)
                else -> null
            }
            Not -> when(unaryOp.inner){
                is LiteralFalse -> LiteralTrue.value
                else -> null
            }
            GetLen -> when(unaryOp.inner) {
                is LiteralStr -> LiteralInt((unaryOp.inner as LiteralStr).str.length)
                else -> null
            }
            BitNot -> null
        }
    }
}