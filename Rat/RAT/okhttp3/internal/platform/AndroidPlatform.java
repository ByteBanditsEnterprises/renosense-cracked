//Raddon On Top!

package okhttp3.internal.platform;

import java.net.*;
import okhttp3.internal.*;
import java.io.*;
import android.os.*;
import javax.annotation.*;
import java.util.*;
import okhttp3.*;
import java.nio.charset.*;
import android.util.*;
import java.lang.reflect.*;
import okhttp3.internal.tls.*;
import java.security.*;
import javax.net.ssl.*;
import java.security.cert.*;

class AndroidPlatform extends Platform
{
    private static final int MAX_LOG_LENGTH = 4000;
    private final Class<?> sslParametersClass;
    private final Class<?> sslSocketClass;
    private final Method setUseSessionTickets;
    private final Method setHostname;
    private final Method getAlpnSelectedProtocol;
    private final Method setAlpnProtocols;
    private final CloseGuard closeGuard;
    
    AndroidPlatform(final Class<?> sslParametersClass, final Class<?> sslSocketClass, final Method setUseSessionTickets, final Method setHostname, final Method getAlpnSelectedProtocol, final Method setAlpnProtocols) {
        this.closeGuard = CloseGuard.get();
        this.sslParametersClass = sslParametersClass;
        this.sslSocketClass = sslSocketClass;
        this.setUseSessionTickets = setUseSessionTickets;
        this.setHostname = setHostname;
        this.getAlpnSelectedProtocol = getAlpnSelectedProtocol;
        this.setAlpnProtocols = setAlpnProtocols;
    }
    
    @Override
    public void connectSocket(final Socket socket, final InetSocketAddress address, final int connectTimeout) throws IOException {
        try {
            socket.connect(address, connectTimeout);
        }
        catch (AssertionError e) {
            if (Util.isAndroidGetsocknameError(e)) {
                throw new IOException(e);
            }
            throw e;
        }
        catch (ClassCastException e2) {
            if (Build.VERSION.SDK_INT == 26) {
                throw new IOException("Exception in connect", e2);
            }
            throw e2;
        }
    }
    
    @Nullable
    @Override
    protected X509TrustManager trustManager(final SSLSocketFactory sslSocketFactory) {
        Object context = Platform.readFieldOrNull(sslSocketFactory, this.sslParametersClass, "sslParameters");
        if (context == null) {
            try {
                final Class<?> gmsSslParametersClass = Class.forName("com.google.android.gms.org.conscrypt.SSLParametersImpl", false, sslSocketFactory.getClass().getClassLoader());
                context = Platform.readFieldOrNull(sslSocketFactory, gmsSslParametersClass, "sslParameters");
            }
            catch (ClassNotFoundException e) {
                return super.trustManager(sslSocketFactory);
            }
        }
        final X509TrustManager x509TrustManager = Platform.readFieldOrNull(context, X509TrustManager.class, "x509TrustManager");
        if (x509TrustManager != null) {
            return x509TrustManager;
        }
        return Platform.readFieldOrNull(context, X509TrustManager.class, "trustManager");
    }
    
    @Override
    public void configureTlsExtensions(final SSLSocket sslSocket, final String hostname, final List<Protocol> protocols) throws IOException {
        if (!this.sslSocketClass.isInstance(sslSocket)) {
            return;
        }
        try {
            if (hostname != null) {
                this.setUseSessionTickets.invoke(sslSocket, true);
                this.setHostname.invoke(sslSocket, hostname);
            }
            this.setAlpnProtocols.invoke(sslSocket, Platform.concatLengthPrefixed(protocols));
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new AssertionError((Object)e);
        }
    }
    
    @Nullable
    @Override
    public String getSelectedProtocol(final SSLSocket socket) {
        if (!this.sslSocketClass.isInstance(socket)) {
            return null;
        }
        try {
            final byte[] alpnResult = (byte[])this.getAlpnSelectedProtocol.invoke(socket, new Object[0]);
            return (alpnResult != null) ? new String(alpnResult, StandardCharsets.UTF_8) : null;
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new AssertionError((Object)e);
        }
    }
    
    @Override
    public void log(final int level, String message, @Nullable final Throwable t) {
        final int logLevel = (level == 5) ? 5 : 3;
        if (t != null) {
            message = message + '\n' + Log.getStackTraceString(t);
        }
        for (int i = 0, length = message.length(); i < length; ++i) {
            int newline = message.indexOf(10, i);
            newline = ((newline != -1) ? newline : length);
            do {
                final int end = Math.min(newline, i + 4000);
                Log.println(logLevel, "OkHttp", message.substring(i, end));
                i = end;
            } while (i < newline);
        }
    }
    
    @Nullable
    @Override
    public Object getStackTraceForCloseable(final String closer) {
        return this.closeGuard.createAndOpen(closer);
    }
    
