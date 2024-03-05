package org.luakt.lib;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class Lua {
    // binary operator function
    public static Object add(Object left, Object right) {
        if(left instanceof Long && right instanceof Long){
            return (Long) left + (Long)right;
        }
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() + r.doubleValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.ADD);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.ADD);
        }
        if(res != null) return res;
        errorBinOp("+", left, right);
        return null;
    }
    public static Object sub(Object left, Object right) {
        if(left instanceof Long && right instanceof Long){
            return (Long) left - (Long)right;
        }
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() - r.doubleValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.SUB);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.SUB);
        }
        if(res != null) return res;
        errorBinOp("-", left, right);
        return null;
    }
    public static Object mul(Object left, Object right) {
        if(left instanceof Long && right instanceof Long){
            return (Long) left * (Long)right;
        }
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() * r.doubleValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.MUL);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.MUL);
        }
        if(res != null) return res;
        errorBinOp("*", left, right);
        return null;
    }
    public static Object div(Object left, Object right) {
        if(left instanceof Long && right instanceof Long && (Long)left % (Long)right == 0){
            return (Long) left / (Long)right;
        }
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() / r.doubleValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.DIV);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.DIV);
        }
        if(res != null) return res;
        errorBinOp("/", left, right);
        return null;
    }
    public static Object mod(Object left, Object right) {
        if(left instanceof Long && right instanceof Long){
            return (Long) left % (Long)right;
        }
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() % r.doubleValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.MOD);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.MOD);
        }
        if(res != null) return res;
        errorBinOp("%", left, right);
        return null;
    }
    public static Object pow(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return Math.pow(l.doubleValue(), r.doubleValue());
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.POW);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.POW);
        }
        if(res != null) return res;
        errorBinOp("^", left, right);
        return null;
    }
    public static Object idiv(Object left, Object right) {
        if(left instanceof Long && right instanceof Long){
            return Math.floorDiv((Long) left, (Long)right);
        }
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return Math.floor(l.doubleValue() / r.doubleValue());
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.IDIV);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.IDIV);
        }
        if(res != null) return res;
        errorBinOp("//", left, right);
        return null;
    }
    public static Object band(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.longValue() & r.longValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.BAND);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.BAND);
        }
        if(res != null) return res;
        errorBinOp("&", left, right);
        return null;
    }
    public static Object bor(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.longValue() | r.longValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.BOR);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.BOR);
        }
        if(res != null) return res;
        errorBinOp("|", left, right);
        return null;
    }
    public static Object bxor(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.longValue() ^ r.longValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.BXOR);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.BXOR);
        }
        if(res != null) return res;
        errorBinOp("^", left, right);
        return null;
    }
    public static Object shl(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.longValue() << r.longValue();
        }
        if(left instanceof LuaTable && ((LuaTable) left).meta != null && ((LuaTable) left).meta.containsKey(Name.SHL)){
            Object res = callMetaFn((LuaTable) left, right, Name.SHL);
            if(res != null) return res;
        }
        errorBinOp("<<", left, right);
        return null;
    }
    public static Object shr(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.longValue() >> r.longValue();
        }
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            Object res = callMetaFn((LuaTable) left, right, Name.SHR);
            if(res != null) return res;
        }
        errorBinOp(">>", left, right);
        return null;
    }
    public static Object concat(Object left, Object right) {
        if((left instanceof Number || left instanceof String)
           && (right instanceof Number || right instanceof String)){
           return left.toString() + right;
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.CONCAT);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.CONCAT);
        }
        if(res != null) return res;
        errorBinOp("..", left, right);
        return null;
    }
    public static Object eq(Object left, Object right) {
        if(Objects.equals(left,right)){
            return true;
        }
        if(left instanceof Number && right instanceof Number){
            return ((Number) left).doubleValue() == ((Number) right).doubleValue();
        }
        Object res = null;
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            res = callMetaFn((LuaTable) left, right, Name.EQ);
        }else if(right instanceof LuaTable && ((LuaTable) right).meta != null){
            res = callMetaFn((LuaTable) right, left, Name.EQ);
        }
        if(res == null) return false;
        return res;
    }
    public static Object lt(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() < r.doubleValue();
        }
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            Object res = callMetaFn((LuaTable) left, right, Name.LT);
            if(res != null) return res;
        }
        errorBinOp("<", left, right);
        return null;
    }
    public static Object le(Object left, Object right) {

        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() <= r.doubleValue();
        }
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            Object res = callMetaFn((LuaTable) left, right, Name.LE);
            if(res != null) return res;
        }
        errorBinOp("<=", left, right);
        return null;
    }
    public static Object index(Object tab, Object key){
        if(tab instanceof LuaTable){
            LuaTable table =  ((LuaTable) tab);
            Object val = table.get(key);
            if(val != null || table.meta == null) return val;
            Object fn = Lua.index(table.meta, Name.INDEX);
            if(fn != null){
                return Lua.call(fn, new Object[]{key});
            }
        }
        return null;
    }

    // unary operator function
    public static Object len(Object obj) {
        if(obj instanceof String){
            return ((String) obj).length();
        }
        if(obj instanceof LuaTable){
            if(((LuaTable) obj).meta != null && ((LuaTable) obj).meta.containsKey(Name.LEN)){
                Object res = callMetaFn((LuaTable) obj, obj, Name.LEN);
                if(res != null) return res;
            }
            return ((LuaTable) obj).size();
        }
        errorUnaryOp("#", obj);
        return null;
    }
    public static Object unm(Object obj) {
        if(obj instanceof Long) return - (Long) obj;
        Number n = toNumber(obj);
        if(n != null) return - n.doubleValue();
        if(obj instanceof LuaTable && ((LuaTable) obj).meta != null && ((LuaTable) obj).meta.containsKey(Name.UNM)){
            Object res = callMetaFn((LuaTable) obj, obj, Name.UNM);
            if(res != null) return res;
        }
        errorUnaryOp("-", obj);
        return null;
    }
    public static Object not(Object obj) {
        if(obj == null) return true;
        return obj instanceof Boolean && !(Boolean) obj;
    }
    public static Object bnot(Object obj) {
        Number n = toNumber(obj);
        if(n != null) return ~n.longValue();
        if(obj instanceof LuaTable && ((LuaTable) obj).meta != null){
            Object res = callMetaFn((LuaTable) obj, obj, Name.BNOT);
            if(res != null) return res;
        }
        errorUnaryOp("~", obj);
        return null;
    }

    private static Object callMetaFn(LuaTable table, Object arg, String fnKey) {
        if(table == null || table.meta == null) return null;
        final Object fn = Lua.index(table.meta, fnKey);
        if(fn == null) return null;
        return Lua.call(fn, new Object[]{table, arg});
    }

    // following function are not meta method
    public static Object ne(Object left, Object right) {
        return !judge(eq(left,right));
    }
    public static Object gt(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() > r.doubleValue();
        }
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            Object res = callMetaFn((LuaTable) left, right, Name.LE);
            if(res != null) return ! judge(res);
        }
        errorBinOp(">", left, right);
        return null;
    }
    public static Object ge(Object left, Object right) {
        Number l = toNumber(left);
        Number r = toNumber(right);
        if(l != null && r != null){
            return l.doubleValue() >= r.doubleValue();
        }
        if(left instanceof LuaTable && ((LuaTable) left).meta != null){
            Object res = callMetaFn((LuaTable) left, right, Name.LT);
            if(res != null) return ! judge(res);
        }
        errorBinOp(">=", left, right);
        return null;
    }


    public static boolean judge(Object obj){
        if(obj == null) return false;
        return !(obj instanceof Boolean) || (Boolean) obj;
    }


    public static ConcurrentHashMap<Class<?>, Object> loaded = new ConcurrentHashMap<>();

    public static Object require(final Object state, String mod, boolean isJavaLib) {
        if(isJavaLib){
            try {
                return Class.forName(mod);
            } catch (ClassNotFoundException e) {
                throw new LuaRuntimeException(e);
            }
        }
        final Class<?> modClass;
        try {
            modClass = Class.forName(mod);
        } catch (ClassNotFoundException e) {
            throw new LuaRuntimeException(e);
        }
        return loaded.compute(modClass, (key, val) -> {
            if(val != null){
                return val;
            }
            try {
                LuaModule module = (LuaModule) modClass.getConstructors()[0].newInstance();
                if(state == null) throw new Exception("state is null");
                module.setState(state);
                final Object[] objs = module.exec(null, null);
                if(objs.length == 1){
                    return objs[0];
                }else{
                    return objs;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Object access(Object module, Object keyObj) {
        if(module == null) return null;
        if(module instanceof LuaTable){
            Object val = Lua.index(module, keyObj);
            if(val == null){
                val = LibTable.table.get(keyObj);
            }
            return val;
        }
        if(module instanceof Object[]){
            int index = toNumber(keyObj).intValue();
            Object[] array = ((Object[])module);
            return (index >= 0 && index < array.length) ? array[index] : null;
        }
        if(module instanceof String){
            Object res = LibString.table.get(keyObj);
            if(res != null) {
                return res;
            }
        }
        if(!(keyObj instanceof String)) {
            error("unexpect key in index operation : " + toString(module) + " [" + toString(keyObj) + "]");
        }

        String key = (String) keyObj;
        Class<?> cls;
        Object obj = null;
        if(module instanceof Class<?>){
            // get static field value or method or array of method (when target are overload functions)
            cls = (Class<?>) module;
        }else{
            // get non-static field value or virtual method
            obj = module;
            cls = obj.getClass();
        }
        for (Field field : cls.getFields()) {
            if (field.getName().equals(key)) {
                try {
                    return field.get(obj);
                } catch (IllegalAccessException ignored) {

                }
            }
        }
        int count = 0;
        for (Method method : cls.getMethods()) {
            if (method.getName().equals(key)) {
                count++;
            }
        }
        if(count < 0){
            return null;
        }else if(count == 1){
            for (Method method : cls.getMethods()) {
                if (method.getName().equals(key)) return method;
            }
        }else{
            Method[] methods = new Method[count];
            int idx = 0;
            for (Method method : cls.getMethods()) {
                if (method.getName().equals(key)){
                    methods[idx] = method;
                    idx++;
                }
            }
            return methods;
        }
        error("unexpect index operation : " + toString(module) + " [" + toString(key) + "]");
        return null;
    }

    public static void put(Object container, Object key, Object value){
        if(container instanceof LuaTable){
            LuaTable table = (LuaTable) container;
            if(table.containsKey(key)){
                table.put(key, value);
                return;
            }
            // try use '__newindex' meta
            final Object meta = Lua.index(table.meta, Name.NEW_INDEX);
            if(meta instanceof LuaTable){
                Lua.put(meta, key, value);
            }else if(meta instanceof LuaFunc) {
                Lua.call(meta, new Object[]{container, key, value});
            }else{
                table.put(key, value);
            }
            return;
        }
        error("unexpected field put : " + toString(container) + " [" + toString(key) + "]" + " = " + toString(value));
    }

    // entrance for ANY method invocation
    public static Object call(Object fn, Object[] args){
        Object res = null;
        if(fn instanceof LuaModFunc){
            // have inner flatten
            res = ((LuaModFunc)fn).call(args);
        }else if(fn instanceof LuaFunc){
            args = flatten(args, -1);
            res = ((LuaFunc) fn).call(args);
        }else if(fn instanceof LuaTable){
            final Object func = Lua.index(((LuaTable) fn).meta, Name.CALL);
            if(func != null){
                res = Lua.call(func, args);
            }else{
                error("table : " + toString(fn) + " is not callable");
            }
        }else if (fn instanceof Method) {
            // method invoke
            args = flatten(args, -1);
            Method m = (Method) fn;
            try{
                if (Modifier.isStatic(m.getModifiers())) {
                    res = m.invoke(null, args);
                }else{
                    res = m.invoke(args[0], slice(args,1,-1));
                }
            }catch (Exception e){
                throw new LuaRuntimeException(e);
            }
        }else if (fn instanceof Method[]){
            // overloaded method
            args = flatten(args, -1);
            Method[] methods = (Method[]) fn;
            Method targetMethod = null;
            boolean isStatic = false;
            for (Method method : methods) {
                final Class<?>[] types = method.getParameterTypes();
                if(Modifier.isStatic(method.getModifiers())){
                    if(types.length != args.length) continue;
                    boolean match = true;
                    for (int i = 0; i < args.length; i++) {
                        if(!matchType(args[i].getClass(), types[i])){
                            match = false;
                            break;
                        }
                    }
                    if(match){
                        targetMethod = method;
                        isStatic = true;
                        final Class<?>[] paramTypes = targetMethod.getParameterTypes();
                        for (int i = 0; i < args.length; i++) {
                            if(args[i] instanceof Number){
                                args[i] = castNumType((Number) args[i], paramTypes[i]);
                            }
                        }
                        break;
                    }
                }else{
                    if(types.length != args.length-1){
                        continue;
                    }
                    boolean match = true;
                    for (int i = 1; i < args.length; i++) {
                        if(!matchType(args[i].getClass(), types[i-1])){
                            match = false;
                            break;
                        }
                    }
                    if(match){
                        targetMethod = method;
                        final Class<?>[] paramTypes = targetMethod.getParameterTypes();
                        for (int i = 1; i < args.length; i++) {
                            if(args[i] instanceof Number){
                                args[i] = castNumType((Number) args[i], paramTypes[i-1]);
                            }
                        }
                        break;
                    }
                }
            }
            try {
                if(targetMethod != null && isStatic){
                    res = targetMethod.invoke(null, args);
                }else if(targetMethod != null){
                    res = targetMethod.invoke(args[0], slice(args, 1,-1));
                }else{
                    errorArgTypes(methods, args);
                }
            } catch (Exception e){
                throw new LuaRuntimeException(e);
            }
        }else if (fn instanceof Class<?>) {
            // constructor
            args = flatten(args, -1);
            Class<?> cls = ((Class<?>) fn);
            final Constructor<?>[] cons = cls.getDeclaredConstructors();
            Constructor<?> targetCon = null;
            for (Constructor<?> con : cons) {
                final Class<?>[] types = con.getParameterTypes();
                if(args.length != types.length) continue;
                boolean match = true;
                for (int i = 0; i < args.length; i++) {
                    if(!matchType(args[i].getClass(), types[i])){
                        match = false;
                        break;
                    }
                }
                if(match){
                    targetCon = con;
                    final Class<?>[] paramTypes = targetCon.getParameterTypes();
                    for (int i = 0; i < args.length; i++) {
                        if(args[i] instanceof Number){
                            args[i] = castNumType((Number) args[i], paramTypes[i]);
                        }
                    }
                }
            }
            if (targetCon != null){
                try {
                    res = targetCon.newInstance(args);
                } catch (Exception e) {
                    throw new LuaRuntimeException(e);
                }
            }else{
                errorArgTypes(cons, args);
            }
        }else{
            error(toString(fn) + " is not callable with arg types : " + toString(args));
        }
        if(res instanceof Object[] && ((Object[]) res).length == 1){
            return ((Object[])res)[0];
        }
        return res;
    }

    private static final Object[] emptyArray = new Object[0];
    public static Object[] flatten(Object[] args, int needLen){
        if(args == null){
            args = emptyArray;
        }
        // has no length limitation
        if(needLen < 0){
            for (int i = 0; i < args.length-1; i++) {
                if(args[i] instanceof Object[]){
                    Object[] arr = (Object[]) args[i];
                    args[i] = arr.length == 0 ? null : arr[0];
                }
            }
            if(args.length > 0 && args[args.length-1] instanceof Object[]){
                Object[] arr = (Object[]) args[args.length-1];
                if(arr.length <= 1){
                    args[args.length-1] = arr.length == 0 ? null : arr[0];
                    return args;
                }
                // unfold the last array
                Object[] rets = new Object[args.length - 1 + arr.length];
                System.arraycopy(args, 0, rets, 0, args.length - 1);
                System.arraycopy(arr, 0, rets, args.length-1, arr.length);
                return rets;
            }
            return args;
        }
        // have 'needLen' - 10001 normal arg and 1 varArg
        if(needLen >= 10000){
            needLen -= 10000;
            Object[] rets = new Object[needLen];
            if (args.length < needLen){
                for (int i = 0; i < args.length-1; i++) {
                    if(args[i] instanceof Object[]){
                        Object[] arr = (Object[]) args[i];
                        args[i] = arr.length == 0 ? null : arr[0];
                    }
                }
                System.arraycopy(args, 0, rets, 0, args.length);
                rets[rets.length-1] = emptyArray;
                return rets;
            }
            for (int i = 0; i < needLen - 1; i++) {
                if(args[i] instanceof Object[]){
                    Object[] arr = (Object[]) args[i];
                    rets[i] = arr.length == 0 ? null : arr[0];
                }else{
                    rets[i] = args[i];
                }
            }
            Object[] varargs = new Object[args.length + 1 - needLen];
            int varargIdx = 0;
            for (int i = needLen-1; i < args.length; i++) {
                if(args[i] instanceof Object[]){
                    Object[] arr = (Object[]) args[i];
                    varargs[varargIdx] = arr.length == 0 ? null : arr[0];
                }else{
                    varargs[varargIdx] = args[i];
                }
                varargIdx++;
            }
            rets[needLen-1] = varargs;
            return rets;
        }
        Object[] rets = new Object[needLen];
        int limit = Math.min(needLen, args.length);
        for (int i = 0; i < limit; i++) {
            if(args[i] instanceof Object[]){
                Object[] arr = (Object[]) args[i];
                rets[i] = arr.length == 0 ? null : arr[0];
            }else{
                rets[i] = args[i];
            }
        }
        return rets;
    }

    public static class Name {
        public static final String ADD = "__add";
        public static final String SUB = "__sub";
        public static final String MUL = "__mul";
        public static final String DIV = "__div";
        public static final String MOD = "__mod";
        public static final String POW = "__pow";
        public static final String UNM = "__unm";
        public static final String IDIV = "__idiv";
        public static final String BAND = "__band";
        public static final String BOR = "__bor";
        public static final String BXOR = "__bxor";
        public static final String BNOT = "__bnot";
        public static final String SHL = "__shl";
        public static final String SHR = "__shr";
        public static final String CONCAT = "__concat";
        public static final String LEN = "__len";
        public static final String EQ = "__eq";
        public static final String LT = "__lt";
        public static final String LE = "__le";
        public static final String INDEX = "__index";
        public static final String NEW_INDEX = "__newindex";
        public static final String CALL = "__call";

        // Follows are also meta key, but not as operator
        public static final String GC = "__gc";
        public static final String CLOSE = "__close";
        public static final String MODE = "__mode";
        public static final String NAME = "__name";

        public static final String TO_STR = "__tostring";
    }

    @Contract("_, _, _ -> fail")
    private static void errorBinOp(String op, Object left, Object right) throws LuaRuntimeException{
        throw new LuaRuntimeException("Unexpected binary '" + op + "' operation between " + left + " and " + right);
    }

    @Contract("_, _ -> fail")
    private static void errorUnaryOp(String op, Object obj) throws LuaRuntimeException{
        throw new LuaRuntimeException("Unexpected unary '" + op + "' operation for " + obj);
    }

    @Contract("_, _ -> fail")
    private static void errorArgTypes(Executable[] fns, Object[] args) throws LuaRuntimeException{
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fns.length; i++) {
            sb.append(i).append(" : ").append(fns[i].toGenericString()).append("\n");
        }
        throw new LuaRuntimeException("incorrect param, only have some method(s) as follows : \n" + sb);
    }

    @Contract("_ -> fail")
    private static void error(String msg){
        throw new LuaRuntimeException(msg);
    }

    public static Object[] slice(Object[] array, int from, int to){
        if(to == -1) to = array.length;
        Object[] res = new Object[to - from];
        int idx = 0;
        for (int i = from; i < to; i++) {
            res[idx] = array[i];
            idx++;
        }
        return res;
    }

    private static boolean matchType(Class<?> src, Class<?> target){
        //        System.out.println(src.getName() + (res ? " == " : " != ") + target.getName());
        return src == target
                || target.isAssignableFrom(src)
                || (src == Boolean.class && target == boolean.class)
                || (isNumber(src) && isNumber(target));
    }

    private static boolean isNumber(Class<?> cls){
        return cls == Number.class
                || Number.class.isAssignableFrom(cls)
                || cls == int.class
                || cls == long.class
                || cls == double.class
                || cls == float.class
                || cls == short.class;
    }

    private static Number castNumType(Number num, Class<?> type){
        if(type == int.class || type == Integer.class){
            return num.intValue();
        }
        if(type == long.class || type == Long.class){
            return num.longValue();
        }
        if(type == double.class || type == Double.class){
            return num.doubleValue();
        }
        if(type == float.class || type == Float.class){
            return num.floatValue();
        }
        if(type == short.class || type == Short.class){
            return num.shortValue();
        }
        return num.byteValue();
    }

    @Nullable
    public static Number toNumber(Object s){
        if(s == null) return null;
        if(s instanceof Number){
            return (Number) s;
        }
        if(s instanceof String){
            try{
                return Long.valueOf((String) s);
            }catch (Exception e0){
                try {
                    return Double.valueOf((String) s);
                }catch (Exception e1){
                    return null;
                }
            }
        }
        return null;
    }

    @NotNull
    public static String toString(Object obj){
        if(obj == null) return "nil";
        if(obj instanceof LuaTable){
            LuaTable table = (LuaTable) obj;
            try {
                Object fn = Lua.index(table, Name.TO_STR);
                if(fn != null) {
                    return Lua.call(fn, new Object[]{obj}).toString();
                }
            } catch (Exception ignored) {}
        }
        if(obj instanceof LuaJKFunc){
            return "LuaJavaFunc$" + obj.hashCode();
        }
        if(obj instanceof Method){
            return "JavaFunc(" + ((Method)obj).toGenericString() + ")";
        }
        if(obj instanceof Method[]){
            Method[] methods = (Method[]) obj;
            StringBuilder sb = new StringBuilder("JavaFunc(");
            for (Method method : methods) {
                sb.append(method.toGenericString());
                sb.append(";");
            }
            sb.append(")");
            return sb.toString();
        }
        return obj.toString();
    }

    @NotNull
    public static String toString(Object[] objs){
        objs = flatten(objs, -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length-1; i++) {
            sb.append(toString(objs[i]));
            sb.append(", ");
        }
        sb.append(toString(objs[objs.length-1]));
        return sb.toString();
    }
}
