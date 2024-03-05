package org.luakt.parse

import org.luakt.syntax.Location
import java.util.Collections

class ParseContext() {
    // path/to/xyz.lua -> path.to.XyzLua
    val loaded: MutableMap<String, String> = Collections.synchronizedMap(mutableMapOf<String,String>())
    val globals : MutableMap<String, Location> = mutableMapOf()
}