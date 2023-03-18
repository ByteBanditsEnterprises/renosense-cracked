//Raddon On Top!

package okhttp3.internal.connection;

import java.lang.ref.*;
import java.util.*;
import java.io.*;
import okhttp3.internal.platform.*;
import okio.*;
import java.security.cert.*;
import okhttp3.internal.tls.*;
import javax.net.ssl.*;
import okhttp3.internal.http1.*;
import java.util.concurrent.*;
import javax.annotation.*;
import okhttp3.internal.*;
import okhttp3.*;
import okhttp3.internal.http.*;
import okhttp3.internal.ws.*;
import java.net.*;
import okhttp3.internal.http2.*;

public final class RealConnection extends Http2Connection.Listener implements Connection
{
    private static final String NPE_THROW_WITH_NULL = "throw with null exception";
    private static final int MAX_TUNNEL_ATTEMPTS = 21;
    public final RealConnectionPool connectionPool;
    private final Route route;
    private Socket rawSocket;
    private Socket socket;
    private Handshake handshake;
    private Protocol protocol;
    private Http2Connection http2Connection;
    private BufferedSource source;
    private BufferedSink sink;
    boolean noNewExchanges;
    int routeFailureCount;
    int successCount;
    private int refusedStreamCount;
    private int allocationLimit;
    final List<Reference<Transmitter>> transmitters;
    long idleAtNanos;
    
    public RealConnection(final RealConnectionPool connectionPool, final Route route) {
        this.allocationLimit = 1;
        this.transmitters = new ArrayList<Reference<Transmitter>>();
        this.idleAtNanos = Long.MAX_VALUE;
        this.connectionPool = connectionPool;
        this.route = route;
    }
    
    public void noNewExchanges() {
        assert !Thread.holdsLock(this.connectionPool);
        synchronized (this.connectionPool) {
            this.noNewExchanges = true;
        }
    }
    
    static RealConnection testConnection(final RealConnectionPool connectionPool, final Route route, final Socket socket, final long idleAtNanos) {
        final RealConnection result = new RealConnection(connectionPool, route);
        result.socket = socket;
        result.idleAtNanos = idleAtNanos;
        return result;
    }
    
