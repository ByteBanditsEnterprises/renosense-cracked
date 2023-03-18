//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface Rasapi32 extends StdCallLibrary
{
    public static final Rasapi32 INSTANCE = (Rasapi32)Native.load("Rasapi32", (Class)Rasapi32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    
    int RasDial(final WinRas.RASDIALEXTENSIONS.ByReference p0, final String p1, final WinRas.RASDIALPARAMS.ByReference p2, final int p3, final WinRas.RasDialFunc2 p4, final WinNT.HANDLEByReference p5);
    
    int RasEnumConnections(final WinRas.RASCONN[] p0, final IntByReference p1, final IntByReference p2);
    
    int RasGetConnectionStatistics(final WinNT.HANDLE p0, final Structure.ByReference p1);
    
    int RasGetConnectStatus(final WinNT.HANDLE p0, final Structure.ByReference p1);
    
    int RasGetCredentials(final String p0, final String p1, final WinRas.RASCREDENTIALS.ByReference p2);
    
    int RasGetEntryProperties(final String p0, final String p1, final WinRas.RASENTRY.ByReference p2, final IntByReference p3, final Pointer p4, final Pointer p5);
    
    int RasGetProjectionInfo(final WinNT.HANDLE p0, final int p1, final Pointer p2, final IntByReference p3);
    
    int RasHangUp(final WinNT.HANDLE p0);
    
    int RasSetEntryProperties(final String p0, final String p1, final WinRas.RASENTRY.ByReference p2, final int p3, final byte[] p4, final int p5);
    
    int RasGetEntryDialParams(final String p0, final WinRas.RASDIALPARAMS.ByReference p1, final WinDef.BOOLByReference p2);
    
    int RasGetErrorString(final int p0, final char[] p1, final int p2);
}
