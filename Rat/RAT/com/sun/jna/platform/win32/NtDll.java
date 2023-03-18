//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface NtDll extends StdCallLibrary
{
    public static final NtDll INSTANCE = (NtDll)Native.load("NtDll", (Class)NtDll.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    
    int ZwQueryKey(final WinNT.HANDLE p0, final int p1, final Structure p2, final int p3, final IntByReference p4);
    
    int NtSetSecurityObject(final WinNT.HANDLE p0, final int p1, final Pointer p2);
    
    int NtQuerySecurityObject(final WinNT.HANDLE p0, final int p1, final Pointer p2, final int p3, final IntByReference p4);
    
    int RtlNtStatusToDosError(final int p0);
}
