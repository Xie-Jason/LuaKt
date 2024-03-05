package org.luakt.parse

import org.luakt.lexer.Token

abstract class AbsParser(
    private val tokenList : List<Token>
) {
    private var idx = -1

    protected val curr
        get() = tokenList[idx]
    protected val hasNext
        get() = idx+1 < tokenList.size

    protected fun next() = tokenList[++idx]
    protected fun ahead() = tokenList[idx+1]
    protected fun ahead(gap : Int) : Token? {
        val index = idx + gap
        if(index >= tokenList.size || index < 0) return null
        return tokenList[index]
    }

    protected fun back(step : Int = 1){
        idx -= step
    }
}