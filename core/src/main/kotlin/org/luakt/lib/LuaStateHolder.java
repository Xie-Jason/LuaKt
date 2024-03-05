package org.luakt.lib;

public interface LuaStateHolder {
    void set(String key, Object value);
    Object get(String key);
}
