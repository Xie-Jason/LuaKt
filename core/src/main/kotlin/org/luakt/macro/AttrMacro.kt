package org.luakt.macro

import org.luakt.lexer.Token
import org.luakt.syntax.Expression
import org.luakt.syntax.Statement
import org.luakt.syntax.SyntaxNode

class AttrMacro(
    val name : String,
    val attach : Set<Class<out SyntaxNode>>,
    private val expandFn : (MacroContext, List<Token>, SyntaxNode) -> List<SyntaxNode>
){
    fun expand(ctx : MacroContext,args : List<Token>, input : SyntaxNode) : List<SyntaxNode>{
        val list = this.expandFn(ctx, args, input)
        if(input is Expression){
            assert(list.size == 1 && list[0] is Expression)
        }else{
            assert(list.all { it is Statement })
        }
        return list
    }
}
