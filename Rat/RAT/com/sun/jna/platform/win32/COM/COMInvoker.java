//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.*;

public abstract class COMInvoker extends PointerType
{
    protected int _invokeNativeInt(final int vtableId, final Object[] args) {
        final Pointer vptr = this.getPointer().getPointer(0L);
        final Function func = Function.getFunction(vptr.getPointer(vtableId * Native.POINTER_SIZE));
        return func.invokeInt(args);
    }
    
    protected Object _invokeNativeObject(final int vtableId, final Object[] args, final Class<?> returnType) {
        final Pointer vptr = this.getPointer().getPointer(0L);
        final Function func = Function.getFunction(vptr.getPointer(vtableId * Native.POINTER_SIZE));
        return func.invoke((Class)returnType, args);
    }
    
    protected void _invokeNativeVoid(final int vtableId, final Object[] args) {
        final Pointer vptr = this.getPointer().getPointer(0L);
        final Function func = Function.getFunction(vptr.getPointer(vtableId * Native.POINTER_SIZE));
        func.invokeVoid(args);
    }
}
