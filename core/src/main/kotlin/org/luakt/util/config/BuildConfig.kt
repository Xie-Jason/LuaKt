package org.luakt.util.config

class BuildConfig(
    sourceDir : String,
    outputDir : String,
    val entrance : String,
    val release : Boolean,
    val threads : Int,
    analyzeDir : String,
) {
    val srcDir : String
    val outDir : String
    val analyzeOutputDir : String
    val needAnalyzeOutput : Boolean

    init {
        val dir1 = sourceDir.replace("\\","/")
        srcDir = if (dir1.endsWith("/")) dir1 else "$dir1/"
        val dir2 = outputDir.replace("\\","/")
        outDir = if (dir2.endsWith("/")) dir2 else "$dir2/"
        val dir3 = analyzeDir.replace("\\","/")
        analyzeOutputDir = if (dir3.endsWith("/")) dir3 else "$dir3/"
        needAnalyzeOutput = analyzeOutputDir.isNotEmpty()
    }

    companion object{
        val empty = BuildConfig("","","",false,0, "")
    }
}


class VisualConfig(
    // 各环节的编译时间
    val compileTime : Boolean,
    // 文件相互之间require关系
    val requireRelation : Boolean,
    // 宏展开后的代码
    val codeAfterMacroExpand : Boolean,
    // 宏展开前的语法树
    val astBeforeMacroExpand : Boolean,
    // 宏展开后的语法树
    val astAfterMacroExpand : Boolean,
    // 输出文件信息
    val outputInfo : Boolean,
    // 反编译后的字节码
    val bytecodeInfo : Boolean
)