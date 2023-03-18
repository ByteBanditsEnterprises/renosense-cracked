//Raddon On Top!

package org.apache.commons.lang3;

import java.net.*;
import java.util.*;

public class ClassLoaderUtils
{
    public static String toString(final ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            return toString((URLClassLoader)classLoader);
        }
        return classLoader.toString();
    }
    
    public static String toString(final URLClassLoader classLoader) {
        return classLoader + Arrays.toString(classLoader.getURLs());
    }
}
