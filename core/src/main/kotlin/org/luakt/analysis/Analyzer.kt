package org.luakt.analysis

import org.luakt.parse.LuaScript
import org.luakt.syntax.*

/*
Target :
   1. find global variable, record into ParsedScript directly
   2. find and count local variable, temp record into FuncAnalysisContext, finally record into DeclaredFunc
   3. find UpValues captured by closure, record into DeclaredFunc (capture info) and ParsedScript (save info)
   4. allocate index for all global-var, upVal and func
   5. find check and combine all label and goto, and check break
*/
class Analyzer(
    private val parsedScript: LuaScript
){
    fun analysis(){
        analysis(parsedScript)
        parsedScript.requires.values.forEach(this::analysis)
    }

    private fun analysis(script: LuaScript){
        if(script.globals.isEmpty()){
            script.fillGlobalSymbol()
        }
        val ctx = AnalysisContext(script)
        ctx.enterFunc()
        ctx.fnCtx.enterBlock(false)
        script.body.forEach {
            analysis(ctx,it)
        }
        val locals = ctx.fnCtx.exitBlock()
        script.body.locals.putAll(locals)
        script.meta = ctx.exitFunc()
        script.fnCount = ctx.funcIndex
    }

    private fun analysis(ctx : AnalysisContext, stmt : Statement){
        when(stmt){
            // variable declaration
            is LocalDeclare -> {
                stmt.left.forEachIndexed { idx, name ->
                    if (ctx.fnCtx.isDefinedInBlock(name)) {
                        ctx.errorRepeatSymbol(stmt.leftTokens[idx])
                    }
                    val loc = ctx.fnCtx.newLocal(name)
                    stmt.locations.add(loc)
                }
                stmt.right.forEach{
                    analysis(ctx,it)
                }
            }
            is Assign -> {
                stmt.left.forEach {
                    if(it is Var){
                        if(ctx.isDefined(it.name)){
                            // is an assign
                            analysis(ctx, it)
                        }else{
                            // is a declaration
                            it.location = ctx.newGlobal(it.name)
                        }
                    }else{
                        analysis(ctx, it)
                    }
                }
                stmt.right.forEach{
                    analysis(ctx,it)
                }
            }
            // variable declaration and block
            is ForEqLoop -> {
                // first local-var of block, need not check
                ctx.fnCtx.enterBlock(true)
                val loc = ctx.fnCtx.newLocal(stmt.name)
                stmt.location = loc
                analysis(ctx, stmt.start)
                analysis(ctx, stmt.limit)
                if(stmt.step != null) analysis(ctx, stmt.step)
                stmt.body.forEach {
                    analysis(ctx, it)
                }
                val locals = ctx.fnCtx.exitBlock()
                stmt.body.locals.putAll(locals)
            }
            is ForInLoop -> {
                ctx.fnCtx.enterBlock(true)
                stmt.names.forEachIndexed { idx, name ->
                    if(ctx.fnCtx.isDefinedInBlock(name)){
                        ctx.errorRepeatSymbol(stmt.nameTokens[idx])
                    }
                    val loc = ctx.fnCtx.newLocal(name)
                    stmt.locations.add(loc)
                }
                analysis(ctx, stmt.iter)
                stmt.body.forEach {
                    analysis(ctx, it)
                }
                val locals = ctx.fnCtx.exitBlock()
                stmt.body.locals.putAll(locals)
            }
            // block
            is Block -> {
                ctx.fnCtx.enterBlock(false)
                stmt.body.forEach {
                    analysis(ctx, it)
                }
                val locals = ctx.fnCtx.exitBlock()
                stmt.body.locals.putAll(locals)
            }
            is RepeatLoop -> {
                ctx.fnCtx.enterBlock(true)
                stmt.body.forEach {
                    analysis(ctx, it)
                }
                val locals = ctx.fnCtx.exitBlock()
                stmt.body.locals.putAll(locals)
                // condition not located in loop body
                analysis(ctx, stmt.cond)
            }
            is WhileLoop -> {
                analysis(ctx, stmt.cond)
                ctx.fnCtx.enterBlock(true)
                stmt.body.forEach {
                    analysis(ctx, it)
                }
                val locals = ctx.fnCtx.exitBlock()
                stmt.body.locals.putAll(locals)
            }
            is IfElse -> {
                stmt.branches.forEach { pair ->
                    val (cond, body) = pair
                    analysis(ctx, cond)
                    ctx.fnCtx.enterBlock(false)
                    body.forEach {
                        analysis(ctx, it)
                    }
                    val locals = ctx.fnCtx.exitBlock()
                    body.locals.putAll(locals)
                }
            }
            // just expressions
            is Return -> {
                stmt.list.forEach {
                    analysis(ctx, it)
                }
            }
            is Discard -> {
                analysis(ctx, stmt.expr)
            }
            is Goto -> {
                ctx.fnCtx.addGoto(stmt)
            }
            is Label -> {
                if(ctx.fnCtx.couldNewLabel(stmt)){
                    ctx.fnCtx.newLabel(stmt)
                }else{
                    ctx.errorRepeatSymbol(stmt.name, stmt)
                }
            }
            is Break -> {
                if(ctx.fnCtx.couldPutBreak().not()){
                    ctx.errorInvalidBreak(stmt)
                }
            }
            // nothing
            is None -> {}
        }
    }

    private fun analysis(ctx : AnalysisContext, expr : Expression){
        when(expr){
            is Var -> {
                val loc = ctx.fnCtx.findLocal(expr.name)
                if(loc != null){
                    expr.location = loc
                    return
                }
                val globalLoc = ctx.findGlobal(expr.name)
                if(globalLoc != null){
                    expr.location = globalLoc
                    return
                }
                if(ctx.isDefined(expr.name)){
                    if(ctx.isDefinedInOutFn(expr.name)){
                        expr.location = ctx.newUpValue(expr.name, true)
                    }else{
                        expr.location = ctx.newUpValue(expr.name, false)
                    }
                }else{
                    ctx.errorUnknownSymbol(expr.name, expr)
                }
            }
            is BinOp -> {
                analysis(ctx, expr.left)
                expr.then.forEach {
                    analysis(ctx, it.second)
                }
            }
            is UnaryOp -> {
                analysis(ctx, expr.inner)
            }
            is FieldAccess -> {
                analysis(ctx, expr.obj)
                expr.fields.forEach {
                    analysis(ctx, it)
                }
            }
            is FuncInvoke -> {
                analysis(ctx, expr.func)
                expr.args.forEach {
                    analysis(ctx, it)
                }
            }
            is LiteralTable -> {
                expr.pairs.forEach {
                    analysis(ctx, it.first)
                    analysis(ctx, it.second)
                }
            }
            is DeclaredFunc -> {
                expr.fnIdx = ctx.nextFnIdx()
                ctx.enterFunc()
                ctx.fnCtx.enterBlock(false)
                // add params into local
                expr.params.forEachIndexed { idx,it ->
                    if (ctx.fnCtx.isDefinedInFunc(it)){
                        ctx.errorRepeatSymbol(expr.paramTokens[idx])
                    }
                    val loc = ctx.fnCtx.newLocal(it)
                    expr.paramsLocations.add(loc)
                }
                expr.body.forEach {
                    analysis(ctx, it)
                }
                val blockLocals = ctx.fnCtx.exitBlock()
                expr.body.locals.putAll(blockLocals)
//                expr.upValues.addAll(ctx.fnCtx.upValues)
                expr.meta = ctx.exitFunc()
            }
            // do nothing
            // Literal, Require, UsedProcMacro
            else -> {}
        }
    }
}