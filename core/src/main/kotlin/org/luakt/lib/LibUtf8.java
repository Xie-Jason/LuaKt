package org.luakt.lib;

import java.util.ArrayList;

public class LibUtf8 {
    private final static String charpattern = "[\\0-\\x7F\\xC2-\\xF4][\\x80-\\xBF]*";

    private static final Object codes = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("codes", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("codes", args[0], Lib.T_STR);
        String s = (String) args[0];
        char[] chars = s.toCharArray();
        ArrayList<Object[]> list = new ArrayList<>(chars.length);
        int idx = 0;
        for (char c : chars) {
            list.add(new Integer[]{ idx, (int) c});
            if(c <= 0xff){
                idx++;
            }else{
                idx+=2;
            }
        }
        return new LuaWrapIter<>(
            list.iterator()
        );
    };

    private static final Object len = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("len", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("len", args[0], Lib.T_STR);
        String s = ((String) args[0]);
        int[] range = Lib.getRange(args, -1, s.length(), "len");
        return s.codePointCount(range[0]-1,range[1]);
    };

    private static final Object codepoint = (LuaJKFunc) args -> {
        if (args.length < 1) Lib.errorLen("codepoint", args.length, 1);
        if(!(args[0] instanceof String)) Lib.errorType("codepoint", args[0], Lib.T_STR);
        String s = (String) args[0];
        int[] range = Lib.getRange(args, Integer.MIN_VALUE, s.length(), "codepoint");
        return new LuaWrapIter<>(
                s.substring(range[0]-1,range[1]).codePoints().mapToObj(Long::valueOf).iterator()
        );
    };


    public final static LuaTable table;
    static {
        LuaTable tb = new LuaTable();
        tb.put("char", LibString._char);
        tb.put("charpattern", charpattern);
        tb.put("codes", codes);
        tb.put("codepoint", codepoint);
        tb.put("len", len);
        table = tb;
    }
}
