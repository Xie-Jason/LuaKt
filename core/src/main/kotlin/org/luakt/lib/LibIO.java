package org.luakt.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LibIO {

    private static OutputStream output = System.out;
    private static InputStream input = System.in;

    private static final Object write = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("io.write", args.length,1);
        try {
            output.write(Lua.toString(args[0]).getBytes());
        } catch (IOException e) {
            throw new LuaRuntimeException(e);
        }
        return null;
    };

    private static final Object flush = (LuaJKFunc) args -> {
        try {
            output.flush();
        } catch (IOException e) {
            throw new LuaRuntimeException(e);
        }
        return null;
    };


    public final static LuaTable table;
    static {
        LuaTable tb = new LuaTable();
        tb.put("write", write);
        table = tb;
    }
}
