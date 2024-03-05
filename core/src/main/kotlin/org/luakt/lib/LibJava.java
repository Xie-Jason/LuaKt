package org.luakt.lib;

public class LibJava {
    private static final Object type = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("java.type", args.length,1);
        return args[0].getClass().getSimpleName();
    };

    public final static LuaTable table;
    static {
        LuaTable tb = new LuaTable();
        tb.put("type", type);
        table = tb;
    }

}
