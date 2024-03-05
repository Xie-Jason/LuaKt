package org.luakt.lib;

import java.util.Arrays;

public class LuaModFunc implements LuaFunc {
    private LuaModule module;
    private int fnId;
    private Object[] upValues;

    @Override
    public Object[] call(Object[] args) {
        if(fnId == 2){
            System.out.println(Arrays.toString(this.upValues));
        }
        return module.call(this.fnId, args, this.upValues);
    }

    public LuaModFunc(LuaModule module, int fnId, Object[] upValues) {
        this.module = module;
        this.fnId = fnId;
        this.upValues = upValues;
    }

    public static LuaModFunc make(LuaModule module, int fnId, Object[] upValues){
        return new LuaModFunc(module, fnId, upValues);
    }

    public static LuaModFunc make(LuaModule module, int fnId){
        return new LuaModFunc(module, fnId, null);
    }

    public void init(Object[] upValues){
        this.upValues = upValues;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LuaFunc(");
        sb.append("module=").append(module.getClass().getSimpleName());
        sb.append(", fnId=").append(fnId);
        sb.append(", upValueSize=").append(upValues.length);
        sb.append(')');
        return sb.toString();
    }
}

