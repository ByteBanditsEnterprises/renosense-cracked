//Raddon On Top!

package com.sun.jna.platform.linux;

import com.sun.jna.*;

public interface LibRT extends Library
{
    public static final LibRT INSTANCE = (LibRT)Native.load("rt", (Class)LibRT.class);
    
    int shm_open(final String p0, final int p1, final int p2);
    
    int shm_unlink(final String p0);
}
