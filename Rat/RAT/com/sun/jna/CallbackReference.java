//Raddon On Top!

package com.sun.jna;

import java.io.*;
import java.lang.ref.*;
import com.sun.jna.internal.*;
import java.util.concurrent.*;
import java.util.*;
import java.lang.reflect.*;

public class CallbackReference extends WeakReference<Callback> implements Closeable
{
    static final Map<Callback, CallbackReference> callbackMap;
    static final Map<Callback, CallbackReference> directCallbackMap;
    static final Map<Pointer, Reference<Callback>[]> pointerCallbackMap;
    static final Map<Object, Object> allocations;
    private static final Map<Long, Reference<CallbackReference>> allocatedMemory;
    private static final Method PROXY_CALLBACK_METHOD;
    private static final Class<?> DLL_CALLBACK_CLASS;
    private static final Map<Callback, CallbackThreadInitializer> initializers;
    Cleaner.Cleanable cleanable;
    Pointer cbstruct;
    Pointer trampoline;
    CallbackProxy proxy;
    Method method;
    int callingConvention;
    
    static CallbackThreadInitializer setCallbackThreadInitializer(final Callback cb, final CallbackThreadInitializer initializer) {
        synchronized (CallbackReference.initializers) {
            if (initializer != null) {
                return CallbackReference.initializers.put(cb, initializer);
            }
            return CallbackReference.initializers.remove(cb);
        }
    }
    
    private static ThreadGroup initializeThread(Callback cb, final AttachOptions args) {
        CallbackThreadInitializer init = null;
        if (cb instanceof DefaultCallbackProxy) {
            cb = ((DefaultCallbackProxy)cb).getCallback();
        }
        synchronized (CallbackReference.initializers) {
            init = CallbackReference.initializers.get(cb);
        }
        ThreadGroup group = null;
        if (init != null) {
            group = init.getThreadGroup(cb);
            args.name = init.getName(cb);
            args.daemon = init.isDaemon(cb);
            args.detach = init.detach(cb);
            args.write();
        }
        return group;
    }
    
    public static Callback getCallback(final Class<?> type, final Pointer p) {
        return getCallback(type, p, false);
    }
    
    private static Callback getCallback(final Class<?> type, final Pointer p, final boolean direct) {
        if (p == null) {
            return null;
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Callback type must be an interface");
        }
        final Map<Callback, CallbackReference> map = direct ? CallbackReference.directCallbackMap : CallbackReference.callbackMap;
        synchronized (CallbackReference.pointerCallbackMap) {
            final Reference<Callback>[] array = CallbackReference.pointerCallbackMap.get(p);
            Callback cb = getTypeAssignableCallback(type, array);
            if (cb != null) {
                return cb;
            }
            cb = createCallback(type, p);
            CallbackReference.pointerCallbackMap.put(p, addCallbackToArray(cb, array));
            map.remove(cb);
            return cb;
        }
    }
    
