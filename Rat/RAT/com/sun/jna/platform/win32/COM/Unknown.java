//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.*;

public class Unknown extends COMInvoker implements IUnknown
{
    public Unknown() {
    }
    
    public Unknown(final Pointer pvInstance) {
        this.setPointer(pvInstance);
    }
    
    public WinNT.HRESULT QueryInterface(final Guid.REFIID riid, final PointerByReference ppvObject) {
        return (WinNT.HRESULT)this._invokeNativeObject(0, new Object[] { this.getPointer(), riid, ppvObject }, (Class)WinNT.HRESULT.class);
    }
    
    public int AddRef() {
        return this._invokeNativeInt(1, new Object[] { this.getPointer() });
    }
    
    public int Release() {
        return this._invokeNativeInt(2, new Object[] { this.getPointer() });
    }
    
    public static class ByReference extends Unknown implements Structure.ByReference
    {
    }
}
