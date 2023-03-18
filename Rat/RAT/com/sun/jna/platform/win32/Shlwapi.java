//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface Shlwapi extends StdCallLibrary
{
    public static final Shlwapi INSTANCE = (Shlwapi)Native.load("Shlwapi", (Class)Shlwapi.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    
    WinNT.HRESULT StrRetToStr(final ShTypes.STRRET p0, final Pointer p1, final PointerByReference p2);
    
    boolean PathIsUNC(final String p0);
}
