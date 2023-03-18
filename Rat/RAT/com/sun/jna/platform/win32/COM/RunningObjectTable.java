//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.*;

public class RunningObjectTable extends Unknown implements IRunningObjectTable
{
    public RunningObjectTable() {
    }
    
    public RunningObjectTable(final Pointer pointer) {
        super(pointer);
    }
    
    public WinNT.HRESULT Register(final WinDef.DWORD grfFlags, final Pointer punkObject, final Pointer pmkObjectName, final WinDef.DWORDByReference pdwRegister) {
        final int vTableId = 3;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(3, new Object[] { this.getPointer(), grfFlags, punkObject, pmkObjectName, pdwRegister }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    public WinNT.HRESULT Revoke(final WinDef.DWORD dwRegister) {
        final int vTableId = 4;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), dwRegister }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    public WinNT.HRESULT IsRunning(final Pointer pmkObjectName) {
        final int vTableId = 5;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(5, new Object[] { this.getPointer(), pmkObjectName }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    public WinNT.HRESULT GetObject(final Pointer pmkObjectName, final PointerByReference ppunkObject) {
        final int vTableId = 6;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(6, new Object[] { this.getPointer(), pmkObjectName, ppunkObject }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    public WinNT.HRESULT NoteChangeTime(final WinDef.DWORD dwRegister, final WinBase.FILETIME pfiletime) {
        final int vTableId = 7;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(7, new Object[] { this.getPointer(), dwRegister, pfiletime }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    public WinNT.HRESULT GetTimeOfLastChange(final Pointer pmkObjectName, final WinBase.FILETIME.ByReference pfiletime) {
        final int vTableId = 8;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(8, new Object[] { this.getPointer(), pmkObjectName, pfiletime }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    public WinNT.HRESULT EnumRunning(final PointerByReference ppenumMoniker) {
        final int vTableId = 9;
        final WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(9, new Object[] { this.getPointer(), ppenumMoniker }, (Class)WinNT.HRESULT.class);
        return hr;
    }
    
    public static class ByReference extends RunningObjectTable implements Structure.ByReference
    {
    }
}
