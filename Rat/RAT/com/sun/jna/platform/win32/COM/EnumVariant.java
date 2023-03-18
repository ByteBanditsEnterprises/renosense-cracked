//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.*;

public class EnumVariant extends Unknown implements IEnumVariant
{
    public static final Guid.IID IID;
    public static final Guid.REFIID REFIID;
    
    public EnumVariant() {
    }
    
    public EnumVariant(final Pointer p) {
        this.setPointer(p);
    }
    
    @Override
    public Variant.VARIANT[] Next(final int count) {
        final Variant.VARIANT[] resultStaging = new Variant.VARIANT[count];
        final IntByReference resultCount = new IntByReference();
        final WinNT.HRESULT hresult = (WinNT.HRESULT)this._invokeNativeObject(3, new Object[] { this.getPointer(), resultStaging.length, resultStaging, resultCount }, (Class)WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
        final Variant.VARIANT[] result = new Variant.VARIANT[resultCount.getValue()];
        System.arraycopy(resultStaging, 0, result, 0, resultCount.getValue());
        return result;
    }
    
    @Override
    public void Skip(final int count) {
        final WinNT.HRESULT hresult = (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), count }, (Class)WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
    }
    
    @Override
    public void Reset() {
        final WinNT.HRESULT hresult = (WinNT.HRESULT)this._invokeNativeObject(5, new Object[] { this.getPointer() }, (Class)WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
    }
    
    @Override
    public EnumVariant Clone() {
        final PointerByReference pbr = new PointerByReference();
        final WinNT.HRESULT hresult = (WinNT.HRESULT)this._invokeNativeObject(6, new Object[] { this.getPointer(), pbr }, (Class)WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
        return new EnumVariant(pbr.getValue());
    }
    
    static {
        IID = new Guid.IID("{00020404-0000-0000-C000-000000000046}");
        REFIID = new Guid.REFIID(EnumVariant.IID);
    }
}
