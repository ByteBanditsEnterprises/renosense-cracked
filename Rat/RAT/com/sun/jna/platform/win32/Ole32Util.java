//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.*;

public abstract class Ole32Util
{
    public static Guid.GUID getGUIDFromString(final String guidString) {
        final Guid.GUID lpiid = new Guid.GUID();
        final WinNT.HRESULT hr = Ole32.INSTANCE.IIDFromString(guidString, lpiid);
        if (!hr.equals((Object)W32Errors.S_OK)) {
            throw new RuntimeException(hr.toString());
        }
        return lpiid;
    }
    
    public static String getStringFromGUID(final Guid.GUID guid) {
        final Guid.GUID pguid = new Guid.GUID(guid.getPointer());
        final int max = 39;
        final char[] lpsz = new char[max];
        final int len = Ole32.INSTANCE.StringFromGUID2(pguid, lpsz, max);
        if (len == 0) {
            throw new RuntimeException("StringFromGUID2");
        }
        lpsz[len - 1] = '\0';
        return Native.toString(lpsz);
    }
    
    public static Guid.GUID generateGUID() {
        final Guid.GUID pguid = new Guid.GUID();
        final WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateGuid(pguid);
        if (!hr.equals((Object)W32Errors.S_OK)) {
            throw new RuntimeException(hr.toString());
        }
        return pguid;
    }
}
