//Raddon On Top!

package okhttp3.internal.platform;

import org.conscrypt.*;
import javax.annotation.*;
import java.util.*;
import okhttp3.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.*;

public class ConscryptPlatform extends Platform
{
    private ConscryptPlatform() {
    }
    
    private Provider getProvider() {
        return Conscrypt.newProviderBuilder().provideTrustManager().build();
    }
    
    @Nullable
    public X509TrustManager trustManager(final SSLSocketFactory sslSocketFactory) {
        if (!Conscrypt.isConscrypt(sslSocketFactory)) {
            return super.trustManager(sslSocketFactory);
        }
        try {
            final Object sp = Platform.readFieldOrNull(sslSocketFactory, Object.class, "sslParameters");
            if (sp != null) {
                return Platform.readFieldOrNull(sp, X509TrustManager.class, "x509TrustManager");
            }
            return null;
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on Conscrypt", e);
        }
    }
    
    @Override
    public void configureTlsExtensions(final SSLSocket sslSocket, final String hostname, final List<Protocol> protocols) throws IOException {
        if (Conscrypt.isConscrypt(sslSocket)) {
            if (hostname != null) {
                Conscrypt.setUseSessionTickets(sslSocket, true);
                Conscrypt.setHostname(sslSocket, hostname);
            }
            final List<String> names = Platform.alpnProtocolNames(protocols);
            Conscrypt.setApplicationProtocols(sslSocket, (String[])names.toArray(new String[0]));
        }
        else {
            super.configureTlsExtensions(sslSocket, hostname, protocols);
        }
    }
    
    @Nullable
    @Override
    public String getSelectedProtocol(final SSLSocket sslSocket) {
        if (Conscrypt.isConscrypt(sslSocket)) {
            return Conscrypt.getApplicationProtocol(sslSocket);
        }
        return super.getSelectedProtocol(sslSocket);
    }
    
    @Override
    public SSLContext getSSLContext() {
        try {
            return SSLContext.getInstance("TLSv1.3", this.getProvider());
        }
        catch (NoSuchAlgorithmException e) {
            try {
                return SSLContext.getInstance("TLS", this.getProvider());
            }
            catch (NoSuchAlgorithmException e2) {
                throw new IllegalStateException("No TLS provider", e);
            }
        }
    }
    
    public static ConscryptPlatform buildIfSupported() {
        try {
            Class.forName("org.conscrypt.Conscrypt");
            if (!Conscrypt.isAvailable()) {
                return null;
            }
            return new ConscryptPlatform();
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    @Override
    public void configureSslSocketFactory(final SSLSocketFactory socketFactory) {
        if (Conscrypt.isConscrypt(socketFactory)) {
            Conscrypt.setUseEngineSocket(socketFactory, true);
        }
    }
}
