//Raddon On Top!

package org.apache.commons.lang3;

public class ClassPathUtils
{
    public static String toFullyQualifiedName(final Class<?> context, final String resourceName) {
        Validate.notNull(context, "context", new Object[0]);
        Validate.notNull(resourceName, "resourceName", new Object[0]);
        return toFullyQualifiedName(context.getPackage(), resourceName);
    }
    
    public static String toFullyQualifiedName(final Package context, final String resourceName) {
        Validate.notNull(context, "context", new Object[0]);
        Validate.notNull(resourceName, "resourceName", new Object[0]);
        return context.getName() + "." + resourceName;
    }
    
    public static String toFullyQualifiedPath(final Class<?> context, final String resourceName) {
        Validate.notNull(context, "context", new Object[0]);
        Validate.notNull(resourceName, "resourceName", new Object[0]);
        return toFullyQualifiedPath(context.getPackage(), resourceName);
    }
    
    public static String toFullyQualifiedPath(final Package context, final String resourceName) {
        Validate.notNull(context, "context", new Object[0]);
        Validate.notNull(resourceName, "resourceName", new Object[0]);
        return context.getName().replace('.', '/') + "/" + resourceName;
    }
}
