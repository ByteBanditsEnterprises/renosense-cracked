//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import java.net.*;
import okhttp3.internal.cache.*;
import javax.net.*;
import okhttp3.internal.platform.*;
import java.security.*;
import okhttp3.internal.ws.*;
import okhttp3.internal.*;
import javax.net.ssl.*;
import okhttp3.internal.connection.*;
import okhttp3.internal.proxy.*;
import okhttp3.internal.tls.*;
import java.util.concurrent.*;
import java.time.*;
import org.codehaus.mojo.animal_sniffer.*;
import java.util.*;

public class OkHttpClient implements Cloneable, Call.Factory, WebSocket.Factory
{
    static final List<Protocol> DEFAULT_PROTOCOLS;
    static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS;
    final Dispatcher dispatcher;
    @Nullable
    final Proxy proxy;
    final List<Protocol> protocols;
    final List<ConnectionSpec> connectionSpecs;
    final List<Interceptor> interceptors;
    final List<Interceptor> networkInterceptors;
    final EventListener.Factory eventListenerFactory;
    final ProxySelector proxySelector;
    final CookieJar cookieJar;
    @Nullable
    final Cache cache;
    @Nullable
    final InternalCache internalCache;
    final SocketFactory socketFactory;
    final SSLSocketFactory sslSocketFactory;
    final CertificateChainCleaner certificateChainCleaner;
    final HostnameVerifier hostnameVerifier;
    final CertificatePinner certificatePinner;
    final Authenticator proxyAuthenticator;
    final Authenticator authenticator;
    final ConnectionPool connectionPool;
    final Dns dns;
    final boolean followSslRedirects;
    final boolean followRedirects;
    final boolean retryOnConnectionFailure;
    final int callTimeout;
    final int connectTimeout;
    final int readTimeout;
    final int writeTimeout;
    final int pingInterval;
    
    public OkHttpClient() {
        this(new Builder());
    }
    
    OkHttpClient(final Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.proxy = builder.proxy;
        this.protocols = builder.protocols;
        this.connectionSpecs = builder.connectionSpecs;
        this.interceptors = (List<Interceptor>)Util.immutableList((List)builder.interceptors);
        this.networkInterceptors = (List<Interceptor>)Util.immutableList((List)builder.networkInterceptors);
        this.eventListenerFactory = builder.eventListenerFactory;
        this.proxySelector = builder.proxySelector;
        this.cookieJar = builder.cookieJar;
        this.cache = builder.cache;
        this.internalCache = builder.internalCache;
        this.socketFactory = builder.socketFactory;
        boolean isTLS = false;
        for (final ConnectionSpec spec : this.connectionSpecs) {
            isTLS = (isTLS || spec.isTls());
        }
        if (builder.sslSocketFactory != null || !isTLS) {
            this.sslSocketFactory = builder.sslSocketFactory;
            this.certificateChainCleaner = builder.certificateChainCleaner;
        }
        else {
            final X509TrustManager trustManager = Util.platformTrustManager();
            this.sslSocketFactory = newSslSocketFactory(trustManager);
            this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
        }
        if (this.sslSocketFactory != null) {
            Platform.get().configureSslSocketFactory(this.sslSocketFactory);
        }
        this.hostnameVerifier = builder.hostnameVerifier;
        this.certificatePinner = builder.certificatePinner.withCertificateChainCleaner(this.certificateChainCleaner);
        this.proxyAuthenticator = builder.proxyAuthenticator;
        this.authenticator = builder.authenticator;
        this.connectionPool = builder.connectionPool;
        this.dns = builder.dns;
        this.followSslRedirects = builder.followSslRedirects;
        this.followRedirects = builder.followRedirects;
        this.retryOnConnectionFailure = builder.retryOnConnectionFailure;
        this.callTimeout = builder.callTimeout;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.pingInterval = builder.pingInterval;
        if (this.interceptors.contains(null)) {
            throw new IllegalStateException("Null interceptor: " + this.interceptors);
        }
        if (this.networkInterceptors.contains(null)) {
            throw new IllegalStateException("Null network interceptor: " + this.networkInterceptors);
        }
    }
    
