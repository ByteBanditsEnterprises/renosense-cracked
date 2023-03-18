//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.*;

public class Moniker extends Unknown implements IMoniker
{
    static final int vTableIdStart = 7;
    
    public Moniker() {
    }
    
    public Moniker(final Pointer pointer) {
        super(pointer);
    }
    
    public void BindToObject() {
        final int vTableId = 8;
        throw new UnsupportedOperationException();
    }
    
    public void BindToStorage() {
        final int vTableId = 9;
        throw new UnsupportedOperationException();
    }
    
    public void Reduce() {
        final int vTableId = 10;
        throw new UnsupportedOperationException();
    }
    
    public void ComposeWith() {
        final int vTableId = 11;
        throw new UnsupportedOperationException();
    }
    
    public void Enum() {
        final int vTableId = 12;
        throw new UnsupportedOperationException();
    }
    
    public void IsEqual() {
        final int vTableId = 13;
        throw new UnsupportedOperationException();
    }
    
    public void Hash() {
        final int vTableId = 14;
        throw new UnsupportedOperationException();
    }
    
    public void IsRunning() {
        final int vTableId = 15;
        throw new UnsupportedOperationException();
    }
    
    public void GetTimeOfLastChange() {
        final int vTableId = 16;
        throw new UnsupportedOperationException();
    }
    
    public void Inverse() {
        final int vTableId = 17;
        throw new UnsupportedOperationException();
    }
    
    public void CommonPrefixWith() {
        final int vTableId = 18;
        throw new UnsupportedOperationException();
    }
    
    public void RelativePathTo() {
        final int vTableId = 19;
        throw new UnsupportedOperationException();
    }
    
    public String GetDisplayName(final Pointer pbc, final Pointer pmkToLeft) {
        final int vTableId = 20;
        final PointerByReference ppszDisplayNameRef = new PointerByReference();
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(20, new Object[] { this.getPointer(), pbc, pmkToLeft, ppszDisplayNameRef }, (Class)WinNT.HRESULT.class);
        COMUtils.checkRC(hr);
        final Pointer ppszDisplayName = ppszDisplayNameRef.getValue();
        if (ppszDisplayName == null) {
            return null;
        }
        final WTypes.LPOLESTR oleStr = new WTypes.LPOLESTR(ppszDisplayName);
        final String name = oleStr.getValue();
        Ole32.INSTANCE.CoTaskMemFree(ppszDisplayName);
        return name;
    }
    
    public void ParseDisplayName() {
        final int vTableId = 21;
        throw new UnsupportedOperationException();
    }
    
    public void IsSystemMoniker() {
        final int vTableId = 22;
        throw new UnsupportedOperationException();
    }
    
    public boolean IsDirty() {
        throw new UnsupportedOperationException();
    }
    
    public void Load(final IStream stm) {
        throw new UnsupportedOperationException();
    }
    
    public void Save(final IStream stm) {
        throw new UnsupportedOperationException();
    }
    
    public void GetSizeMax() {
        throw new UnsupportedOperationException();
    }
    
    public Guid.CLSID GetClassID() {
        throw new UnsupportedOperationException();
    }
    
    public static class ByReference extends Moniker implements Structure.ByReference
    {
    }
}
