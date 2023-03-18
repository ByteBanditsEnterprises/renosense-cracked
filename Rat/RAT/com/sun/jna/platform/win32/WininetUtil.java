//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.*;
import java.util.*;

public class WininetUtil
{
    public static Map<String, String> getCache() {
        final List<Wininet.INTERNET_CACHE_ENTRY_INFO> items = new ArrayList<Wininet.INTERNET_CACHE_ENTRY_INFO>();
        WinNT.HANDLE cacheHandle = null;
        Win32Exception we = null;
        int lastError = 0;
        final Map<String, String> cacheItems = new LinkedHashMap<String, String>();
        try {
            IntByReference size = new IntByReference();
            cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry((String)null, (Wininet.INTERNET_CACHE_ENTRY_INFO)null, size);
            lastError = Native.getLastError();
            if (lastError == 259) {
                return cacheItems;
            }
            if (lastError != 0 && lastError != 122) {
                throw new Win32Exception(lastError);
            }
            Wininet.INTERNET_CACHE_ENTRY_INFO entry = new Wininet.INTERNET_CACHE_ENTRY_INFO(size.getValue());
            cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry((String)null, entry, size);
            if (cacheHandle == null) {
                throw new Win32Exception(Native.getLastError());
            }
            items.add(entry);
            while (true) {
                size = new IntByReference();
                boolean result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, (Wininet.INTERNET_CACHE_ENTRY_INFO)null, size);
                if (!result) {
                    lastError = Native.getLastError();
                    if (lastError == 259) {
                        break;
                    }
                    if (lastError != 0 && lastError != 122) {
                        throw new Win32Exception(lastError);
                    }
                }
                entry = new Wininet.INTERNET_CACHE_ENTRY_INFO(size.getValue());
                result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, entry, size);
                if (!result) {
                    lastError = Native.getLastError();
                    if (lastError == 259) {
                        break;
                    }
                    if (lastError != 0 && lastError != 122) {
                        throw new Win32Exception(lastError);
                    }
                }
                items.add(entry);
            }
            for (final Wininet.INTERNET_CACHE_ENTRY_INFO item : items) {
                cacheItems.put(item.lpszSourceUrlName.getWideString(0L), (item.lpszLocalFileName == null) ? "" : item.lpszLocalFileName.getWideString(0L));
            }
        }
        catch (Win32Exception e) {
            we = e;
        }
        finally {
            if (cacheHandle != null && !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle) && we != null) {
                final Win32Exception e2 = new Win32Exception(Native.getLastError());
                e2.addSuppressedReflected((Throwable)we);
                we = e2;
            }
        }
        if (we != null) {
            throw we;
        }
        return cacheItems;
    }
}
