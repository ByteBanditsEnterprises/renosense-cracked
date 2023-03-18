//Raddon On Top!

package com.sun.jna.platform;

import com.sun.jna.*;

public class EnumConverter<T extends Enum<T>> implements TypeConverter
{
    private final Class<T> clazz;
    
    public EnumConverter(final Class<T> clazz) {
        this.clazz = clazz;
    }
    
    public T fromNative(final Object input, final FromNativeContext context) {
        final Integer i = (Integer)input;
        final T[] vals = this.clazz.getEnumConstants();
        return vals[i];
    }
    
    public Integer toNative(final Object input, final ToNativeContext context) {
        final T t = this.clazz.cast(input);
        return t.ordinal();
    }
    
    public Class<Integer> nativeType() {
        return Integer.class;
    }
}
