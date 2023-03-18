//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.*;

public interface IDispatch extends IUnknown
{
    public static final Guid.IID IID_IDISPATCH = new Guid.IID("00020400-0000-0000-C000-000000000046");
    
    WinNT.HRESULT GetTypeInfoCount(final WinDef.UINTByReference p0);
    
    WinNT.HRESULT GetTypeInfo(final WinDef.UINT p0, final WinDef.LCID p1, final PointerByReference p2);
    
    WinNT.HRESULT GetIDsOfNames(final Guid.REFIID p0, final WString[] p1, final int p2, final WinDef.LCID p3, final OaIdl.DISPIDByReference p4);
    
    WinNT.HRESULT Invoke(final OaIdl.DISPID p0, final Guid.REFIID p1, final WinDef.LCID p2, final WinDef.WORD p3, final OleAuto.DISPPARAMS.ByReference p4, final Variant.VARIANT.ByReference p5, final OaIdl.EXCEPINFO.ByReference p6, final IntByReference p7);
}
