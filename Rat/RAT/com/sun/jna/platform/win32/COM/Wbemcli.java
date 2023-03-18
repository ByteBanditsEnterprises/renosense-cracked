//Raddon On Top!

package com.sun.jna.platform.win32.COM;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.platform.win32.*;

public interface Wbemcli
{
    public static final int WBEM_FLAG_RETURN_WBEM_COMPLETE = 0;
    public static final int WBEM_FLAG_RETURN_IMMEDIATELY = 16;
    public static final int WBEM_FLAG_FORWARD_ONLY = 32;
    public static final int WBEM_FLAG_NO_ERROR_OBJECT = 64;
    public static final int WBEM_FLAG_SEND_STATUS = 128;
    public static final int WBEM_FLAG_ENSURE_LOCATABLE = 256;
    public static final int WBEM_FLAG_DIRECT_READ = 512;
    public static final int WBEM_MASK_RESERVED_FLAGS = 126976;
    public static final int WBEM_FLAG_USE_AMENDED_QUALIFIERS = 131072;
    public static final int WBEM_FLAG_STRONG_VALIDATION = 1048576;
    public static final int WBEM_INFINITE = -1;
    public static final int WBEM_S_NO_ERROR = 0;
    public static final int WBEM_S_FALSE = 1;
    public static final int WBEM_S_TIMEDOUT = 262148;
    public static final int WBEM_S_NO_MORE_DATA = 262149;
    public static final int WBEM_E_INVALID_NAMESPACE = -2147217394;
    public static final int WBEM_E_INVALID_CLASS = -2147217392;
    public static final int WBEM_E_INVALID_QUERY = -2147217385;
    public static final int CIM_ILLEGAL = 4095;
    public static final int CIM_EMPTY = 0;
    public static final int CIM_SINT8 = 16;
    public static final int CIM_UINT8 = 17;
    public static final int CIM_SINT16 = 2;
    public static final int CIM_UINT16 = 18;
    public static final int CIM_SINT32 = 3;
    public static final int CIM_UINT32 = 19;
    public static final int CIM_SINT64 = 20;
    public static final int CIM_UINT64 = 21;
    public static final int CIM_REAL32 = 4;
    public static final int CIM_REAL64 = 5;
    public static final int CIM_BOOLEAN = 11;
    public static final int CIM_STRING = 8;
    public static final int CIM_DATETIME = 101;
    public static final int CIM_REFERENCE = 102;
    public static final int CIM_CHAR16 = 103;
    public static final int CIM_OBJECT = 13;
    public static final int CIM_FLAG_ARRAY = 8192;
    
    public static class IWbemClassObject extends Unknown
    {
        public IWbemClassObject() {
        }
        
        public IWbemClassObject(final Pointer pvInstance) {
            super(pvInstance);
        }
        