    private static SSLSocketFactory newSslSocketFactory(final X509TrustManager trustManager) {
        try {
            final SSLContext sslContext = Platform.get().getSSLContext();
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            return sslContext.getSocketFactory();
        }
        catch (GeneralSecurityException e) {
            throw new AssertionError("No System TLS", e);
        }
    }
    
    public int callTimeoutMillis() {
        return this.callTimeout;
    }
    
    public int connectTimeoutMillis() {
        return this.connectTimeout;
    }
    
    public int readTimeoutMillis() {
        return this.readTimeout;
    }
    
    public int writeTimeoutMillis() {
        return this.writeTimeout;
    }
    
    public int pingIntervalMillis() {
        return this.pingInterval;
    }
    
    @Nullable
    public Proxy proxy() {
        return this.proxy;
    }
    
    public ProxySelector proxySelector() {
        return this.proxySelector;
    }
    
    public CookieJar cookieJar() {
        return this.cookieJar;
    }
    
    @Nullable
    public Cache cache() {
        return this.cache;
    }
    
    @Nullable
    InternalCache internalCache() {
        return (this.cache != null) ? this.cache.internalCache : this.internalCache;
    }
    
    public Dns dns() {
        return this.dns;
    }
    
    public SocketFactory socketFactory() {
        return this.socketFactory;
    }
    
    public SSLSocketFactory sslSocketFactory() {
        return this.sslSocketFactory;
    }
    
    public HostnameVerifier hostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public CertificatePinner certificatePinner() {
        return this.certificatePinner;
    }
    
    public Authenticator authenticator() {
        return this.authenticator;
    }
    
    public Authenticator proxyAuthenticator() {
        return this.proxyAuthenticator;
    }
    
    public ConnectionPool connectionPool() {
        return this.connectionPool;
    }
    
    public boolean followSslRedirects() {
        return this.followSslRedirects;
    }
    
    public boolean followRedirects() {
        return this.followRedirects;
    }
    
    public boolean retryOnConnectionFailure() {
        return this.retryOnConnectionFailure;
    }
    
    public Dispatcher dispatcher() {
        return this.dispatcher;
    }
    
    public List<Protocol> protocols() {
        return this.protocols;
    }
    
    public List<ConnectionSpec> connectionSpecs() {
        return this.connectionSpecs;
    }
    
    public List<Interceptor> interceptors() {
        return this.interceptors;
    }
    
    public List<Interceptor> networkInterceptors() {
        return this.networkInterceptors;
    }
    
    public EventListener.Factory eventListenerFactory() {
        return this.eventListenerFactory;
    }
    
    public Call newCall(final Request request) {
        return (Call)RealCall.newRealCall(this, request, false);
    }
    
    public WebSocket newWebSocket(final Request request, final WebSocketListener listener) {
        final RealWebSocket webSocket = new RealWebSocket(request, listener, new Random(), (long)this.pingInterval);
        webSocket.connect(this);
        return (WebSocket)webSocket;
    }
    
    public Builder newBuilder() {
        return new Builder(this);
    }
    
    static {
        DEFAULT_PROTOCOLS = Util.immutableList((Object[])new Protocol[] { Protocol.HTTP_2, Protocol.HTTP_1_1 });
        DEFAULT_CONNECTION_SPECS = Util.immutableList((Object[])new ConnectionSpec[] { ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT });
        Internal.instance = new Internal() {
            public void addLenient(final Headers.Builder builder, final String line) {
                builder.addLenient(line);
            }
            
            public void addLenient(final Headers.Builder builder, final String name, final String value) {
                builder.addLenient(name, value);
            }
            
            public RealConnectionPool realConnectionPool(final ConnectionPool connectionPool) {
                return connectionPool.delegate;
            }
            
            public boolean equalsNonHost(final Address a, final Address b) {
                return a.equalsNonHost(b);
            }
            
            public int code(final Response.Builder responseBuilder) {
                return responseBuilder.code;
            }
            
            public void apply(final ConnectionSpec tlsConfiguration, final SSLSocket sslSocket, final boolean isFallback) {
                tlsConfiguration.apply(sslSocket, isFallback);
            }
            
            public Call newWebSocketCall(final OkHttpClient client, final Request originalRequest) {
                return (Call)RealCall.newRealCall(client, originalRequest, true);
            }
            
            public void initExchange(final Response.Builder responseBuilder, final Exchange exchange) {
                responseBuilder.initExchange(exchange);
            }
            
            @Nullable
            public Exchange exchange(final Response response) {
                return response.exchange;
            }
        };
    }
    
