//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.*;

public abstract class NtDllUtil
{
    public static String getKeyName(final WinReg.HKEY hkey) {
        final IntByReference resultLength = new IntByReference();
        int rc = NtDll.INSTANCE.ZwQueryKey((WinNT.HANDLE)hkey, 0, (Structure)null, 0, resultLength);
        if (rc != -1073741789 || resultLength.getValue() <= 0) {
            throw new Win32Exception(rc);
        }
        final Wdm.KEY_BASIC_INFORMATION keyInformation = new Wdm.KEY_BASIC_INFORMATION(resultLength.getValue());
        rc = NtDll.INSTANCE.ZwQueryKey((WinNT.HANDLE)hkey, 0, (Structure)keyInformation, resultLength.getValue(), resultLength);
        if (rc != 0) {
            throw new Win32Exception(rc);
        }
        return keyInformation.getName();
    }
}
