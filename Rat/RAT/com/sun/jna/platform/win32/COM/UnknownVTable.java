//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.win32.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;

@FieldOrder({ "QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback" })
public class UnknownVTable extends Structure
{
    public QueryInterfaceCallback QueryInterfaceCallback;
    public AddRefCallback AddRefCallback;
    public ReleaseCallback ReleaseCallback;
    
    public static class ByReference extends UnknownVTable implements Structure.ByReference
    {
    }
    
    public interface ReleaseCallback extends StdCallLibrary.StdCallCallback
    {
        int invoke(final Pointer p0);
    }
    
    public interface AddRefCallback extends StdCallLibrary.StdCallCallback
    {
        int invoke(final Pointer p0);
    }
    
    public interface QueryInterfaceCallback extends StdCallLibrary.StdCallCallback
    {
        WinNT.HRESULT invoke(final Pointer p0, final Guid.REFIID p1, final PointerByReference p2);
    }
}
