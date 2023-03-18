//Raddon On Top!

package okhttp3;

import javax.net.*;
import java.net.*;
import javax.annotation.*;
import javax.net.ssl.*;
import okhttp3.internal.*;
import java.util.*;

public final class Address
{
    final HttpUrl url;
    final Dns dns;
    final SocketFactory socketFactory;
    final Authenticator proxyAuthenticator;
    final List<Protocol> protocols;
    final List<ConnectionSpec> connectionSpecs;
    final ProxySelector proxySelector;
    @Nullable
    final Proxy proxy;
    @Nullable
    final SSLSocketFactory sslSocketFactory;
    @Nullable
    final HostnameVerifier hostnameVerifier;
    @Nullable
    final CertificatePinner certificatePinner;
    
    public Address(final String uriHost, final int uriPort, final Dns dns, final SocketFactory socketFactory, @Nullable final SSLSocketFactory sslSocketFactory, @Nullable final HostnameVerifier hostnameVerifier, @Nullable final CertificatePinner certificatePinner, final Authenticator proxyAuthenticator, @Nullable final Proxy proxy, final List<Protocol> protocols, final List<ConnectionSpec> connectionSpecs, final ProxySelector proxySelector) {
        this.url = new HttpUrl.Builder().scheme((sslSocketFactory != null) ? "https" : "http").host(uriHost).port(uriPort).build();
        if (dns == null) {
            throw new NullPointerException("dns == null");
        }
        this.dns = dns;
        if (socketFactory == null) {
            throw new NullPointerException("socketFactory == null");
        }
        this.socketFactory = socketFactory;
        if (proxyAuthenticator == null) {
            throw new NullPointerException("proxyAuthenticator == null");
        }
        this.proxyAuthenticator = proxyAuthenticator;
        if (protocols == null) {
            throw new NullPointerException("protocols == null");
        }
        this.protocols = Util.immutableList(protocols);
        if (connectionSpecs == null) {
            throw new NullPointerException("connectionSpecs == null");
        }
        this.connectionSpecs = Util.immutableList(connectionSpecs);
        if (proxySelector == null) {
            throw new NullPointerException("proxySelector == null");
        }
        this.proxySelector = proxySelector;
        this.proxy = proxy;
        this.sslSocketFactory = sslSocketFactory;
        this.hostnameVerifier = hostnameVerifier;
        this.certificatePinner = certificatePinner;
    }
    
    public HttpUrl url() {
        return this.url;
    }
    
    public Dns dns() {
        return this.dns;
    }
    
    public SocketFactory socketFactory() {
        return this.socketFactory;
    }
    
    public Authenticator proxyAuthenticator() {
        return this.proxyAuthenticator;
    }
    
    public List<Protocol> protocols() {
        return this.protocols;
    }
    
    public List<ConnectionSpec> connectionSpecs() {
        return this.connectionSpecs;
    }
    
    public ProxySelector proxySelector() {
        return this.proxySelector;
    }
    
    @Nullable
    public Proxy proxy() {
        return this.proxy;
    }
    
    @Nullable
    public SSLSocketFactory sslSocketFactory() {
        return this.sslSocketFactory;
    }
    
    @Nullable
    public HostnameVerifier hostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    @Nullable
    public CertificatePinner certificatePinner() {
        return this.certificatePinner;
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        return other instanceof Address && this.url.equals(((Address)other).url) && this.equalsNonHost((Address)other);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.url.hashCode();
        result = 31 * result + this.dns.hashCode();
        result = 31 * result + this.proxyAuthenticator.hashCode();
        result = 31 * result + this.protocols.hashCode();
        result = 31 * result + this.connectionSpecs.hashCode();
        result = 31 * result + this.proxySelector.hashCode();
        result = 31 * result + Objects.hashCode(this.proxy);
        result = 31 * result + Objects.hashCode(this.sslSocketFactory);
        result = 31 * result + Objects.hashCode(this.hostnameVerifier);
        result = 31 * result + Objects.hashCode(this.certificatePinner);
        return result;
    }
    
    boolean equalsNonHost(final Address that) {
        return this.dns.equals(that.dns) && this.proxyAuthenticator.equals(that.proxyAuthenticator) && this.protocols.equals(that.protocols) && this.connectionSpecs.equals(that.connectionSpecs) && this.proxySelector.equals(that.proxySelector) && Objects.equals(this.proxy, that.proxy) && Objects.equals(this.sslSocketFactory, that.sslSocketFactory) && Objects.equals(this.hostnameVerifier, that.hostnameVerifier) && Objects.equals(this.certificatePinner, that.certificatePinner) && this.url().port() == that.url().port();
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder().append("Address{").append(this.url.host()).append(":").append(this.url.port());
        if (this.proxy != null) {
            result.append(", proxy=").append(this.proxy);
        }
        else {
            result.append(", proxySelector=").append(this.proxySelector);
        }
        result.append("}");
        return result.toString();
    }
}
