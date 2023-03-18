//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.*;

public interface IPersist extends IUnknown
{
    Guid.CLSID GetClassID();
}