    public void connect(final int connectTimeout, final int readTimeout, final int writeTimeout, final int pingIntervalMillis, final boolean connectionRetryEnabled, final Call call, final EventListener eventListener) {
        if (this.protocol != null) {
            throw new IllegalStateException("already connected");
        }
        RouteException routeException = null;
        final List<ConnectionSpec> connectionSpecs = (List<ConnectionSpec>)this.route.address().connectionSpecs();
        final ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector((List)connectionSpecs);
        if (this.route.address().sslSocketFactory() == null) {
            if (!connectionSpecs.contains(ConnectionSpec.CLEARTEXT)) {
                throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
            }
            final String host = this.route.address().url().host();
            if (!Platform.get().isCleartextTrafficPermitted(host)) {
                throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + host + " not permitted by network security policy"));
            }
        }
        else if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            throw new RouteException(new UnknownServiceException("H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"));
        }
    Label_0396:
        while (true) {
            try {
                if (this.route.requiresTunnel()) {
                    this.connectTunnel(connectTimeout, readTimeout, writeTimeout, call, eventListener);
                    if (this.rawSocket == null) {
                        break Label_0396;
                    }
                }
                else {
                    this.connectSocket(connectTimeout, readTimeout, call, eventListener);
                }
                this.establishProtocol(connectionSpecSelector, pingIntervalMillis, call, eventListener);
                eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), this.protocol);
            }
            catch (IOException e) {
                Util.closeQuietly(this.socket);
                Util.closeQuietly(this.rawSocket);
                this.socket = null;
                this.rawSocket = null;
                this.source = null;
                this.sink = null;
                this.handshake = null;
                this.protocol = null;
                this.http2Connection = null;
                eventListener.connectFailed(call, this.route.socketAddress(), this.route.proxy(), (Protocol)null, e);
                if (routeException == null) {
                    routeException = new RouteException(e);
                }
                else {
                    routeException.addConnectException(e);
                }
                if (!connectionRetryEnabled || !connectionSpecSelector.connectionFailed(e)) {
                    throw routeException;
                }
                continue;
            }
            break;
        }
        if (this.route.requiresTunnel() && this.rawSocket == null) {
            final ProtocolException exception = new ProtocolException("Too many tunnel connections attempted: 21");
            throw new RouteException(exception);
        }
        if (this.http2Connection != null) {
            synchronized (this.connectionPool) {
                this.allocationLimit = this.http2Connection.maxConcurrentStreams();
            }
        }
    }
    
    private void connectTunnel(final int connectTimeout, final int readTimeout, final int writeTimeout, final Call call, final EventListener eventListener) throws IOException {
        Request tunnelRequest = this.createTunnelRequest();
        final HttpUrl url = tunnelRequest.url();
        for (int i = 0; i < 21; ++i) {
            this.connectSocket(connectTimeout, readTimeout, call, eventListener);
            tunnelRequest = this.createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
            if (tunnelRequest == null) {
                break;
            }
            Util.closeQuietly(this.rawSocket);
            this.rawSocket = null;
            this.sink = null;
            this.source = null;
            eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), (Protocol)null);
        }
    }
    
    private void connectSocket(final int connectTimeout, final int readTimeout, final Call call, final EventListener eventListener) throws IOException {
        final Proxy proxy = this.route.proxy();
        final Address address = this.route.address();
        this.rawSocket = ((proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.HTTP) ? address.socketFactory().createSocket() : new Socket(proxy));
        eventListener.connectStart(call, this.route.socketAddress(), proxy);
        this.rawSocket.setSoTimeout(readTimeout);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
        }
        catch (ConnectException e) {
            final ConnectException ce = new ConnectException("Failed to connect to " + this.route.socketAddress());
            ce.initCause(e);
            throw ce;
        }
        try {
            this.source = Okio.buffer(Okio.source(this.rawSocket));
            this.sink = Okio.buffer(Okio.sink(this.rawSocket));
        }
        catch (NullPointerException npe) {
            if ("throw with null exception".equals(npe.getMessage())) {
                throw new IOException(npe);
            }
        }
    }
    
    private void establishProtocol(final ConnectionSpecSelector connectionSpecSelector, final int pingIntervalMillis, final Call call, final EventListener eventListener) throws IOException {
        if (this.route.address().sslSocketFactory() != null) {
            eventListener.secureConnectStart(call);
            this.connectTls(connectionSpecSelector);
            eventListener.secureConnectEnd(call, this.handshake);
            if (this.protocol == Protocol.HTTP_2) {
                this.startHttp2(pingIntervalMillis);
            }
            return;
        }
        if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            this.socket = this.rawSocket;
            this.protocol = Protocol.H2_PRIOR_KNOWLEDGE;
            this.startHttp2(pingIntervalMillis);
            return;
        }
        this.socket = this.rawSocket;
        this.protocol = Protocol.HTTP_1_1;
    }
    
    private void startHttp2(final int pingIntervalMillis) throws IOException {
        this.socket.setSoTimeout(0);
        (this.http2Connection = new Http2Connection.Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).pingIntervalMillis(pingIntervalMillis).build()).start();
    }
    
    private void connectTls(final ConnectionSpecSelector connectionSpecSelector) throws IOException {
        final Address address = this.route.address();
        final SSLSocketFactory sslSocketFactory = address.sslSocketFactory();
        boolean success = false;
        SSLSocket sslSocket = null;
        try {
            sslSocket = (SSLSocket)sslSocketFactory.createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
            final ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
            if (connectionSpec.supportsTlsExtensions()) {
                Platform.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
            }
            sslSocket.startHandshake();
            final SSLSession sslSocketSession = sslSocket.getSession();
            final Handshake unverifiedHandshake = Handshake.get(sslSocketSession);
            if (!address.hostnameVerifier().verify(address.url().host(), sslSocketSession)) {
                final List<Certificate> peerCertificates = (List<Certificate>)unverifiedHandshake.peerCertificates();
                if (!peerCertificates.isEmpty()) {
                    final X509Certificate cert = peerCertificates.get(0);
                    throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified:\n    certificate: " + CertificatePinner.pin((Certificate)cert) + "\n    DN: " + cert.getSubjectDN().getName() + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
                }
                throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified (no certificates)");
            }
            else {
                address.certificatePinner().check(address.url().host(), unverifiedHandshake.peerCertificates());
                final String maybeProtocol = connectionSpec.supportsTlsExtensions() ? Platform.get().getSelectedProtocol(sslSocket) : null;
                this.socket = sslSocket;
                this.source = Okio.buffer(Okio.source(this.socket));
                this.sink = Okio.buffer(Okio.sink(this.socket));
                this.handshake = unverifiedHandshake;
                this.protocol = ((maybeProtocol != null) ? Protocol.get(maybeProtocol) : Protocol.HTTP_1_1);
                success = true;
            }
        }
        catch (AssertionError e) {
            if (Util.isAndroidGetsocknameError(e)) {
                throw new IOException(e);
            }
            throw e;
        }
        finally {
            if (sslSocket != null) {
                Platform.get().afterHandshake(sslSocket);
            }
            if (!success) {
                Util.closeQuietly(sslSocket);
            }
        }
    }
    
    private Request createTunnel(final int readTimeout, final int writeTimeout, Request tunnelRequest, final HttpUrl url) throws IOException {
        final String requestLine = "CONNECT " + Util.hostHeader(url, true) + " HTTP/1.1";
        while (true) {
            final Http1ExchangeCodec tunnelCodec = new Http1ExchangeCodec(null, null, this.source, this.sink);
            this.source.timeout().timeout((long)readTimeout, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout((long)writeTimeout, TimeUnit.MILLISECONDS);
            tunnelCodec.writeRequest(tunnelRequest.headers(), requestLine);
            tunnelCodec.finishRequest();
            final Response response = tunnelCodec.readResponseHeaders(false).request(tunnelRequest).build();
            tunnelCodec.skipConnectBody(response);
            switch (response.code()) {
                case 200: {
                    if (!this.source.getBuffer().exhausted() || !this.sink.buffer().exhausted()) {
                        throw new IOException("TLS tunnel buffered too many bytes!");
                    }
                    return null;
                }
                case 407: {
                    tunnelRequest = this.route.address().proxyAuthenticator().authenticate(this.route, response);
                    if (tunnelRequest == null) {
                        throw new IOException("Failed to authenticate with proxy");
                    }
                    if ("close".equalsIgnoreCase(response.header("Connection"))) {
                        return tunnelRequest;
                    }
                    continue;
                }
                default: {
                    throw new IOException("Unexpected response code for CONNECT: " + response.code());
                }
            }
        }
    }
    
    private Request createTunnelRequest() throws IOException {
        final Request proxyConnectRequest = new Request.Builder().url(this.route.address().url()).method("CONNECT", null).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
        final Response fakeAuthChallengeResponse = new Response.Builder().request(proxyConnectRequest).protocol(Protocol.HTTP_1_1).code(407).message("Preemptive Authenticate").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1L).receivedResponseAtMillis(-1L).header("Proxy-Authenticate", "OkHttp-Preemptive").build();
        final Request authenticatedRequest = this.route.address().proxyAuthenticator().authenticate(this.route, fakeAuthChallengeResponse);
        return (authenticatedRequest != null) ? authenticatedRequest : proxyConnectRequest;
    }
    
    boolean isEligible(final Address address, @Nullable final List<Route> routes) {
        if (this.transmitters.size() >= this.allocationLimit || this.noNewExchanges) {
            return false;
        }
        if (!Internal.instance.equalsNonHost(this.route.address(), address)) {
            return false;
        }
        if (address.url().host().equals(this.route().address().url().host())) {
            return true;
        }
        if (this.http2Connection == null) {
            return false;
        }
        if (routes == null || !this.routeMatchesAny(routes)) {
            return false;
        }
        if (address.hostnameVerifier() != OkHostnameVerifier.INSTANCE) {
            return false;
        }
        if (!this.supportsUrl(address.url())) {
            return false;
        }
        try {
            address.certificatePinner().check(address.url().host(), this.handshake().peerCertificates());
        }
        catch (SSLPeerUnverifiedException e) {
            return false;
        }
        return true;
    }
    
    private boolean routeMatchesAny(final List<Route> candidates) {
        for (int i = 0, size = candidates.size(); i < size; ++i) {
            final Route candidate = candidates.get(i);
            if (candidate.proxy().type() == Proxy.Type.DIRECT && this.route.proxy().type() == Proxy.Type.DIRECT && this.route.socketAddress().equals(candidate.socketAddress())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean supportsUrl(final HttpUrl url) {
        return url.port() == this.route.address().url().port() && (url.host().equals(this.route.address().url().host()) || (this.handshake != null && OkHostnameVerifier.INSTANCE.verify(url.host(), this.handshake.peerCertificates().get(0))));
    }
    
    ExchangeCodec newCodec(final OkHttpClient client, final Interceptor.Chain chain) throws SocketException {
        if (this.http2Connection != null) {
            return new Http2ExchangeCodec(client, this, chain, this.http2Connection);
        }
        this.socket.setSoTimeout(chain.readTimeoutMillis());
        this.source.timeout().timeout((long)chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
        this.sink.timeout().timeout((long)chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        return new Http1ExchangeCodec(client, this, this.source, this.sink);
    }
    
    RealWebSocket.Streams newWebSocketStreams(final Exchange exchange) throws SocketException {
        this.socket.setSoTimeout(0);
        this.noNewExchanges();
        return new RealWebSocket.Streams(true, this.source, this.sink) {
            @Override
            public void close() throws IOException {
                exchange.bodyComplete(-1L, true, true, (IOException)null);
            }
        };
    }
    
    public Route route() {
        return this.route;
    }
    
    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }
    
    public Socket socket() {
        return this.socket;
    }
    
    public boolean isHealthy(final boolean doExtensiveChecks) {
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        if (this.http2Connection != null) {
            return this.http2Connection.isHealthy(System.nanoTime());
        }
        if (doExtensiveChecks) {
            try {
                final int readTimeout = this.socket.getSoTimeout();
                try {
                    this.socket.setSoTimeout(1);
                    return !this.source.exhausted();
                }
                finally {
                    this.socket.setSoTimeout(readTimeout);
                }
            }
            catch (SocketTimeoutException ex) {}
            catch (IOException e) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onStream(final Http2Stream stream) throws IOException {
        stream.close(ErrorCode.REFUSED_STREAM, null);
    }
    
    @Override
    public void onSettings(final Http2Connection connection) {
        synchronized (this.connectionPool) {
            this.allocationLimit = connection.maxConcurrentStreams();
        }
    }
    
    public Handshake handshake() {
        return this.handshake;
    }
    
    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }
    
    void trackFailure(@Nullable final IOException e) {
        assert !Thread.holdsLock(this.connectionPool);
        synchronized (this.connectionPool) {
            if (e instanceof StreamResetException) {
                final ErrorCode errorCode = ((StreamResetException)e).errorCode;
                if (errorCode == ErrorCode.REFUSED_STREAM) {
                    ++this.refusedStreamCount;
                    if (this.refusedStreamCount > 1) {
                        this.noNewExchanges = true;
                        ++this.routeFailureCount;
                    }
                }
                else if (errorCode != ErrorCode.CANCEL) {
                    this.noNewExchanges = true;
                    ++this.routeFailureCount;
                }
            }
            else if (!this.isMultiplexed() || e instanceof ConnectionShutdownException) {
                this.noNewExchanges = true;
                if (this.successCount == 0) {
                    if (e != null) {
                        this.connectionPool.connectFailed(this.route, e);
                    }
                    ++this.routeFailureCount;
                }
            }
        }
    }
    
    public Protocol protocol() {
        return this.protocol;
    }
    
    public String toString() {
        return "Connection{" + this.route.address().url().host() + ":" + this.route.address().url().port() + ", proxy=" + this.route.proxy() + " hostAddress=" + this.route.socketAddress() + " cipherSuite=" + ((this.handshake != null) ? this.handshake.cipherSuite() : "none") + " protocol=" + this.protocol + '}';
    }
}