        public WinNT.HRESULT Get(final WString wszName, final int lFlags, final Variant.VARIANT.ByReference pVal, final IntByReference pType, final IntByReference plFlavor) {
            return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), wszName, lFlags, pVal, pType, plFlavor }, (Class)WinNT.HRESULT.class);
        }
        
        public WinNT.HRESULT Get(final String wszName, final int lFlags, final Variant.VARIANT.ByReference pVal, final IntByReference pType, final IntByReference plFlavor) {
            return this.Get((wszName == null) ? null : new WString(wszName), lFlags, pVal, pType, plFlavor);
        }
        
        public WinNT.HRESULT GetNames(final String wszQualifierName, final int lFlags, final Variant.VARIANT.ByReference pQualifierVal, final PointerByReference pNames) {
            return this.GetNames((wszQualifierName == null) ? null : new WString(wszQualifierName), lFlags, pQualifierVal, pNames);
        }
        
        public WinNT.HRESULT GetNames(final WString wszQualifierName, final int lFlags, final Variant.VARIANT.ByReference pQualifierVal, final PointerByReference pNames) {
            return (WinNT.HRESULT)this._invokeNativeObject(7, new Object[] { this.getPointer(), wszQualifierName, lFlags, pQualifierVal, pNames }, (Class)WinNT.HRESULT.class);
        }
        
        public String[] GetNames(final String wszQualifierName, final int lFlags, final Variant.VARIANT.ByReference pQualifierVal) {
            final PointerByReference pbr = new PointerByReference();
            COMUtils.checkRC(this.GetNames(wszQualifierName, lFlags, pQualifierVal, pbr));
            final Object[] nameObjects = (Object[])OaIdlUtil.toPrimitiveArray(new OaIdl.SAFEARRAY(pbr.getValue()), true);
            final String[] names = new String[nameObjects.length];
            for (int i = 0; i < nameObjects.length; ++i) {
                names[i] = (String)nameObjects[i];
            }
            return names;
        }
        
        public WinNT.HRESULT GetQualifierSet(final PointerByReference ppQualSet) {
            return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[] { this.getPointer(), ppQualSet }, (Class)WinNT.HRESULT.class);
        }
        
        public IWbemQualifierSet GetQualifierSet() {
            final PointerByReference ppQualSet = new PointerByReference();
            final WinNT.HRESULT hr = this.GetQualifierSet(ppQualSet);
            COMUtils.checkRC(hr);
            final IWbemQualifierSet qualifier = new IWbemQualifierSet(ppQualSet.getValue());
            return qualifier;
        }
        
        public WinNT.HRESULT GetPropertyQualifierSet(final WString wszProperty, final PointerByReference ppQualSet) {
            return (WinNT.HRESULT)this._invokeNativeObject(11, new Object[] { this.getPointer(), wszProperty, ppQualSet }, (Class)WinNT.HRESULT.class);
        }
        
        public IWbemQualifierSet GetPropertyQualifierSet(final String strProperty) {
            final WString wszProperty = new WString(strProperty);
            final PointerByReference ppQualSet = new PointerByReference();
            COMUtils.checkRC(this.GetPropertyQualifierSet(wszProperty, ppQualSet));
            final IWbemQualifierSet qualifier = new IWbemQualifierSet(ppQualSet.getValue());
            return qualifier;
        }
    }
    
    public static class IWbemQualifierSet extends Unknown
    {
        public IWbemQualifierSet(final Pointer pvInstance) {
            super(pvInstance);
        }
        
        public WinNT.HRESULT Get(final WString wszName, final int lFlags, final Variant.VARIANT.ByReference pVal, final IntByReference plFlavor) {
            return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[] { this.getPointer(), wszName, lFlags, pVal, plFlavor }, (Class)WinNT.HRESULT.class);
        }
        
        public String Get(final String wszName) {
            final WString wszNameStr = new WString(wszName);
            final Variant.VARIANT.ByReference pQualifierVal = new Variant.VARIANT.ByReference();
            final WinNT.HRESULT hres = this.Get(wszNameStr, 0, pQualifierVal, null);
            if (hres.intValue() == -2147217406) {
                return null;
            }
            final int qualifierInt = pQualifierVal.getVarType().intValue();
            switch (qualifierInt) {
                case 11: {
                    return String.valueOf(pQualifierVal.booleanValue());
                }
                case 8: {
                    return pQualifierVal.stringValue();
                }
                default: {
                    return null;
                }
            }
        }
        
        public WinNT.HRESULT GetNames(final int lFlags, final PointerByReference pNames) {
            return (WinNT.HRESULT)this._invokeNativeObject(6, new Object[] { this.getPointer(), lFlags, pNames }, (Class)WinNT.HRESULT.class);
        }
        
        public String[] GetNames() {
            final PointerByReference pbr = new PointerByReference();
            COMUtils.checkRC(this.GetNames(0, pbr));
            final Object[] nameObjects = (Object[])OaIdlUtil.toPrimitiveArray(new OaIdl.SAFEARRAY(pbr.getValue()), true);
            final String[] qualifierNames = new String[nameObjects.length];
            for (int i = 0; i < nameObjects.length; ++i) {
                qualifierNames[i] = (String)nameObjects[i];
            }
            return qualifierNames;
        }
    }
    
    public static class IEnumWbemClassObject extends Unknown
    {
        public IEnumWbemClassObject() {
        }
        
        public IEnumWbemClassObject(final Pointer pvInstance) {
            super(pvInstance);
        }
        
        public WinNT.HRESULT Next(final int lTimeOut, final int uCount, final Pointer[] ppObjects, final IntByReference puReturned) {
            return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[] { this.getPointer(), lTimeOut, uCount, ppObjects, puReturned }, (Class)WinNT.HRESULT.class);
        }
        
        public IWbemClassObject[] Next(final int lTimeOut, final int uCount) {
            final Pointer[] resultArray = new Pointer[uCount];
            final IntByReference resultCount = new IntByReference();
            final WinNT.HRESULT result = this.Next(lTimeOut, uCount, resultArray, resultCount);
            COMUtils.checkRC(result);
            final IWbemClassObject[] returnValue = new IWbemClassObject[resultCount.getValue()];
            for (int i = 0; i < resultCount.getValue(); ++i) {
                returnValue[i] = new IWbemClassObject(resultArray[i]);
            }
            return returnValue;
        }
    }
    
    public static class IWbemLocator extends Unknown
    {
        public static final Guid.CLSID CLSID_WbemLocator;
        public static final Guid.GUID IID_IWbemLocator;
        
        public IWbemLocator() {
        }
        
        private IWbemLocator(final Pointer pvInstance) {
            super(pvInstance);
        }
        
        public static IWbemLocator create() {
            final PointerByReference pbr = new PointerByReference();
            final WinNT.HRESULT hres = Ole32.INSTANCE.CoCreateInstance(IWbemLocator.CLSID_WbemLocator, null, 1, IWbemLocator.IID_IWbemLocator, pbr);
            if (COMUtils.FAILED(hres)) {
                return null;
            }
            return new IWbemLocator(pbr.getValue());
        }
        
        public WinNT.HRESULT ConnectServer(final WTypes.BSTR strNetworkResource, final WTypes.BSTR strUser, final WTypes.BSTR strPassword, final WTypes.BSTR strLocale, final int lSecurityFlags, final WTypes.BSTR strAuthority, final IWbemContext pCtx, final PointerByReference ppNamespace) {
            return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[] { this.getPointer(), strNetworkResource, strUser, strPassword, strLocale, lSecurityFlags, strAuthority, pCtx, ppNamespace }, (Class)WinNT.HRESULT.class);
        }
        
        public IWbemServices ConnectServer(final String strNetworkResource, final String strUser, final String strPassword, final String strLocale, final int lSecurityFlags, final String strAuthority, final IWbemContext pCtx) {
            final WTypes.BSTR strNetworkResourceBSTR = OleAuto.INSTANCE.SysAllocString(strNetworkResource);
            final WTypes.BSTR strUserBSTR = OleAuto.INSTANCE.SysAllocString(strUser);
            final WTypes.BSTR strPasswordBSTR = OleAuto.INSTANCE.SysAllocString(strPassword);
            final WTypes.BSTR strLocaleBSTR = OleAuto.INSTANCE.SysAllocString(strLocale);
            final WTypes.BSTR strAuthorityBSTR = OleAuto.INSTANCE.SysAllocString(strAuthority);
            final PointerByReference pbr = new PointerByReference();
            try {
                final WinNT.HRESULT result = this.ConnectServer(strNetworkResourceBSTR, strUserBSTR, strPasswordBSTR, strLocaleBSTR, lSecurityFlags, strAuthorityBSTR, pCtx, pbr);
                COMUtils.checkRC(result);
                return new IWbemServices(pbr.getValue());
            }
            finally {
                OleAuto.INSTANCE.SysFreeString(strNetworkResourceBSTR);
                OleAuto.INSTANCE.SysFreeString(strUserBSTR);
                OleAuto.INSTANCE.SysFreeString(strPasswordBSTR);
                OleAuto.INSTANCE.SysFreeString(strLocaleBSTR);
                OleAuto.INSTANCE.SysFreeString(strAuthorityBSTR);
            }
        }
        
        static {
            CLSID_WbemLocator = new Guid.CLSID("4590f811-1d3a-11d0-891f-00aa004b2e24");
            IID_IWbemLocator = new Guid.GUID("dc12a687-737f-11cf-884d-00aa004b2e24");
        }
    }
    
    public static class IWbemServices extends Unknown
    {
        public IWbemServices() {
        }
        
        public IWbemServices(final Pointer pvInstance) {
            super(pvInstance);
        }
        
        public WinNT.HRESULT ExecQuery(final WTypes.BSTR strQueryLanguage, final WTypes.BSTR strQuery, final int lFlags, final IWbemContext pCtx, final PointerByReference ppEnum) {
            return (WinNT.HRESULT)this._invokeNativeObject(20, new Object[] { this.getPointer(), strQueryLanguage, strQuery, lFlags, pCtx, ppEnum }, (Class)WinNT.HRESULT.class);
        }
        
        public IEnumWbemClassObject ExecQuery(final String strQueryLanguage, final String strQuery, final int lFlags, final IWbemContext pCtx) {
            final WTypes.BSTR strQueryLanguageBSTR = OleAuto.INSTANCE.SysAllocString(strQueryLanguage);
            final WTypes.BSTR strQueryBSTR = OleAuto.INSTANCE.SysAllocString(strQuery);
            try {
                final PointerByReference pbr = new PointerByReference();
                final WinNT.HRESULT res = this.ExecQuery(strQueryLanguageBSTR, strQueryBSTR, lFlags, pCtx, pbr);
                COMUtils.checkRC(res);
                return new IEnumWbemClassObject(pbr.getValue());
            }
            finally {
                OleAuto.INSTANCE.SysFreeString(strQueryLanguageBSTR);
                OleAuto.INSTANCE.SysFreeString(strQueryBSTR);
            }
        }
        
        public WinNT.HRESULT GetObject(final WTypes.BSTR strObjectPath, final int lFlags, final IWbemContext pCtx, final PointerByReference ppObject, final PointerByReference ppCallResult) {
            return (WinNT.HRESULT)this._invokeNativeObject(6, new Object[] { this.getPointer(), strObjectPath, lFlags, pCtx, ppObject, ppCallResult }, (Class)WinNT.HRESULT.class);
        }
        
        public IWbemClassObject GetObject(final String strObjectPath, final int lFlags, final IWbemContext pCtx) {
            final WTypes.BSTR strObjectPathBSTR = OleAuto.INSTANCE.SysAllocString(strObjectPath);
            try {
                final PointerByReference ppObject = new PointerByReference();
                final WinNT.HRESULT res = this.GetObject(strObjectPathBSTR, lFlags, pCtx, ppObject, null);
                COMUtils.checkRC(res);
                return new IWbemClassObject(ppObject.getValue());
            }
            finally {
                OleAuto.INSTANCE.SysFreeString(strObjectPathBSTR);
            }
        }
    }
    
    public static class IWbemContext extends Unknown
    {
        public static final Guid.CLSID CLSID_WbemContext;
        public static final Guid.GUID IID_IWbemContext;
        
        public IWbemContext() {
        }
        
        public static IWbemContext create() {
            final PointerByReference pbr = new PointerByReference();
            final WinNT.HRESULT hres = Ole32.INSTANCE.CoCreateInstance(IWbemContext.CLSID_WbemContext, null, 1, IWbemContext.IID_IWbemContext, pbr);
            if (COMUtils.FAILED(hres)) {
                return null;
            }
            return new IWbemContext(pbr.getValue());
        }
        
        public IWbemContext(final Pointer pvInstance) {
            super(pvInstance);
        }
        
        public void SetValue(final String wszName, final int lFlag, final Variant.VARIANT pValue) {
            final WTypes.BSTR wszNameBSTR = OleAuto.INSTANCE.SysAllocString(wszName);
            try {
                final WinNT.HRESULT res = (WinNT.HRESULT)this._invokeNativeObject(8, new Object[] { this.getPointer(), wszNameBSTR, lFlag, pValue }, (Class)WinNT.HRESULT.class);
                COMUtils.checkRC(res);
            }
            finally {
                OleAuto.INSTANCE.SysFreeString(wszNameBSTR);
            }
        }
        
        public void SetValue(final String wszName, final int lFlag, final boolean pValue) {
            final Variant.VARIANT aVariant = new Variant.VARIANT();
            aVariant.setValue(11, pValue ? Variant.VARIANT_TRUE : Variant.VARIANT_FALSE);
            this.SetValue(wszName, lFlag, aVariant);
            OleAuto.INSTANCE.VariantClear(aVariant);
        }
        
        public void SetValue(final String wszName, final int lFlag, final String pValue) {
            final Variant.VARIANT aVariant = new Variant.VARIANT();
            final WTypes.BSTR strValue = OleAuto.INSTANCE.SysAllocString(pValue);
            try {
                aVariant.setValue(30, strValue);
                this.SetValue(wszName, lFlag, aVariant);
            }
            finally {
                OleAuto.INSTANCE.SysFreeString(strValue);
            }
        }
        
        static {
            CLSID_WbemContext = new Guid.CLSID("674B6698-EE92-11D0-AD71-00C04FD8FDFF");
            IID_IWbemContext = new Guid.GUID("44aca674-e8fc-11d0-a07c-00c04fb68820");
        }
    }
    
    public interface WBEM_CONDITION_FLAG_TYPE
    {
        public static final int WBEM_FLAG_ALWAYS = 0;
        public static final int WBEM_FLAG_ONLY_IF_TRUE = 1;
        public static final int WBEM_FLAG_ONLY_IF_FALSE = 2;
        public static final int WBEM_FLAG_ONLY_IF_IDENTICAL = 3;
        public static final int WBEM_MASK_PRIMARY_CONDITION = 3;
        public static final int WBEM_FLAG_KEYS_ONLY = 4;
        public static final int WBEM_FLAG_REFS_ONLY = 8;
        public static final int WBEM_FLAG_LOCAL_ONLY = 16;
        public static final int WBEM_FLAG_PROPAGATED_ONLY = 32;
        public static final int WBEM_FLAG_SYSTEM_ONLY = 48;
        public static final int WBEM_FLAG_NONSYSTEM_ONLY = 64;
        public static final int WBEM_MASK_CONDITION_ORIGIN = 112;
        public static final int WBEM_FLAG_CLASS_OVERRIDES_ONLY = 256;
        public static final int WBEM_FLAG_CLASS_LOCAL_AND_OVERRIDES = 512;
        public static final int WBEM_MASK_CLASS_CONDITION = 768;
    }
}
