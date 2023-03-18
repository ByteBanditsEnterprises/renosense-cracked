//Raddon On Top!

package okhttp3.internal.platform;

import javax.annotation.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.*;
import okhttp3.internal.*;
import okio.*;
import java.lang.reflect.*;
import javax.net.ssl.*;
import java.security.*;
import okhttp3.internal.tls.*;
import okhttp3.*;

public class Platform
{
    private static final Platform PLATFORM;
    public static final int INFO = 4;
    public static final int WARN = 5;
    private static final Logger logger;
    
    public static Platform get() {
        return Platform.PLATFORM;
    }
    
    public String getPrefix() {
        return "OkHttp";
    }
    
    @Nullable
    protected X509TrustManager trustManager(final SSLSocketFactory sslSocketFactory) {
        try {
            final Class<?> sslContextClass = Class.forName("sun.security.ssl.SSLContextImpl");
            final Object context = readFieldOrNull(sslSocketFactory, sslContextClass, "context");
            if (context == null) {
                return null;
            }
            return readFieldOrNull(context, X509TrustManager.class, "trustManager");
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    public void configureTlsExtensions(final SSLSocket sslSocket, @Nullable final String hostname, final List<Protocol> protocols) throws IOException {
    }
    
    public void afterHandshake(final SSLSocket sslSocket) {
    }
    
    @Nullable
    public String getSelectedProtocol(final SSLSocket socket) {
        return null;
    }
    
    public void connectSocket(final Socket socket, final InetSocketAddress address, final int connectTimeout) throws IOException {
        socket.connect(address, connectTimeout);
    }
    
    public void log(final int level, final String message, @Nullable final Throwable t) {
        final Level logLevel = (level == 5) ? Level.WARNING : Level.INFO;
        Platform.logger.log(logLevel, message, t);
    }
    
    public boolean isCleartextTrafficPermitted(final String hostname) {
        return true;
    }
    
    @Nullable
    public Object getStackTraceForCloseable(final String closer) {
        if (Platform.logger.isLoggable(Level.FINE)) {
            return new Throwable(closer);
        }
        return null;
    }
    
    public void logCloseableLeak(String message, final Object stackTrace) {
        if (stackTrace == null) {
            message += " To see where this was allocated, set the OkHttpClient logger level to FINE: Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);";
        }
        this.log(5, message, (Throwable)stackTrace);
    }
    
    public static List<String> alpnProtocolNames(final List<Protocol> protocols) {
        final List<String> names = new ArrayList<String>(protocols.size());
        for (int i = 0, size = protocols.size(); i < size; ++i) {
            final Protocol protocol = protocols.get(i);
            if (protocol != Protocol.HTTP_1_0) {
                names.add(protocol.toString());
            }
        }
        return names;
    }
    
    public CertificateChainCleaner buildCertificateChainCleaner(final X509TrustManager trustManager) {
        return new BasicCertificateChainCleaner(this.buildTrustRootIndex(trustManager));
    }
    
    public CertificateChainCleaner buildCertificateChainCleaner(final SSLSocketFactory sslSocketFactory) {
        final X509TrustManager trustManager = this.trustManager(sslSocketFactory);
        if (trustManager == null) {
            throw new IllegalStateException("Unable to extract the trust manager on " + get() + ", sslSocketFactory is " + sslSocketFactory.getClass());
        }
        return this.buildCertificateChainCleaner(trustManager);
    }
    
    public static boolean isConscryptPreferred() {
        if ("conscrypt".equals(Util.getSystemProperty("okhttp.platform", null))) {
            return true;
        }
        final String preferredProvider = Security.getProviders()[0].getName();
        return "Conscrypt".equals(preferredProvider);
    }
    
    private static Platform findPlatform() {
        if (isAndroid()) {
            return findAndroidPlatform();
        }
        return findJvmPlatform();
    }
    
    public static boolean isAndroid() {
        return "Dalvik".equals(System.getProperty("java.vm.name"));
    }
    
    private static Platform findJvmPlatform() {
        if (isConscryptPreferred()) {
            final Platform conscrypt = (Platform)ConscryptPlatform.buildIfSupported();
            if (conscrypt != null) {
                return conscrypt;
            }
        }
        final Platform jdk9 = (Platform)Jdk9Platform.buildIfSupported();
        if (jdk9 != null) {
            return jdk9;
        }
        final Platform jdkWithJettyBoot = Jdk8WithJettyBootPlatform.buildIfSupported();
        if (jdkWithJettyBoot != null) {
            return jdkWithJettyBoot;
        }
        return new Platform();
    }
    
    private static Platform findAndroidPlatform() {
        final Platform android10 = Android10Platform.buildIfSupported();
        if (android10 != null) {
            return android10;
        }
        final Platform android11 = AndroidPlatform.buildIfSupported();
        if (android11 == null) {
            throw new NullPointerException("No platform found on Android");
        }
        return android11;
    }
    
    static byte[] concatLengthPrefixed(final List<Protocol> protocols) {
        final Buffer result = new Buffer();
        for (int i = 0, size = protocols.size(); i < size; ++i) {
            final Protocol protocol = protocols.get(i);
            if (protocol != Protocol.HTTP_1_0) {
                result.writeByte(protocol.toString().length());
                result.writeUtf8(protocol.toString());
            }
        }
        return result.readByteArray();
    }
    
    @Nullable
    static <T> T readFieldOrNull(final Object instance, final Class<T> fieldType, final String fieldName) {
        for (Class<?> c = instance.getClass(); c != Object.class; c = c.getSuperclass()) {
            try {
                final Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                final Object value = field.get(instance);
                if (!fieldType.isInstance(value)) {
                    return null;
                }
                return fieldType.cast(value);
            }
            catch (NoSuchFieldException ex) {}
            catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }
        if (!fieldName.equals("delegate")) {
            final Object delegate = readFieldOrNull(instance, Object.class, "delegate");
            if (delegate != null) {
                return (T)readFieldOrNull(delegate, (Class<Object>)fieldType, fieldName);
            }
        }
        return null;
    }
    
    public SSLContext getSSLContext() {
        try {
            return SSLContext.getInstance("TLS");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No TLS provider", e);
        }
    }
    
    public TrustRootIndex buildTrustRootIndex(final X509TrustManager trustManager) {
        return new BasicTrustRootIndex(trustManager.getAcceptedIssuers());
    }
    
    public void configureSslSocketFactory(final SSLSocketFactory socketFactory) {
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    static {
        PLATFORM = findPlatform();
        logger = Logger.getLogger(OkHttpClient.class.getName());
    }
}
