//Raddon On Top!

package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.COM.*;
import java.util.*;

public class RunningObjectTable implements IRunningObjectTable
{
    ObjectFactory factory;
    com.sun.jna.platform.win32.COM.RunningObjectTable raw;
    
    protected RunningObjectTable(final com.sun.jna.platform.win32.COM.RunningObjectTable raw, final ObjectFactory factory) {
        this.raw = raw;
        this.factory = factory;
    }
    
    public Iterable<IDispatch> enumRunning() {
        assert COMUtils.comIsInitialized() : "COM not initialized";
        final PointerByReference ppenumMoniker = new PointerByReference();
        final WinNT.HRESULT hr = this.raw.EnumRunning(ppenumMoniker);
        COMUtils.checkRC(hr);
        final EnumMoniker raw = new EnumMoniker(ppenumMoniker.getValue());
        return (Iterable<IDispatch>)new com.sun.jna.platform.win32.COM.util.EnumMoniker((IEnumMoniker)raw, (com.sun.jna.platform.win32.COM.IRunningObjectTable)this.raw, this.factory);
    }
    
    public <T> List<T> getActiveObjectsByInterface(final Class<T> comInterface) {
        assert COMUtils.comIsInitialized() : "COM not initialized";
        final List<T> result = new ArrayList<T>();
        for (final IDispatch obj : this.enumRunning()) {
            try {
                final T dobj = (T)obj.queryInterface((Class)comInterface);
                result.add(dobj);
            }
            catch (COMException ex) {}
        }
        return result;
    }
}
