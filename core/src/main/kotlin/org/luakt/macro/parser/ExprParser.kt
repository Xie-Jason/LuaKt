package org.luakt.macro.parser

import org.luakt.lexer.CodeContext
import org.luakt.lexer.Token
import org.luakt.parse.ParseContext
import org.luakt.parse.Parser
import org.luakt.syntax.Expression
import org.luakt.util.config.BuildConfig

class ExprParser(
    tokenList: List<Token>,
    filepath: String,
    codeCtx: CodeContext,
    context: ParseContext
) : Parser(tokenList, filepath, codeCtx, context, BuildConfig.empty) {
    fun parseExpression() : Expression{
        return super.parseExpr()
    }
}