//Raddon On Top!

package com.sun.jna;

import java.util.*;
import java.lang.reflect.*;

public class Function extends Pointer
{
    public static final int MAX_NARGS = 256;
    public static final int C_CONVENTION = 0;
    public static final int ALT_CONVENTION = 63;
    private static final int MASK_CC = 63;
    public static final int THROW_LAST_ERROR = 64;
    public static final int USE_VARARGS = 384;
    static final Integer INTEGER_TRUE;
    static final Integer INTEGER_FALSE;
    private NativeLibrary library;
    private final String functionName;
    final String encoding;
    final int callFlags;
    final Map<String, ?> options;
    static final String OPTION_INVOKING_METHOD = "invoking-method";
    private static final VarArgsChecker IS_VARARGS;
    
    public static Function getFunction(final String libraryName, final String functionName) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName);
    }
    
    public static Function getFunction(final String libraryName, final String functionName, final int callFlags) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags, null);
    }
    
    public static Function getFunction(final String libraryName, final String functionName, final int callFlags, final String encoding) {
        return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags, encoding);
    }
    
    public static Function getFunction(final Pointer p) {
        return getFunction(p, 0, null);
    }
    
    public static Function getFunction(final Pointer p, final int callFlags) {
        return getFunction(p, callFlags, null);
    }
    
    public static Function getFunction(final Pointer p, final int callFlags, final String encoding) {
        return new Function(p, callFlags, encoding);
    }
    
    Function(final NativeLibrary library, final String functionName, final int callFlags, final String encoding) {
        this.checkCallingConvention(callFlags & 0x3F);
        if (functionName == null) {
            throw new NullPointerException("Function name must not be null");
        }
        this.library = library;
        this.functionName = functionName;
        this.callFlags = callFlags;
        this.options = library.options;
        this.encoding = ((encoding != null) ? encoding : Native.getDefaultStringEncoding());
        try {
            this.peer = library.getSymbolAddress(functionName);
        }
        catch (UnsatisfiedLinkError e) {
            throw new UnsatisfiedLinkError("Error looking up function '" + functionName + "': " + e.getMessage());
        }
    }
    
    Function(final Pointer functionAddress, final int callFlags, final String encoding) {
        this.checkCallingConvention(callFlags & 0x3F);
        if (functionAddress == null || functionAddress.peer == 0L) {
            throw new NullPointerException("Function address may not be null");
        }
        this.functionName = functionAddress.toString();
        this.callFlags = callFlags;
        this.peer = functionAddress.peer;
        this.options = (Map<String, ?>)Collections.EMPTY_MAP;
        this.encoding = ((encoding != null) ? encoding : Native.getDefaultStringEncoding());
    }
    
    private void checkCallingConvention(final int convention) throws IllegalArgumentException {
        if ((convention & 0x3F) != convention) {
            throw new IllegalArgumentException("Unrecognized calling convention: " + convention);
        }
    }
    
    public String getName() {
        return this.functionName;
    }
    
    public int getCallingConvention() {
        return this.callFlags & 0x3F;
    }
    
    public Object invoke(final Class<?> returnType, final Object[] inArgs) {
        return this.invoke(returnType, inArgs, this.options);
    }
    
    public Object invoke(final Class<?> returnType, final Object[] inArgs, final Map<String, ?> options) {
        final Method invokingMethod = (Method)options.get("invoking-method");
        final Class<?>[] paramTypes = (Class<?>[])((invokingMethod != null) ? invokingMethod.getParameterTypes() : null);
        return this.invoke(invokingMethod, paramTypes, returnType, inArgs, options);
    }
    
    Object invoke(final Method invokingMethod, final Class<?>[] paramTypes, final Class<?> returnType, final Object[] inArgs, final Map<String, ?> options) {
        Object[] args = new Object[0];
        if (inArgs != null) {
            if (inArgs.length > 256) {
                throw new UnsupportedOperationException("Maximum argument count is 256");
            }
            args = new Object[inArgs.length];
            System.arraycopy(inArgs, 0, args, 0, args.length);
        }
        final TypeMapper mapper = (TypeMapper)options.get("type-mapper");
        final boolean allowObjects = Boolean.TRUE.equals(options.get("allow-objects"));
        final boolean isVarArgs = args.length > 0 && invokingMethod != null && isVarArgs(invokingMethod);
        final int fixedArgs = (args.length > 0 && invokingMethod != null) ? fixedArgs(invokingMethod) : 0;
        for (int i = 0; i < args.length; ++i) {
            final Class<?> paramType = (invokingMethod != null) ? ((isVarArgs && i >= paramTypes.length - 1) ? paramTypes[paramTypes.length - 1].getComponentType() : paramTypes[i]) : null;
            args[i] = this.convertArgument(args, i, invokingMethod, mapper, allowObjects, paramType);
        }
        Class<?> nativeReturnType = returnType;
        FromNativeConverter resultConverter = null;
        if (NativeMapped.class.isAssignableFrom(returnType)) {
            final NativeMappedConverter tc = (NativeMappedConverter)(resultConverter = (FromNativeConverter)NativeMappedConverter.getInstance(returnType));
            nativeReturnType = tc.nativeType();
        }
        else if (mapper != null) {
            resultConverter = mapper.getFromNativeConverter(returnType);
            if (resultConverter != null) {
                nativeReturnType = (Class<?>)resultConverter.nativeType();
            }
        }
        Object result = this.invoke(args, nativeReturnType, allowObjects, fixedArgs);
        if (resultConverter != null) {
            FromNativeContext context;
            if (invokingMethod != null) {
                context = new MethodResultContext(returnType, this, inArgs, invokingMethod);
            }
            else {
                context = new FunctionResultContext(returnType, this, inArgs);
            }
            result = resultConverter.fromNative(result, context);
        }
        if (inArgs != null) {
            for (int j = 0; j < inArgs.length; ++j) {
                final Object inArg = inArgs[j];
                if (inArg != null) {
                    if (inArg instanceof Structure) {
                        if (!(inArg instanceof Structure.ByValue)) {
                            ((Structure)inArg).autoRead();
                        }
                    }
                    else if (args[j] instanceof PostCallRead) {
                        ((PostCallRead)args[j]).read();
                        if (args[j] instanceof PointerArray) {
                            final PointerArray array = (PointerArray)args[j];
                            if (Structure.ByReference[].class.isAssignableFrom(inArg.getClass())) {
                                final Class<? extends Structure> type = (Class<? extends Structure>)inArg.getClass().getComponentType();
                                final Structure[] ss = (Structure[])inArg;
                                for (int si = 0; si < ss.length; ++si) {
                                    final Pointer p = array.getPointer(Native.POINTER_SIZE * si);
                                    ss[si] = Structure.updateStructureByReference((Class<Structure>)type, ss[si], p);
                                }
                            }
                        }
                    }
                    else if (Structure[].class.isAssignableFrom(inArg.getClass())) {
                        Structure.autoRead((Structure[])inArg);
                    }
                }
            }
        }
        return result;
    }
    
    Object invoke(final Object[] args, final Class<?> returnType, final boolean allowObjects) {
        return this.invoke(args, returnType, allowObjects, 0);
    }
    
    Object invoke(final Object[] args, final Class<?> returnType, final boolean allowObjects, final int fixedArgs) {
        Object result = null;
        final int callFlags = this.callFlags | (fixedArgs & 0x3) << 7;
        if (returnType == null || returnType == Void.TYPE || returnType == Void.class) {
            Native.invokeVoid(this, this.peer, callFlags, args);
            result = null;
        }
        else if (returnType == Boolean.TYPE || returnType == Boolean.class) {
            result = valueOf(Native.invokeInt(this, this.peer, callFlags, args) != 0);
        }
        else if (returnType == Byte.TYPE || returnType == Byte.class) {
            result = (byte)Native.invokeInt(this, this.peer, callFlags, args);
        }
        else if (returnType == Short.TYPE || returnType == Short.class) {
            result = (short)Native.invokeInt(this, this.peer, callFlags, args);
        }
        else if (returnType == Character.TYPE || returnType == Character.class) {
            result = (char)Native.invokeInt(this, this.peer, callFlags, args);
        }
        else if (returnType == Integer.TYPE || returnType == Integer.class) {
            result = Native.invokeInt(this, this.peer, callFlags, args);
        }
        else if (returnType == Long.TYPE || returnType == Long.class) {
            result = Native.invokeLong(this, this.peer, callFlags, args);
        }
        else if (returnType == Float.TYPE || returnType == Float.class) {
            result = Native.invokeFloat(this, this.peer, callFlags, args);
        }
        else if (returnType == Double.TYPE || returnType == Double.class) {
            result = Native.invokeDouble(this, this.peer, callFlags, args);
        }
        else if (returnType == String.class) {
            result = this.invokeString(callFlags, args, false);
        }
        else if (returnType == WString.class) {
            final String s = this.invokeString(callFlags, args, true);
            if (s != null) {
                result = new WString(s);
            }
        }
        else {
            if (Pointer.class.isAssignableFrom(returnType)) {
                return this.invokePointer(callFlags, args);
            }
            if (Structure.class.isAssignableFrom(returnType)) {
                if (Structure.ByValue.class.isAssignableFrom(returnType)) {
                    final Structure s2 = Native.invokeStructure(this, this.peer, callFlags, args, Structure.newInstance(returnType));
                    s2.autoRead();
                    result = s2;
                }
                else {
                    result = this.invokePointer(callFlags, args);
                    if (result != null) {
                        final Structure s2 = Structure.newInstance(returnType, (Pointer)result);
                        s2.conditionalAutoRead();
                        result = s2;
                    }
                }
            }
            else if (Callback.class.isAssignableFrom(returnType)) {
                result = this.invokePointer(callFlags, args);
                if (result != null) {
                    result = CallbackReference.getCallback((Class)returnType, (Pointer)result);
                }
            }
            else if (returnType == String[].class) {
                final Pointer p = this.invokePointer(callFlags, args);
                if (p != null) {
                    result = p.getStringArray(0L, this.encoding);
                }
            }
            else if (returnType == WString[].class) {
                final Pointer p = this.invokePointer(callFlags, args);
                if (p != null) {
                    final String[] arr = p.getWideStringArray(0L);
                    final WString[] warr = new WString[arr.length];
                    for (int i = 0; i < arr.length; ++i) {
                        warr[i] = new WString(arr[i]);
                    }
                    result = warr;
                }
            }
            else if (returnType == Pointer[].class) {
                final Pointer p = this.invokePointer(callFlags, args);
                if (p != null) {
                    result = p.getPointerArray(0L);
                }
            }
            else {
                if (!allowObjects) {
                    throw new IllegalArgumentException("Unsupported return type " + returnType + " in function " + this.getName());
                }
                result = Native.invokeObject(this, this.peer, callFlags, args);
                if (result != null && !returnType.isAssignableFrom(result.getClass())) {
                    throw new ClassCastException("Return type " + returnType + " does not match result " + result.getClass());
                }
            }
        }
        return result;
    }
    
    private Pointer invokePointer(final int callFlags, final Object[] args) {
        final long ptr = Native.invokePointer(this, this.peer, callFlags, args);
        return (ptr == 0L) ? null : new Pointer(ptr);
    }
    
    private Object convertArgument(final Object[] args, final int index, final Method invokingMethod, final TypeMapper mapper, final boolean allowObjects, final Class<?> expectedType) {
        Object arg = args[index];
        if (arg != null) {
            final Class<?> type = arg.getClass();
            ToNativeConverter converter = null;
            if (NativeMapped.class.isAssignableFrom(type)) {
                converter = NativeMappedConverter.getInstance(type);
            }
            else if (mapper != null) {
                converter = mapper.getToNativeConverter(type);
            }
            if (converter != null) {
                ToNativeContext context;
                if (invokingMethod != null) {
                    context = new MethodParameterContext(this, args, index, invokingMethod);
                }
                else {
                    context = new FunctionParameterContext(this, args, index);
                }
                arg = converter.toNative(arg, context);
            }
        }
        if (arg == null || this.isPrimitiveArray(arg.getClass())) {
            return arg;
        }
        final Class<?> argClass = arg.getClass();
        if (arg instanceof Structure) {
            final Structure struct = (Structure)arg;
            struct.autoWrite();
            if (struct instanceof Structure.ByValue) {
                Class<?> ptype = struct.getClass();
                if (invokingMethod != null) {
                    final Class<?>[] ptypes = invokingMethod.getParameterTypes();
                    if (Function.IS_VARARGS.isVarArgs(invokingMethod)) {
                        if (index < ptypes.length - 1) {
                            ptype = ptypes[index];
                        }
                        else {
                            final Class<?> etype = ptypes[ptypes.length - 1].getComponentType();
                            if (etype != Object.class) {
                                ptype = etype;
                            }
                        }
                    }
                    else {
                        ptype = ptypes[index];
                    }
                }
                if (Structure.ByValue.class.isAssignableFrom(ptype)) {
                    return struct;
                }
            }
            return struct.getPointer();
        }
        if (arg instanceof Callback) {
            return CallbackReference.getFunctionPointer((Callback)arg);
        }
        if (arg instanceof String) {
            return new NativeString((String)arg, false).getPointer();
        }
        if (arg instanceof WString) {
            return new NativeString(arg.toString(), true).getPointer();
        }
        if (arg instanceof Boolean) {
            return Boolean.TRUE.equals(arg) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
        }
        if (String[].class == argClass) {
            return new StringArray((String[])arg, this.encoding);
        }
        if (WString[].class == argClass) {
            return new StringArray((WString[])arg);
        }
        if (Pointer[].class == argClass) {
            return new PointerArray((Pointer[])arg);
        }
        if (NativeMapped[].class.isAssignableFrom(argClass)) {
            return new NativeMappedArray((NativeMapped[])arg);
        }
        if (Structure[].class.isAssignableFrom(argClass)) {
            final Structure[] ss = (Structure[])arg;
            final Class<?> type2 = argClass.getComponentType();
            final boolean byRef = Structure.ByReference.class.isAssignableFrom(type2);
            if (expectedType != null && !Structure.ByReference[].class.isAssignableFrom(expectedType)) {
                if (byRef) {
                    throw new IllegalArgumentException("Function " + this.getName() + " declared Structure[] at parameter " + index + " but array of " + type2 + " was passed");
                }
                for (int i = 0; i < ss.length; ++i) {
                    if (ss[i] instanceof Structure.ByReference) {
                        throw new IllegalArgumentException("Function " + this.getName() + " declared Structure[] at parameter " + index + " but element " + i + " is of Structure.ByReference type");
                    }
                }
            }
            if (byRef) {
                Structure.autoWrite(ss);
                final Pointer[] pointers = new Pointer[ss.length + 1];
                for (int j = 0; j < ss.length; ++j) {
                    pointers[j] = ((ss[j] != null) ? ss[j].getPointer() : null);
                }
                return new PointerArray(pointers);
            }
            if (ss.length == 0) {
                throw new IllegalArgumentException("Structure array must have non-zero length");
            }
            if (ss[0] == null) {
                Structure.newInstance(type2).toArray(ss);
                return ss[0].getPointer();
            }
            Structure.autoWrite(ss);
            return ss[0].getPointer();
        }
        else {
            if (argClass.isArray()) {
                throw new IllegalArgumentException("Unsupported array argument type: " + argClass.getComponentType());
            }
            if (allowObjects) {
                return arg;
            }
            if (!Native.isSupportedNativeType(arg.getClass())) {
                throw new IllegalArgumentException("Unsupported argument type " + arg.getClass().getName() + " at parameter " + index + " of function " + this.getName());
            }
            return arg;
        }
    }
    
    private boolean isPrimitiveArray(final Class<?> argClass) {
        return argClass.isArray() && argClass.getComponentType().isPrimitive();
    }
    
    public void invoke(final Object[] args) {
        this.invoke(Void.class, args);
    }
    
    private String invokeString(final int callFlags, final Object[] args, final boolean wide) {
        final Pointer ptr = this.invokePointer(callFlags, args);
        String s = null;
        if (ptr != null) {
            if (wide) {
                s = ptr.getWideString(0L);
            }
            else {
                s = ptr.getString(0L, this.encoding);
            }
        }
        return s;
    }
    
    @Override
    public String toString() {
        if (this.library != null) {
            return "native function " + this.functionName + "(" + this.library.getName() + ")@0x" + Long.toHexString(this.peer);
        }
        return "native function@0x" + Long.toHexString(this.peer);
    }
    
    public Object invokeObject(final Object[] args) {
        return this.invoke(Object.class, args);
    }
    
    public Pointer invokePointer(final Object[] args) {
        return (Pointer)this.invoke(Pointer.class, args);
    }
    
    public String invokeString(final Object[] args, final boolean wide) {
        final Object o = this.invoke((Class<?>)(wide ? WString.class : String.class), args);
        return (o != null) ? o.toString() : null;
    }
    
    public int invokeInt(final Object[] args) {
        return (int)this.invoke(Integer.class, args);
    }
    
    public long invokeLong(final Object[] args) {
        return (long)this.invoke(Long.class, args);
    }
    
    public float invokeFloat(final Object[] args) {
        return (float)this.invoke(Float.class, args);
    }
    
    public double invokeDouble(final Object[] args) {
        return (double)this.invoke(Double.class, args);
    }
    
    public void invokeVoid(final Object[] args) {
        this.invoke(Void.class, args);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() == this.getClass()) {
            final Function other = (Function)o;
            return other.callFlags == this.callFlags && other.options.equals(this.options) && other.peer == this.peer;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.callFlags + this.options.hashCode() + super.hashCode();
    }
    
    static Object[] concatenateVarArgs(Object[] inArgs) {
        if (inArgs != null && inArgs.length > 0) {
            final Object lastArg = inArgs[inArgs.length - 1];
            final Class<?> argType = (lastArg != null) ? lastArg.getClass() : null;
            if (argType != null && argType.isArray()) {
                final Object[] varArgs = (Object[])lastArg;
                for (int i = 0; i < varArgs.length; ++i) {
                    if (varArgs[i] instanceof Float) {
                        varArgs[i] = varArgs[i];
                    }
                }
                final Object[] fullArgs = new Object[inArgs.length + varArgs.length];
                System.arraycopy(inArgs, 0, fullArgs, 0, inArgs.length - 1);
                System.arraycopy(varArgs, 0, fullArgs, inArgs.length - 1, varArgs.length);
                fullArgs[fullArgs.length - 1] = null;
                inArgs = fullArgs;
            }
        }
        return inArgs;
    }
    
    static boolean isVarArgs(final Method m) {
        return Function.IS_VARARGS.isVarArgs(m);
    }
    
    static int fixedArgs(final Method m) {
        return Function.IS_VARARGS.fixedArgs(m);
    }
    
    static Boolean valueOf(final boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }
    
    static {
        INTEGER_TRUE = -1;
        INTEGER_FALSE = 0;
        IS_VARARGS = VarArgsChecker.create();
    }
    
    private static class NativeMappedArray extends Memory implements PostCallRead
    {
        private final NativeMapped[] original;
        
        public NativeMappedArray(final NativeMapped[] arg) {
            super(Native.getNativeSize(arg.getClass(), arg));
            this.setValue(0L, this.original = arg, this.original.getClass());
        }
        
        @Override
        public void read() {
            this.getValue(0L, this.original.getClass(), this.original);
        }
    }
    
    private static class PointerArray extends Memory implements PostCallRead
    {
        private final Pointer[] original;
        
        public PointerArray(final Pointer[] arg) {
            super(Native.POINTER_SIZE * (arg.length + 1));
            this.original = arg;
            for (int i = 0; i < arg.length; ++i) {
                this.setPointer(i * Native.POINTER_SIZE, arg[i]);
            }
            this.setPointer(Native.POINTER_SIZE * arg.length, null);
        }
        
        @Override
        public void read() {
            this.read(0L, this.original, 0, this.original.length);
        }
    }
    
    public interface PostCallRead
    {
        void read();
    }
}
