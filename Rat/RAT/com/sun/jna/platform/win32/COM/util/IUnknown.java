//Raddon On Top!

package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.util.annotation.*;
import com.sun.jna.platform.win32.COM.*;

@ComInterface(iid = "{00000000-0000-0000-C000-000000000046}")
public interface IUnknown
{
     <T> T queryInterface(final Class<T> p0) throws COMException;
}
