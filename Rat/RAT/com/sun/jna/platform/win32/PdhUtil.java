//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.*;
import java.util.*;

public abstract class PdhUtil
{
    private static final int CHAR_TO_BYTES;
    private static final String ENGLISH_COUNTER_KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
    private static final String ENGLISH_COUNTER_VALUE = "Counter";
    
    public static String PdhLookupPerfNameByIndex(final String szMachineName, final int dwNameIndex) {
        WinDef.DWORDByReference pcchNameBufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
        int result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, (Pointer)null, pcchNameBufferSize);
        Memory mem = null;
        if (result != -1073738819) {
            if (result != 0 && result != -2147481646) {
                throw new PdhException(result);
            }
            if (pcchNameBufferSize.getValue().intValue() < 1) {
                return "";
            }
            mem = new Memory((long)(pcchNameBufferSize.getValue().intValue() * PdhUtil.CHAR_TO_BYTES));
            result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, (Pointer)mem, pcchNameBufferSize);
        }
        else {
            for (int bufferSize = 32; bufferSize <= 1024; bufferSize *= 2) {
                pcchNameBufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(bufferSize));
                mem = new Memory((long)(bufferSize * PdhUtil.CHAR_TO_BYTES));
                result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, (Pointer)mem, pcchNameBufferSize);
                if (result != -1073738819 && result != -1073738814) {
                    break;
                }
            }
        }
        if (result != 0) {
            throw new PdhException(result);
        }
        if (PdhUtil.CHAR_TO_BYTES == 1) {
            return mem.getString(0L);
        }
        return mem.getWideString(0L);
    }
    
    public static int PdhLookupPerfIndexByEnglishName(final String szNameBuffer) {
        final String[] counters = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter");
        for (int i = 1; i < counters.length; i += 2) {
            if (counters[i].equals(szNameBuffer)) {
                try {
                    return Integer.parseInt(counters[i - 1]);
                }
                catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public static PdhEnumObjectItems PdhEnumObjectItems(final String szDataSource, final String szMachineName, final String szObjectName, final int dwDetailLevel) {
        final List<String> counters = new ArrayList<String>();
        final List<String> instances = new ArrayList<String>();
        final WinDef.DWORDByReference pcchCounterListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
        final WinDef.DWORDByReference pcchInstanceListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
        int result = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, (Pointer)null, pcchCounterListLength, (Pointer)null, pcchInstanceListLength, dwDetailLevel, 0);
        if (result != 0 && result != -2147481646) {
            throw new PdhException(result);
        }
        Memory mszCounterList = null;
        Memory mszInstanceList = null;
        do {
            if (pcchCounterListLength.getValue().intValue() > 0) {
                mszCounterList = new Memory((long)(pcchCounterListLength.getValue().intValue() * PdhUtil.CHAR_TO_BYTES));
            }
            if (pcchInstanceListLength.getValue().intValue() > 0) {
                mszInstanceList = new Memory((long)(pcchInstanceListLength.getValue().intValue() * PdhUtil.CHAR_TO_BYTES));
            }
            result = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, (Pointer)mszCounterList, pcchCounterListLength, (Pointer)mszInstanceList, pcchInstanceListLength, dwDetailLevel, 0);
            if (result == -2147481646) {
                if (mszCounterList != null) {
                    final long tooSmallSize = mszCounterList.size() / PdhUtil.CHAR_TO_BYTES;
                    pcchCounterListLength.setValue(new WinDef.DWORD(tooSmallSize + 1024L));
                    mszCounterList.close();
                }
                if (mszInstanceList == null) {
                    continue;
                }
                final long tooSmallSize = mszInstanceList.size() / PdhUtil.CHAR_TO_BYTES;
                pcchInstanceListLength.setValue(new WinDef.DWORD(tooSmallSize + 1024L));
                mszInstanceList.close();
            }
        } while (result == -2147481646);
        if (result != 0) {
            throw new PdhException(result);
        }
        if (mszCounterList != null) {
            String s;
            for (int offset = 0; offset < mszCounterList.size(); offset += (s.length() + 1) * PdhUtil.CHAR_TO_BYTES) {
                s = null;
                if (PdhUtil.CHAR_TO_BYTES == 1) {
                    s = mszCounterList.getString((long)offset);
                }
                else {
                    s = mszCounterList.getWideString((long)offset);
                }
                if (s.isEmpty()) {
                    break;
                }
                counters.add(s);
            }
        }
        if (mszInstanceList != null) {
            String s;
            for (int offset = 0; offset < mszInstanceList.size(); offset += (s.length() + 1) * PdhUtil.CHAR_TO_BYTES) {
                s = null;
                if (PdhUtil.CHAR_TO_BYTES == 1) {
                    s = mszInstanceList.getString((long)offset);
                }
                else {
                    s = mszInstanceList.getWideString((long)offset);
                }
                if (s.isEmpty()) {
                    break;
                }
                instances.add(s);
            }
        }
        return new PdhEnumObjectItems(counters, instances);
    }
    
    static {
        CHAR_TO_BYTES = (Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE);
    }
    
    public static class PdhEnumObjectItems
    {
        private final List<String> counters;
        private final List<String> instances;
        
        public PdhEnumObjectItems(final List<String> counters, final List<String> instances) {
            this.counters = this.copyAndEmptyListForNullList(counters);
            this.instances = this.copyAndEmptyListForNullList(instances);
        }
        
        public List<String> getCounters() {
            return this.counters;
        }
        
        public List<String> getInstances() {
            return this.instances;
        }
        
        private List<String> copyAndEmptyListForNullList(final List<String> inputList) {
            if (inputList == null) {
                return new ArrayList<String>();
            }
            return new ArrayList<String>(inputList);
        }
        
        @Override
        public String toString() {
            return "PdhEnumObjectItems{counters=" + this.counters + ", instances=" + this.instances + '}';
        }
    }
    
    public static final class PdhException extends RuntimeException
    {
        private final int errorCode;
        
        public PdhException(final int errorCode) {
            super(String.format("Pdh call failed with error code 0x%08X", errorCode));
            this.errorCode = errorCode;
        }
        
        public int getErrorCode() {
            return this.errorCode;
        }
    }
}
