//Raddon On Top!

package com.sun.jna.platform.unix;

import com.sun.jna.*;

public interface LibC extends LibCAPI, Library
{
    public static final String NAME = "c";
    public static final LibC INSTANCE = (LibC)Native.load("c", (Class)LibC.class);
}
