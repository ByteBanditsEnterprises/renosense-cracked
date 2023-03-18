//Raddon On Top!

package com.sun.jna.platform.win32.COM.util.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
@Deprecated
public @interface ComEventCallback {
    int dispid() default -1;
    
    String name() default "";
}
