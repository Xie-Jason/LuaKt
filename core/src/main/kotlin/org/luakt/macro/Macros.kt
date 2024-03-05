package org.luakt.macro

import org.luakt.lexer.Token
import org.luakt.macro.parser.TypeParser
import org.luakt.syntax.*
import kotlin.math.min

object BuiltinMacros{

    val Type = AttrMacro("Type", setOf(LocalDeclare::class.java, Assign::class.java), ::typeExpand)
    val Decorator = AttrMacro("Decorator", setOf(DeclaredFunc::class.java), ::decoExpand)

    private fun decoExpand(ctx: MacroContext, args: List<Token>, func : SyntaxNode): List<SyntaxNode> {
        /*
        local f1 = function(fn, ...)
            // advice
            local r = fn(...)
            // advice
            return r
        end
        local f2 = @Decorator(f1) function(arg1, arg2)
           // bala
        end
        ↓
        local f2 = function(arg1, arg2)
            local innerFn = function(arg1, arg2)
                // bala
            end
            return f1(f2, arg1, arg2)
        end
        */
        val enhancedFuncName = ctx.randName("EnhancedFunc")
        val decoFn = ctx.newExprParser(args).parseExpression()
        val pureArgs : MutableList<Expression> = mutableListOf(Var(enhancedFuncName))
        pureArgs.addAll((func as DeclaredFunc).params.map(::Var))
        return listOf(DeclaredFunc(
            func.params,
            mutableListOf(
                LocalDeclare(mutableListOf(enhancedFuncName), mutableListOf(func)),
                Return(mutableListOf(
                    FuncInvoke(decoFn, pureArgs)
                ))
            )
        ))
    }

    private fun typeExpand(ctx : MacroContext, args : List<Token>, stmt : SyntaxNode) : MutableList<SyntaxNode> {
        /*
        @Def(number,number) local v, t = (1 + 2), 3
        ↓
        local v, t = (1 + 2), 3
        if ((v ~= nil) and (type(v) ~= "number")) then
            panic()
        end
        if ((t ~= nil) and (type(t) ~= "number")) then
            panic()
        end
        */
        val symbols : List<Expression> = when(stmt){
            is LocalDeclare -> stmt.left.map(::Var)
            is Assign -> stmt.left
            else -> throw Exception()
        }
        val types = TypeParser(args).parse()

        fun genCheckCond(expr : Expression, type : DeclaredType) : Expression{
            return when(type){
                is DeclaredBuiltinType -> {
                    BinOp(
                        FuncInvoke(Var("type"), mutableListOf(expr)),
                        mutableListOf(BinOpType.NotEq to LiteralStr(type.name))
                    )
                }
                is DeclaredCustomType -> {
                    val typ = ctx.storage["Def"]?.get(type.name) as DeclaredType?
                    typ ?: throw Exception()
                    genCheckCond(expr, type)
                }
                is DeclaredUnionType -> {
                    BinOp(
                        genCheckCond(expr, type.types[0]),
                        (1..type.types.lastIndex)
                            .map { BinOpType.And to genCheckCond(expr, type.types[it]) }
                            .toMutableList()
                    )
                }

                is DeclaredJavaType -> {
                    val ifUserData = BinOp(
                        FuncInvoke(Var("type"), mutableListOf(expr)),
                        mutableListOf(BinOpType.NotEq to LiteralStr("userdata"))
                    )
                    val ifJavaType = BinOp(
                        FuncInvoke(Var("javaType"), mutableListOf(expr)),
                        mutableListOf(BinOpType.NotEq to LiteralStr(type.name))
                    )
                    BinOp(ifUserData, mutableListOf(BinOpType.Or to ifJavaType))
                }
            }
        }
        fun genCheckCondWithNull(expr : Expression, type : DeclaredType) : Expression{
            val cond = genCheckCond(expr, type)
            return if(type.nullable.not()){
                // if( v == nil || <cond> )
                BinOp(
                    BinOp(expr, mutableListOf(BinOpType.Eqs to LiteralNil.value)),
                    mutableListOf(BinOpType.Or to cond)
                )
            } else{
                // if( v != nil && <cond> )
                BinOp(
                    BinOp(expr, mutableListOf(BinOpType.NotEq to LiteralNil.value)),
                    mutableListOf(BinOpType.And to cond)
                )
            }
        }
        fun genTypeCheck(expr : Expression, type : DeclaredType) : Statement?{
            if(type == DeclaredType.anyNullable) return null
            return IfElse(mutableListOf(
                genCheckCondWithNull(expr, type) to Body(mutableListOf(
                    Discard(FuncInvoke(Var("error"), mutableListOf()))
                ))
            ))
        }

        val list = mutableListOf(stmt as Statement)
        for (idx in 0..min(types.lastIndex, symbols.lastIndex) ){
            val s = genTypeCheck(symbols[idx], types[idx])
            s ?: continue
            list.add(s)
        }
        return list.toMutableList()
    }

}