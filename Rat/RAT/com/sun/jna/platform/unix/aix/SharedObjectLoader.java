//Raddon On Top!

package com.sun.jna.platform.unix.aix;

import com.sun.jna.*;
import java.util.*;

final class SharedObjectLoader
{
    private SharedObjectLoader() {
    }
    
    static Perfstat getPerfstatInstance() {
        final Map<String, Object> options = getOptions();
        try {
            return (Perfstat)Native.load("/usr/lib/libperfstat.a(shr_64.o)", (Class)Perfstat.class, (Map)options);
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            return (Perfstat)Native.load("/usr/lib/libperfstat.a(shr.o)", (Class)Perfstat.class, (Map)options);
        }
    }
    
    private static Map<String, Object> getOptions() {
        final int RTLD_MEMBER = 262144;
        final int RTLD_GLOBAL = 65536;
        final int RTLD_LAZY = 4;
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put("open-flags", RTLD_MEMBER | RTLD_GLOBAL | RTLD_LAZY);
        return Collections.unmodifiableMap((Map<? extends String, ?>)options);
    }
}
