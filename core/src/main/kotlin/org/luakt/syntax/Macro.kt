package org.luakt.syntax

import org.luakt.lexer.Token

class UsedAttrMacro(
    val name : String,
    val nameTk : Token,
    val tokens : List<Token>
) {
    override fun toString(): String = "@$name(${tokens.joinToString()})"
}