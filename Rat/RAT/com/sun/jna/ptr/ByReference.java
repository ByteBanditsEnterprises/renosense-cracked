//Raddon On Top!

package com.sun.jna.ptr;

import com.sun.jna.*;
import java.lang.reflect.*;

public abstract class ByReference extends PointerType
{
    protected ByReference(final int dataSize) {
        this.setPointer((Pointer)new Memory((long)dataSize));
    }
    
    public String toString() {
        try {
            final Method getValue = this.getClass().getMethod("getValue", (Class<?>[])new Class[0]);
            final Object value = getValue.invoke(this, new Object[0]);
            if (value == null) {
                return String.format("null@0x%x", Pointer.nativeValue(this.getPointer()));
            }
            return String.format("%s@0x%x=%s", value.getClass().getSimpleName(), Pointer.nativeValue(this.getPointer()), value);
        }
        catch (Exception ex) {
            return String.format("ByReference Contract violated - %s#getValue raised exception: %s", this.getClass().getName(), ex.getMessage());
        }
    }
}
