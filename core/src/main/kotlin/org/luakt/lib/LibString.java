package org.luakt.lib;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibString {
    private static final ConcurrentHashMap<String, Pattern> patternMap = new ConcurrentHashMap<>();

    private static final String BYTE = "byte";
    private static final Object _byte = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen(BYTE, args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType(BYTE, args[0], Lib.T_STR);
        byte[] bytes = ((String) args[0]).getBytes();
        int[] range = Lib.getRange(args, Integer.MIN_VALUE, bytes.length, BYTE);
        int start = range[0], end = range[1];
        Object[] rets = new Object[end + 1 - start];
        int idx = 0;
        for (int i = start-1; i < end; i++) {
            rets[idx++] = (long)bytes[i];
        }
        return rets;
    };

    private static final String SUB = "sub";
    private static final Object sub = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen(SUB, args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType(SUB, args[0], Lib.T_STR);
        String str = (String) args[0];
        int[] range = Lib.getRange(args, -1, str.length(), SUB);
        int start = range[0], end = range[1];
        return str.substring(start-1, end);
    };

    static final Object _char = (LuaJKFunc) args -> {
        StringBuilder sb = new StringBuilder();
        for (Object num : args) {
            Number n = Lua.toNumber(num);
            if(n == null) Lib.errorType("char", num, Lib.T_NUM);
            sb.append((char)n.intValue());
        }
        return sb.toString();
    };

    private static final Object format = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("format", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("format", args[0], Lib.T_STR);
        return String.format((String) args[0], Lua.slice(args,1,-1));
    };

    private static final Object len = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("len", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("len", args[0], Lib.T_STR);
        return ((String) args[0]).length();
    };

    private static final Object lower = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("lower", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("lower", args[0], Lib.T_STR);
        return ((String) args[0]).toLowerCase();
    };

    private static final Object upper = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("upper", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("upper", args[0], Lib.T_STR);
        return ((String) args[0]).toUpperCase();
    };

    private static final Object reverse = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("reverse", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("reverse", args[0], Lib.T_STR);
        return new StringBuilder((String) args[0]).reverse().toString();
    };

    private static final String FIND = "find";
    private static final Object find = (LuaJKFunc) args -> {
        // check and prepare args
        if (args.length < 2) Lib.errorLen(FIND, args.length, 2);
        if(!(args[0] instanceof String)) Lib.errorType(FIND, args[0], Lib.T_STR);
        if(!(args[1] instanceof String)) Lib.errorType(FIND, args[1], Lib.T_STR);
        String str = (String) args[0];
        String patternStr = (String) args[1];
        int init = 0;
        if(args.length > 2){
            Number n = Lua.toNumber(args[2]);
            if(n == null) Lib.errorType(FIND, args[2], Lib.T_NUM);
            init = n.intValue();
            if(init < 0) init = str.length() + 1 + init;
            init--;
        }
        if(args.length > 3 && !(args[3] instanceof Boolean)) Lib.errorType(FIND, args[3], Lib.T_BOOL);
        boolean plain = args.length > 3 && (Boolean)args[3];
        // find
        if(plain){
            return str.indexOf(patternStr, init) + 1;
        }else{
            Pattern pattern = patternMap.computeIfAbsent(patternStr, Pattern::compile);
            final Matcher matcher = pattern.matcher(str);
            ArrayList<Object> list = new ArrayList<>();
            while (matcher.find(init)){
                list.add(matcher.start() + 1);
            }
            return list.toArray();
        }
    };

    private static final Object match = (LuaJKFunc) args -> {
        if (args.length < 2) Lib.errorLen(FIND, args.length, 2);
        if(!(args[0] instanceof String)) Lib.errorType(FIND, args[0], Lib.T_STR);
        if(!(args[1] instanceof String)) Lib.errorType(FIND, args[1], Lib.T_STR);
        String str = (String) args[0];
        String patternStr = (String) args[1];
        int init = 0;
        if(args.length > 2){
            Number n = Lua.toNumber(args[2]);
            if(n == null) Lib.errorType(FIND, args[2], Lib.T_NUM);
            init = n.intValue();
            if(init < 0) init = str.length() + 1 + init;
            init--;
        }

        Pattern pattern = patternMap.computeIfAbsent(patternStr, Pattern::compile);
        final Matcher matcher = pattern.matcher(str);
        if(matcher.find(init)){
            return str.substring(matcher.start(), matcher.end());
        }
        return null;
    };


    public final static LuaTable table;
    static {
        LuaTable tb = new LuaTable();
        tb.put("byte", _byte);
        tb.put("char", _char);
        tb.put("format", format);
        tb.put("find", find);
        tb.put("len", len);
        tb.put("lower", lower);
        tb.put("upper", upper);
        tb.put("reverse", reverse);
        tb.put("match", match);
        tb.put("sub", sub);
        table = tb;
    }
}
