package org.luakt.lib;

public class LuaRuntimeException extends RuntimeException{
    public LuaRuntimeException() {
    }

    public LuaRuntimeException(String message) {
        super(message);
    }

    public LuaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LuaRuntimeException(Throwable cause) {
        super(cause);
    }
}
