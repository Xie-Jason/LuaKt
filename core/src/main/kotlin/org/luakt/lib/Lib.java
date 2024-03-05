package org.luakt.lib;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Lib {

    public static final Object print = (LuaJKFunc) args -> {
        for (int i = 0; i < args.length; i++) {
            if(i == args.length-1){
                System.out.print(Lua.toString(args[i]));
            }else{
                System.out.print(Lua.toString(args[i]) + "  ");
            }
        }
        System.out.println();
        return null;
    };

    public static final Object type  = (LuaJKFunc) args -> {
        if(args.length == 0) return "nil";
        return _type(args[0]);
    };


    static String _type(Object value){
        String res;
        if(value == null) res = T_NIL;
        else if(value instanceof String) res = T_STR;
        else if(value instanceof Long) res = T_NUM;
        else if(value instanceof Double) res = T_NUM;
        else if(value instanceof Boolean) res = T_BOOL;
        else if(value instanceof LuaTable) res = T_TAB;
        else if(value instanceof LuaModFunc
                || value instanceof LuaJKFunc
                || value instanceof Method
                || value instanceof Method[]) res = T_FUN;
        else res = T_USR;
        return res;
    }
    static String T_STR = "string";
    static String T_NUM = "number";
    static String T_BOOL = "boolean";
    static String T_TAB = "table";
    static String T_FUN = "function";
    static String T_USR = "userdata";
    static String T_NIL = "nil";

    public static final Object pairs = (LuaJKFunc) args -> {
        if(args.length < 1) errorLen("pairs", args.length, 1);
        if(!(args[0] instanceof LuaTable)) errorType("pairs", args[0],T_TAB);
        LuaTable table = (LuaTable) args[0];
        return new LuaWrapIter<Map.Entry<Object, Object>, Object>(
            table.entrySet().iterator(),
            entry -> new Object[]{ entry.getKey(), entry.getValue() }
        );
    };

    public static final Object ipairs = (LuaJKFunc) args -> {
        if(args.length < 1) errorLen("pairs", args.length, 1);
        if(!(args[0] instanceof LuaTable)) errorType("pairs", args[0],T_TAB);
        LuaTable table = (LuaTable) args[0];
        return new LuaIPairIter(table);
    };

    public static final Object rawequal = (LuaJKFunc) args -> {
        if (args.length < 2) errorLen("rawequal", args.length, 2);
        Object left = args[0];
        Object right = args[1];
        if(Objects.equals(left,right)){
            return true;
        }
        if(left instanceof Number && right instanceof Number){
            return ((Number) left).doubleValue() == ((Number) right).doubleValue();
        }
        return false;
    };

    public static final Object rawlen = (LuaJKFunc) args -> {
        if (args.length < 2) errorLen("rawlen", args.length, 2);
        if(args[0] instanceof String){
            return ((String) args[0]).length();
        }
        if(args[0] instanceof LuaTable){
            return ((LuaTable) args[0]).size();
        }
        errorType("rawlen", args[0], T_STR,T_TAB);
        return null;
    };

    public static final Object rawget = (LuaJKFunc) args -> {
        if (args.length < 2) errorLen("rawget", args.length, 2);
        if(!(args[0] instanceof LuaTable)) errorType("rawget", args[0], T_TAB);
        return ((LuaTable)args[0]).get(args[1]);
    };

    public static final Object rawset = (LuaJKFunc) args -> {
        if (args.length < 3) errorLen("rawset", args.length, 3);
        if(!(args[0] instanceof LuaTable)) errorType("rawset", args[0], T_TAB);
        ((LuaTable)args[0]).put(args[1], args[2]);
        return args[0];
    };

    public static final Object select = (LuaJKFunc) args -> {
        args = Lua.flatten(args, 10002);
        Object key = args[0];
        Object[] values = (Object[]) args[1];
        if("#".equals(key)){
            return values.length;
        }
        return values[Objects.requireNonNull(Lua.toNumber(key)).intValue()];
    };

    public static final Object setmetatable = (LuaJKFunc) args -> {
        if (args.length < 2) errorLen("setmetatable", args.length, 2);
        if(!(args[0] instanceof LuaTable)) errorType("setmetatable", args[0], T_TAB);
        if(args[1] != null && !(args[1] instanceof LuaTable)) errorType("setmetatable", args[1], T_NIL,T_TAB);

        if(args[1] == null){
            ((LuaTable)args[0]).meta = null;
        }else{
            ((LuaTable)args[0]).meta = (LuaTable) args[1];
        }
        return args[0];
    };

    public static final Object getmetatable = (LuaJKFunc) args -> {
        if (args.length < 1) errorLen("getmetatable", args.length, 1);
        if(!(args[0] instanceof LuaTable)) errorType("getmetatable", args[0], T_TAB);
        return ((LuaTable)args[0]).meta;
    };

    public static final Object tonumber = (LuaJKFunc) args -> {
        if (args.length < 1) errorLen("tonumber", args.length, 1);
        return Lua.toNumber(args[0]);
    };

    public static final Object tostring = (LuaJKFunc) args -> {
        if (args.length < 1) errorLen("tostring", args.length, 1);
        return Lua.toString(args[0]);
    };

    public static final Object pcall = (LuaJKFunc) args -> {
        if (args.length < 1) errorLen("pcall", args.length, 1);
        try{
            return Lua.call(args[0], Lua.slice(args, 1, -1));
        }catch (Exception ignored){

        }
        return null;
    };

    /**
     * @param defaultEnd the default value of end, if equals Integer.MIN_VALUE,
     *                   means the default value of end is start
     * @param length the length of whole range
     * @return the rets[0] count from 1, and the rets[1] represent the index of last selected element
     * */
    @NotNull
    public static int[] getRange(Object[] args, int defaultEnd, int length, String fn){
        int start = 1, end = defaultEnd;
        if(args.length > 1){
            Number n = Lua.toNumber(args[1]);
            if(n == null) errorType(fn, args[1], T_NUM);
            start = n.intValue();
            if(start < 0) start = length + 1 + start;
            start = Math.max(1, start);
            end = start;
        }
        if(args.length > 2){
            Number n = Lua.toNumber(args[2]);
            if(n == null) errorType(fn, args[2], T_NUM);
            end = n.intValue();
            if(end < 0) end = length + 1 + end;
            end = Math.min(length, end);
        }
        if(end == Integer.MIN_VALUE){
            end = start;
        }
        return new int[]{ start, end };
    }

    public static Object string = LibString.table;
    public static Object utf8 = LibUtf8.table;
    public static Object table = LibTable.table;
    public static Object math = LibMath.table;
    public static Object java = LibJava.table;
    public static Object io = LibIO.table;

    public final static LuaTable global;
    static {
        LuaTable tb = new LuaTable();
        tb.put("type",    type);
        tb.put("print",   print);
        tb.put("pairs",   pairs);
        tb.put("ipairs",  ipairs);
        tb.put("select",  select);
        tb.put("rawget",  rawget);
        tb.put("rawlen",  rawlen);
        tb.put("rawset",  rawset);
        tb.put("rawequal",rawequal);
        tb.put("tonumber",tonumber);
        tb.put("tostring",tostring);
        tb.put("setmetatable", setmetatable);
        tb.put("getmetatable", getmetatable);
        tb.put("pcall", pcall);
        // TODO xpcall
        // library
        tb.put("string", string);
        tb.put("utf8", utf8);
        tb.put("table", table);
        tb.put("math", math);
        tb.put("java", java);
        tb.put("io", io);
        global = tb;
    }

    @Contract("_, _, _ -> fail")
    static void errorType(String fn, Object found, String ...expect){
        if(expect.length == 1){
            throw new LuaRuntimeException("bad argument type in library function '" + fn + "', expect " + expect[0] + ", found " + _type(found));
        }else{
            throw new LuaRuntimeException("bad argument type in library function '" + fn + "', expect one of " + Arrays.toString(expect) + ", found " + _type(found));
        }
    }

    @Contract("_, _, _ -> fail")
    static void errorLen(String fn, int found, int expect){
        throw new LuaRuntimeException("bad argument length in library function '" + fn + "', expect " + expect +", found " + found);
    }
}
