//Raddon On Top!

package com.sun.jna;

import java.util.*;

public interface Callback
{
    public static final String METHOD_NAME = "callback";
    public static final List<String> FORBIDDEN_NAMES = Collections.unmodifiableList((List<? extends String>)Arrays.asList("hashCode", "equals", "toString"));
    
    public interface UncaughtExceptionHandler
    {
        void uncaughtException(final Callback p0, final Throwable p1);
    }
}
