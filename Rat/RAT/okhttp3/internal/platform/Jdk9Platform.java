//Raddon On Top!

package okhttp3.internal.platform;

import java.util.*;
import okhttp3.*;
import java.lang.reflect.*;
import javax.annotation.*;
import javax.net.ssl.*;

final class Jdk9Platform extends Platform
{
    final Method setProtocolMethod;
    final Method getProtocolMethod;
    
    Jdk9Platform(final Method setProtocolMethod, final Method getProtocolMethod) {
        this.setProtocolMethod = setProtocolMethod;
        this.getProtocolMethod = getProtocolMethod;
    }
    
    @Override
    public void configureTlsExtensions(final SSLSocket sslSocket, final String hostname, final List<Protocol> protocols) {
        try {
            final SSLParameters sslParameters = sslSocket.getSSLParameters();
            final List<String> names = Platform.alpnProtocolNames(protocols);
            this.setProtocolMethod.invoke(sslParameters, names.toArray(new String[names.size()]));
            sslSocket.setSSLParameters(sslParameters);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new AssertionError("failed to set SSL parameters", e);
        }
    }
    
    @Nullable
    @Override
    public String getSelectedProtocol(final SSLSocket socket) {
        try {
            final String protocol = (String)this.getProtocolMethod.invoke(socket, new Object[0]);
            if (protocol == null || protocol.equals("")) {
                return null;
            }
            return protocol;
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof UnsupportedOperationException) {
                return null;
            }
            throw new AssertionError("failed to get ALPN selected protocol", e);
        }
        catch (IllegalAccessException e2) {
            throw new AssertionError("failed to get ALPN selected protocol", e2);
        }
    }
    
    public X509TrustManager trustManager(final SSLSocketFactory sslSocketFactory) {
        throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on JDK 9+");
    }
    
    public static Jdk9Platform buildIfSupported() {
        try {
            final Method setProtocolMethod = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            final Method getProtocolMethod = SSLSocket.class.getMethod("getApplicationProtocol", (Class<?>[])new Class[0]);
            return new Jdk9Platform(setProtocolMethod, getProtocolMethod);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }
}
