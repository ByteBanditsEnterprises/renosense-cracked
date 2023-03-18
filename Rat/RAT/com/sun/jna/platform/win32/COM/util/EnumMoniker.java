//Raddon On Top!

package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;
import java.util.*;
import com.sun.jna.platform.win32.COM.*;

public class EnumMoniker implements Iterable<IDispatch>
{
    ObjectFactory factory;
    IRunningObjectTable rawRot;
    IEnumMoniker raw;
    Moniker rawNext;
    
    protected EnumMoniker(final IEnumMoniker raw, final IRunningObjectTable rawRot, final ObjectFactory factory) {
        assert COMUtils.comIsInitialized() : "COM not initialized";
        this.rawRot = rawRot;
        this.raw = raw;
        this.factory = factory;
        final WinNT.HRESULT hr = raw.Reset();
        COMUtils.checkRC(hr);
        this.cacheNext();
    }
    
    protected void cacheNext() {
        assert COMUtils.comIsInitialized() : "COM not initialized";
        final PointerByReference rgelt = new PointerByReference();
        final WinDef.ULONGByReference pceltFetched = new WinDef.ULONGByReference();
        final WinNT.HRESULT hr = this.raw.Next(new WinDef.ULONG(1L), rgelt, pceltFetched);
        if (WinNT.S_OK.equals((Object)hr) && pceltFetched.getValue().intValue() > 0) {
            this.rawNext = new Moniker(rgelt.getValue());
        }
        else {
            if (!WinNT.S_FALSE.equals((Object)hr)) {
                COMUtils.checkRC(hr);
            }
            this.rawNext = null;
        }
    }
    
    @Override
    public Iterator<IDispatch> iterator() {
        return new Iterator<IDispatch>() {
            @Override
            public boolean hasNext() {
                return null != EnumMoniker.this.rawNext;
            }
            
            @Override
            public IDispatch next() {
                assert COMUtils.comIsInitialized() : "COM not initialized";
                final Moniker moniker = EnumMoniker.this.rawNext;
                final PointerByReference ppunkObject = new PointerByReference();
                final WinNT.HRESULT hr = EnumMoniker.this.rawRot.GetObject(moniker.getPointer(), ppunkObject);
                COMUtils.checkRC(hr);
                final Dispatch dispatch = new Dispatch(ppunkObject.getValue());
                EnumMoniker.this.cacheNext();
                final IDispatch d = EnumMoniker.this.factory.createProxy(IDispatch.class, (com.sun.jna.platform.win32.COM.IDispatch)dispatch);
                final int n = dispatch.Release();
                return d;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
}