    public static final class Builder
    {
        Dispatcher dispatcher;
        @Nullable
        Proxy proxy;
        List<Protocol> protocols;
        List<ConnectionSpec> connectionSpecs;
        final List<Interceptor> interceptors;
        final List<Interceptor> networkInterceptors;
        EventListener.Factory eventListenerFactory;
        ProxySelector proxySelector;
        CookieJar cookieJar;
        @Nullable
        Cache cache;
        @Nullable
        InternalCache internalCache;
        SocketFactory socketFactory;
        @Nullable
        SSLSocketFactory sslSocketFactory;
        @Nullable
        CertificateChainCleaner certificateChainCleaner;
        HostnameVerifier hostnameVerifier;
        CertificatePinner certificatePinner;
        Authenticator proxyAuthenticator;
        Authenticator authenticator;
        ConnectionPool connectionPool;
        Dns dns;
        boolean followSslRedirects;
        boolean followRedirects;
        boolean retryOnConnectionFailure;
        int callTimeout;
        int connectTimeout;
        int readTimeout;
        int writeTimeout;
        int pingInterval;
        
        public Builder() {
            this.interceptors = new ArrayList<Interceptor>();
            this.networkInterceptors = new ArrayList<Interceptor>();
            this.dispatcher = new Dispatcher();
            this.protocols = OkHttpClient.DEFAULT_PROTOCOLS;
            this.connectionSpecs = OkHttpClient.DEFAULT_CONNECTION_SPECS;
            this.eventListenerFactory = EventListener.factory(EventListener.NONE);
            this.proxySelector = ProxySelector.getDefault();
            if (this.proxySelector == null) {
                this.proxySelector = (ProxySelector)new NullProxySelector();
            }
            this.cookieJar = CookieJar.NO_COOKIES;
            this.socketFactory = SocketFactory.getDefault();
            this.hostnameVerifier = (HostnameVerifier)OkHostnameVerifier.INSTANCE;
            this.certificatePinner = CertificatePinner.DEFAULT;
            this.proxyAuthenticator = Authenticator.NONE;
            this.authenticator = Authenticator.NONE;
            this.connectionPool = new ConnectionPool();
            this.dns = Dns.SYSTEM;
            this.followSslRedirects = true;
            this.followRedirects = true;
            this.retryOnConnectionFailure = true;
            this.callTimeout = 0;
            this.connectTimeout = 10000;
            this.readTimeout = 10000;
            this.writeTimeout = 10000;
            this.pingInterval = 0;
        }
        
        Builder(final OkHttpClient okHttpClient) {
            this.interceptors = new ArrayList<Interceptor>();
            this.networkInterceptors = new ArrayList<Interceptor>();
            this.dispatcher = okHttpClient.dispatcher;
            this.proxy = okHttpClient.proxy;
            this.protocols = okHttpClient.protocols;
            this.connectionSpecs = okHttpClient.connectionSpecs;
            this.interceptors.addAll(okHttpClient.interceptors);
            this.networkInterceptors.addAll(okHttpClient.networkInterceptors);
            this.eventListenerFactory = okHttpClient.eventListenerFactory;
            this.proxySelector = okHttpClient.proxySelector;
            this.cookieJar = okHttpClient.cookieJar;
            this.internalCache = okHttpClient.internalCache;
            this.cache = okHttpClient.cache;
            this.socketFactory = okHttpClient.socketFactory;
            this.sslSocketFactory = okHttpClient.sslSocketFactory;
            this.certificateChainCleaner = okHttpClient.certificateChainCleaner;
            this.hostnameVerifier = okHttpClient.hostnameVerifier;
            this.certificatePinner = okHttpClient.certificatePinner;
            this.proxyAuthenticator = okHttpClient.proxyAuthenticator;
            this.authenticator = okHttpClient.authenticator;
            this.connectionPool = okHttpClient.connectionPool;
            this.dns = okHttpClient.dns;
            this.followSslRedirects = okHttpClient.followSslRedirects;
            this.followRedirects = okHttpClient.followRedirects;
            this.retryOnConnectionFailure = okHttpClient.retryOnConnectionFailure;
            this.callTimeout = okHttpClient.callTimeout;
            this.connectTimeout = okHttpClient.connectTimeout;
            this.readTimeout = okHttpClient.readTimeout;
            this.writeTimeout = okHttpClient.writeTimeout;
            this.pingInterval = okHttpClient.pingInterval;
        }
        
