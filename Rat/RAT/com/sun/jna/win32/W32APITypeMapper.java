//Raddon On Top!

package com.sun.jna.win32;

import com.sun.jna.*;

public class W32APITypeMapper extends DefaultTypeMapper
{
    public static final TypeMapper UNICODE;
    public static final TypeMapper ASCII;
    public static final TypeMapper DEFAULT;
    
    protected W32APITypeMapper(final boolean unicode) {
        if (unicode) {
            final TypeConverter stringConverter = (TypeConverter)new TypeConverter() {
                public Object toNative(final Object value, final ToNativeContext context) {
                    if (value == null) {
                        return null;
                    }
                    if (value instanceof String[]) {
                        return new StringArray((String[])value, true);
                    }
                    return new WString(value.toString());
                }
                
                public Object fromNative(final Object value, final FromNativeContext context) {
                    if (value == null) {
                        return null;
                    }
                    return value.toString();
                }
                
                public Class<?> nativeType() {
                    return WString.class;
                }
            };
            this.addTypeConverter((Class)String.class, stringConverter);
            this.addToNativeConverter((Class)String[].class, (ToNativeConverter)stringConverter);
        }
        final TypeConverter booleanConverter = (TypeConverter)new TypeConverter() {
            public Object toNative(final Object value, final ToNativeContext context) {
                return Boolean.TRUE.equals(value) ? 1 : 0;
            }
            
            public Object fromNative(final Object value, final FromNativeContext context) {
                return ((int)value != 0) ? Boolean.TRUE : Boolean.FALSE;
            }
            
            public Class<?> nativeType() {
                return Integer.class;
            }
        };
        this.addTypeConverter((Class)Boolean.class, booleanConverter);
    }
    
    static {
        UNICODE = (TypeMapper)new W32APITypeMapper(true);
        ASCII = (TypeMapper)new W32APITypeMapper(false);
        DEFAULT = (Boolean.getBoolean("w32.ascii") ? W32APITypeMapper.ASCII : W32APITypeMapper.UNICODE);
    }
}
