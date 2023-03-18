//Raddon On Top!

package org.apache.commons.lang3.builder;

final class IDKey
{
    private final Object value;
    private final int id;
    
    IDKey(final Object _value) {
        this.id = System.identityHashCode(_value);
        this.value = _value;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof IDKey)) {
            return false;
        }
        final IDKey idKey = (IDKey)other;
        return this.id == idKey.id && this.value == idKey.value;
    }
}
