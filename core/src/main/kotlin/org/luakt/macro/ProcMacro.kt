package org.luakt.macro

import org.luakt.lexer.Token
import org.luakt.syntax.Expression
import org.luakt.syntax.SyntaxNode

class ProcMacro (
    val name : String,
    val attach : Set<Class<out SyntaxNode>>,
    val expand : (List<Token>) -> Expression
)