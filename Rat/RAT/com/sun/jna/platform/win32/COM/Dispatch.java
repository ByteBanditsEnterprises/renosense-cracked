//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.*;
import com.sun.jna.*;

public class Dispatch extends Unknown implements IDispatch
{
    public Dispatch() {
    }
    
    public Dispatch(final Pointer pvInstance) {
        super(pvInstance);
    }
    
    @Override
    public WinNT.HRESULT GetTypeInfoCount(final WinDef.UINTByReference pctinfo) {
        return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[] { this.getPointer(), pctinfo }, (Class)WinNT.HRESULT.class);
    }
    
    @Override
    public WinNT.HRESULT GetTypeInfo(final WinDef.UINT iTInfo, final WinDef.LCID lcid, final PointerByReference ppTInfo) {
        return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), iTInfo, lcid, ppTInfo }, (Class)WinNT.HRESULT.class);
    }
    
    @Override
    public WinNT.HRESULT GetIDsOfNames(final Guid.REFIID riid, final WString[] rgszNames, final int cNames, final WinDef.LCID lcid, final OaIdl.DISPIDByReference rgDispId) {
        return (WinNT.HRESULT)this._invokeNativeObject(5, new Object[] { this.getPointer(), riid, rgszNames, cNames, lcid, rgDispId }, (Class)WinNT.HRESULT.class);
    }
    
    @Override
    public WinNT.HRESULT Invoke(final OaIdl.DISPID dispIdMember, final Guid.REFIID riid, final WinDef.LCID lcid, final WinDef.WORD wFlags, final OleAuto.DISPPARAMS.ByReference pDispParams, final Variant.VARIANT.ByReference pVarResult, final OaIdl.EXCEPINFO.ByReference pExcepInfo, final IntByReference puArgErr) {
        return (WinNT.HRESULT)this._invokeNativeObject(6, new Object[] { this.getPointer(), dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr }, (Class)WinNT.HRESULT.class);
    }
    
    public static class ByReference extends Dispatch implements Structure.ByReference
    {
    }
}
