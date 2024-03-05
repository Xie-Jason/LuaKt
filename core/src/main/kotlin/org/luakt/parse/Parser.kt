package org.luakt.parse

import org.luakt.lexer.*
import org.luakt.syntax.*
import org.luakt.util.Lambda
import org.luakt.util.Path
import org.luakt.util.ThreadPool
import org.luakt.util.config.BuildConfig
import org.luakt.util.log.Log
import java.io.File
import java.util.concurrent.Future

open class Parser(
    tokenList : List<Token>,
    private val filepath : String,
    private val codeCtx : CodeContext,
    val context : ParseContext,
    private val config : BuildConfig
) : AbsParser(tokenList) {
    private val futures = mutableListOf<Future<Pair<LuaScript,Throwable?>>>()

    fun parse() : LuaScript{
        val block = parseBlock()
        val results = futures.map{ it.get() }
        results.forEach {
            if(it.second != null) throw it.second!!
        }
        val scripts = results.map{ it.first }.associateBy(LuaScript::file,Lambda::self)
        return LuaScript(
            filepath.replace("\\","/"),
            Body(block),
            scripts,
            codeCtx,
            context.globals,
            Path.luaToOutput(config.outDir + filepath)
        )
    }

    protected open fun parseBlock() : MutableList<Statement> {
        val stmts = mutableListOf<Statement>()
        while(hasNext && ahead().type == TokenType.Semi){
            next()
        }
        while (hasNext && when(ahead().type){
            // could interrupt the block parse
            TokenType.ElseIf, TokenType.Else, TokenType.End, TokenType.Until -> false
            else -> true
        }){

            if(hasNext) stmts.add(parseStatement())
            while(hasNext && ahead().type == TokenType.Semi){
                next()
            }
        }
        return stmts
    }

    protected open fun parseStatement() : Statement {
        val macro = if (ahead().type == TokenType.Attr) parseAttrMacro() else null
        val stmt : Statement = when(next().type){
            TokenType.Function -> {
                val (leftVal, func) = parseFunc()
                Assign(mutableListOf(leftVal), mutableListOf(func))
            }
            TokenType.Local -> parseLocal()
            TokenType.While -> parseWhileLoop()
            TokenType.Repeat -> parseRepeatLoop()
            TokenType.For -> parseForLoop()
            TokenType.If -> parseIfElse()
            TokenType.Do -> {
                val begin = curr
                val block = parseBlock()
                val end = next()
                assertTk(end, TokenType.End)
                Block(begin, end, Body(block))
            }
            TokenType.Return -> {
                val ret = curr
                if (hasNext && isEndOfStmt(ahead()).not()){
                    val args = parseElems()
                    Return(ret, args)
                }else{
                    Return(ret, mutableListOf())
                }
            }
            TokenType.TwoColon -> {
                val begin = curr
                assertNext(TokenType.Id)
                val name = curr.str
                assertNext(TokenType.TwoColon)
                Label(begin, curr, name)
            }
            TokenType.Goto -> {
                val begin = curr
                assertNext(TokenType.Id)
                val label = curr.str
                val end = curr
                Goto(begin, end, label)
            }
            TokenType.Break -> Break(curr)
            TokenType.Semi -> None(curr)
            else -> {
                back()
                val elems = parseLeftValue()
                if (hasNext && ahead().type == TokenType.Eq){
                    // Value Assign
                    assertNext(TokenType.Eq)
                    val right = parseElems()
                    for (leftVal in elems) {
                        assertLeftValue(leftVal)
                    }
                    Assign(elems, right)
                } else if(elems.size == 1 && haveSideEffect(elems[0])){
                    // Function Call
                    Discard(elems[0])
                } else {
                    // expr1, expr2, expr3 ...
//                    elems.forEach(::println)
//                    println(elems.last().endLine - elems.first().beginLine)
                    throw StaticCheckException(
                        ParseError,
                        elems.first().beginLine,
                        elems.first().beginOffset,
                        elems.last().endIdx - elems.first().beginIdx,
                        "pure expression with discarded value have no side effect",
                        "",
                        codeCtx.before(elems.first().beginIdx, false),
                        codeCtx.after(elems.last().endIdx),
                        filepath,
                        codeCtx.lines(elems.first().beginIdx, elems.last().endLine + 1 - elems.first().beginLine),
                        elems.first().beginOffset,
                        elems.last().endOffset
                    )
                }
            }
        }
        if (macro != null){
            stmt.addMacro(macro)
        }
        return stmt
    }

    private fun isEndOfStmt(token: Token) : Boolean{
        return when(token.type){
            TokenType.Local, TokenType.TwoColon, TokenType.Semi,
            TokenType.Break, TokenType.Goto, TokenType.Return,
            TokenType.Do,  TokenType.If, TokenType.For,
            TokenType.While, TokenType.Repeat, TokenType.End -> true
            else -> false
        }
    }

    // while <expr> do <block> end
    protected open fun parseWhileLoop() : WhileLoop{
        val begin = curr
        val cond = parseExpr()
        assertNext(TokenType.Do)
        val block = parseBlock()
        val end = next()
        assertTk(end, TokenType.End)
        return WhileLoop(begin, end, cond, Body(block))
    }

    // repeat <block> until <expr>
    protected open fun parseRepeatLoop() : RepeatLoop{
        val rpt = curr
        val block = parseBlock()
        assertHasNext()
        assertNext(TokenType.Until)
        val cond = parseExpr()
        return RepeatLoop(rpt, cond, Body(block))
    }

    // local function <name> (<params>) <block> end
    // local <values> = <expr_list>
    protected open fun parseLocal(): LocalDeclare {
        val local = curr
        return if (hasNext && ahead().type == TokenType.Function) {
            val fnTk = next()
            assertHasNext()
            val (leftVal, func) = parseFunc()
            assertVar(leftVal)
            val variable = leftVal as Var
            LocalDeclare(
                fnTk,
                mutableListOf(variable.name),
                mutableListOf(func),
                mutableListOf(variable.toToken())
            )
        } else {
            val leftValues = parseLeftValue()
            leftValues.forEach(::assertVar)
            val vars = leftValues.map { (it as Var).name }.toMutableList()
            val leftTks = leftValues.map { (it as Var).toToken() }.toMutableList()
            val elems = if (hasNext && ahead().type == TokenType.Eq) {
                next()
                parseElems()
            } else {
                mutableListOf()
            }
            LocalDeclare(
                local,
                vars,
                elems,
                leftTks
            )
        }
    }

    // for <name> = <init> , <limit> [, <step>] do block end
    // for <names> in <expr> do block end
    protected open fun parseForLoop(): Statement {
        val begin = curr
        val (names, tokens) = parseNames(false)
        assertHasNext()
        assertTk(ahead(), TokenType.Eq, TokenType.In)
        return if (hasNext && ahead().type == TokenType.Eq) {
            val tkEq = next()
            if (names.size != 1) {
                throw constructException(
                    tkEq,
                    "have more than one declared variable before '=' in for loop",
                    "reduce declared variable to only one"
                )
            }
            val range = parseElems().toMutableList()
            if (range.size < 2 || range.size > 3) {
                throw constructException(
                    tkEq,
                    "have incorrect range argument size after '=' in for loop, expect 2 or 3, found ${range.size}",
                    "update range expressions into 2 or 3, which means : start, limit, step(optional)"
                )
            }
            if (range.size == 2) {
                range.add(LiteralInt(1))
            }
            assertNext(TokenType.Do)
            val block = parseBlock()
            assertHasNext()
            val end = next()
            assertTk(end, TokenType.End)
            ForEqLoop(begin, end, names[0], range[0], range[1], range[2], Body(block))
        } else {
            next()
            val iter = parseExpr()
            assertNext(TokenType.Do)
            val block = parseBlock()
            val end = next()
            assertTk(end, TokenType.End)
            ForInLoop(begin, end, names, iter, Body(block), tokens)
        }
    }

    // if <expr> then <block> {elseif <exp> then <block>} [else <block>] end
    protected open fun parseIfElse(): IfElse {
        val begin = curr
        val ifCond = parseExpr()
        assertNext(TokenType.Then)
        val ifBlock = parseBlock()
        val branches = mutableListOf(Pair(ifCond, Body(ifBlock)))
        while (hasNext && ahead().type == TokenType.ElseIf) {
            next()
            val cond = parseExpr()
            assertNext(TokenType.Then)
            val block = parseBlock()
            branches.add(Pair(cond, Body(block)))
        }
        if (hasNext && ahead().type == TokenType.Else) {
            next()
            val block = parseBlock()
            branches.add(Pair(LiteralTrue.value, Body(block)))
        }
        val end = next()
        assertTk(end, TokenType.End)
        return IfElse(begin, end, branches)
    }

    protected open fun assertVar(expr: Expression) {
        if(expr !is Var){
            throw constructException(
                expr,
                "expect a Variable",
                ""
            )
        }
    }

    protected open fun assertLeftValue(expr: Expression) {
        if (isLeftValue(expr).not()) {
            throw constructException(
                expr,
                "expect a left value ( Variable or FieldAccess )",
                ""
            )
        }
    }

    private fun constructException(expr: Expression, reason : String, help: String) : StaticCheckException{
        return StaticCheckException.make(ParseError, expr, reason, help, codeCtx, filepath)
    }

    protected open fun parseLeftValue() : MutableList<Expression> {
        val list = mutableListOf<Expression>()
        while (hasNext){
            val expr = parseExpr()
            list.add(expr)
            if(hasNext && ahead().type == TokenType.Comma){
                next()
                continue
            }else{
                break
            }
        }
        return list
    }

    protected open fun parseAttrMacro() : List<UsedAttrMacro> {
        val list = mutableListOf<UsedAttrMacro>()
        while (hasNext && ahead().type == TokenType.Attr){
            val attr = next()
            val tokens = mutableListOf<Token>()
            if(hasNext && ahead().type == TokenType.LParen){
                next()
                var braces = 1
                while (braces > 0 && hasNext){
                    val tk = next()
                    tokens.add(tk)
                    if (tk.type == TokenType.LParen) braces++
                    if (tk.type == TokenType.RParen) braces--
                }
                tokens.removeLast() // remove last ')'
            }
            list.add(UsedAttrMacro(attr.str, attr, tokens))
        }
        return list
    }

    // 6th highest priority
    protected open fun parseExpr() : Expression{
        val macros = if(hasNext && ahead().type == TokenType.Attr){
            parseAttrMacro()
        }else null
        var expr = parseBinOp(::senior) {
            when (it) {
                TokenType.And -> BinOpType.And
                TokenType.Or -> BinOpType.Or
                else -> null
            }
        }
        if(macros != null){
            expr.addMacro(macros)
        }else if(expr is UnaryOp){
            val tryFoldExpr = ConstantFolder.foldUnaryOp(expr)
            if (tryFoldExpr != null){
                expr = tryFoldExpr
            }
        }
        return expr
    }

    // 5th highest priority
    private fun senior() : Expression = parseBinOp(::medium){
        when(it){
            TokenType.Eqs -> BinOpType.Eqs
            TokenType.NotEq -> BinOpType.NotEq
            TokenType.Gt -> BinOpType.Gt
            TokenType.Lt -> BinOpType.Lt
            TokenType.GtEq -> BinOpType.GtEq
            TokenType.LtEq -> BinOpType.LtEq
            else -> null
        }
    }

    // 4th highest priority
    private fun medium() : Expression = parseBinOp(::junior){
        when(it){
            TokenType.LShift -> BinOpType.LShift
            TokenType.RShift -> BinOpType.RShift
            TokenType.BitAnd -> BinOpType.BitAnd
            TokenType.BitOr -> BinOpType.BitOr
            TokenType.Wave -> BinOpType.BitXor
            else -> null
        }
    }

    // 3rd highest priority
    private fun junior() : Expression = parseBinOp(::elementary){
        when(it){
            TokenType.Add -> BinOpType.Add
            TokenType.Sub -> BinOpType.Sub
            TokenType.Concat -> BinOpType.Concat
            else -> null
        }
    }

    // 2nd highest priority
    private fun elementary() : Expression = parseBinOp(::primary){
        when(it){
            TokenType.Mul -> BinOpType.Mul
            TokenType.Div -> BinOpType.Div
            TokenType.Pow -> BinOpType.Pow
            TokenType.Mod -> BinOpType.Mod
            TokenType.DivNoRem -> BinOpType.DivNoRem
            else -> null
        }
    }

    private inline fun parseBinOp(subExprFn : ()->Expression, opFn : (TokenType)->BinOpType?) : Expression{
        val left = subExprFn()
        val list = mutableListOf<Pair<BinOpType,Expression>>()
        while (hasNext){
            val op = opFn(ahead().type)
            op ?: break
            next()
            list.add(op to subExprFn())
        }
        return if(list.isEmpty()){
            left
        }else{
            BinOp(left,list)
        }
    }

    // highest priority
    protected open fun primary() : Expression{
        assertHasNext()
        var expr : Expression = when(next().type){
            // literal
            TokenType.Int -> LiteralInt(curr)
            TokenType.Num -> LiteralNum(curr)
            TokenType.Str -> LiteralStr(curr)
            TokenType.Nil -> LiteralNil(curr)
            TokenType.True  -> LiteralTrue(curr)
            TokenType.False -> LiteralFalse(curr)
            // unary operation
            TokenType.Sub -> UnaryOp(curr,parseExpr(),UniOpType.Neg)
            TokenType.GetLen -> UnaryOp(curr,parseExpr(),UniOpType.GetLen)
            TokenType.Not -> UnaryOp(curr,parseExpr(),UniOpType.Not)
            TokenType.Wave -> UnaryOp(curr,parseExpr(), UniOpType.BitNot)
            // ( expr )
            TokenType.LParen -> {
                // ( expr )
                val expr = parseExpr()
                assertNext(TokenType.RParen)
                expr
            }
            TokenType.LBrace -> parseTable()
            TokenType.Function -> parseLambda()
            // proc macro
            TokenType.Proc -> {
                val proc = curr
                assertNext(TokenType.LParen)
                val args = mutableListOf<Token>()
                var braces = 1
                while (braces > 0 && hasNext){
                    val tk = next()
                    args.add(tk)
                    if (tk.type == TokenType.LParen) braces++
                    if (tk.type == TokenType.RParen) braces--
                }
                args.removeLast() // remove last ')'
                assertTk(curr, TokenType.RParen)
                UsedProcMacro(proc, curr, proc.str, args)
            }
            // var
            TokenType.VarArg -> Var(curr)
            TokenType.Id -> {
                // process require ahead of func process
                if(curr.str == "require" && hasNext && ahead().type == TokenType.Str){
                    val req = curr
                    val path = next()
                    val require = Require(req, path, path.str)
                    processRequire(require)
                    require
                } else if(curr.str == "require" && ahead().type == TokenType.LParen) {
                    val req = curr
                    assertNext(TokenType.LParen)
                    assertNext(TokenType.Str)
                    val path = curr
                    assertNext(TokenType.RParen)
                    val require = Require(req, path, path.str)
                    processRequire(require)
                    require
                } else Var(curr)
            }
            else -> {
                throw constructException(curr, "unexpect token of ${curr.type}", "")
            }
        }
        while (hasNext){
            when(ahead().type){
                // func invoke
                // <func_expr> <args>
                // args ::= ( <expr_list> ) | <literal_str> | <literal_table>
                TokenType.LParen -> {
                    assertNext(TokenType.LParen)
                    val args = if(hasNext && ahead().type == TokenType.RParen){
                        mutableListOf()
                    }else {
                        parseElems()
                    }
                    assertNext(TokenType.RParen)
                    expr = FuncInvoke(curr,expr,args)
                }
                TokenType.Str -> {
                    val arg = next()
                    expr = FuncInvoke(curr, expr, mutableListOf(LiteralStr(arg)))
                }
                TokenType.LBrace -> {
                    val table = parseTable()
                    expr = FuncInvoke(curr, expr, mutableListOf(table))
                }
                // method invoke
                // <expr> : <method> ( <args> )
                TokenType.Colon -> {
                    next()
                    assertNext(TokenType.Id)
                    val method = curr
                    assertHasNext()
                    assertNext(TokenType.LParen, TokenType.LBrace, TokenType.Str)
                    val args : MutableList<Expression> = when(curr.type){
                        TokenType.Str -> mutableListOf(LiteralStr(curr))
                        TokenType.LBrace -> mutableListOf(parseTable())
                        else -> {
                            if(hasNext && ahead().type == TokenType.RParen){
                                assertNext(TokenType.RParen)
                                mutableListOf()
                            }else{
                                val elems = parseElems()
                                assertNext(TokenType.RParen)
                                elems
                            }
                        }
                    }
                    args.add(0, expr)
                    expr = FuncInvoke(curr, FieldAccess(expr, mutableListOf(LiteralStr(method))), args)
//                    expr = MethodInvoke(tokenList[tempIdx-1],curr,expr,method,args)
                }
                // value access
                TokenType.LBracket -> {
                    assertNext(TokenType.LBracket)
                    val keyExpr = parseExpr()
                    val close = next()
                    assertTk(close, TokenType.RBracket)
                    if(expr is FieldAccess){
                        expr.fields.add(keyExpr)
                        expr.endLine = close.line
                        expr.endOffset = close.offset
                    }else{
                        expr = FieldAccess(expr, mutableListOf(keyExpr))
                    }
                }
                // field access
                TokenType.Dot -> {
                    assertNext(TokenType.Dot)
                    val key = next()
                    assertTk(key, TokenType.Id)
                    if(expr is FieldAccess){
                        expr.fields.add(LiteralStr(key))
                        expr.endLine = key.line
                        expr.endOffset = key.offset
                    }else{
                        expr = FieldAccess(expr, mutableListOf(LiteralStr(key)))
                    }
                }
                else -> break
            }
        }
        return expr
    }

    protected open fun parseTable() : Expression{
        val open = curr
        assertTk(curr, TokenType.LBrace)
        val map = mutableListOf<Pair<Expression,Expression>>()
        var counter = 0L
        while (hasNext){
            if(ahead().type == TokenType.LBracket){
                next()
                val key = parseExpr()
                assertNext(TokenType.RBracket)
                assertNext(TokenType.Eq)
                val value = parseExpr()
                map.add(key to value)
            }else if(hasNext && ahead().type != TokenType.RBrace){
                val expr = parseExpr()
                if(hasNext && ahead().type == TokenType.Eq){
                    next()
                    val value = parseExpr()
                    assertVar(expr)
                    map.add(LiteralStr((expr as Var).name) to value)
                }else{
                    map.add(LiteralInt(++counter) to expr)
                }
            }
            if(hasNext && ahead().type == TokenType.Semi || ahead().type == TokenType.Comma){
                next()
                continue
            }else {
                assertNext(TokenType.RBrace)
                break
            }
        }
        return LiteralTable(open,curr,map)
    }

    protected open fun parseLambda() : DeclaredFunc{
        val fn = curr
        assertNext(TokenType.LParen)
        val  (params, tokens) = parseNames(true)
        assertNext(TokenType.RParen)
        val stmts = parseBlock()
        assertNext(TokenType.End)
        return DeclaredFunc(fn,curr,params,Body(stmts),tokens)
    }

    protected open fun parseFunc() : Pair<Expression,DeclaredFunc>{
        val fn = curr
        assertNext(TokenType.Id)
        var fnVal : Expression = Var(curr)
        var addSelfParam = false
        var colonToken = Token.empty
        while (hasNext){
            assertNext(TokenType.Colon, TokenType.Comma, TokenType.LParen)
            when(curr.type){
                TokenType.Comma -> {
                    assertNext(TokenType.Id)
                    if(fnVal is FieldAccess){
                        fnVal.fields.add(LiteralStr(curr))
                    }else{
                        fnVal = FieldAccess(fnVal, mutableListOf(LiteralStr(curr)))
                    }
                }
                TokenType.Colon -> {
                    addSelfParam = true
                    colonToken = curr
                    assertNext(TokenType.Id)
                    if(fnVal is FieldAccess){
                        fnVal.fields.add(LiteralStr(curr))
                    }else{
                        fnVal = FieldAccess(fnVal, mutableListOf(LiteralStr(curr)))
                    }
                    assertNext(TokenType.LParen)
                    break
                }
                TokenType.LParen -> break
                else -> {}
            }
        }
        assertTk(curr, TokenType.LParen)
        val (params, tokens) = parseNames(true)
        if(addSelfParam){
            params.add(0,"self")
            tokens.add(0,colonToken)
        }
        assertNext(TokenType.RParen)

        val stmts = parseBlock()
        assertNext(TokenType.End)

        return fnVal to DeclaredFunc(fn,curr,params,Body(stmts),tokens)
    }

    protected open fun parseNames(isFn : Boolean) : Pair<MutableList<String>,MutableList<Token>>{
        val list = mutableListOf<String>()
        val tokens = mutableListOf<Token>()
        while (hasNext && ahead().type == TokenType.Id){
            val tk = next()
            assertTk(tk, TokenType.Id)
            tokens.add(tk)
            list.add(tk.str)
            if (hasNext && ahead().type == TokenType.Comma){
                next()
            }
        }
        if (isFn && hasNext && ahead().type == TokenType.VarArg){
            tokens.add(next())
            list.add("...")
        }
        return list to tokens
    }

    private object Check{
        val pathReg = Regex("^([A-Za-z0-9_]+\\/)*[A-Za-z]+[A-Za-z0-9_]*\\.lua\$")
        val classReg = Regex("^([A-Za-z0-9_]+\\.)*[A-Za-z0-9_]+\$")
    }

    protected open fun processRequire(require: Require){
        val path = config.srcDir + require.path.replace("\\","/")
        if(Check.pathReg.matches(require.path.replace("\\","/"))){
//            println("is lua $path")
            require.isJavaLib = false
            val loadPath = context.loaded[path]
            if (loadPath != null){
                // required lua file is parsed
                require.loadPath = loadPath
                return
            }
            // path/to/xyz.lua -> path/to/XyzLua.class
            val out = Path.luaToOutput(config.outDir + require.path)
            require.outPath = out
            // path/to/XyzLua.class -> path.to.XyzLua
            require.loadPath = Path.luaToClassName(require.path)
            context.loaded[path] = require.loadPath

        }else if(Check.classReg.matches(require.path)){
//            println("is java ${require.path}")
            require.isJavaLib = true
            require.loadPath = require.path
            return
        }else{
            throw constructException(
                require,
                "expect a lua file or java class name, rather than '${require.path}'",
                ""
            )
        }
        // submit
        val future : Future<Pair<LuaScript,Throwable?>> = ThreadPool.submit {
            val file = File(path)
            if (file.exists().not() || file.isFile.not()) {
                val log = Log.from {
                    red("Compile Error -> File Not Found : ").ln()
                    normal("The path : ").blue(path).ln()
                    normal("which process from require('").blue(require.path).normal("')")
                        .red(" is not found or not a file").ln()
                }
                return@submit LuaScript.empty to log
            }
            try {
                val src = file.readText().toCharArray()
                val tokens = Lexer(src, require.path).tokenize()
                return@submit Parser(tokens, require.path, codeCtx, context, config).parse() to null
            } catch (e: Exception) {
                return@submit LuaScript.empty to e
            }
        }
        futures.add(future)
    }

    protected open fun parseElems() : MutableList<Expression>{
        val list = mutableListOf<Expression>()
        while (hasNext){
            list.add(parseExpr())
            if(hasNext && ahead().type == TokenType.Comma){
                next()
                continue
            }else{
                break
            }
        }
        return list
    }

    protected open fun assertTk(token: Token, type : TokenType){
        if (token.type != type){
            val (reason, help) = expectTkType(token, type)
            throw constructException(token, reason, help)
        }
    }
    
    protected open fun assertNext(vararg types : TokenType){
        assertHasNext()
        val token = next()
        if(types.indexOf(token.type) < 0){
            val (reason, help) = expectTkTypes(token, types)
            throw constructException(token, reason, help)
        }
    }

    protected open fun assertTk(token : Token, vararg types : TokenType){
        if(types.indexOf(token.type) < 0){
            val (reason, help) = expectTkTypes(token, types)
            throw constructException(token, reason, help)
        }
    }

    private fun constructException(token: Token, reason: String, help: String) : StaticCheckException {
        return StaticCheckException.make(ParseError,token, reason, help, codeCtx, filepath)
    }

    protected open fun assertHasNext(){
        if(!hasNext){
            val (reason, help) = expectHasNext(curr)
            throw StaticCheckException(ParseError,0,0,0,reason,help, file = filepath)
        }
    }

    protected open fun isLeftValue(expr : Expression) : Boolean{
        return when(expr){
            is FieldAccess -> true
            is Var -> true
            else -> false
        }
    }

    protected open fun haveSideEffect(expr : Expression) : Boolean {
        return when(expr){
            is BinOp -> true
            is FuncInvoke -> true
            is UnaryOp -> true
            is UsedProcMacro -> true
            else -> false
        }
    }
    
    companion object{
        private const val ParseError = "Parse Error"

        // expect token type
        private fun expectTkType(token: Token, type: TokenType) : Pair<String,String> {
            val reason = "expect a token of $type, found a token of ${token.type}"
            val help = ""
            return Pair(reason, help)
        }
        // expect one of token types
        private fun expectTkTypes(token: Token, type: Array<out TokenType>) : Pair<String,String> {
            val reason = "expect a token of [${type.joinToString()}], found a token of ${token.type}"
            val help = ""
            return Pair(reason, help)
        }
        // expect has next
        private fun expectHasNext(curr : Token) : Pair<String,String> {
            val reason = "expect has continuous code content after last $curr"
            val help = ""
            return Pair(reason, help)
        }
    }
}