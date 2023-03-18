//Raddon On Top!

package org.sqlite.util;

import java.net.*;

public class ResourceFinder
{
    public static URL find(final Class<?> referenceClass, final String resourceFileName) {
        return find(referenceClass.getClassLoader(), referenceClass.getPackage(), resourceFileName);
    }
    
    public static URL find(final ClassLoader classLoader, final Package basePackage, final String resourceFileName) {
        return find(classLoader, basePackage.getName(), resourceFileName);
    }
    
    public static URL find(final ClassLoader classLoader, final String packageName, final String resourceFileName) {
        final String packagePath = packagePath(packageName);
        String resourcePath = packagePath + resourceFileName;
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        return classLoader.getResource(resourcePath);
    }
    
    private static String packagePath(final Class<?> referenceClass) {
        return packagePath(referenceClass.getPackage());
    }
    
    private static String packagePath(final Package basePackage) {
        return packagePath(basePackage.getName());
    }
    
    private static String packagePath(final String packageName) {
        final String packageAsPath = packageName.replaceAll("\\.", "/");
        return packageAsPath.endsWith("/") ? packageAsPath : (packageAsPath + "/");
    }
}
