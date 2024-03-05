package org.luakt.lexer

class Lexer(
    private val src : CharArray,
    private val file : String
) {
    // index in src
    private var idx = 0
    // num of line
    private var line = 1
    // index of last char in previous line
    private var prev = -1
    // distance from first char of this line
    private val offset
        get() = idx - prev - 1
    // lines context
    private val ctx = CodeContext(src)

    private fun fromOne(type: TokenType) : Token {
        return Token(line,offset,1,idx, type, null)
    }

    private fun fromTwo(type: TokenType) : Token {
        return Token(line,offset - 1,2,idx-1, type, null)
    }

    private fun fromThree(type: TokenType, data : Any? = null) : Token {
        return Token(line,offset - 2,3,idx-2, type, data)
    }

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()

        fun pos() : Array<Int> = arrayOf(line,offset,1,idx)
        fun posTwo() : Array<Int> = arrayOf(line,offset - 1,2,idx-1)
        fun posThree() : Array<Int> = arrayOf(line,offset-2,3,idx-2)

        while (idx < src.size){
            when(src[idx]){
                '\n' -> {
                    line++
                    prev = idx
                }
                in blanks -> assert(src[idx] != '\n')
                '+' -> tokens.add(fromOne(TokenType.Add))
                '*' -> tokens.add(fromOne(TokenType.Mul))
                '%' -> tokens.add(fromOne(TokenType.Mod))
                '^' -> tokens.add(fromOne(TokenType.Pow))
                '#' -> tokens.add(fromOne(TokenType.GetLen))
                ',' -> tokens.add(fromOne(TokenType.Comma))
                ';' -> tokens.add(fromOne(TokenType.Semi))
                '!' -> tokens.add(fromOne(TokenType.Excla))
                '|' -> tokens.add(fromOne(TokenType.BitOr))
                '&' -> tokens.add(fromOne(TokenType.BitAnd))
                '?' -> tokens.add(fromOne(TokenType.Ques))
                ':' -> tokens.add(if (idx+1 < src.size && src[idx+1] == ':'){
                    idx++
                    fromTwo(TokenType.TwoColon)
                }else{
                    fromOne(TokenType.Colon)
                })
                '-' -> {
                    if (idx+1 < src.size && src[idx+1] == '-'){
                        eatComment()
                    }else if(idx+1 < src.size && src[idx+1] == '>'){
                        idx++
                        tokens.add(fromTwo(TokenType.Arrow))
                    }else{
                        tokens.add(fromOne(TokenType.Sub))
                    }
                }
                '/' -> {
                    tokens.add(if (idx+1 < src.size && src[idx+1] == '/'){
                        idx++
                        fromTwo(TokenType.DivNoRem)
                    }else{
                        fromOne(TokenType.Div)
                    })
                }
                '=' -> {
                    tokens.add(if (idx+1 < src.size && src[idx+1] == '='){
                        idx++
                        fromTwo(TokenType.Eqs)
                    }else{
                        fromOne(TokenType.Eq)
                    })
                }
                '.' -> {
                    tokens.add(if(idx+2 < src.size && src[idx+1] == '.' && src[idx+2] == '.') {
                        idx+=2
                        fromThree(TokenType.VarArg, "...")
                    }else if (idx+1 < src.size && src[idx+1] == '.'){
                        idx++
                        fromTwo(TokenType.Concat)
                    }else{
                        fromOne(TokenType.Dot)
                    })
                }
                '>' -> {
                    tokens.add(if (idx+1 < src.size && src[idx+1] == '='){
                        idx++
                        fromTwo(TokenType.GtEq)
                    }else if(idx+1 < src.size && src[idx+1] == '>'){
                        idx++
                        fromTwo(TokenType.RShift)
                    }else{
                        fromOne(TokenType.Gt)
                    })
                }
                '<' -> {
                    tokens.add(if (idx+1 < src.size && src[idx+1] == '='){
                        idx++
                        fromTwo(TokenType.LtEq)
                    }else if(idx+1 < src.size && src[idx+1] == '<'){
                        idx++
                        fromTwo(TokenType.LShift)
                    }else{
                        fromOne(TokenType.Lt)
                    })
                }
                '~' -> {
                    if(idx+1 < src.size && src[idx+1] == '='){
                        idx++
                        tokens.add(fromTwo(TokenType.NotEq))
                    }else{
                        tokens.add(fromOne( TokenType.Wave))
                    }
                    /* else{
                        throw LexerException.unexpectChar(line,offset,1,'~',ctxBefore(),ctxAfter(),file)
                    }*/
                }
                '(' -> tokens.add(fromOne(TokenType.LParen))
                ')' -> tokens.add(fromOne(TokenType.RParen))
                '{' -> tokens.add(fromOne(TokenType.LBrace))
                '}' -> tokens.add(fromOne(TokenType.RBrace))
                ']' -> tokens.add(fromOne(TokenType.RBracket))
                '[' -> {
                    tokens.add(if(idx < src.size && src[idx+1] == '['){
                        multiLineText()
                    }else{
                        fromOne(TokenType.LBracket)
                    })
                }
                '@' -> tokens.add(attr())
                in '0'..'9' -> tokens.add(num())
                '"','\'' -> tokens.add(str())
                else -> if(src[idx].isLetter() || src[idx] == '_'){
                    val id = id()
                    val typ = Token.identifiers[id.str]
                    tokens.add(if(typ != null) Token(id.line,id.offset, id.len, id.idx, typ) else id)
                }else{
                    throw LexerException.unexpectChar(line,offset,1,src[idx],ctxBefore(),ctxAfter(),file)
                }
            }
            idx++
        }
        return tokens
    }

    private fun num() : Token{
        val begin = idx
        val ofs = offset
        var base = 10
        var float = false
        var hasE = false
        while (idx < src.size &&  when(src[idx]){
                in '0'..'9' -> true
                'e','E'-> {
                    hasE = true
                    true
                }
                'x' -> {
                    base = 16
                    true
                }
                '.' -> {
                    float = true
                    true
                }
                '+','-' -> hasE
                in 'a'..'f',
                in 'A'..'F' -> base == 16
                else -> false
            }){
            idx++
        }
        val s = String(src,begin,idx-begin)
        return if (float){
            val n : Double? = s.toDoubleOrNull() ?: s.toLongOrNull()?.toDouble()
            n ?: throw LexerException.unresolvedNumber(line,ofs,s.length,s,ctxBefore(),ctxAfter(),file)
            idx--
            Token(line,begin - prev,idx - begin + 1,begin,TokenType.Num, n)
        } else {
            val n : Long? = if(base == 10) {
                s.toLongOrNull()
            } else if(s.startsWith("0x")) {
                s.substring(2).toLongOrNull(16)
            } else null
            n ?: throw LexerException.unresolvedNumber(line,ofs,s.length,s,ctxBefore(),ctxAfter(),file)
            idx--
            Token(line,begin - prev,idx - begin + 1,begin,TokenType.Int,n)
        }
    }

    private fun str() : Token{
        val sep = src[idx++]
        val begin = idx
        val offset = idx - prev
        assert(sep == '"' || sep == '\'')
        val buf = StringBuilder(16)
        while (src[idx] != sep){
            if (src[idx] == '\\' && idx+1 < src.size && isValidEscape(src[idx+1])){
                buf.append(processEscape(src[idx+1]))
                idx += 2
                continue
            }
            buf.append(src[idx])
            idx++
        }
        return Token(
            line,
            offset = offset,
            len = idx - begin + 1,
            idx = begin,
            TokenType.Str,
            String(src,begin,idx - begin)
        )
    }

    private fun id(): Token {
        val ofs = offset
        val begin = idx
        idx++
        while (idx < src.size && (src[idx].isLetterOrDigit() || src[idx] == '_')){
            idx++
        }
        val type = if(idx < src.size && src[idx] == '!'){
            idx++
            TokenType.Proc
        }else {
            TokenType.Id
        }
        idx--
        return Token(
            line = line,
            offset = ofs,
            len = idx-begin+1,
            idx =  begin,
            type,
            String(src,begin,idx-begin+1)
        )
    }


    private fun attr() : Token {
        val ofs = offset
        idx++
        val begin = idx
        while (idx < src.size && (src[idx].isLetterOrDigit() || src[idx] == '_')){
            idx++
        }
        idx--
        return Token(
            line = line,
            offset = ofs,
            len = idx-begin+1,
            idx =  begin,
            TokenType.Attr,
            String(src,begin,idx-begin+1)
        )
    }


    private fun multiLineText() : Token{
        assert(src[idx] == '[' && src[idx+1] == '[')
        val beginLine = line
        val beginOffset = offset
        val beginIdx = idx
        val strBegin = idx+2
        idx+=3
        while (idx < src.size && (src[idx] != ']' || src[idx-1] != ']')){
            if(src[idx] == '\n'){
                line++
                prev = idx
            }
            idx++
        }
        val len = idx-1-strBegin
        return Token(
            beginLine,
            beginOffset,
            len,
            beginIdx,
            TokenType.Str,
            String(src,strBegin,len).trim()
        )
    }

    private fun eatComment() {
        assert(src[idx] == '-' && src[idx+1] == '-')
        idx+=1
        if(idx+2<src.size && src[idx+1] == '[' && src[idx+2] == '['){
            idx+=2
            while (idx < src.size && when(src[idx]){
                    '\n' -> {
                        line++
                        prev = idx
                        true
                    }
                    ']' -> (idx+1 < src.size && src[idx+1] == ']').not()
                    else -> true
                }){
                idx++
            }
            idx++
        }else{
            idx++
            while (idx < src.size && src[idx] != '\n'){
                idx++
            }
            line++
            prev = idx
        }
    }

    private fun ctxBefore() : String = ctx.before(idx)

    private fun ctxAfter() : String = ctx.after(idx)

    companion object{
        private val blanks : CharRange = 0.toChar()..' '
        private fun isValidEscape(c : Char) : Boolean = processEscape(c) != ' '
        private fun processEscape(c : Char) : Char {
            return when(c){
                'b' -> '\b'
                'r' -> '\r'
                't' -> '\t'
                'n' -> '\n'
                '\\' -> '\\'
                '"'  -> '"'
                '\'' -> '\''
                else -> ' '
            }
        }
    }
}