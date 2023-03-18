//Raddon On Top!

package com.sun.jna.platform.win32.COM.util.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface ComInterface {
    String iid() default "";
}
