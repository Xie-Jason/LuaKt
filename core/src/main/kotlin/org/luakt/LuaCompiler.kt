package org.luakt

import org.luakt.analysis.Analyzer
import org.luakt.codegen.CodeGenerator
import org.luakt.lexer.CodeContext
import org.luakt.lexer.Lexer
import org.luakt.lexer.Token
import org.luakt.macro.MacroProcessor
import org.luakt.parse.LuaScript
import org.luakt.parse.ParseContext
import org.luakt.parse.Parser
import org.luakt.util.Path
import org.luakt.util.ThreadPool
import org.luakt.util.config.BuildConfig
import org.luakt.util.buf.FileWriter
import org.luakt.util.log.Loggable
import org.luakt.util.log.Logger
import java.io.File
import java.io.FileOutputStream

object LuaCompiler {
    @JvmStatic
    fun compile(config : BuildConfig){
        try {
            val src = File(config.srcDir + config.entrance).readText().toCharArray()
            val tokens = Lexer(src,config.entrance).tokenize()
            printTokens(tokens)
            val parser = Parser(tokens, config.entrance, CodeContext(src), ParseContext(), config)
            val script = parser.parse()
            MacroProcessor(script, parser.context).process()
            Analyzer(script).analysis()
            if(config.needAnalyzeOutput){
                outputAnalyzeResult(config.analyzeOutputDir, script)
            }
            CodeGenerator(script, config).generate().forEach {
                println("${it.selfClass} -> ${it.outputPath}")
                it.write(FileWriter(FileOutputStream(it.outputPath)))
            }
        }catch (e : Throwable){
            if(e is Loggable){
                Logger.log(e)
            }
            e.printStackTrace()

        }
        ThreadPool.destroy()
    }

    private fun outputAnalyzeResult(dir : String, rootScript: LuaScript ){
        for (pair in rootScript.dumpSource(::StringBuilder)) {
            val (filename, buf) = pair
            val output = dir + Path.normalize(filename)
            println("analyze output $output")
            val file = File(output)
            file.writeText(buf.toString())
        }
    }

    private fun printTokens(tokens : List<Token>){
        println("   行   列   长度  索引  类型 ")
        tokens.forEach(::println)
    }
}

fun main(){
    val config = BuildConfig(
        "D:\\Kotlin\\LuaKt\\core\\src\\main\\resources\\example",
        "D:/self/out/",
        "fib.lua",
        false,
        2,
        "D:\\Kotlin\\LuaKt\\core\\src\\main\\resources\\analysis"
    )
    LuaCompiler.compile(config)
}