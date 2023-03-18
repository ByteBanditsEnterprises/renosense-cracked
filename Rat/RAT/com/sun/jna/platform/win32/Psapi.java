//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import java.util.*;
import com.sun.jna.*;

public interface Psapi extends StdCallLibrary
{
    public static final Psapi INSTANCE = (Psapi)Native.load("psapi", (Class)Psapi.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    
    int GetModuleFileNameExA(final WinNT.HANDLE p0, final WinNT.HANDLE p1, final byte[] p2, final int p3);
    
    int GetModuleFileNameExW(final WinNT.HANDLE p0, final WinNT.HANDLE p1, final char[] p2, final int p3);
    
    int GetModuleFileNameEx(final WinNT.HANDLE p0, final WinNT.HANDLE p1, final Pointer p2, final int p3);
    
    boolean EnumProcessModules(final WinNT.HANDLE p0, final WinDef.HMODULE[] p1, final int p2, final IntByReference p3);
    
    boolean GetModuleInformation(final WinNT.HANDLE p0, final WinDef.HMODULE p1, final MODULEINFO p2, final int p3);
    
    int GetProcessImageFileName(final WinNT.HANDLE p0, final char[] p1, final int p2);
    
    boolean GetPerformanceInfo(final PERFORMANCE_INFORMATION p0, final int p1);
    
    boolean EnumProcesses(final int[] p0, final int p1, final IntByReference p2);
    
    boolean QueryWorkingSetEx(final WinNT.HANDLE p0, final Pointer p1, final int p2);
    
    @FieldOrder({ "lpBaseOfDll", "SizeOfImage", "EntryPoint" })
    public static class MODULEINFO extends Structure
    {
        public Pointer EntryPoint;
        public Pointer lpBaseOfDll;
        public int SizeOfImage;
    }
    
    @FieldOrder({ "cb", "CommitTotal", "CommitLimit", "CommitPeak", "PhysicalTotal", "PhysicalAvailable", "SystemCache", "KernelTotal", "KernelPaged", "KernelNonpaged", "PageSize", "HandleCount", "ProcessCount", "ThreadCount" })
    public static class PERFORMANCE_INFORMATION extends Structure
    {
        public WinDef.DWORD cb;
        public BaseTSD.SIZE_T CommitTotal;
        public BaseTSD.SIZE_T CommitLimit;
        public BaseTSD.SIZE_T CommitPeak;
        public BaseTSD.SIZE_T PhysicalTotal;
        public BaseTSD.SIZE_T PhysicalAvailable;
        public BaseTSD.SIZE_T SystemCache;
        public BaseTSD.SIZE_T KernelTotal;
        public BaseTSD.SIZE_T KernelPaged;
        public BaseTSD.SIZE_T KernelNonpaged;
        public BaseTSD.SIZE_T PageSize;
        public WinDef.DWORD HandleCount;
        public WinDef.DWORD ProcessCount;
        public WinDef.DWORD ThreadCount;
    }
    
    @FieldOrder({ "VirtualAddress", "VirtualAttributes" })
    public static class PSAPI_WORKING_SET_EX_INFORMATION extends Structure
    {
        public Pointer VirtualAddress;
        public BaseTSD.ULONG_PTR VirtualAttributes;
        
        public boolean isValid() {
            return this.getBitFieldValue(1, 0) == 1;
        }
        
        public int getShareCount() {
            return this.getBitFieldValue(3, 1);
        }
        
        public int getWin32Protection() {
            return this.getBitFieldValue(11, 4);
        }
        
        public boolean isShared() {
            return this.getBitFieldValue(1, 15) == 1;
        }
        
        public int getNode() {
            return this.getBitFieldValue(6, 16);
        }
        
        public boolean isLocked() {
            return this.getBitFieldValue(1, 22) == 1;
        }
        
        public boolean isLargePage() {
            return this.getBitFieldValue(1, 23) == 1;
        }
        
        public boolean isBad() {
            return this.getBitFieldValue(1, 25) == 1;
        }
        
        private int getBitFieldValue(final int maskLength, final int rightShiftAmount) {
            long bitMask = 0L;
            for (int l = 0; l < maskLength; ++l) {
                bitMask |= 1 << l;
            }
            return (int)(this.VirtualAttributes.longValue() >>> rightShiftAmount & bitMask);
        }
    }
}
