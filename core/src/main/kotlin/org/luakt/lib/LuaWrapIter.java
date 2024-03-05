package org.luakt.lib;

import java.util.Iterator;
import java.util.function.Function;

public class LuaWrapIter<T,R> extends LuaIter {
    private final Iterator<T> iter;
    private Function<T, R> transform;

    @Override
    public Object next() {
        if(!iter.hasNext()) {
            return null;
        }
        T next = iter.next();
        if(next == null) {
            return null;
        }
        if(transform != null){
            return transform.apply(next);
        }
        return next;
    }

    public LuaWrapIter(Iterator<T> iter) {
        this.iter = iter;
    }

    public LuaWrapIter(Iterator<T> iter, Function<T, R> transform) {
        this.iter = iter;
        this.transform = transform;
    }
}
