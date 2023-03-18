//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.win32.*;
import com.sun.jna.platform.*;
import java.util.*;
import com.sun.jna.*;

public interface Dxva2 extends StdCallLibrary, PhysicalMonitorEnumerationAPI, HighLevelMonitorConfigurationAPI, LowLevelMonitorConfigurationAPI
{
    public static final Map<String, Object> DXVA_OPTIONS = Collections.unmodifiableMap((Map<? extends String, ?>)new HashMap<String, Object>() {
        private static final long serialVersionUID = -1987971664975780480L;
        
        {
            ((HashMap<String, Dxva2$1$1>)this).put("type-mapper", new DefaultTypeMapper() {
                {
                    this.addTypeConverter((Class)MC_POSITION_TYPE.class, (TypeConverter)new EnumConverter((Class)MC_POSITION_TYPE.class));
                    this.addTypeConverter((Class)MC_SIZE_TYPE.class, (TypeConverter)new EnumConverter((Class)MC_SIZE_TYPE.class));
                    this.addTypeConverter((Class)MC_GAIN_TYPE.class, (TypeConverter)new EnumConverter((Class)MC_GAIN_TYPE.class));
                    this.addTypeConverter((Class)MC_DRIVE_TYPE.class, (TypeConverter)new EnumConverter((Class)MC_DRIVE_TYPE.class));
                }
            });
        }
    });
    public static final Dxva2 INSTANCE = (Dxva2)Native.load("Dxva2", (Class)Dxva2.class, (Map)Dxva2.DXVA_OPTIONS);
    
    WinDef.BOOL GetMonitorCapabilities(final WinNT.HANDLE p0, final WinDef.DWORDByReference p1, final WinDef.DWORDByReference p2);
    
    WinDef.BOOL SaveCurrentMonitorSettings(final WinNT.HANDLE p0);
    
    WinDef.BOOL GetMonitorTechnologyType(final WinNT.HANDLE p0, final MC_DISPLAY_TECHNOLOGY_TYPE.ByReference p1);
    
    WinDef.BOOL GetMonitorBrightness(final WinNT.HANDLE p0, final WinDef.DWORDByReference p1, final WinDef.DWORDByReference p2, final WinDef.DWORDByReference p3);
    
    WinDef.BOOL GetMonitorContrast(final WinNT.HANDLE p0, final WinDef.DWORDByReference p1, final WinDef.DWORDByReference p2, final WinDef.DWORDByReference p3);
    
    WinDef.BOOL GetMonitorColorTemperature(final WinNT.HANDLE p0, final MC_COLOR_TEMPERATURE.ByReference p1);
    
    WinDef.BOOL GetMonitorRedGreenOrBlueDrive(final WinNT.HANDLE p0, final MC_DRIVE_TYPE p1, final WinDef.DWORDByReference p2, final WinDef.DWORDByReference p3, final WinDef.DWORDByReference p4);
    
    WinDef.BOOL GetMonitorRedGreenOrBlueGain(final WinNT.HANDLE p0, final MC_GAIN_TYPE p1, final WinDef.DWORDByReference p2, final WinDef.DWORDByReference p3, final WinDef.DWORDByReference p4);
    
    WinDef.BOOL SetMonitorBrightness(final WinNT.HANDLE p0, final int p1);
    
    WinDef.BOOL SetMonitorContrast(final WinNT.HANDLE p0, final int p1);
    
    WinDef.BOOL SetMonitorColorTemperature(final WinNT.HANDLE p0, final MC_COLOR_TEMPERATURE p1);
    
    WinDef.BOOL SetMonitorRedGreenOrBlueDrive(final WinNT.HANDLE p0, final MC_DRIVE_TYPE p1, final int p2);
    
    WinDef.BOOL SetMonitorRedGreenOrBlueGain(final WinNT.HANDLE p0, final MC_GAIN_TYPE p1, final int p2);
    
    WinDef.BOOL DegaussMonitor(final WinNT.HANDLE p0);
    
    WinDef.BOOL GetMonitorDisplayAreaSize(final WinNT.HANDLE p0, final MC_SIZE_TYPE p1, final WinDef.DWORDByReference p2, final WinDef.DWORDByReference p3, final WinDef.DWORDByReference p4);
    
    WinDef.BOOL GetMonitorDisplayAreaPosition(final WinNT.HANDLE p0, final MC_POSITION_TYPE p1, final WinDef.DWORDByReference p2, final WinDef.DWORDByReference p3, final WinDef.DWORDByReference p4);
    
    WinDef.BOOL SetMonitorDisplayAreaSize(final WinNT.HANDLE p0, final MC_SIZE_TYPE p1, final int p2);
    
    WinDef.BOOL SetMonitorDisplayAreaPosition(final WinNT.HANDLE p0, final MC_POSITION_TYPE p1, final int p2);
    
    WinDef.BOOL RestoreMonitorFactoryColorDefaults(final WinNT.HANDLE p0);
    
    WinDef.BOOL RestoreMonitorFactoryDefaults(final WinNT.HANDLE p0);
    
    WinDef.BOOL GetVCPFeatureAndVCPFeatureReply(final WinNT.HANDLE p0, final WinDef.BYTE p1, final MC_VCP_CODE_TYPE.ByReference p2, final WinDef.DWORDByReference p3, final WinDef.DWORDByReference p4);
    
    WinDef.BOOL SetVCPFeature(final WinNT.HANDLE p0, final WinDef.BYTE p1, final WinDef.DWORD p2);
    
    WinDef.BOOL SaveCurrentSettings(final WinNT.HANDLE p0);
    
    WinDef.BOOL GetCapabilitiesStringLength(final WinNT.HANDLE p0, final WinDef.DWORDByReference p1);
    
    WinDef.BOOL CapabilitiesRequestAndCapabilitiesReply(final WinNT.HANDLE p0, final WTypes.LPSTR p1, final WinDef.DWORD p2);
    
    WinDef.BOOL GetTimingReport(final WinNT.HANDLE p0, final MC_TIMING_REPORT p1);
    
    WinDef.BOOL GetNumberOfPhysicalMonitorsFromHMONITOR(final WinUser.HMONITOR p0, final WinDef.DWORDByReference p1);
    
    WinDef.BOOL GetPhysicalMonitorsFromHMONITOR(final WinUser.HMONITOR p0, final int p1, final PHYSICAL_MONITOR[] p2);
    
    WinDef.BOOL DestroyPhysicalMonitor(final WinNT.HANDLE p0);
    
    WinDef.BOOL DestroyPhysicalMonitors(final int p0, final PHYSICAL_MONITOR[] p1);
}
