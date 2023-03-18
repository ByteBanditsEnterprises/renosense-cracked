//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface Version extends StdCallLibrary
{
    public static final Version INSTANCE = (Version)Native.load("version", (Class)Version.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    
    int GetFileVersionInfoSize(final String p0, final IntByReference p1);
    
    boolean GetFileVersionInfo(final String p0, final int p1, final int p2, final Pointer p3);
    
    boolean VerQueryValue(final Pointer p0, final String p1, final PointerByReference p2, final IntByReference p3);
}
