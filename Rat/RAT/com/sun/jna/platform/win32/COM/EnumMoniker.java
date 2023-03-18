//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;

public class EnumMoniker extends Unknown implements IEnumMoniker
{
    public EnumMoniker(final Pointer pointer) {
        super(pointer);
    }
    
    @Override
    public WinNT.HRESULT Next(final WinDef.ULONG celt, final PointerByReference rgelt, final WinDef.ULONGByReference pceltFetched) {
        final int vTableId = 3;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(3, new Object[] { this.getPointer(), celt, rgelt, pceltFetched }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    @Override
    public WinNT.HRESULT Skip(final WinDef.ULONG celt) {
        final int vTableId = 4;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), celt }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    @Override
    public WinNT.HRESULT Reset() {
        final int vTableId = 5;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(5, new Object[] { this.getPointer() }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    @Override
    public WinNT.HRESULT Clone(final PointerByReference ppenum) {
        final int vTableId = 6;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(6, new Object[] { this.getPointer(), ppenum }, (Class)WinNT.HRESULT.class);
        return hr;
    }
}
