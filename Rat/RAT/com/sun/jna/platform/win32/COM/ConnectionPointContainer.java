//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.*;

public class ConnectionPointContainer extends Unknown implements IConnectionPointContainer
{
    public ConnectionPointContainer(final Pointer pointer) {
        super(pointer);
    }
    
    public WinNT.HRESULT EnumConnectionPoints() {
        final int vTableId = 3;
        throw new UnsupportedOperationException();
    }
    
    @Override
    public WinNT.HRESULT FindConnectionPoint(final Guid.REFIID riid, final PointerByReference ppCP) {
        final int vTableId = 4;
        return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), riid, ppCP }, (Class)WinNT.HRESULT.class);
    }
}
