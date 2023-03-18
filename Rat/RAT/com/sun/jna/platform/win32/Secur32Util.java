//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.*;
import java.util.*;

public abstract class Secur32Util
{
    public static String getUserNameEx(final int format) {
        char[] buffer = new char[128];
        final IntByReference len = new IntByReference(buffer.length);
        boolean result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
        if (!result) {
            final int rc = Kernel32.INSTANCE.GetLastError();
            switch (rc) {
                case 234: {
                    buffer = new char[len.getValue() + 1];
                    result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
                    break;
                }
                default: {
                    throw new Win32Exception(Native.getLastError());
                }
            }
        }
        if (!result) {
            throw new Win32Exception(Native.getLastError());
        }
        return Native.toString(buffer);
    }
    
    public static SecurityPackage[] getSecurityPackages() {
        final IntByReference pcPackages = new IntByReference();
        final Sspi.PSecPkgInfo pPackageInfo = new Sspi.PSecPkgInfo();
        int rc = Secur32.INSTANCE.EnumerateSecurityPackages(pcPackages, pPackageInfo);
        if (0 != rc) {
            throw new Win32Exception(rc);
        }
        final Sspi.SecPkgInfo[] packagesInfo = pPackageInfo.toArray(pcPackages.getValue());
        final ArrayList<SecurityPackage> packages = new ArrayList<SecurityPackage>(pcPackages.getValue());
        for (final Sspi.SecPkgInfo packageInfo : packagesInfo) {
            final SecurityPackage securityPackage = new SecurityPackage();
            securityPackage.name = packageInfo.Name.toString();
            securityPackage.comment = packageInfo.Comment.toString();
            packages.add(securityPackage);
        }
        rc = Secur32.INSTANCE.FreeContextBuffer(pPackageInfo.pPkgInfo.getPointer());
        if (0 != rc) {
            throw new Win32Exception(rc);
        }
        return packages.toArray(new SecurityPackage[0]);
    }
    
    public static class SecurityPackage
    {
        public String name;
        public String comment;
    }
}
