//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.*;

public interface PhysicalMonitorEnumerationAPI
{
    public static final int PHYSICAL_MONITOR_DESCRIPTION_SIZE = 128;
    
    @FieldOrder({ "hPhysicalMonitor", "szPhysicalMonitorDescription" })
    public static class PHYSICAL_MONITOR extends Structure
    {
        public WinNT.HANDLE hPhysicalMonitor;
        public char[] szPhysicalMonitorDescription;
        
        public PHYSICAL_MONITOR() {
            this.szPhysicalMonitorDescription = new char[128];
        }
    }
}
