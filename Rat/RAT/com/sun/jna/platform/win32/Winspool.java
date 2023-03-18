//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import java.util.*;
import com.sun.jna.*;

public interface Winspool extends StdCallLibrary
{
    public static final Winspool INSTANCE = (Winspool)Native.load("Winspool.drv", (Class)Winspool.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    public static final int CCHDEVICENAME = 32;
    public static final int PRINTER_STATUS_PAUSED = 1;
    public static final int PRINTER_STATUS_ERROR = 2;
    public static final int PRINTER_STATUS_PENDING_DELETION = 4;
    public static final int PRINTER_STATUS_PAPER_JAM = 8;
    public static final int PRINTER_STATUS_PAPER_OUT = 16;
    public static final int PRINTER_STATUS_MANUAL_FEED = 32;
    public static final int PRINTER_STATUS_PAPER_PROBLEM = 64;
    public static final int PRINTER_STATUS_OFFLINE = 128;
    public static final int PRINTER_STATUS_IO_ACTIVE = 256;
    public static final int PRINTER_STATUS_BUSY = 512;
    public static final int PRINTER_STATUS_PRINTING = 1024;
    public static final int PRINTER_STATUS_OUTPUT_BIN_FULL = 2048;
    public static final int PRINTER_STATUS_NOT_AVAILABLE = 4096;
    public static final int PRINTER_STATUS_WAITING = 8192;
    public static final int PRINTER_STATUS_PROCESSING = 16384;
    public static final int PRINTER_STATUS_INITIALIZING = 32768;
    public static final int PRINTER_STATUS_WARMING_UP = 65536;
    public static final int PRINTER_STATUS_TONER_LOW = 131072;
    public static final int PRINTER_STATUS_NO_TONER = 262144;
    public static final int PRINTER_STATUS_PAGE_PUNT = 524288;
    public static final int PRINTER_STATUS_USER_INTERVENTION = 1048576;
    public static final int PRINTER_STATUS_OUT_OF_MEMORY = 2097152;
    public static final int PRINTER_STATUS_DOOR_OPEN = 4194304;
    public static final int PRINTER_STATUS_SERVER_UNKNOWN = 8388608;
    public static final int PRINTER_STATUS_POWER_SAVE = 16777216;
    public static final int PRINTER_ATTRIBUTE_QUEUED = 1;
    public static final int PRINTER_ATTRIBUTE_DIRECT = 2;
    public static final int PRINTER_ATTRIBUTE_DEFAULT = 4;
    public static final int PRINTER_ATTRIBUTE_SHARED = 8;
    public static final int PRINTER_ATTRIBUTE_NETWORK = 16;
    public static final int PRINTER_ATTRIBUTE_HIDDEN = 32;
    public static final int PRINTER_ATTRIBUTE_LOCAL = 64;
    public static final int PRINTER_ATTRIBUTE_ENABLE_DEVQ = 128;
    public static final int PRINTER_ATTRIBUTE_KEEPPRINTEDJOBS = 256;
    public static final int PRINTER_ATTRIBUTE_DO_COMPLETE_FIRST = 512;
    public static final int PRINTER_ATTRIBUTE_WORK_OFFLINE = 1024;
    public static final int PRINTER_ATTRIBUTE_ENABLE_BIDI = 2048;
    public static final int PRINTER_ATTRIBUTE_RAW_ONLY = 4096;
    public static final int PRINTER_ATTRIBUTE_PUBLISHED = 8192;
    public static final int PRINTER_ATTRIBUTE_FAX = 16384;
    public static final int PRINTER_ATTRIBUTE_TS = 32768;
    public static final int PRINTER_ATTRIBUTE_PUSHED_USER = 131072;
    public static final int PRINTER_ATTRIBUTE_PUSHED_MACHINE = 262144;
    public static final int PRINTER_ATTRIBUTE_MACHINE = 524288;
    public static final int PRINTER_ATTRIBUTE_FRIENDLY_NAME = 1048576;
    public static final int PRINTER_ATTRIBUTE_TS_GENERIC_DRIVER = 2097152;
    public static final int PRINTER_CHANGE_ADD_PRINTER = 1;
    public static final int PRINTER_CHANGE_SET_PRINTER = 2;
    public static final int PRINTER_CHANGE_DELETE_PRINTER = 4;
    public static final int PRINTER_CHANGE_FAILED_CONNECTION_PRINTER = 8;
    public static final int PRINTER_CHANGE_PRINTER = 255;
    public static final int PRINTER_CHANGE_ADD_JOB = 256;
    public static final int PRINTER_CHANGE_SET_JOB = 512;
    public static final int PRINTER_CHANGE_DELETE_JOB = 1024;
    public static final int PRINTER_CHANGE_WRITE_JOB = 2048;
    public static final int PRINTER_CHANGE_JOB = 65280;
    public static final int PRINTER_CHANGE_ADD_FORM = 65536;
    public static final int PRINTER_CHANGE_SET_FORM = 131072;
    public static final int PRINTER_CHANGE_DELETE_FORM = 262144;
    public static final int PRINTER_CHANGE_FORM = 458752;
    public static final int PRINTER_CHANGE_ADD_PORT = 1048576;
    public static final int PRINTER_CHANGE_CONFIGURE_PORT = 2097152;
    public static final int PRINTER_CHANGE_DELETE_PORT = 4194304;
    public static final int PRINTER_CHANGE_PORT = 7340032;
    public static final int PRINTER_CHANGE_ADD_PRINT_PROCESSOR = 16777216;
    public static final int PRINTER_CHANGE_DELETE_PRINT_PROCESSOR = 67108864;
    public static final int PRINTER_CHANGE_PRINT_PROCESSOR = 117440512;
    public static final int PRINTER_CHANGE_SERVER = 134217728;
    public static final int PRINTER_CHANGE_ADD_PRINTER_DRIVER = 268435456;
    public static final int PRINTER_CHANGE_SET_PRINTER_DRIVER = 536870912;
    public static final int PRINTER_CHANGE_DELETE_PRINTER_DRIVER = 1073741824;
    public static final int PRINTER_CHANGE_PRINTER_DRIVER = 1879048192;
    public static final int PRINTER_CHANGE_TIMEOUT = Integer.MIN_VALUE;
    public static final int PRINTER_CHANGE_ALL_WIN7 = 2138570751;
    public static final int PRINTER_CHANGE_ALL = 2004353023;
    public static final int PRINTER_ENUM_DEFAULT = 1;
    public static final int PRINTER_ENUM_LOCAL = 2;
    public static final int PRINTER_ENUM_CONNECTIONS = 4;
    public static final int PRINTER_ENUM_FAVORITE = 4;
    public static final int PRINTER_ENUM_NAME = 8;
    public static final int PRINTER_ENUM_REMOTE = 16;
    public static final int PRINTER_ENUM_SHARED = 32;
    public static final int PRINTER_ENUM_NETWORK = 64;
    public static final int PRINTER_ENUM_EXPAND = 16384;
    public static final int PRINTER_ENUM_CONTAINER = 32768;
    public static final int PRINTER_ENUM_ICONMASK = 16711680;
    public static final int PRINTER_ENUM_ICON1 = 65536;
    public static final int PRINTER_ENUM_ICON2 = 131072;
    public static final int PRINTER_ENUM_ICON3 = 262144;
    public static final int PRINTER_ENUM_ICON4 = 524288;
    public static final int PRINTER_ENUM_ICON5 = 1048576;
    public static final int PRINTER_ENUM_ICON6 = 2097152;
    public static final int PRINTER_ENUM_ICON7 = 4194304;
    public static final int PRINTER_ENUM_ICON8 = 8388608;
    public static final int PRINTER_ENUM_HIDE = 16777216;
    public static final int PRINTER_NOTIFY_OPTIONS_REFRESH = 1;
    public static final int PRINTER_NOTIFY_INFO_DISCARDED = 1;
    public static final int PRINTER_NOTIFY_TYPE = 0;
    public static final int JOB_NOTIFY_TYPE = 1;
    public static final short PRINTER_NOTIFY_FIELD_SERVER_NAME = 0;
    public static final short PRINTER_NOTIFY_FIELD_PRINTER_NAME = 1;
    public static final short PRINTER_NOTIFY_FIELD_SHARE_NAME = 2;
    public static final short PRINTER_NOTIFY_FIELD_PORT_NAME = 3;
    public static final short PRINTER_NOTIFY_FIELD_DRIVER_NAME = 4;
    public static final short PRINTER_NOTIFY_FIELD_COMMENT = 5;
    public static final short PRINTER_NOTIFY_FIELD_LOCATION = 6;
    public static final short PRINTER_NOTIFY_FIELD_DEVMODE = 7;
    public static final short PRINTER_NOTIFY_FIELD_SEPFILE = 8;
    public static final short PRINTER_NOTIFY_FIELD_PRINT_PROCESSOR = 9;
    public static final short PRINTER_NOTIFY_FIELD_PARAMETERS = 10;
    public static final short PRINTER_NOTIFY_FIELD_DATATYPE = 11;
    public static final short PRINTER_NOTIFY_FIELD_SECURITY_DESCRIPTOR = 12;
    public static final short PRINTER_NOTIFY_FIELD_ATTRIBUTES = 13;
    public static final short PRINTER_NOTIFY_FIELD_PRIORITY = 14;
    public static final short PRINTER_NOTIFY_FIELD_DEFAULT_PRIORITY = 15;
    public static final short PRINTER_NOTIFY_FIELD_START_TIME = 16;
    public static final short PRINTER_NOTIFY_FIELD_UNTIL_TIME = 17;
    public static final short PRINTER_NOTIFY_FIELD_STATUS = 18;
    public static final short PRINTER_NOTIFY_FIELD_STATUS_STRING = 19;
    public static final short PRINTER_NOTIFY_FIELD_CJOBS = 20;
    public static final short PRINTER_NOTIFY_FIELD_AVERAGE_PPM = 21;
    public static final short PRINTER_NOTIFY_FIELD_TOTAL_PAGES = 22;
    public static final short PRINTER_NOTIFY_FIELD_PAGES_PRINTED = 23;
    public static final short PRINTER_NOTIFY_FIELD_TOTAL_BYTES = 24;
    public static final short PRINTER_NOTIFY_FIELD_BYTES_PRINTED = 25;
    public static final short PRINTER_NOTIFY_FIELD_OBJECT_GUID = 26;
    public static final short PRINTER_NOTIFY_FIELD_FRIENDLY_NAME = 27;
    public static final short PRINTER_NOTIFY_FIELD_BRANCH_OFFICE_PRINTING = 28;
    public static final short JOB_NOTIFY_FIELD_PRINTER_NAME = 0;
    public static final short JOB_NOTIFY_FIELD_MACHINE_NAME = 1;
    public static final short JOB_NOTIFY_FIELD_PORT_NAME = 2;
    public static final short JOB_NOTIFY_FIELD_USER_NAME = 3;
    public static final short JOB_NOTIFY_FIELD_NOTIFY_NAME = 4;
    public static final short JOB_NOTIFY_FIELD_DATATYPE = 5;
    public static final short JOB_NOTIFY_FIELD_PRINT_PROCESSOR = 6;
    public static final short JOB_NOTIFY_FIELD_PARAMETERS = 7;
    public static final short JOB_NOTIFY_FIELD_DRIVER_NAME = 8;
    public static final short JOB_NOTIFY_FIELD_DEVMODE = 9;
    public static final short JOB_NOTIFY_FIELD_STATUS = 10;
    public static final short JOB_NOTIFY_FIELD_STATUS_STRING = 11;
    public static final short JOB_NOTIFY_FIELD_SECURITY_DESCRIPTOR = 12;
    public static final short JOB_NOTIFY_FIELD_DOCUMENT = 13;
    public static final short JOB_NOTIFY_FIELD_PRIORITY = 14;
    public static final short JOB_NOTIFY_FIELD_POSITION = 15;
    public static final short JOB_NOTIFY_FIELD_SUBMITTED = 16;
    public static final short JOB_NOTIFY_FIELD_START_TIME = 17;
    public static final short JOB_NOTIFY_FIELD_UNTIL_TIME = 18;
    public static final short JOB_NOTIFY_FIELD_TIME = 19;
    public static final short JOB_NOTIFY_FIELD_TOTAL_PAGES = 20;
    public static final short JOB_NOTIFY_FIELD_PAGES_PRINTED = 21;
    public static final short JOB_NOTIFY_FIELD_TOTAL_BYTES = 22;
    public static final short JOB_NOTIFY_FIELD_BYTES_PRINTED = 23;
    public static final short JOB_NOTIFY_FIELD_REMOTE_JOB_ID = 24;
    public static final int PRINTER_NOTIFY_CATEGORY_ALL = 4096;
    public static final int PRINTER_NOTIFY_CATEGORY_3D = 8192;
    
    boolean EnumPrinters(final int p0, final String p1, final int p2, final Pointer p3, final int p4, final IntByReference p5, final IntByReference p6);
    
    boolean GetPrinter(final WinNT.HANDLE p0, final int p1, final Pointer p2, final int p3, final IntByReference p4);
    
    boolean OpenPrinter(final String p0, final WinNT.HANDLEByReference p1, final LPPRINTER_DEFAULTS p2);
    
    boolean ClosePrinter(final WinNT.HANDLE p0);
    
    @Deprecated
    WinNT.HANDLE FindFirstPrinterChangeNotification(final WinNT.HANDLE p0, final int p1, final int p2, final WinDef.LPVOID p3);
    
    WinNT.HANDLE FindFirstPrinterChangeNotification(final WinNT.HANDLE p0, final int p1, final int p2, final PRINTER_NOTIFY_OPTIONS p3);
    
    @Deprecated
    boolean FindNextPrinterChangeNotification(final WinNT.HANDLE p0, final WinDef.DWORDByReference p1, final WinDef.LPVOID p2, final WinDef.LPVOID p3);
    
    boolean FindNextPrinterChangeNotification(final WinNT.HANDLE p0, final WinDef.DWORDByReference p1, final PRINTER_NOTIFY_OPTIONS p2, final PointerByReference p3);
    
    boolean FindClosePrinterChangeNotification(final WinNT.HANDLE p0);
    
    boolean FreePrinterNotifyInfo(final Pointer p0);
    
    boolean EnumJobs(final WinNT.HANDLE p0, final int p1, final int p2, final int p3, final Pointer p4, final int p5, final IntByReference p6, final IntByReference p7);
    
    @FieldOrder({ "Flags", "pDescription", "pName", "pComment" })
    public static class PRINTER_INFO_1 extends Structure
    {
        public int Flags;
        public String pDescription;
        public String pName;
        public String pComment;
        
        public PRINTER_INFO_1() {
        }
        
        public PRINTER_INFO_1(final int size) {
            super((Pointer)new Memory((long)size));
        }
    }
    
    @FieldOrder({ "pServerName", "pPrinterName", "pShareName", "pPortName", "pDriverName", "pComment", "pLocation", "pDevMode", "pSepFile", "pPrintProcessor", "pDatatype", "pParameters", "pSecurityDescriptor", "Attributes", "Priority", "DefaultPriority", "StartTime", "UntilTime", "Status", "cJobs", "AveragePPM" })
    public static class PRINTER_INFO_2 extends Structure
    {
        public String pServerName;
        public String pPrinterName;
        public String pShareName;
        public String pPortName;
        public String pDriverName;
        public String pComment;
        public String pLocation;
        public WinDef.INT_PTR pDevMode;
        public String pSepFile;
        public String pPrintProcessor;
        public String pDatatype;
        public String pParameters;
        public WinDef.INT_PTR pSecurityDescriptor;
        public int Attributes;
        public int Priority;
        public int DefaultPriority;
        public int StartTime;
        public int UntilTime;
        public int Status;
        public int cJobs;
        public int AveragePPM;
        
        public PRINTER_INFO_2() {
        }
        
        public PRINTER_INFO_2(final int size) {
            super((Pointer)new Memory((long)size));
        }
        
        public boolean hasAttribute(final int value) {
            return (this.Attributes & value) == value;
        }
    }
    
    @FieldOrder({ "pPrinterName", "pServerName", "Attributes" })
    public static class PRINTER_INFO_4 extends Structure
    {
        public String pPrinterName;
        public String pServerName;
        public WinDef.DWORD Attributes;
        
        public PRINTER_INFO_4() {
        }
        
        public PRINTER_INFO_4(final int size) {
            super((Pointer)new Memory((long)size));
        }
    }
    
    @FieldOrder({ "pDatatype", "pDevMode", "DesiredAccess" })
    public static class LPPRINTER_DEFAULTS extends Structure
    {
        public String pDatatype;
        public Pointer pDevMode;
        public int DesiredAccess;
    }
    
    @FieldOrder({ "Version", "Flags", "Count", "pTypes" })
    public static class PRINTER_NOTIFY_OPTIONS extends Structure
    {
        public int Version;
        public int Flags;
        public int Count;
        public PRINTER_NOTIFY_OPTIONS_TYPE.ByReference pTypes;
        
        public PRINTER_NOTIFY_OPTIONS() {
            this.Version = 2;
        }
    }
    
    @FieldOrder({ "Type", "Reserved0", "Reserved1", "Reserved2", "Count", "pFields" })
    public static class PRINTER_NOTIFY_OPTIONS_TYPE extends Structure
    {
        public short Type;
        public short Reserved0;
        public int Reserved1;
        public int Reserved2;
        public int Count;
        public Pointer pFields;
        
        public void setFields(final short[] fields) {
            final long shortSizeInBytes = 2L;
            final Memory fieldsMemory = new Memory(fields.length * 2L);
            fieldsMemory.write(0L, fields, 0, fields.length);
            this.pFields = (Pointer)fieldsMemory;
            this.Count = fields.length;
        }
        
        public short[] getFields() {
            return this.pFields.getShortArray(0L, this.Count);
        }
        
        public static class ByReference extends PRINTER_NOTIFY_OPTIONS_TYPE implements Structure.ByReference
        {
        }
    }
    
    @FieldOrder({ "Version", "Flags", "Count", "aData" })
    public static class PRINTER_NOTIFY_INFO extends Structure
    {
        public int Version;
        public int Flags;
        public int Count;
        public PRINTER_NOTIFY_INFO_DATA[] aData;
        
        public PRINTER_NOTIFY_INFO() {
            this.aData = new PRINTER_NOTIFY_INFO_DATA[1];
        }
        
        @Override
        public void read() {
            final int count = (int)this.readField("Count");
            this.aData = new PRINTER_NOTIFY_INFO_DATA[count];
            if (count == 0) {
                this.Count = count;
                this.Version = (int)this.readField("Version");
                this.Flags = (int)this.readField("Flags");
            }
            else {
                super.read();
            }
        }
    }
    
    @FieldOrder({ "cbBuf", "pBuf" })
    public static class NOTIFY_DATA_DATA extends Structure
    {
        public int cbBuf;
        public Pointer pBuf;
    }
    
    public static class NOTIFY_DATA extends Union
    {
        public int[] adwData;
        public NOTIFY_DATA_DATA Data;
        
        public NOTIFY_DATA() {
            this.adwData = new int[2];
        }
    }
    
    @FieldOrder({ "Type", "Field", "Reserved", "Id", "NotifyData" })
    public static class PRINTER_NOTIFY_INFO_DATA extends Structure
    {
        public short Type;
        public short Field;
        public int Reserved;
        public int Id;
        public NOTIFY_DATA NotifyData;
        
        @Override
        public void read() {
            super.read();
            boolean numericData;
            if (this.Type == 0) {
                switch (this.Field) {
                }
                numericData = false;
            }
            else {
                switch (this.Field) {
                }
                numericData = false;
            }
            if (numericData) {
                this.NotifyData.setType(int[].class);
            }
            else {
                this.NotifyData.setType(NOTIFY_DATA_DATA.class);
            }
            this.NotifyData.read();
        }
    }
    
    @FieldOrder({ "JobId", "pPrinterName", "pMachineName", "pUserName", "pDocument", "pDatatype", "pStatus", "Status", "Priority", "Position", "TotalPages", "PagesPrinted", "Submitted" })
    public static class JOB_INFO_1 extends Structure
    {
        public int JobId;
        public String pPrinterName;
        public String pMachineName;
        public String pUserName;
        public String pDocument;
        public String pDatatype;
        public String pStatus;
        public int Status;
        public int Priority;
        public int Position;
        public int TotalPages;
        public int PagesPrinted;
        public WinBase.SYSTEMTIME Submitted;
        
        public JOB_INFO_1() {
        }
        
        public JOB_INFO_1(final int size) {
            super((Pointer)new Memory((long)size));
        }
    }
}
