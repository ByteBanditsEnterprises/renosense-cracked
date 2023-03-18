//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.*;

public class TypeLib extends Unknown implements ITypeLib
{
    public TypeLib() {
    }
    
    public TypeLib(final Pointer pvInstance) {
        super(pvInstance);
    }
    
    public WinDef.UINT GetTypeInfoCount() {
        return (WinDef.UINT)this._invokeNativeObject(3, new Object[] { this.getPointer() }, (Class)WinDef.UINT.class);
    }
    
    public WinNT.HRESULT GetTypeInfo(final WinDef.UINT index, final PointerByReference pTInfo) {
        return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), index, pTInfo }, (Class)WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT GetTypeInfoType(final WinDef.UINT index, final OaIdl.TYPEKIND.ByReference pTKind) {
        return (WinNT.HRESULT)this._invokeNativeObject(5, new Object[] { this.getPointer(), index, pTKind }, (Class)WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT GetTypeInfoOfGuid(final Guid.GUID guid, final PointerByReference pTinfo) {
        return (WinNT.HRESULT)this._invokeNativeObject(6, new Object[] { this.getPointer(), guid, pTinfo }, (Class)WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT GetLibAttr(final PointerByReference ppTLibAttr) {
        return (WinNT.HRESULT)this._invokeNativeObject(7, new Object[] { this.getPointer(), ppTLibAttr }, (Class)WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT GetTypeComp(final PointerByReference pTComp) {
        return (WinNT.HRESULT)this._invokeNativeObject(8, new Object[] { this.getPointer(), pTComp }, (Class)WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT GetDocumentation(final int index, final WTypes.BSTRByReference pBstrName, final WTypes.BSTRByReference pBstrDocString, final WinDef.DWORDByReference pdwHelpContext, final WTypes.BSTRByReference pBstrHelpFile) {
        return (WinNT.HRESULT)this._invokeNativeObject(9, new Object[] { this.getPointer(), index, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile }, (Class)WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT IsName(final WTypes.LPOLESTR szNameBuf, final WinDef.ULONG lHashVal, final WinDef.BOOLByReference pfName) {
        return (WinNT.HRESULT)this._invokeNativeObject(10, new Object[] { this.getPointer(), szNameBuf, lHashVal, pfName }, (Class)WinNT.HRESULT.class);
    }
    
    public WinNT.HRESULT FindName(final WTypes.LPOLESTR szNameBuf, final WinDef.ULONG lHashVal, final Pointer[] ppTInfo, final OaIdl.MEMBERID[] rgMemId, final WinDef.USHORTByReference pcFound) {
        return (WinNT.HRESULT)this._invokeNativeObject(11, new Object[] { this.getPointer(), szNameBuf, lHashVal, ppTInfo, rgMemId, pcFound }, (Class)WinNT.HRESULT.class);
    }
    
    public void ReleaseTLibAttr(final OaIdl.TLIBATTR pTLibAttr) {
        this._invokeNativeObject(12, new Object[] { this.getPointer(), pTLibAttr.getPointer() }, (Class)WinNT.HRESULT.class);
    }
    
    public static class ByReference extends TypeLib implements Structure.ByReference
    {
    }
}
