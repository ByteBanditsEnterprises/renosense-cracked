//Raddon On Top!

package com.sun.jna;

import java.util.*;

public class DefaultTypeMapper implements TypeMapper
{
    private List<Entry> toNativeConverters;
    private List<Entry> fromNativeConverters;
    
    public DefaultTypeMapper() {
        this.toNativeConverters = new ArrayList<Entry>();
        this.fromNativeConverters = new ArrayList<Entry>();
    }
    
    private Class<?> getAltClass(final Class<?> cls) {
        if (cls == Boolean.class) {
            return Boolean.TYPE;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.class;
        }
        if (cls == Byte.class) {
            return Byte.TYPE;
        }
        if (cls == Byte.TYPE) {
            return Byte.class;
        }
        if (cls == Character.class) {
            return Character.TYPE;
        }
        if (cls == Character.TYPE) {
            return Character.class;
        }
        if (cls == Short.class) {
            return Short.TYPE;
        }
        if (cls == Short.TYPE) {
            return Short.class;
        }
        if (cls == Integer.class) {
            return Integer.TYPE;
        }
        if (cls == Integer.TYPE) {
            return Integer.class;
        }
        if (cls == Long.class) {
            return Long.TYPE;
        }
        if (cls == Long.TYPE) {
            return Long.class;
        }
        if (cls == Float.class) {
            return Float.TYPE;
        }
        if (cls == Float.TYPE) {
            return Float.class;
        }
        if (cls == Double.class) {
            return Double.TYPE;
        }
        if (cls == Double.TYPE) {
            return Double.class;
        }
        return null;
    }
    
    public void addToNativeConverter(final Class<?> cls, final ToNativeConverter converter) {
        this.toNativeConverters.add(new Entry(cls, converter));
        final Class<?> alt = this.getAltClass(cls);
        if (alt != null) {
            this.toNativeConverters.add(new Entry(alt, converter));
        }
    }
    
    public void addFromNativeConverter(final Class<?> cls, final FromNativeConverter converter) {
        this.fromNativeConverters.add(new Entry(cls, converter));
        final Class<?> alt = this.getAltClass(cls);
        if (alt != null) {
            this.fromNativeConverters.add(new Entry(alt, converter));
        }
    }
    
    public void addTypeConverter(final Class<?> cls, final TypeConverter converter) {
        this.addFromNativeConverter(cls, converter);
        this.addToNativeConverter(cls, converter);
    }
    
    private Object lookupConverter(final Class<?> javaClass, final Collection<? extends Entry> converters) {
        for (final Entry entry : converters) {
            if (entry.type.isAssignableFrom(javaClass)) {
                return entry.converter;
            }
        }
        return null;
    }
    
    @Override
    public FromNativeConverter getFromNativeConverter(final Class<?> javaType) {
        return (FromNativeConverter)this.lookupConverter(javaType, this.fromNativeConverters);
    }
    
    @Override
    public ToNativeConverter getToNativeConverter(final Class<?> javaType) {
        return (ToNativeConverter)this.lookupConverter(javaType, this.toNativeConverters);
    }
    
    private static class Entry
    {
        public Class<?> type;
        public Object converter;
        
        public Entry(final Class<?> type, final Object converter) {
            this.type = type;
            this.converter = converter;
        }
    }
}
