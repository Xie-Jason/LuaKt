package org.luakt.util.log

typealias LogConverter = (Log) -> String

object Logger {
    private val stdout : LogTarget = object : LogTarget {
        override fun write(l: String) = println(l)
    }

    @JvmStatic
    fun log(s : String, target: LogTarget = stdout){
        target.write(s)
    }

    @JvmStatic
    fun log(e : Any, converter: LogConverter = ::consoleColorfulConvertor, target: LogTarget = stdout) {
        target.write(when (e) {
            is Loggable -> e.toLog().stringify(converter)
            is Exception -> e.stackTraceToString()
            else -> e.toString()
        })
    }



    @JvmStatic
    fun consoleColorfulConvertor(log: Log) = StringBuilder().apply {
        log.items().forEach {
            val (str, style) = it
            if (style == Style.Normal) {
                append(str)
            } else {
                append("\u001b[")
                append(
                    when (style) {
                        Style.Normal -> 0
                        Style.Black -> 30
                        Style.Red -> 31
                        Style.Green -> 32
                        Style.Yellow -> 33
                        Style.Blue -> 34
                        Style.Purple -> 35
                        Style.Cyan -> 36
                        Style.White -> 37
                        Style.BoldBlack -> "1;30"
                        Style.BoldRed -> "1;31"
                        Style.BoldGreen -> "1;32"
                        Style.BoldYellow -> "1;33"
                        Style.BoldBlue -> "1;34"
                        Style.BoldPurple -> "1;35"
                        Style.BoldCyan -> "1;36"
                        Style.BoldWhite -> "1;37"
                        Style.UnderLineBlack -> "4;30"
                        Style.UnderLineRed -> "4;31"
                        Style.UnderLineGreen -> "4;32"
                        Style.UnderLineYellow -> "4;33"
                        Style.UnderLineBlue -> "4;34"
                        Style.UnderLinePurple -> "4;35"
                        Style.UnderLineCyan -> "4;36"
                        Style.UnderLineWhite -> "4;37"
                    }
                )
                append("m")
                append(str)
                append("\u001b[m")
            }
        }
    }.toString()

    @JvmStatic
    fun consolePlainConvertor(log : Log) = StringBuilder().apply {
        log.items().map(Pair<String,Style>::first).forEach(this::append)
    }.toString()

    @JvmStatic
    fun htmlLog(log: Log) = ""
}