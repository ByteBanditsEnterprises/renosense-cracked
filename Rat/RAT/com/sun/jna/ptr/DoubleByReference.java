//Raddon On Top!

package com.sun.jna.ptr;

import com.sun.jna.*;

public class DoubleByReference extends ByReference
{
    public DoubleByReference() {
        this(0.0);
    }
    
    public DoubleByReference(final double value) {
        super(8);
        this.setValue(value);
    }
    
    public void setValue(final double value) {
        this.getPointer().setDouble(0L, value);
    }
    
    public double getValue() {
        return this.getPointer().getDouble(0L);
    }
    
    public String toString() {
        return String.format("double@0x%x=%s", Pointer.nativeValue(this.getPointer()), this.getValue());
    }
}
