package org.luakt.lib;

public class LuaIntIter extends LuaIter{
    private long var;
    private final long limit;
    private final long step;

    public LuaIntIter(long init, long limit, long step) {
        this.var = init - step;
        this.limit = limit;
        this.step = step;
    }

    public static LuaIter make(Object init, Object limit, Object step){
        Number i = Lua.toNumber(init);
        Number l = Lua.toNumber(limit);
        Number s = Lua.toNumber(step);
        if(i == null || l == null || s == null || i.longValue() >= l.longValue()){
            throw new RuntimeException("Lua Runtime Error : bad range (" + init + ", " + limit + ", " + step + " ) in numerical for loop");
        }
        return new LuaIntIter(
            i.longValue(),
            l.longValue(),
            s.longValue()
        );
    }

    @Override
    public Object next() {
        var += step;
        if(var >= limit){
            return null;
        }
        return var;
    }
}
