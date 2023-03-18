//Raddon On Top!

package com.sun.jna.platform.wince;

import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface CoreDLL extends WinNT, Library
{
    public static final CoreDLL INSTANCE = (CoreDLL)Native.load("coredll", (Class)CoreDLL.class, (Map)W32APIOptions.UNICODE_OPTIONS);
}
