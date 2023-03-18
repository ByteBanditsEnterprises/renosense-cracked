//Raddon On Top!

package com.sun.jna;

import java.lang.reflect.*;
import java.util.*;

public abstract class Union extends Structure
{
    private Structure.StructField activeField;
    
    protected Union() {
    }
    
    protected Union(final Pointer p) {
        super(p);
    }
    
    protected Union(final Pointer p, final int alignType) {
        super(p, alignType);
    }
    
    protected Union(final TypeMapper mapper) {
        super(mapper);
    }
    
    protected Union(final Pointer p, final int alignType, final TypeMapper mapper) {
        super(p, alignType, mapper);
    }
    
    protected List<String> getFieldOrder() {
        final List<Field> flist = (List<Field>)this.getFieldList();
        final List<String> list = new ArrayList<String>(flist.size());
        for (final Field f : flist) {
            list.add(f.getName());
        }
        return list;
    }
    
    public void setType(final Class<?> type) {
        this.ensureAllocated();
        for (final Structure.StructField f : this.fields().values()) {
            if (f.type == type) {
                this.activeField = f;
                return;
            }
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }
    
    public void setType(final String fieldName) {
        this.ensureAllocated();
        final Structure.StructField f = this.fields().get(fieldName);
        if (f != null) {
            this.activeField = f;
            return;
        }
        throw new IllegalArgumentException("No field named " + fieldName + " in " + this);
    }
    
    public Object readField(final String fieldName) {
        this.ensureAllocated();
        this.setType(fieldName);
        return super.readField(fieldName);
    }
    
    public void writeField(final String fieldName) {
        this.ensureAllocated();
        this.setType(fieldName);
        super.writeField(fieldName);
    }
    
    public void writeField(final String fieldName, final Object value) {
        this.ensureAllocated();
        this.setType(fieldName);
        super.writeField(fieldName, value);
    }
    
    public Object getTypedValue(final Class<?> type) {
        this.ensureAllocated();
        for (final Structure.StructField f : this.fields().values()) {
            if (f.type == type) {
                this.activeField = f;
                this.read();
                return this.getFieldValue(this.activeField.field);
            }
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }
    
    public Object setTypedValue(final Object object) {
        final Structure.StructField f = this.findField(object.getClass());
        if (f != null) {
            this.activeField = f;
            this.setFieldValue(f.field, object);
            return this;
        }
        throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
    }
    
    private Structure.StructField findField(final Class<?> type) {
        this.ensureAllocated();
        for (final Structure.StructField f : this.fields().values()) {
            if (f.type.isAssignableFrom(type)) {
                return f;
            }
        }
        return null;
    }
    
    protected void writeField(final Structure.StructField field) {
        if (field == this.activeField) {
            super.writeField(field);
        }
    }
    
    protected Object readField(final Structure.StructField field) {
        if (field == this.activeField || (!Structure.class.isAssignableFrom(field.type) && !String.class.isAssignableFrom(field.type) && !WString.class.isAssignableFrom(field.type))) {
            return super.readField(field);
        }
        return null;
    }
    
    protected int getNativeAlignment(final Class<?> type, final Object value, final boolean isFirstElement) {
        return super.getNativeAlignment((Class)type, value, true);
    }
}