        public Builder callTimeout(final long timeout, final TimeUnit unit) {
            this.callTimeout = Util.checkDuration("timeout", timeout, unit);
            return this;
        }
        
        @IgnoreJRERequirement
        public Builder callTimeout(final Duration duration) {
            this.callTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
            return this;
        }
        
        public Builder connectTimeout(final long timeout, final TimeUnit unit) {
            this.connectTimeout = Util.checkDuration("timeout", timeout, unit);
            return this;
        }
        
        @IgnoreJRERequirement
        public Builder connectTimeout(final Duration duration) {
            this.connectTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
            return this;
        }
        
        public Builder readTimeout(final long timeout, final TimeUnit unit) {
            this.readTimeout = Util.checkDuration("timeout", timeout, unit);
            return this;
        }
        
        @IgnoreJRERequirement
        public Builder readTimeout(final Duration duration) {
            this.readTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
            return this;
        }
        
        public Builder writeTimeout(final long timeout, final TimeUnit unit) {
            this.writeTimeout = Util.checkDuration("timeout", timeout, unit);
            return this;
        }
        
        @IgnoreJRERequirement
        public Builder writeTimeout(final Duration duration) {
            this.writeTimeout = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
            return this;
        }
        
        public Builder pingInterval(final long interval, final TimeUnit unit) {
            this.pingInterval = Util.checkDuration("interval", interval, unit);
            return this;
        }
        
        @IgnoreJRERequirement
        public Builder pingInterval(final Duration duration) {
            this.pingInterval = Util.checkDuration("timeout", duration.toMillis(), TimeUnit.MILLISECONDS);
            return this;
        }
        
        public Builder proxy(@Nullable final Proxy proxy) {
            this.proxy = proxy;
            return this;
        }
        
        public Builder proxySelector(final ProxySelector proxySelector) {
            if (proxySelector == null) {
                throw new NullPointerException("proxySelector == null");
            }
            this.proxySelector = proxySelector;
            return this;
        }
        
        public Builder cookieJar(final CookieJar cookieJar) {
            if (cookieJar == null) {
                throw new NullPointerException("cookieJar == null");
            }
            this.cookieJar = cookieJar;
            return this;
        }
        
        public Builder cache(@Nullable final Cache cache) {
            this.cache = cache;
            this.internalCache = null;
            return this;
        }
        
        public Builder dns(final Dns dns) {
            if (dns == null) {
                throw new NullPointerException("dns == null");
            }
            this.dns = dns;
            return this;
        }
        
        public Builder socketFactory(final SocketFactory socketFactory) {
            if (socketFactory == null) {
                throw new NullPointerException("socketFactory == null");
            }
            if (socketFactory instanceof SSLSocketFactory) {
                throw new IllegalArgumentException("socketFactory instanceof SSLSocketFactory");
            }
            this.socketFactory = socketFactory;
            return this;
        }
        
        @Deprecated
        public Builder sslSocketFactory(final SSLSocketFactory sslSocketFactory) {
            if (sslSocketFactory == null) {
                throw new NullPointerException("sslSocketFactory == null");
            }
            this.sslSocketFactory = sslSocketFactory;
            this.certificateChainCleaner = Platform.get().buildCertificateChainCleaner(sslSocketFactory);
            return this;
        }
        
        public Builder sslSocketFactory(final SSLSocketFactory sslSocketFactory, final X509TrustManager trustManager) {
            if (sslSocketFactory == null) {
                throw new NullPointerException("sslSocketFactory == null");
            }
            if (trustManager == null) {
                throw new NullPointerException("trustManager == null");
            }
            this.sslSocketFactory = sslSocketFactory;
            this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
            return this;
        }
        
