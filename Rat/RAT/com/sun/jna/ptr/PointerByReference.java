//Raddon On Top!

package com.sun.jna.ptr;

import com.sun.jna.*;

public class PointerByReference extends ByReference
{
    public PointerByReference() {
        this(null);
    }
    
    public PointerByReference(final Pointer value) {
        super(Native.POINTER_SIZE);
        this.setValue(value);
    }
    
    public void setValue(final Pointer value) {
        this.getPointer().setPointer(0L, value);
    }
    
    public Pointer getValue() {
        return this.getPointer().getPointer(0L);
    }
}
