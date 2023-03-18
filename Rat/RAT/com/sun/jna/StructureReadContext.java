//Raddon On Top!

package com.sun.jna;

import java.lang.reflect.*;

public class StructureReadContext extends FromNativeContext
{
    private Structure structure;
    private Field field;
    
    StructureReadContext(final Structure struct, final Field field) {
        super((Class)field.getType());
        this.structure = struct;
        this.field = field;
    }
    
    public Structure getStructure() {
        return this.structure;
    }
    
    public Field getField() {
        return this.field;
    }
}
