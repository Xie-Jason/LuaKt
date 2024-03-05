package org.luakt.lib;

public class LibTable {

    private static final Object concat = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("concat", args.length, 1);
        if(!(args[0] instanceof LuaTable)) Lib.errorType("concat", args[0], Lib.T_TAB);
        final LuaTable table = (LuaTable) args[0];
        final int[] range = Lib.getRange(args, Integer.MIN_VALUE, table.size(), "concat");
        StringBuilder sb = new StringBuilder();
        for (int i = range[0]; i < range[1]; i++) {
            sb.append(table.get((long)i));
        }
        return sb;
    };
    private static final Object insert = (LuaJKFunc) args -> {
        if (args.length < 2) Lib.errorLen("insert", args.length, 2);
        if(!(args[0] instanceof LuaTable)) Lib.errorType("insert", args[0], Lib.T_TAB);
        final LuaTable table = (LuaTable) args[0];
        if(args.length >= 3){
            Number num = Lua.toNumber(args[1]);
            if(num == null) Lib.errorType("insert", args[1], Lib.T_NUM);
            Long currKey = num.longValue();
            Object val = args[2];
            while (val != null){
                val = table.put(currKey, val);
                currKey++;
            }
        }else{
            table.put((long)(table.size() + 1), args[1]);
        }
        return null;
    };
    private static final Object move = (LuaJKFunc) args -> {
        if (args.length < 4) Lib.errorLen("move", args.length, 1);
        if(!(args[0] instanceof LuaTable)) Lib.errorType("move", args[0], Lib.T_TAB);
        final LuaTable fromTable = (LuaTable) args[0];
        final long[] tuples = new long[3];
        for (int i = 0; i < 3; i++) {
            Number n = Lua.toNumber(args[i+1]);
            if(n == null) Lib.errorType("move", args[i+1], Lib.T_NUM);
            tuples[i] = n.longValue();
        }
        LuaTable toTable = fromTable;
        if(args.length > 4){
            if(!(args[4] instanceof LuaTable)) Lib.errorType("move", args[4], Lib.T_TAB);
            toTable = (LuaTable) args[4];
        }
        for (long oldKey = tuples[0], newKey = tuples[2]; oldKey < tuples[1]; oldKey++, newKey++){
            toTable.put(newKey, fromTable.remove(oldKey));
        }
        return null;
    };
    private static final Object pack = (LuaJKFunc) args -> {
        final LuaTable table = new LuaTable();
        long key = 1;
        for (Object arg : args) {
            table.put(key++, arg);
        }
        table.put("n", (long)args.length);
        return table;
    };
    private static final Object remove = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("remove", args.length, 1);
        if(!(args[0] instanceof LuaTable)) Lib.errorType("remove", args[0], Lib.T_TAB);
        final LuaTable table = (LuaTable) args[0];
        long pos = table.size();
        if(args.length > 1){
            Number n = Lua.toNumber(args[1]);
            if(n == null)  Lib.errorType("remove", args[1], Lib.T_NUM);
            pos = n.longValue();
        }
        Object val;
        do {
            val = table.remove(pos + 1);
            table.put(pos, val);
            pos++;
        }while (val != null);
        return null;
    };
    private static final Object sort = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("sort", args.length, 1);
        if(!(args[0] instanceof LuaTable)) Lib.errorType("sort", args[0], Lib.T_TAB);
        return null;
    };
    private static final Object unpack = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("unpack", args.length, 1);
        if(!(args[0] instanceof LuaTable)) Lib.errorType("unpack", args[0], Lib.T_TAB);
        final LuaTable table = (LuaTable) args[0];
        final int[] range = Lib.getRange(args, table.size(), table.size(), "unpack");
        final Object[] rets = new Object[range[1] + 1 - range[0]];
        for (int i = range[0]-1, idx = 0; i <= range[1]; i++, idx++) {
            rets[idx] = table.get((long)i);
        }
        return rets;
    };

    public final static LuaTable table;
    static {
        LuaTable tb = new LuaTable();
        tb.put("concat", concat);
        tb.put("move", move);
        tb.put("insert", insert);
        tb.put("sort", sort);
        tb.put("pack", pack);
        tb.put("unpack", unpack);
        tb.put("remove", remove);
        table = tb;
    }
}
