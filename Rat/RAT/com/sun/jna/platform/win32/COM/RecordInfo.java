//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.*;
import com.sun.jna.*;

public class RecordInfo extends Unknown implements IRecordInfo
{
    public RecordInfo() {
    }
    
    public RecordInfo(final Pointer pvInstance) {
        super(pvInstance);
    }
    
    public WinNT.HRESULT RecordInit(final WinDef.PVOID pvNew) {
        return null;
    }
    
    public WinNT.HRESULT RecordClear(final WinDef.PVOID pvExisting) {
        return null;
    }
    
    public WinNT.HRESULT RecordCopy(final WinDef.PVOID pvExisting, final WinDef.PVOID pvNew) {
        return null;
    }
    
    public WinNT.HRESULT GetGuid(final Guid.GUID pguid) {
        return null;
    }
    
    public WinNT.HRESULT GetName(final WTypes.BSTR pbstrName) {
        return null;
    }
    
    public WinNT.HRESULT GetSize(final WinDef.ULONG pcbSize) {
        return null;
    }
    
    public WinNT.HRESULT GetTypeInfo(final ITypeInfo ppTypeInfo) {
        return null;
    }
    
    public WinNT.HRESULT GetField(final WinDef.PVOID pvData, final WString szFieldName, final Variant.VARIANT pvarField) {
        return null;
    }
    
    public WinNT.HRESULT GetFieldNoCopy(final WinDef.PVOID pvData, final WString szFieldName, final Variant.VARIANT pvarField, final WinDef.PVOID ppvDataCArray) {
        return null;
    }
    
    public WinNT.HRESULT PutField(final WinDef.ULONG wFlags, final WinDef.PVOID pvData, final WString szFieldName, final Variant.VARIANT pvarField) {
        return null;
    }
    
    public WinNT.HRESULT PutFieldNoCopy(final WinDef.ULONG wFlags, final WinDef.PVOID pvData, final WString szFieldName, final Variant.VARIANT pvarField) {
        return null;
    }
    
    public WinNT.HRESULT GetFieldNames(final WinDef.ULONG pcNames, final WTypes.BSTR rgBstrNames) {
        return null;
    }
    
    public WinDef.BOOL IsMatchingType(final IRecordInfo pRecordInfo) {
        return null;
    }
    
    public WinDef.PVOID RecordCreate() {
        return null;
    }
    
    public WinNT.HRESULT RecordCreateCopy(final WinDef.PVOID pvSource, final WinDef.PVOID ppvDest) {
        return null;
    }
    
    public WinNT.HRESULT RecordDestroy(final WinDef.PVOID pvRecord) {
        return null;
    }
    
    public static class ByReference extends RecordInfo implements Structure.ByReference
    {
    }
}
