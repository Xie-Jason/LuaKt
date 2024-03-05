package org.luakt.lib;

public class LuaIPairIter extends LuaIter{
    private LuaTable table;
    private long index = 0;
    public LuaIPairIter(LuaTable table) {
        this.table = table;
    }

    @Override
    public Object next() {
        index++;
        try {
            final Object val = Lua.index(table, index);
            if(val == null) return null;
            return new Object[]{ index, val };
        } catch (Exception e) {
            return null;
        }
    }
}
