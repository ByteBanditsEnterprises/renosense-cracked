//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface Cfgmgr32 extends Library
{
    public static final Cfgmgr32 INSTANCE = (Cfgmgr32)Native.load("Cfgmgr32", (Class)Cfgmgr32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    public static final int CR_SUCCESS = 0;
    public static final int CR_DEFAULT = 1;
    public static final int CR_OUT_OF_MEMORY = 2;
    public static final int CR_INVALID_POINTER = 3;
    public static final int CR_INVALID_FLAG = 4;
    public static final int CR_INVALID_DEVNODE = 5;
    public static final int CR_INVALID_DEVINST = 5;
    public static final int CR_INVALID_RES_DES = 6;
    public static final int CR_INVALID_LOG_CONF = 7;
    public static final int CR_INVALID_ARBITRATOR = 8;
    public static final int CR_INVALID_NODELIST = 9;
    public static final int CR_DEVNODE_HAS_REQS = 10;
    public static final int CR_DEVINST_HAS_REQS = 10;
    public static final int CR_INVALID_RESOURCEID = 11;
    public static final int CR_DLVXD_NOT_FOUND = 12;
    public static final int CR_NO_SUCH_DEVNODE = 13;
    public static final int CR_NO_SUCH_DEVINST = 13;
    public static final int CR_NO_MORE_LOG_CONF = 14;
    public static final int CR_NO_MORE_RES_DES = 15;
    public static final int CR_ALREADY_SUCH_DEVNODE = 16;
    public static final int CR_ALREADY_SUCH_DEVINST = 16;
    public static final int CR_INVALID_RANGE_LIST = 17;
    public static final int CR_INVALID_RANGE = 18;
    public static final int CR_FAILURE = 19;
    public static final int CR_NO_SUCH_LOGICAL_DEV = 20;
    public static final int CR_CREATE_BLOCKED = 21;
    public static final int CR_NOT_SYSTEM_VM = 22;
    public static final int CR_REMOVE_VETOED = 23;
    public static final int CR_APM_VETOED = 24;
    public static final int CR_INVALID_LOAD_TYPE = 25;
    public static final int CR_BUFFER_SMALL = 26;
    public static final int CR_NO_ARBITRATOR = 27;
    public static final int CR_NO_REGISTRY_HANDLE = 28;
    public static final int CR_REGISTRY_ERROR = 29;
    public static final int CR_INVALID_DEVICE_ID = 30;
    public static final int CR_INVALID_DATA = 31;
    public static final int CR_INVALID_API = 32;
    public static final int CR_DEVLOADER_NOT_READY = 33;
    public static final int CR_NEED_RESTART = 34;
    public static final int CR_NO_MORE_HW_PROFILES = 35;
    public static final int CR_DEVICE_NOT_THERE = 36;
    public static final int CR_NO_SUCH_VALUE = 37;
    public static final int CR_WRONG_TYPE = 38;
    public static final int CR_INVALID_PRIORITY = 39;
    public static final int CR_NOT_DISABLEABLE = 40;
    public static final int CR_FREE_RESOURCES = 41;
    public static final int CR_QUERY_VETOED = 42;
    public static final int CR_CANT_SHARE_IRQ = 43;
    public static final int CR_NO_DEPENDENT = 44;
    public static final int CR_SAME_RESOURCES = 45;
    public static final int CR_NO_SUCH_REGISTRY_KEY = 46;
    public static final int CR_INVALID_MACHINENAME = 47;
    public static final int CR_REMOTE_COMM_FAILURE = 48;
    public static final int CR_MACHINE_UNAVAILABLE = 49;
    public static final int CR_NO_CM_SERVICES = 50;
    public static final int CR_ACCESS_DENIED = 51;
    public static final int CR_CALL_NOT_IMPLEMENTED = 52;
    public static final int CR_INVALID_PROPERTY = 53;
    public static final int CR_DEVICE_INTERFACE_ACTIVE = 54;
    public static final int CR_NO_SUCH_DEVICE_INTERFACE = 55;
    public static final int CR_INVALID_REFERENCE_STRING = 56;
    public static final int CR_INVALID_CONFLICT_LIST = 57;
    public static final int CR_INVALID_INDEX = 58;
    public static final int CR_INVALID_STRUCTURE_SIZE = 59;
    public static final int NUM_CR_RESULTS = 60;
    public static final int CM_LOCATE_DEVNODE_NORMAL = 0;
    public static final int CM_LOCATE_DEVNODE_PHANTOM = 1;
    public static final int CM_LOCATE_DEVNODE_CANCELREMOVE = 2;
    public static final int CM_LOCATE_DEVNODE_NOVALIDATION = 4;
    public static final int CM_LOCATE_DEVNODE_BITS = 7;
    public static final int CM_DRP_DEVICEDESC = 1;
    public static final int CM_DRP_HARDWAREID = 2;
    public static final int CM_DRP_COMPATIBLEIDS = 3;
    public static final int CM_DRP_SERVICE = 5;
    public static final int CM_DRP_CLASS = 8;
    public static final int CM_DRP_CLASSGUID = 9;
    public static final int CM_DRP_DRIVER = 10;
    public static final int CM_DRP_CONFIGFLAGS = 11;
    public static final int CM_DRP_MFG = 12;
    public static final int CM_DRP_FRIENDLYNAME = 13;
    public static final int CM_DRP_LOCATION_INFORMATION = 14;
    public static final int CM_DRP_PHYSICAL_DEVICE_OBJECT_NAME = 15;
    public static final int CM_DRP_CAPABILITIES = 16;
    public static final int CM_DRP_UI_NUMBER = 17;
    public static final int CM_DRP_UPPERFILTERS = 18;
    public static final int CM_DRP_LOWERFILTERS = 19;
    public static final int CM_DRP_BUSTYPEGUID = 20;
    public static final int CM_DRP_LEGACYBUSTYPE = 21;
    public static final int CM_DRP_BUSNUMBER = 22;
    public static final int CM_DRP_ENUMERATOR_NAME = 23;
    public static final int CM_DRP_SECURITY = 24;
    public static final int CM_DRP_SECURITY_SDS = 25;
    public static final int CM_DRP_DEVTYPE = 26;
    public static final int CM_DRP_EXCLUSIVE = 27;
    public static final int CM_DRP_CHARACTERISTICS = 28;
    public static final int CM_DRP_ADDRESS = 29;
    public static final int CM_DRP_UI_NUMBER_DESC_FORMAT = 30;
    public static final int CM_DRP_DEVICE_POWER_DATA = 31;
    public static final int CM_DRP_REMOVAL_POLICY = 32;
    public static final int CM_DRP_REMOVAL_POLICY_HW_DEFAULT = 33;
    public static final int CM_DRP_REMOVAL_POLICY_OVERRIDE = 34;
    public static final int CM_DRP_INSTALL_STATE = 35;
    public static final int CM_DRP_LOCATION_PATHS = 36;
    public static final int CM_DRP_BASE_CONTAINERID = 37;
    
    int CM_Locate_DevNode(final IntByReference p0, final String p1, final int p2);
    
    int CM_Get_Parent(final IntByReference p0, final int p1, final int p2);
    
    int CM_Get_Child(final IntByReference p0, final int p1, final int p2);
    
    int CM_Get_Sibling(final IntByReference p0, final int p1, final int p2);
    
    int CM_Get_Device_ID(final int p0, final Pointer p1, final int p2, final int p3);
    
    int CM_Get_Device_ID_Size(final IntByReference p0, final int p1, final int p2);
    
    int CM_Get_DevNode_Registry_Property(final int p0, final int p1, final IntByReference p2, final Pointer p3, final IntByReference p4, final int p5);
}
