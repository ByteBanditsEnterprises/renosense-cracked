//Raddon On Top!

package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.*;

public interface IComEventCallbackListener
{
    void setDispatchCallbackListener(final IDispatchCallback p0);
    
    void errorReceivingCallbackEvent(final String p0, final Exception p1);
}
