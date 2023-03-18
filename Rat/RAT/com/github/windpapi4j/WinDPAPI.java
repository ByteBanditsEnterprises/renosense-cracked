//Raddon On Top!

package com.github.windpapi4j;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public final class WinDPAPI
{
    private final Crypt32 cryptoApi;
    private final Kernel32 kernelApi;
    private static final short FACILITY_WIN32 = 7;
    private static final boolean IS_WINDOWS_OPERATING_SYSTEM;
    private final int flags;
    
    private WinDPAPI(final int flagValue) {
        this.cryptoApi = Crypt32.INSTANCE;
        this.kernelApi = Kernel32.INSTANCE;
        this.flags = flagValue;
    }
    
    public static WinDPAPI newInstance(final CryptProtectFlag... cryptProtectFlags) throws InitializationFailedException {
        try {
            if (!isPlatformSupported()) {
                throw new IllegalStateException("This library only works on Windows operating systems.");
            }
            int flagValue = 0;
            for (final CryptProtectFlag cryptProtectFlag : cryptProtectFlags) {
                flagValue |= cryptProtectFlag.value;
            }
            return new WinDPAPI(flagValue);
        }
        catch (Throwable t) {
            throw new InitializationFailedException("Initialization failed", t);
        }
    }
    
    public static boolean isPlatformSupported() {
        return WinDPAPI.IS_WINDOWS_OPERATING_SYSTEM;
    }
    
    public byte[] protectData(final byte[] data) throws WinAPICallFailedException {
        return this.protectData(data, null);
    }
    
    public byte[] protectData(final byte[] data, final byte[] entropy) throws WinAPICallFailedException {
        return this.protectData(data, entropy, null);
    }
    
    public byte[] protectData(final byte[] data, final byte[] entropy, final String description) throws WinAPICallFailedException {
        this.checkNotNull(data, "Argument data cannot be null");
        try {
            final Crypt32.DATA_BLOB pDataIn = new Crypt32.DATA_BLOB(data);
            final Crypt32.DATA_BLOB pDataProtected = new Crypt32.DATA_BLOB();
            final Crypt32.DATA_BLOB pEntropy = (entropy == null) ? null : new Crypt32.DATA_BLOB(entropy);
            try {
                final boolean apiCallSuccessful = this.cryptoApi.CryptProtectData(pDataIn, description, pEntropy, null, null, this.flags, pDataProtected);
                if (!apiCallSuccessful) {
                    this.raiseHResultExceptionForLastError("CryptProtectData");
                }
                return pDataProtected.getData();
            }
            finally {
                if (pDataProtected.pbData != null) {
                    this.kernelApi.LocalFree(pDataProtected.pbData);
                }
            }
        }
        catch (Throwable t) {
            throw new WinAPICallFailedException("Invocation of CryptProtectData failed", t);
        }
    }
    
    public byte[] unprotectData(final byte[] data) throws WinAPICallFailedException {
        return this.unprotectData(data, null);
    }
    
    public byte[] unprotectData(final byte[] data, final byte[] entropy) throws WinAPICallFailedException {
        this.checkNotNull(data, "Argument data cannot be null");
        try {
            final Crypt32.DATA_BLOB pDataIn = new Crypt32.DATA_BLOB(data);
            final Crypt32.DATA_BLOB pDataUnprotected = new Crypt32.DATA_BLOB();
            final Crypt32.DATA_BLOB pEntropy = (entropy == null) ? null : new Crypt32.DATA_BLOB(entropy);
            final PointerByReference pDescription = new PointerByReference();
            try {
                final boolean apiCallSuccessful = this.cryptoApi.CryptUnprotectData(pDataIn, pDescription, pEntropy, null, null, this.flags, pDataUnprotected);
                if (!apiCallSuccessful) {
                    this.raiseHResultExceptionForLastError("CryptUnprotectData");
                }
                return pDataUnprotected.getData();
            }
            finally {
                if (pDataUnprotected.pbData != null) {
                    this.kernelApi.LocalFree(pDataUnprotected.pbData);
                }
                if (pDescription.getValue() != null) {
                    this.kernelApi.LocalFree(pDescription.getValue());
                }
            }
        }
        catch (Throwable t) {
            throw new WinAPICallFailedException("Invocation of CryptUnprotectData failed", t);
        }
    }
    
    private void checkNotNull(final Object object, final String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }
    
    private void raiseHResultExceptionForLastError(final String methodName) {
        final int winApiErrorCode = this.kernelApi.GetLastError();
        final int hResult = (winApiErrorCode <= 0) ? winApiErrorCode : ((winApiErrorCode & 0xFFFF) | 0x70000 | Integer.MIN_VALUE);
        throw new HResultException(String.format("%s call signalled an error.", methodName), hResult);
    }
    
    private static <T> T loadNativeLibraryJNAFacade(final String name, final Class<T> clazz) {
        return Native.loadLibrary(name, clazz, W32APIOptions.UNICODE_OPTIONS);
    }
    
    static {
        final String operatingSystemName = System.getProperty("os.name");
        IS_WINDOWS_OPERATING_SYSTEM = (operatingSystemName != null && operatingSystemName.startsWith("Windows"));
    }
    
    public enum CryptProtectFlag
    {
        CRYPTPROTECT_UI_FORBIDDEN(1), 
        CRYPTPROTECT_LOCAL_MACHINE(4), 
        CRYPTPROTECT_CRED_SYNC(8), 
        CRYPTPROTECT_AUDIT(16), 
        CRYPTPROTECT_NO_RECOVERY(32), 
        CRYPTPROTECT_VERIFY_PROTECTION(64), 
        CRYPTPROTECT_CRED_REGENERATE(128);
        
        private final int value;
        
        private CryptProtectFlag(final int flagValue) {
            this.value = flagValue;
        }
    }
    
    interface Kernel32 extends StdCallLibrary
    {
        public static final Kernel32 INSTANCE = (Kernel32)loadNativeLibraryJNAFacade("Kernel32", (Class<Object>)Kernel32.class);
        
        Pointer LocalFree(final Pointer p0);
        
        int GetLastError();
    }
    
    interface Crypt32 extends StdCallLibrary
    {
        public static final Crypt32 INSTANCE = (Crypt32)loadNativeLibraryJNAFacade("Crypt32", (Class<Object>)Crypt32.class);
        
        boolean CryptProtectData(final DATA_BLOB p0, final String p1, final DATA_BLOB p2, final Pointer p3, final Pointer p4, final int p5, final DATA_BLOB p6);
        
        boolean CryptUnprotectData(final DATA_BLOB p0, final PointerByReference p1, final DATA_BLOB p2, final Pointer p3, final Pointer p4, final int p5, final DATA_BLOB p6);
        
        public static class DATA_BLOB extends Structure
        {
            public int cbData;
            public Pointer pbData;
            
            DATA_BLOB() {
            }
            
            DATA_BLOB(final byte[] data) {
                (this.pbData = new Memory(data.length)).write(0L, data, 0, data.length);
                this.cbData = data.length;
                this.allocateMemory();
            }
            
            @Override
            protected List<?> getFieldOrder() {
                return Arrays.asList("cbData", "pbData");
            }
            
            public byte[] getData() {
                return (byte[])((this.pbData == null) ? null : this.pbData.getByteArray(0L, this.cbData));
            }
        }
    }
}
