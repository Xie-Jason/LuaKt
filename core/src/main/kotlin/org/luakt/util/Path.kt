package org.luakt.util

object Path {
    private var anonymousCount = 0

    fun fileNameOfPath(path : String) : String{
        val idx1 = path.lastIndexOf('/')
        val idx2 = path.lastIndexOf('\\')
        if(idx1 < 0 && idx2 < 0) return path
        val idx = maxOf(idx1, idx2) + 1
        if(idx >= path.length) return "Anonymous${anonymousCount}"
        return path.substring(idx)
    }

    fun toStdName(name : String) : String{
        return name.split(".", "_")
            .joinToString("") {
                if (it.length > 1) it.substring(0, 1).uppercase() + it.substring(1) else it
            }
    }

    fun luaToOutput(outLua : String) : String{
        val outLua = outLua.replace("\\","/")
        return if(outLua.indexOf("/") >= 0){
            val idx = outLua.lastIndexOf("/")
            outLua.substring(0,idx) + "/" + toStdName(outLua.substring(idx+1)) + ".class"
        }else{
            toStdName(outLua)
        }
    }

    fun luaToClassName(s : String) : String{
        var res = s.replace("\\","/")
        val idx = res.lastIndexOf("/")
        if(idx < 0){
            res = toStdName(res)
        }else{
            res = res.substring(0,idx) + toStdName(res.substring(idx+1))
        }
        res = res.replace('/','.')
        return res
    }

    fun normalize(file: String): String {
        return file.replace("\\","/")
    }
}