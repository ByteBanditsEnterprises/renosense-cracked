//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.*;

public interface IEnumVariant extends IUnknown
{
    IEnumVariant Clone();
    
    Variant.VARIANT[] Next(final int p0);
    
    void Reset();
    
    void Skip(final int p0);
}
