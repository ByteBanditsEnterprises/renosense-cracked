//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;

public interface ITypeComp extends IUnknown
{
    WinNT.HRESULT Bind(final WString p0, final WinDef.ULONG p1, final WinDef.WORD p2, final PointerByReference p3, final OaIdl.DESCKIND.ByReference p4, final OaIdl.BINDPTR.ByReference p5);
    
    WinNT.HRESULT BindType(final WString p0, final WinDef.ULONG p1, final PointerByReference p2, final PointerByReference p3);
}
