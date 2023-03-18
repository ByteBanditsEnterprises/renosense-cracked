//Raddon On Top!

package com.sun.jna.platform.win32.COM.util;

import java.util.*;

public interface IRunningObjectTable
{
    Iterable<IDispatch> enumRunning();
    
     <T> List<T> getActiveObjectsByInterface(final Class<T> p0);
}
