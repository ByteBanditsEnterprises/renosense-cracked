//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface Winsock2 extends Library
{
    public static final Winsock2 INSTANCE = (Winsock2)Native.load("ws2_32", (Class)Winsock2.class, (Map)W32APIOptions.ASCII_OPTIONS);
    
    int gethostname(final byte[] p0, final int p1);
}
