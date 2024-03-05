package org.luakt.lib;

import java.util.Random;

public class LibMath {

    private static final Object abs = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.abs", args.length, 1);
        if(args[0] instanceof Long){
            long n = (long) args[0];
            return Math.abs(n);
        }
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.abs", args[0], Lib.T_NUM);
        return Math.abs(n.doubleValue());
    };

    private static final Object acos = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.acos", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.acos", args[0], Lib.T_NUM);
        return Math.acos(n.doubleValue());
    };

    private static final Object asin = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.asin", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.asin", args[0], Lib.T_NUM);
        return Math.asin(n.doubleValue());
    };

    private static final Object atan = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.atan", args.length, 1);
        Number y = Lua.toNumber(args[0]);
        if(y == null) Lib.errorType("math.atan", args[0], Lib.T_NUM);
        if(args.length > 1){
            Number x = Lua.toNumber(args[1]);
            if(x == null) Lib.errorType("math.atan", args[1], Lib.T_NUM);
            return Math.atan2(y.doubleValue(), x.doubleValue());
        }
        return Math.atan(y.doubleValue());
    };

    private static final Object cos = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.cos", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.cos", args[0], Lib.T_NUM);
        return Math.cos(n.doubleValue());
    };

    private static final Object deg = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.deg", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.deg", args[0], Lib.T_NUM);
        return Math.toDegrees(n.doubleValue());
    };

    private static final Object exp = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.exp", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.exp", args[0], Lib.T_NUM);
        return Math.exp(n.doubleValue());
    };

    private static final Object floor = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.floor", args.length, 1);
        if(args[0] instanceof Long) return args[0];
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.floor", args[0], Lib.T_NUM);
        return Math.floor(n.doubleValue());
    };

    private static final Object log = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.log", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.log", args[0], Lib.T_NUM);
        if(args.length > 1){
            Number base = Lua.toNumber(args[1]);
            if(base == null) Lib.errorType("math.atan", args[1], Lib.T_NUM);
            return Math.log(n.doubleValue()) / Math.log(base.doubleValue());
        }
        return Math.log(n.doubleValue());
    };

    private static final Object rad = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.rad", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.rad", args[0], Lib.T_NUM);
        return Math.toRadians(n.doubleValue());
    };

    private static final Object modf = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.modf", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.modf", args[0], Lib.T_NUM);
        long integer = ((long) Math.floor(n.doubleValue()));
        return new Object[]{
            integer,
            n.doubleValue() - integer
        };
    };

    private static final Object sin = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.sin", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.sin", args[0], Lib.T_NUM);
        return Math.sin(n.doubleValue());
    };

    private static final Object sqrt = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.sqrt", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.sqrt", args[0], Lib.T_NUM);
        return Math.sqrt(n.doubleValue());
    };

    private static final Object tan = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.tan", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.tan", args[0], Lib.T_NUM);
        return Math.tan(n.doubleValue());
    };

    private static final Object tointeger = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.tointeger", args.length, 1);
        if(args[0] instanceof Long) return args[0];
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.tointeger", args[0], Lib.T_NUM);
        if (Math.round(n.doubleValue()) == n.doubleValue()){
            return n.longValue();
        }
        return null;
    };

    private static final Object type = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.type", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) {
            return "nil";
        }else if (Math.round(n.doubleValue()) == n.doubleValue()){
            return "integer";
        }else{
            return "float";
        }
    };

    private static final Object max = (LuaJKFunc) args -> {
        if (args.length < 2) Lib.errorLen("math.max", args.length, 1);
        Object theMax = args[0];
        for (int i = 1; i < args.length; i++) {
            // if theMax < args[i] then theMax = args[i]
            theMax = Lua.judge(Lua.lt(theMax, args[i])) ? args[i]: theMax;
        }
        return theMax;
    };

    private static final Object min = (LuaJKFunc) args -> {
        if (args.length < 2) Lib.errorLen("math.min", args.length, 1);
        Object theMin = args[0];
        for (int i = 1; i < args.length; i++) {
            theMin = Lua.judge(Lua.lt(theMin, args[i])) ? theMin : args[i];
        }
        return theMin;
    };

    private static final Random rand = new Random();
    private static final Object random = (LuaJKFunc) args -> {
        if(args.length == 1){
            Number n = Lua.toNumber(args[0]);
            if(n == null) Lib.errorType("math.random", args[0], Lib.T_NUM);
            return n.longValue() + rand.nextLong();
        }
        if (args.length > 1){
            Number from = Lua.toNumber(args[0]);
            if(from == null) Lib.errorType("math.random", args[0], Lib.T_NUM);
            Number to = Lua.toNumber(args[1]);
            if(to == null) Lib.errorType("math.random", args[1], Lib.T_NUM);
            return from.longValue() + (rand.nextLong() % (to.longValue() - from.longValue()));
        }
        return rand.nextLong();
    };

    private static final Object randomseed = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("math.randomseed", args.length, 1);
        Number n = Lua.toNumber(args[0]);
        if(n == null) Lib.errorType("math.randomseed", args[0], Lib.T_NUM);
        rand.setSeed(n.longValue());
        return null;
    };

    public final static LuaTable table;
    static {
        LuaTable tb = new LuaTable();
        // constant value
        tb.put("huge",Double.MAX_VALUE);
        tb.put("maxinteger", Long.MAX_VALUE);
        tb.put("mininteger", Long.MIN_VALUE);
        tb.put("pi", Math.PI);
        // function
        tb.put("abs", abs);
        tb.put("acos", acos);
        tb.put("asin", asin);
        tb.put("atan", atan);
        tb.put("cos", cos);
        tb.put("deg", deg);
        tb.put("exp", exp);
        tb.put("floor", floor);
        tb.put("log", log);
        tb.put("rad", rad);
        tb.put("modf", modf);
        tb.put("sin", sin);
        tb.put("sqrt", sqrt);
        tb.put("tan", tan);
        tb.put("tointeger", tointeger);
        tb.put("type", type);
        tb.put("random", random);
        tb.put("randomseed", randomseed);
        tb.put("max", max);
        tb.put("min", min);
        table = tb;
    }
}