        public Builder hostnameVerifier(final HostnameVerifier hostnameVerifier) {
            if (hostnameVerifier == null) {
                throw new NullPointerException("hostnameVerifier == null");
            }
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }
        
        public Builder certificatePinner(final CertificatePinner certificatePinner) {
            if (certificatePinner == null) {
                throw new NullPointerException("certificatePinner == null");
            }
            this.certificatePinner = certificatePinner;
            return this;
        }
        
        public Builder authenticator(final Authenticator authenticator) {
            if (authenticator == null) {
                throw new NullPointerException("authenticator == null");
            }
            this.authenticator = authenticator;
            return this;
        }
        
        public Builder proxyAuthenticator(final Authenticator proxyAuthenticator) {
            if (proxyAuthenticator == null) {
                throw new NullPointerException("proxyAuthenticator == null");
            }
            this.proxyAuthenticator = proxyAuthenticator;
            return this;
        }
        
        public Builder connectionPool(final ConnectionPool connectionPool) {
            if (connectionPool == null) {
                throw new NullPointerException("connectionPool == null");
            }
            this.connectionPool = connectionPool;
            return this;
        }
        
        public Builder followSslRedirects(final boolean followProtocolRedirects) {
            this.followSslRedirects = followProtocolRedirects;
            return this;
        }
        
        public Builder followRedirects(final boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }
        
        public Builder retryOnConnectionFailure(final boolean retryOnConnectionFailure) {
            this.retryOnConnectionFailure = retryOnConnectionFailure;
            return this;
        }
        
        public Builder dispatcher(final Dispatcher dispatcher) {
            if (dispatcher == null) {
                throw new IllegalArgumentException("dispatcher == null");
            }
            this.dispatcher = dispatcher;
            return this;
        }
        
        public Builder protocols(List<Protocol> protocols) {
            protocols = new ArrayList<Protocol>(protocols);
            if (!protocols.contains(Protocol.H2_PRIOR_KNOWLEDGE) && !protocols.contains(Protocol.HTTP_1_1)) {
                throw new IllegalArgumentException("protocols must contain h2_prior_knowledge or http/1.1: " + protocols);
            }
            if (protocols.contains(Protocol.H2_PRIOR_KNOWLEDGE) && protocols.size() > 1) {
                throw new IllegalArgumentException("protocols containing h2_prior_knowledge cannot use other protocols: " + protocols);
            }
            if (protocols.contains(Protocol.HTTP_1_0)) {
                throw new IllegalArgumentException("protocols must not contain http/1.0: " + protocols);
            }
            if (protocols.contains(null)) {
                throw new IllegalArgumentException("protocols must not contain null");
            }
            protocols.remove(Protocol.SPDY_3);
            this.protocols = Collections.unmodifiableList((List<? extends Protocol>)protocols);
            return this;
        }
        
        public Builder connectionSpecs(final List<ConnectionSpec> connectionSpecs) {
            this.connectionSpecs = (List<ConnectionSpec>)Util.immutableList((List)connectionSpecs);
            return this;
        }
        
        public List<Interceptor> interceptors() {
            return this.interceptors;
        }
        
        public Builder addInterceptor(final Interceptor interceptor) {
            if (interceptor == null) {
                throw new IllegalArgumentException("interceptor == null");
            }
            this.interceptors.add(interceptor);
            return this;
        }
        
        public List<Interceptor> networkInterceptors() {
            return this.networkInterceptors;
        }
        
        public Builder addNetworkInterceptor(final Interceptor interceptor) {
            if (interceptor == null) {
                throw new IllegalArgumentException("interceptor == null");
            }
            this.networkInterceptors.add(interceptor);
            return this;
        }
        
        public Builder eventListener(final EventListener eventListener) {
            if (eventListener == null) {
                throw new NullPointerException("eventListener == null");
            }
            this.eventListenerFactory = EventListener.factory(eventListener);
            return this;
        }
        
        public Builder eventListenerFactory(final EventListener.Factory eventListenerFactory) {
            if (eventListenerFactory == null) {
                throw new NullPointerException("eventListenerFactory == null");
            }
            this.eventListenerFactory = eventListenerFactory;
            return this;
        }
        
        public OkHttpClient build() {
            return new OkHttpClient(this);
        }
    }
}
