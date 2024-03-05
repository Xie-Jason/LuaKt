package org.luakt.lib;

import java.util.HashMap;
import java.util.Map;

public class LuaTable extends HashMap<Object, Object> {
    public LuaTable meta;

    public LuaTable(Map<?, ?> m) {
        super(m);
    }

    public LuaTable(int initCap) {
        super(initCap);
    }

    public LuaTable() {}

    public static LuaTable make(int cap){
        return new LuaTable(cap);
    }

    @Override
    public String toString() {
//        final StringBuilder sb = new StringBuilder("{");
//        for (Entry<Object, Object> entry : this.entrySet()) {
//            sb.append(Lua.toString(entry.getKey()));
//            sb.append("=");
//            sb.append(Lua.toString(entry.getValue()));
//            sb.append(", ");
//        }
//        if(sb.length() >= 2){
//            sb.delete(sb.length()-2 , sb.length());
//        }
//        sb.append('}');
//        return sb.toString();
        return "LuaTable$" + this.hashCode();
    }
}
