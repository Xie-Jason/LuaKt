package org.luakt.lib;

public abstract class LuaModule {
    public abstract Object[] call(int fnId, Object[] args, Object[] upValues);
    public abstract Object[] exec(Object[] args, Object[] caps);

    public abstract void setState(Object state);
}
