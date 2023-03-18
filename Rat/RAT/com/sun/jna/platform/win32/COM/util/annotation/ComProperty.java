//Raddon On Top!

package com.sun.jna.platform.win32.COM.util.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
public @interface ComProperty {
    String name() default "";
    
    int dispId() default -1;
}
