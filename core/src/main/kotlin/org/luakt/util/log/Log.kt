package org.luakt.util.log

class Log : Loggable, Throwable() {
    private val items: MutableList<Pair<String, Style>> = mutableListOf()

    fun items() : List<Pair<String, Style>>{
        return items
    }

    companion object {
        fun from(f: Log.() -> Unit) : Log {
            val log = Log()
            log.f()
            return log
        }
    }

    fun stringify(converter : (Log) -> String) : String = converter(this)

    fun putLn(s : String) : Log{
        items.add(Pair(s, Style.Normal))
        if (!s.endsWith('\n')) {
            items.add(Pair("\n", Style.Normal))
        }
        return this
    }

    fun ln() : Log{
        if (items.lastOrNull()?.first?.endsWith('\n') != true) {
            items.add(Pair("\n", Style.Normal))
        }
        return this
    }

    fun normal(s : String) : Log {
        items.add(Pair(s, Style.Normal))
        return this
    }

    fun black(s: String): Log {
        items.add(Pair(s, Style.Black))
        return this
    }

    fun red(s: String): Log {
        items.add(Pair(s, Style.Red))
        return this
    }

    fun green(s: String): Log {
        items.add(Pair(s, Style.Green))
        return this
    }

    fun yellow(s: String): Log {
        items.add(Pair(s, Style.Yellow))
        return this
    }

    fun blue(s: String): Log {
        items.add(Pair(s, Style.Blue))
        return this
    }

    fun purple(s: String): Log {
        items.add(Pair(s, Style.Purple))
        return this
    }

    fun cyan(s: String): Log {
        items.add(Pair(s, Style.Cyan))
        return this
    }

    fun white(s: String): Log {
        items.add(Pair(s, Style.White))
        return this
    }

    fun boldBlack(s: String): Log {
        items.add(Pair(s, Style.BoldBlack))
        return this
    }

    fun boldRed(s: String): Log {
        items.add(Pair(s, Style.BoldRed))
        return this
    }

    fun boldGreen(s: String): Log {
        items.add(Pair(s, Style.BoldGreen))
        return this
    }

    fun boldYellow(s: String): Log {
        items.add(Pair(s, Style.BoldYellow))
        return this
    }

    fun boldBlue(s: String): Log {
        items.add(Pair(s, Style.BoldBlue))
        return this
    }

    fun boldPurple(s: String): Log {
        items.add(Pair(s, Style.BoldPurple))
        return this
    }

    fun boldCyan(s: String): Log {
        items.add(Pair(s, Style.BoldCyan))
        return this
    }

    fun boldWhite(s: String): Log {
        items.add(Pair(s, Style.BoldWhite))
        return this
    }

    fun underLineBlack(s: String) {
        items.add(Pair(s, Style.UnderLineBlack))
    }

    fun underLineRed(s: String) {
        items.add(Pair(s, Style.UnderLineRed))
    }

    fun underLineGreen(s: String) {
        items.add(Pair(s, Style.UnderLineGreen))
    }

    fun underLineYellow(s: String) {
        items.add(Pair(s, Style.UnderLineYellow))
    }

    fun underLineBlue(s: String) {
        items.add(Pair(s, Style.UnderLineBlue))
    }

    fun underLinePurple(s: String) {
        items.add(Pair(s, Style.UnderLinePurple))
    }

    fun underLineCyan(s: String) {
        items.add(Pair(s, Style.UnderLineCyan))
    }

    fun underLineWhite(s: String) {
        items.add(Pair(s, Style.UnderLineWhite))
    }

    fun addPrefix(prefix: Log) {
        val temp = MutableList(this.items.size + prefix.items.size) { idx ->
            if (idx < this.items.size) this.items[idx] else prefix.items[idx - this.items.size]
        }
        this.items.clear()
        this.items.addAll(temp)
    }

    fun addSuffix(suffix: Log) {
        this.items.addAll(suffix.items)
    }

    override fun toLog(): Log = this
}