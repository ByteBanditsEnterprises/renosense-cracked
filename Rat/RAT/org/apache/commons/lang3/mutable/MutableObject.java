//Raddon On Top!

package org.apache.commons.lang3.mutable;

import java.io.*;

public class MutableObject<T> implements Mutable<T>, Serializable
{
    private static final long serialVersionUID = 86241875189L;
    private T value;
    
    public MutableObject() {
    }
    
    public MutableObject(final T value) {
        this.value = value;
    }
    
    public T getValue() {
        return this.value;
    }
    
    public void setValue(final T value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            final MutableObject<?> that = (MutableObject<?>)obj;
            return this.value.equals(that.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.value == null) ? 0 : this.value.hashCode();
    }
    
    @Override
    public String toString() {
        return (this.value == null) ? "null" : this.value.toString();
    }
}