    private static Callback getTypeAssignableCallback(final Class<?> type, final Reference<Callback>[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                final Callback cb = array[i].get();
                if (cb != null && type.isAssignableFrom(cb.getClass())) {
                    return cb;
                }
            }
        }
        return null;
    }
    
    private static Reference<Callback>[] addCallbackToArray(final Callback cb, final Reference<Callback>[] array) {
        int reqArraySize = 1;
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i].get() == null) {
                    array[i] = null;
                }
                else {
                    ++reqArraySize;
                }
            }
        }
        final Reference<Callback>[] newArray = (Reference<Callback>[])new Reference[reqArraySize];
        int nidx = 0;
        if (array != null) {
            for (int j = 0; j < array.length; ++j) {
                if (array[j] != null) {
                    newArray[nidx++] = array[j];
                }
            }
        }
        newArray[nidx] = new WeakReference<Callback>(cb);
        return newArray;
    }
    
    private static Callback createCallback(final Class<?> type, final Pointer p) {
        final int ctype = AltCallingConvention.class.isAssignableFrom(type) ? 63 : 0;
        final Map<String, Object> foptions = new HashMap<String, Object>(Native.getLibraryOptions(type));
        foptions.put("invoking-method", getCallbackMethod(type));
        final NativeFunctionHandler h = new NativeFunctionHandler(p, ctype, foptions);
        return (Callback)Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, h);
    }
    
    private CallbackReference(final Callback callback, final int callingConvention, boolean direct) {
        super(callback);
        final TypeMapper mapper = Native.getTypeMapper(callback.getClass());
        this.callingConvention = callingConvention;
        final boolean ppc = Platform.isPPC();
        if (direct) {
            final Method m = getCallbackMethod(callback);
            final Class<?>[] ptypes = m.getParameterTypes();
            for (int i = 0; i < ptypes.length; ++i) {
                if (ppc && (ptypes[i] == Float.TYPE || ptypes[i] == Double.TYPE)) {
                    direct = false;
                    break;
                }
                if (mapper != null && mapper.getFromNativeConverter(ptypes[i]) != null) {
                    direct = false;
                    break;
                }
            }
            if (mapper != null && mapper.getToNativeConverter(m.getReturnType()) != null) {
                direct = false;
            }
        }
        final String encoding = Native.getStringEncoding(callback.getClass());
        long peer = 0L;
        if (direct) {
            this.method = getCallbackMethod(callback);
            final Class<?>[] nativeParamTypes = this.method.getParameterTypes();
            final Class<?> returnType = this.method.getReturnType();
            int flags = 1;
            if (CallbackReference.DLL_CALLBACK_CLASS != null && CallbackReference.DLL_CALLBACK_CLASS.isInstance(callback)) {
                flags |= 0x2;
            }
            peer = Native.createNativeCallback(callback, this.method, nativeParamTypes, returnType, callingConvention, flags, encoding);
        }
        else {
            if (callback instanceof CallbackProxy) {
                this.proxy = (CallbackProxy)callback;
            }
            else {
                this.proxy = (CallbackProxy)new DefaultCallbackProxy(getCallbackMethod(callback), mapper, encoding);
            }
            final Class<?>[] nativeParamTypes = (Class<?>[])this.proxy.getParameterTypes();
            Class<?> returnType = (Class<?>)this.proxy.getReturnType();
            if (mapper != null) {
                for (int j = 0; j < nativeParamTypes.length; ++j) {
                    final FromNativeConverter rc = mapper.getFromNativeConverter(nativeParamTypes[j]);
                    if (rc != null) {
                        nativeParamTypes[j] = rc.nativeType();
                    }
                }
                final ToNativeConverter tn = mapper.getToNativeConverter(returnType);
                if (tn != null) {
                    returnType = tn.nativeType();
                }
            }
            for (int j = 0; j < nativeParamTypes.length; ++j) {
                nativeParamTypes[j] = this.getNativeType(nativeParamTypes[j]);
                if (!isAllowableNativeType(nativeParamTypes[j])) {
                    final String msg = "Callback argument " + nativeParamTypes[j] + " requires custom type conversion";
                    throw new IllegalArgumentException(msg);
                }
            }
            returnType = this.getNativeType(returnType);
            if (!isAllowableNativeType(returnType)) {
                final String msg2 = "Callback return type " + returnType + " requires custom type conversion";
                throw new IllegalArgumentException(msg2);
            }
            final int flags = (CallbackReference.DLL_CALLBACK_CLASS != null && CallbackReference.DLL_CALLBACK_CLASS.isInstance(callback)) ? 2 : 0;
            peer = Native.createNativeCallback((Callback)this.proxy, CallbackReference.PROXY_CALLBACK_METHOD, nativeParamTypes, returnType, callingConvention, flags, encoding);
        }
        this.cbstruct = ((peer != 0L) ? new Pointer(peer) : null);
        if (peer != 0L) {
            CallbackReference.allocatedMemory.put(peer, new WeakReference<CallbackReference>(this));
            this.cleanable = Cleaner.getCleaner().register(this, new CallbackReferenceDisposer(this.cbstruct));
        }
    }
    
    private Class<?> getNativeType(final Class<?> cls) {
        if (Structure.class.isAssignableFrom(cls)) {
            Structure.validate((Class<? extends Structure>)cls);
            if (!Structure.ByValue.class.isAssignableFrom(cls)) {
                return Pointer.class;
            }
        }
        else {
            if (NativeMapped.class.isAssignableFrom(cls)) {
                return NativeMappedConverter.getInstance(cls).nativeType();
            }
            if (cls == String.class || cls == WString.class || cls == String[].class || cls == WString[].class || Callback.class.isAssignableFrom(cls)) {
                return Pointer.class;
            }
        }
        return cls;
    }
    
    private static Method checkMethod(final Method m) {
        if (m.getParameterTypes().length > 256) {
            final String msg = "Method signature exceeds the maximum parameter count: " + m;
            throw new UnsupportedOperationException(msg);
        }
        return m;
    }
    
    static Class<?> findCallbackClass(final Class<?> type) {
        if (!Callback.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
        }
        if (type.isInterface()) {
            return type;
        }
        final Class<?>[] ifaces = type.getInterfaces();
        for (int i = 0; i < ifaces.length; ++i) {
            if (Callback.class.isAssignableFrom(ifaces[i])) {
                try {
                    getCallbackMethod(ifaces[i]);
                    return ifaces[i];
                }
                catch (IllegalArgumentException e) {
                    break;
                }
            }
        }
        if (Callback.class.isAssignableFrom(type.getSuperclass())) {
            return findCallbackClass(type.getSuperclass());
        }
        return type;
    }
    
    private static Method getCallbackMethod(final Callback callback) {
        return getCallbackMethod(findCallbackClass(callback.getClass()));
    }
    
    private static Method getCallbackMethod(final Class<?> cls) {
        final Method[] pubMethods = cls.getDeclaredMethods();
        final Method[] classMethods = cls.getMethods();
        final Set<Method> pmethods = new HashSet<Method>(Arrays.asList(pubMethods));
        pmethods.retainAll(Arrays.asList(classMethods));
        final Iterator<Method> i = pmethods.iterator();
        while (i.hasNext()) {
            final Method m = i.next();
            if (Callback.FORBIDDEN_NAMES.contains(m.getName())) {
                i.remove();
            }
        }
        final Method[] methods = pmethods.toArray(new Method[0]);
        if (methods.length == 1) {
            return checkMethod(methods[0]);
        }
        for (int j = 0; j < methods.length; ++j) {
            final Method k = methods[j];
            if ("callback".equals(k.getName())) {
                return checkMethod(k);
            }
        }
        final String msg = "Callback must implement a single public method, or one public method named 'callback'";
        throw new IllegalArgumentException(msg);
    }
    
    private void setCallbackOptions(final int options) {
        this.cbstruct.setInt(Native.POINTER_SIZE, options);
    }
    
    public Pointer getTrampoline() {
        if (this.trampoline == null) {
            this.trampoline = this.cbstruct.getPointer(0L);
        }
        return this.trampoline;
    }
    
    @Override
    public void close() {
        if (this.cleanable != null) {
            this.cleanable.clean();
        }
        this.cbstruct = null;
    }
    
    @Deprecated
    protected void dispose() {
        this.close();
    }
    
    static void disposeAll() {
        final Collection<Reference<CallbackReference>> refs = new LinkedList<Reference<CallbackReference>>(CallbackReference.allocatedMemory.values());
        for (final Reference<CallbackReference> r : refs) {
            final CallbackReference ref = r.get();
            if (ref != null) {
                ref.close();
            }
        }
    }
    
    private Callback getCallback() {
        return this.get();
    }
    
    private static Pointer getNativeFunctionPointer(final Callback cb) {
        if (Proxy.isProxyClass(cb.getClass())) {
            final Object handler = Proxy.getInvocationHandler(cb);
            if (handler instanceof NativeFunctionHandler) {
                return ((NativeFunctionHandler)handler).getPointer();
            }
        }
        return null;
    }
    
    public static Pointer getFunctionPointer(final Callback cb) {
        return getFunctionPointer(cb, false);
    }
    
    private static Pointer getFunctionPointer(final Callback cb, final boolean direct) {
        Pointer fp = null;
        if (cb == null) {
            return null;
        }
        if ((fp = getNativeFunctionPointer(cb)) != null) {
            return fp;
        }
        final Map<String, ?> options = Native.getLibraryOptions(cb.getClass());
        final int callingConvention = (int)((cb instanceof AltCallingConvention) ? 63 : ((options != null && options.containsKey("calling-convention")) ? options.get("calling-convention") : 0));
        final Map<Callback, CallbackReference> map = direct ? CallbackReference.directCallbackMap : CallbackReference.callbackMap;
        synchronized (CallbackReference.pointerCallbackMap) {
            CallbackReference cbref = map.get(cb);
            if (cbref == null) {
                cbref = new CallbackReference(cb, callingConvention, direct);
                map.put(cb, cbref);
                CallbackReference.pointerCallbackMap.put(cbref.getTrampoline(), addCallbackToArray(cb, null));
                if (CallbackReference.initializers.containsKey(cb)) {
                    cbref.setCallbackOptions(1);
                }
            }
            return cbref.getTrampoline();
        }
    }
    
    private static boolean isAllowableNativeType(final Class<?> cls) {
        return cls == Void.TYPE || cls == Void.class || cls == Boolean.TYPE || cls == Boolean.class || cls == Byte.TYPE || cls == Byte.class || cls == Short.TYPE || cls == Short.class || cls == Character.TYPE || cls == Character.class || cls == Integer.TYPE || cls == Integer.class || cls == Long.TYPE || cls == Long.class || cls == Float.TYPE || cls == Float.class || cls == Double.TYPE || cls == Double.class || (Structure.ByValue.class.isAssignableFrom(cls) && Structure.class.isAssignableFrom(cls)) || Pointer.class.isAssignableFrom(cls);
    }
    
    private static Pointer getNativeString(final Object value, final boolean wide) {
        if (value != null) {
            final NativeString ns = new NativeString(value.toString(), wide);
            CallbackReference.allocations.put(value, ns);
            return ns.getPointer();
        }
        return null;
    }
    
    static {
        callbackMap = new WeakHashMap<Callback, CallbackReference>();
        directCallbackMap = new WeakHashMap<Callback, CallbackReference>();
        pointerCallbackMap = new WeakHashMap<Pointer, Reference<Callback>[]>();
        allocations = Collections.synchronizedMap(new WeakHashMap<Object, Object>());
        allocatedMemory = new ConcurrentHashMap<Long, Reference<CallbackReference>>();
        try {
            PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", Object[].class);
        }
        catch (Exception e2) {
            throw new Error("Error looking up CallbackProxy.callback() method");
        }
        Label_0119: {
            if (Platform.isWindows()) {
                try {
                    DLL_CALLBACK_CLASS = Class.forName("com.sun.jna.win32.DLLCallback");
                    break Label_0119;
                }
                catch (ClassNotFoundException e) {
                    throw new Error("Error loading DLLCallback class", e);
                }
            }
            DLL_CALLBACK_CLASS = null;
        }
        initializers = new WeakHashMap<Callback, CallbackThreadInitializer>();
    }
    
    static class AttachOptions extends Structure
    {
        public static final List<String> FIELDS;
        public boolean daemon;
        public boolean detach;
        public String name;
        
        AttachOptions() {
            this.setStringEncoding("utf8");
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return AttachOptions.FIELDS;
        }
        
        static {
            FIELDS = Structure.createFieldsOrder("daemon", "detach", "name");
        }
    }
    
    private class DefaultCallbackProxy implements CallbackProxy
    {
        private final Method callbackMethod;
        private ToNativeConverter toNative;
        private final FromNativeConverter[] fromNative;
        private final String encoding;
        
        public DefaultCallbackProxy(final Method callbackMethod, final TypeMapper mapper, final String encoding) {
            this.callbackMethod = callbackMethod;
            this.encoding = encoding;
            final Class<?>[] argTypes = callbackMethod.getParameterTypes();
            final Class<?> returnType = callbackMethod.getReturnType();
            this.fromNative = new FromNativeConverter[argTypes.length];
            if (NativeMapped.class.isAssignableFrom(returnType)) {
                this.toNative = NativeMappedConverter.getInstance(returnType);
            }
            else if (mapper != null) {
                this.toNative = mapper.getToNativeConverter(returnType);
            }
            for (int i = 0; i < this.fromNative.length; ++i) {
                if (NativeMapped.class.isAssignableFrom(argTypes[i])) {
                    this.fromNative[i] = new NativeMappedConverter(argTypes[i]);
                }
                else if (mapper != null) {
                    this.fromNative[i] = mapper.getFromNativeConverter(argTypes[i]);
                }
            }
            if (!callbackMethod.isAccessible()) {
                try {
                    callbackMethod.setAccessible(true);
                }
                catch (SecurityException e) {
                    throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + callbackMethod);
                }
            }
        }
        
        public Callback getCallback() {
            return CallbackReference.this.getCallback();
        }
        
        private Object invokeCallback(final Object[] args) {
            final Class<?>[] paramTypes = this.callbackMethod.getParameterTypes();
            final Object[] callbackArgs = new Object[args.length];
            for (int i = 0; i < args.length; ++i) {
                final Class<?> type = paramTypes[i];
                final Object arg = args[i];
                if (this.fromNative[i] != null) {
                    final FromNativeContext context = (FromNativeContext)new CallbackParameterContext((Class)type, this.callbackMethod, args, i);
                    callbackArgs[i] = this.fromNative[i].fromNative(arg, context);
                }
                else {
                    callbackArgs[i] = this.convertArgument(arg, type);
                }
            }
            Object result = null;
            final Callback cb = this.getCallback();
            if (cb != null) {
                try {
                    result = this.convertResult(this.callbackMethod.invoke(cb, callbackArgs));
                }
                catch (IllegalArgumentException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, (Throwable)e);
                }
                catch (IllegalAccessException e2) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, (Throwable)e2);
                }
                catch (InvocationTargetException e3) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e3.getTargetException());
                }
            }
            for (int j = 0; j < callbackArgs.length; ++j) {
                if (callbackArgs[j] instanceof Structure && !(callbackArgs[j] instanceof Structure.ByValue)) {
                    ((Structure)callbackArgs[j]).autoWrite();
                }
            }
            return result;
        }
        
        public Object callback(final Object[] args) {
            try {
                return this.invokeCallback(args);
            }
            catch (Throwable t) {
                Native.getCallbackExceptionHandler().uncaughtException(this.getCallback(), t);
                return null;
            }
        }
        
        private Object convertArgument(Object value, final Class<?> dstType) {
            if (value instanceof Pointer) {
                if (dstType == String.class) {
                    value = ((Pointer)value).getString(0L, this.encoding);
                }
                else if (dstType == WString.class) {
                    value = new WString(((Pointer)value).getWideString(0L));
                }
                else if (dstType == String[].class) {
                    value = ((Pointer)value).getStringArray(0L, this.encoding);
                }
                else if (dstType == WString[].class) {
                    value = ((Pointer)value).getWideStringArray(0L);
                }
                else if (Callback.class.isAssignableFrom(dstType)) {
                    value = CallbackReference.getCallback(dstType, (Pointer)value);
                }
                else if (Structure.class.isAssignableFrom(dstType)) {
                    if (Structure.ByValue.class.isAssignableFrom(dstType)) {
                        final Structure s = Structure.newInstance(dstType);
                        final byte[] buf = new byte[s.size()];
                        ((Pointer)value).read(0L, buf, 0, buf.length);
                        s.getPointer().write(0L, buf, 0, buf.length);
                        s.read();
                        value = s;
                    }
                    else {
                        final Structure s = Structure.newInstance(dstType, (Pointer)value);
                        s.conditionalAutoRead();
                        value = s;
                    }
                }
            }
            else if ((Boolean.TYPE == dstType || Boolean.class == dstType) && value instanceof Number) {
                value = Function.valueOf(((Number)value).intValue() != 0);
            }
            return value;
        }
        
        private Object convertResult(Object value) {
            if (this.toNative != null) {
                value = this.toNative.toNative(value, new CallbackResultContext(this.callbackMethod));
            }
            if (value == null) {
                return null;
            }
            final Class<?> cls = value.getClass();
            if (Structure.class.isAssignableFrom(cls)) {
                if (Structure.ByValue.class.isAssignableFrom(cls)) {
                    return value;
                }
                return ((Structure)value).getPointer();
            }
            else {
                if (cls == Boolean.TYPE || cls == Boolean.class) {
                    return Boolean.TRUE.equals(value) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
                }
                if (cls == String.class || cls == WString.class) {
                    return getNativeString(value, cls == WString.class);
                }
                if (cls == String[].class || cls == WString[].class) {
                    final StringArray sa = (cls == String[].class) ? new StringArray((String[])value, this.encoding) : new StringArray((WString[])value);
                    CallbackReference.allocations.put(value, sa);
                    return sa;
                }
                if (Callback.class.isAssignableFrom(cls)) {
                    return CallbackReference.getFunctionPointer((Callback)value);
                }
                return value;
            }
        }
        
        public Class<?>[] getParameterTypes() {
            return this.callbackMethod.getParameterTypes();
        }
        
        public Class<?> getReturnType() {
            return this.callbackMethod.getReturnType();
        }
    }
    
    private static class NativeFunctionHandler implements InvocationHandler
    {
        private final Function function;
        private final Map<String, ?> options;
        
        public NativeFunctionHandler(final Pointer address, final int callingConvention, final Map<String, ?> options) {
            this.options = options;
            this.function = new Function(address, callingConvention, (String)options.get("string-encoding"));
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, Object[] args) throws Throwable {
            if (Library.Handler.OBJECT_TOSTRING.equals(method)) {
                String str = "Proxy interface to " + this.function;
                final Method m = (Method)this.options.get("invoking-method");
                final Class<?> cls = CallbackReference.findCallbackClass(m.getDeclaringClass());
                str = str + " (" + cls.getName() + ")";
                return str;
            }
            if (Library.Handler.OBJECT_HASHCODE.equals(method)) {
                return this.hashCode();
            }
            if (!Library.Handler.OBJECT_EQUALS.equals(method)) {
                if (Function.isVarArgs(method)) {
                    args = Function.concatenateVarArgs(args);
                }
                return this.function.invoke(method.getReturnType(), args, this.options);
            }
            final Object o = args[0];
            if (o != null && Proxy.isProxyClass(o.getClass())) {
                return Function.valueOf(Proxy.getInvocationHandler(o) == this);
            }
            return Boolean.FALSE;
        }
        
        public Pointer getPointer() {
            return this.function;
        }
    }
    
    private static final class CallbackReferenceDisposer implements Runnable
    {
        private Pointer cbstruct;
        
        public CallbackReferenceDisposer(final Pointer cbstruct) {
            this.cbstruct = cbstruct;
        }
        
        @Override
        public synchronized void run() {
            if (this.cbstruct != null) {
                try {
                    Native.freeNativeCallback(this.cbstruct.peer);
                }
                finally {
                    CallbackReference.allocatedMemory.remove(this.cbstruct.peer);
                    this.cbstruct.peer = 0L;
                    this.cbstruct = null;
                }
            }
        }
    }
}
