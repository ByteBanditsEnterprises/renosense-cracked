//Raddon On Top!

package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.*;

public abstract class AbstractComEventCallbackListener implements IComEventCallbackListener
{
    IDispatchCallback dispatchCallback;
    
    public AbstractComEventCallbackListener() {
        this.dispatchCallback = null;
    }
    
    @Override
    public void setDispatchCallbackListener(final IDispatchCallback dispatchCallback) {
        this.dispatchCallback = dispatchCallback;
    }
}