    @Override
    public void logCloseableLeak(final String message, final Object stackTrace) {
        final boolean reported = this.closeGuard.warnIfOpen(stackTrace);
        if (!reported) {
            this.log(5, message, null);
        }
    }
    
    @Override
    public boolean isCleartextTrafficPermitted(final String hostname) {
        try {
            final Class<?> networkPolicyClass = Class.forName("android.security.NetworkSecurityPolicy");
            final Method getInstanceMethod = networkPolicyClass.getMethod("getInstance", (Class<?>[])new Class[0]);
            final Object networkSecurityPolicy = getInstanceMethod.invoke(null, new Object[0]);
            return this.api24IsCleartextTrafficPermitted(hostname, networkPolicyClass, networkSecurityPolicy);
        }
        catch (ClassNotFoundException | NoSuchMethodException ex3) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            return super.isCleartextTrafficPermitted(hostname);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex4) {
            final Exception ex2;
            final Exception e2 = ex2;
            throw new AssertionError("unable to determine cleartext support", e2);
        }
    }
    
    private boolean api24IsCleartextTrafficPermitted(final String hostname, final Class<?> networkPolicyClass, final Object networkSecurityPolicy) throws InvocationTargetException, IllegalAccessException {
        try {
            final Method isCleartextTrafficPermittedMethod = networkPolicyClass.getMethod("isCleartextTrafficPermitted", String.class);
            return (boolean)isCleartextTrafficPermittedMethod.invoke(networkSecurityPolicy, hostname);
        }
        catch (NoSuchMethodException e) {
            return this.api23IsCleartextTrafficPermitted(hostname, networkPolicyClass, networkSecurityPolicy);
        }
    }
    
    private boolean api23IsCleartextTrafficPermitted(final String hostname, final Class<?> networkPolicyClass, final Object networkSecurityPolicy) throws InvocationTargetException, IllegalAccessException {
        try {
            final Method isCleartextTrafficPermittedMethod = networkPolicyClass.getMethod("isCleartextTrafficPermitted", (Class<?>[])new Class[0]);
            return (boolean)isCleartextTrafficPermittedMethod.invoke(networkSecurityPolicy, new Object[0]);
        }
        catch (NoSuchMethodException e) {
            return super.isCleartextTrafficPermitted(hostname);
        }
    }
    
    @Override
    public CertificateChainCleaner buildCertificateChainCleaner(final X509TrustManager trustManager) {
        try {
            final Class<?> extensionsClass = Class.forName("android.net.http.X509TrustManagerExtensions");
            final Constructor<?> constructor = extensionsClass.getConstructor(X509TrustManager.class);
            final Object extensions = constructor.newInstance(trustManager);
            final Method checkServerTrusted = extensionsClass.getMethod("checkServerTrusted", X509Certificate[].class, String.class, String.class);
            return new AndroidCertificateChainCleaner(extensions, checkServerTrusted);
        }
        catch (Exception e) {
            return super.buildCertificateChainCleaner(trustManager);
        }
    }
    
    @Nullable
    public static Platform buildIfSupported() {
        if (!Platform.isAndroid()) {
            return null;
        }
        Class<?> sslParametersClass;
        Class<?> sslSocketClass;
        try {
            sslParametersClass = Class.forName("com.android.org.conscrypt.SSLParametersImpl");
            sslSocketClass = Class.forName("com.android.org.conscrypt.OpenSSLSocketImpl");
        }
        catch (ClassNotFoundException ignored) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                final Method setUseSessionTickets = sslSocketClass.getDeclaredMethod("setUseSessionTickets", Boolean.TYPE);
                final Method setHostname = sslSocketClass.getMethod("setHostname", String.class);
                final Method getAlpnSelectedProtocol = sslSocketClass.getMethod("getAlpnSelectedProtocol", (Class<?>[])new Class[0]);
                final Method setAlpnProtocols = sslSocketClass.getMethod("setAlpnProtocols", byte[].class);
                return new AndroidPlatform(sslParametersClass, sslSocketClass, setUseSessionTickets, setHostname, getAlpnSelectedProtocol, setAlpnProtocols);
            }
            catch (NoSuchMethodException ex) {}
        }
        throw new IllegalStateException("Expected Android API level 21+ but was " + Build.VERSION.SDK_INT);
    }
    
    @Override
    public TrustRootIndex buildTrustRootIndex(final X509TrustManager trustManager) {
        try {
            final Method method = trustManager.getClass().getDeclaredMethod("findTrustAnchorByIssuerAndSignature", X509Certificate.class);
            method.setAccessible(true);
            return new CustomTrustRootIndex(trustManager, method);
        }
        catch (NoSuchMethodException e) {
            return super.buildTrustRootIndex(trustManager);
        }
    }
    
    @Override
    public SSLContext getSSLContext() {
        boolean tryTls12;
        try {
            tryTls12 = (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22);
        }
        catch (NoClassDefFoundError e2) {
            tryTls12 = true;
        }
        if (tryTls12) {
            try {
                return SSLContext.getInstance("TLSv1.2");
            }
            catch (NoSuchAlgorithmException ex) {}
        }
        try {
            return SSLContext.getInstance("TLS");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No TLS provider", e);
        }
    }
    
    static int getSdkInt() {
        try {
            return Build.VERSION.SDK_INT;
        }
        catch (NoClassDefFoundError ignored) {
            return 0;
        }
    }
    
    static final class AndroidCertificateChainCleaner extends CertificateChainCleaner
    {
        private final Object x509TrustManagerExtensions;
        private final Method checkServerTrusted;
        
        AndroidCertificateChainCleaner(final Object x509TrustManagerExtensions, final Method checkServerTrusted) {
            this.x509TrustManagerExtensions = x509TrustManagerExtensions;
            this.checkServerTrusted = checkServerTrusted;
        }
        
        @Override
        public List<Certificate> clean(final List<Certificate> chain, final String hostname) throws SSLPeerUnverifiedException {
            try {
                final X509Certificate[] certificates = chain.toArray(new X509Certificate[chain.size()]);
                return (List<Certificate>)this.checkServerTrusted.invoke(this.x509TrustManagerExtensions, certificates, "RSA", hostname);
            }
            catch (InvocationTargetException e) {
                final SSLPeerUnverifiedException exception = new SSLPeerUnverifiedException(e.getMessage());
                exception.initCause(e);
                throw exception;
            }
            catch (IllegalAccessException e2) {
                throw new AssertionError((Object)e2);
            }
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof AndroidCertificateChainCleaner;
        }
        
        @Override
        public int hashCode() {
            return 0;
        }
    }
    
    static final class CloseGuard
    {
        private final Method getMethod;
        private final Method openMethod;
        private final Method warnIfOpenMethod;
        
        CloseGuard(final Method getMethod, final Method openMethod, final Method warnIfOpenMethod) {
            this.getMethod = getMethod;
            this.openMethod = openMethod;
            this.warnIfOpenMethod = warnIfOpenMethod;
        }
        
        Object createAndOpen(final String closer) {
            if (this.getMethod != null) {
                try {
                    final Object closeGuardInstance = this.getMethod.invoke(null, new Object[0]);
                    this.openMethod.invoke(closeGuardInstance, closer);
                    return closeGuardInstance;
                }
                catch (Exception ex) {}
            }
            return null;
        }
        
        boolean warnIfOpen(final Object closeGuardInstance) {
            boolean reported = false;
            if (closeGuardInstance != null) {
                try {
                    this.warnIfOpenMethod.invoke(closeGuardInstance, new Object[0]);
                    reported = true;
                }
                catch (Exception ex) {}
            }
            return reported;
        }
        
        static CloseGuard get() {
            Method getMethod;
            Method openMethod;
            Method warnIfOpenMethod;
            try {
                final Class<?> closeGuardClass = Class.forName("dalvik.system.CloseGuard");
                getMethod = closeGuardClass.getMethod("get", (Class<?>[])new Class[0]);
                openMethod = closeGuardClass.getMethod("open", String.class);
                warnIfOpenMethod = closeGuardClass.getMethod("warnIfOpen", (Class<?>[])new Class[0]);
            }
            catch (Exception ignored) {
                getMethod = null;
                openMethod = null;
                warnIfOpenMethod = null;
            }
            return new CloseGuard(getMethod, openMethod, warnIfOpenMethod);
        }
    }
    
    static final class CustomTrustRootIndex implements TrustRootIndex
    {
        private final X509TrustManager trustManager;
        private final Method findByIssuerAndSignatureMethod;
        
        CustomTrustRootIndex(final X509TrustManager trustManager, final Method findByIssuerAndSignatureMethod) {
            this.findByIssuerAndSignatureMethod = findByIssuerAndSignatureMethod;
            this.trustManager = trustManager;
        }
        
        @Override
        public X509Certificate findByIssuerAndSignature(final X509Certificate cert) {
            try {
                final TrustAnchor trustAnchor = (TrustAnchor)this.findByIssuerAndSignatureMethod.invoke(this.trustManager, cert);
                return (trustAnchor != null) ? trustAnchor.getTrustedCert() : null;
            }
            catch (IllegalAccessException e) {
                throw new AssertionError("unable to get issues and signature", e);
            }
            catch (InvocationTargetException e2) {
                return null;
            }
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CustomTrustRootIndex)) {
                return false;
            }
            final CustomTrustRootIndex that = (CustomTrustRootIndex)obj;
            return this.trustManager.equals(that.trustManager) && this.findByIssuerAndSignatureMethod.equals(that.findByIssuerAndSignatureMethod);
        }
        
        @Override
        public int hashCode() {
            return this.trustManager.hashCode() + 31 * this.findByIssuerAndSignatureMethod.hashCode();
        }
    }
}
