//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface Cryptui extends StdCallLibrary
{
    public static final Cryptui INSTANCE = (Cryptui)Native.load("Cryptui", (Class)Cryptui.class, (Map)W32APIOptions.UNICODE_OPTIONS);
    
    WinCrypt.CERT_CONTEXT.ByReference CryptUIDlgSelectCertificateFromStore(final WinCrypt.HCERTSTORE p0, final WinDef.HWND p1, final String p2, final String p3, final int p4, final int p5, final PointerType p6);
}
