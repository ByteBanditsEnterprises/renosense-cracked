//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import java.lang.reflect.*;

public class ClassLoaderObjectInputStream extends ObjectInputStream
{
    private final ClassLoader classLoader;
    
    public ClassLoaderObjectInputStream(final ClassLoader classLoader, final InputStream inputStream) throws IOException, StreamCorruptedException {
        super(inputStream);
        this.classLoader = classLoader;
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(objectStreamClass.getName(), false, this.classLoader);
        }
        catch (ClassNotFoundException cnfe) {
            return super.resolveClass(objectStreamClass);
        }
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] interfaces) throws IOException, ClassNotFoundException {
        final Class<?>[] interfaceClasses = (Class<?>[])new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfaceClasses[i] = Class.forName(interfaces[i], false, this.classLoader);
        }
        try {
            return Proxy.getProxyClass(this.classLoader, interfaceClasses);
        }
        catch (IllegalArgumentException e) {
            return super.resolveProxyClass(interfaces);
        }
    }
}
