//Raddon On Top!

package okhttp3.internal.ws;

import okhttp3.*;
import okhttp3.internal.*;
import java.io.*;
import okhttp3.internal.connection.*;
import javax.annotation.*;
import java.util.concurrent.*;
import java.net.*;
import java.util.*;
import okio.*;

public final class RealWebSocket implements WebSocket, WebSocketReader.FrameCallback
{
    private static final List<Protocol> ONLY_HTTP1;
    private static final long MAX_QUEUE_SIZE = 16777216L;
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000L;
    private final Request originalRequest;
    final WebSocketListener listener;
    private final Random random;
    private final long pingIntervalMillis;
    private final String key;
    private Call call;
    private final Runnable writerRunnable;
    private WebSocketReader reader;
    private WebSocketWriter writer;
    private ScheduledExecutorService executor;
    private Streams streams;
    private final ArrayDeque<ByteString> pongQueue;
    private final ArrayDeque<Object> messageAndCloseQueue;
    private long queueSize;
    private boolean enqueuedClose;
    private ScheduledFuture<?> cancelFuture;
    private int receivedCloseCode;
    private String receivedCloseReason;
    private boolean failed;
    private int sentPingCount;
    private int receivedPingCount;
    private int receivedPongCount;
    private boolean awaitingPong;
    
    public RealWebSocket(final Request request, final WebSocketListener listener, final Random random, final long pingIntervalMillis) {
        this.pongQueue = new ArrayDeque<ByteString>();
        this.messageAndCloseQueue = new ArrayDeque<Object>();
        this.receivedCloseCode = -1;
        if (!"GET".equals(request.method())) {
            throw new IllegalArgumentException("Request must be GET: " + request.method());
        }
        this.originalRequest = request;
        this.listener = listener;
        this.random = random;
        this.pingIntervalMillis = pingIntervalMillis;
        final byte[] nonce = new byte[16];
        random.nextBytes(nonce);
        this.key = ByteString.of(nonce).base64();
        this.writerRunnable = (() -> {
            try {
                while (this.writeOneFrame()) {}
            }
            catch (IOException e) {
                this.failWebSocket(e, null);
            }
        });
    }
    
    @Override
    public Request request() {
        return this.originalRequest;
    }
    
    @Override
    public synchronized long queueSize() {
        return this.queueSize;
    }
    
    @Override
    public void cancel() {
        this.call.cancel();
    }
    
    public void connect(OkHttpClient client) {
        client = client.newBuilder().eventListener(EventListener.NONE).protocols(RealWebSocket.ONLY_HTTP1).build();
        final Request request = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").build();
        (this.call = Internal.instance.newWebSocketCall(client, request)).enqueue((Callback)new Callback() {
            public void onResponse(final Call call, final Response response) {
                final Exchange exchange = Internal.instance.exchange(response);
                Streams streams;
                try {
                    RealWebSocket.this.checkUpgradeSuccess(response, exchange);
                    streams = exchange.newWebSocketStreams();
                }
                catch (IOException e) {
                    if (exchange != null) {
                        exchange.webSocketUpgradeFailed();
                    }
                    RealWebSocket.this.failWebSocket(e, response);
                    Util.closeQuietly((Closeable)response);
                    return;
                }
                try {
                    final String name = "OkHttp WebSocket " + request.url().redact();
                    RealWebSocket.this.initReaderAndWriter(name, streams);
                    RealWebSocket.this.listener.onOpen(RealWebSocket.this, response);
                    RealWebSocket.this.loopReader();
                }
                catch (Exception e2) {
                    RealWebSocket.this.failWebSocket(e2, null);
                }
            }
            
            public void onFailure(final Call call, final IOException e) {
                RealWebSocket.this.failWebSocket(e, null);
            }
        });
    }
    
    void checkUpgradeSuccess(final Response response, @Nullable final Exchange exchange) throws IOException {
        if (response.code() != 101) {
            throw new ProtocolException("Expected HTTP 101 response but was '" + response.code() + " " + response.message() + "'");
        }
        final String headerConnection = response.header("Connection");
        if (!"Upgrade".equalsIgnoreCase(headerConnection)) {
            throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + headerConnection + "'");
        }
        final String headerUpgrade = response.header("Upgrade");
        if (!"websocket".equalsIgnoreCase(headerUpgrade)) {
            throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + headerUpgrade + "'");
        }
        final String headerAccept = response.header("Sec-WebSocket-Accept");
        final String acceptExpected = ByteString.encodeUtf8(this.key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
        if (!acceptExpected.equals(headerAccept)) {
            throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + acceptExpected + "' but was '" + headerAccept + "'");
        }
        if (exchange == null) {
            throw new ProtocolException("Web Socket exchange missing: bad interceptor?");
        }
    }
    
    public void initReaderAndWriter(final String name, final Streams streams) throws IOException {
        synchronized (this) {
            this.streams = streams;
            this.writer = new WebSocketWriter(streams.client, streams.sink, this.random);
            this.executor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(name, false));
            if (this.pingIntervalMillis != 0L) {
                this.executor.scheduleAtFixedRate(new PingRunnable(), this.pingIntervalMillis, this.pingIntervalMillis, TimeUnit.MILLISECONDS);
            }
            if (!this.messageAndCloseQueue.isEmpty()) {
                this.runWriter();
            }
        }
        this.reader = new WebSocketReader(streams.client, streams.source, this);
    }
    
    public void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            this.reader.processNextFrame();
        }
    }
    
    boolean processNextFrame() throws IOException {
        try {
            this.reader.processNextFrame();
            return this.receivedCloseCode == -1;
        }
        catch (Exception e) {
            this.failWebSocket(e, null);
            return false;
        }
    }
    
    void awaitTermination(final int timeout, final TimeUnit timeUnit) throws InterruptedException {
        this.executor.awaitTermination(timeout, timeUnit);
    }
    
    void tearDown() throws InterruptedException {
        if (this.cancelFuture != null) {
            this.cancelFuture.cancel(false);
        }
        this.executor.shutdown();
        this.executor.awaitTermination(10L, TimeUnit.SECONDS);
    }
    
    synchronized int sentPingCount() {
        return this.sentPingCount;
    }
    
    synchronized int receivedPingCount() {
        return this.receivedPingCount;
    }
    
    synchronized int receivedPongCount() {
        return this.receivedPongCount;
    }
    
    @Override
    public void onReadMessage(final String text) throws IOException {
        this.listener.onMessage(this, text);
    }
    
    @Override
    public void onReadMessage(final ByteString bytes) throws IOException {
        this.listener.onMessage(this, bytes);
    }
    
    @Override
    public synchronized void onReadPing(final ByteString payload) {
        if (this.failed || (this.enqueuedClose && this.messageAndCloseQueue.isEmpty())) {
            return;
        }
        this.pongQueue.add(payload);
        this.runWriter();
        ++this.receivedPingCount;
    }
    
    @Override
    public synchronized void onReadPong(final ByteString buffer) {
        ++this.receivedPongCount;
        this.awaitingPong = false;
    }
    
    @Override
    public void onReadClose(final int code, final String reason) {
        if (code == -1) {
            throw new IllegalArgumentException();
        }
        Streams toClose = null;
        synchronized (this) {
            if (this.receivedCloseCode != -1) {
                throw new IllegalStateException("already closed");
            }
            this.receivedCloseCode = code;
            this.receivedCloseReason = reason;
            if (this.enqueuedClose && this.messageAndCloseQueue.isEmpty()) {
                toClose = this.streams;
                this.streams = null;
                if (this.cancelFuture != null) {
                    this.cancelFuture.cancel(false);
                }
                this.executor.shutdown();
            }
        }
        try {
            this.listener.onClosing(this, code, reason);
            if (toClose != null) {
                this.listener.onClosed(this, code, reason);
            }
        }
        finally {
            Util.closeQuietly((Closeable)toClose);
        }
    }
    
    @Override
    public boolean send(final String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        return this.send(ByteString.encodeUtf8(text), 1);
    }
    
    @Override
    public boolean send(final ByteString bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        return this.send(bytes, 2);
    }
    
    private synchronized boolean send(final ByteString data, final int formatOpcode) {
        if (this.failed || this.enqueuedClose) {
            return false;
        }
        if (this.queueSize + data.size() > 16777216L) {
            this.close(1001, null);
            return false;
        }
        this.queueSize += data.size();
        this.messageAndCloseQueue.add(new Message(formatOpcode, data));
        this.runWriter();
        return true;
    }
    
    synchronized boolean pong(final ByteString payload) {
        if (this.failed || (this.enqueuedClose && this.messageAndCloseQueue.isEmpty())) {
            return false;
        }
        this.pongQueue.add(payload);
        this.runWriter();
        return true;
    }
    
    @Override
    public boolean close(final int code, final String reason) {
        return this.close(code, reason, 60000L);
    }
    
    synchronized boolean close(final int code, final String reason, final long cancelAfterCloseMillis) {
        WebSocketProtocol.validateCloseCode(code);
        ByteString reasonBytes = null;
        if (reason != null) {
            reasonBytes = ByteString.encodeUtf8(reason);
            if (reasonBytes.size() > 123L) {
                throw new IllegalArgumentException("reason.size() > 123: " + reason);
            }
        }
        if (this.failed || this.enqueuedClose) {
            return false;
        }
        this.enqueuedClose = true;
        this.messageAndCloseQueue.add(new Close(code, reasonBytes, cancelAfterCloseMillis));
        this.runWriter();
        return true;
    }
    
    private void runWriter() {
        assert Thread.holdsLock(this);
        if (this.executor != null) {
            this.executor.execute(this.writerRunnable);
        }
    }
    
    boolean writeOneFrame() throws IOException {
        Object messageOrClose = null;
        int receivedCloseCode = -1;
        String receivedCloseReason = null;
        Streams streamsToClose = null;
        final WebSocketWriter writer;
        final ByteString pong;
        synchronized (this) {
            if (this.failed) {
                return false;
            }
            writer = this.writer;
            pong = this.pongQueue.poll();
            if (pong == null) {
                messageOrClose = this.messageAndCloseQueue.poll();
                if (messageOrClose instanceof Close) {
                    receivedCloseCode = this.receivedCloseCode;
                    receivedCloseReason = this.receivedCloseReason;
                    if (receivedCloseCode != -1) {
                        streamsToClose = this.streams;
                        this.streams = null;
                        this.executor.shutdown();
                    }
                    else {
                        this.cancelFuture = this.executor.schedule(new CancelRunnable(), ((Close)messageOrClose).cancelAfterCloseMillis, TimeUnit.MILLISECONDS);
                    }
                }
                else if (messageOrClose == null) {
                    return false;
                }
            }
        }
        try {
            if (pong != null) {
                writer.writePong(pong);
            }
            else if (messageOrClose instanceof Message) {
                final ByteString data = ((Message)messageOrClose).data;
                final BufferedSink sink = Okio.buffer(writer.newMessageSink(((Message)messageOrClose).formatOpcode, data.size()));
                sink.write(data);
                sink.close();
                synchronized (this) {
                    this.queueSize -= data.size();
                }
            }
            else {
                if (!(messageOrClose instanceof Close)) {
                    throw new AssertionError();
                }
                final Close close = (Close)messageOrClose;
                writer.writeClose(close.code, close.reason);
                if (streamsToClose != null) {
                    this.listener.onClosed(this, receivedCloseCode, receivedCloseReason);
                }
            }
            return true;
        }
        finally {
            Util.closeQuietly((Closeable)streamsToClose);
        }
    }
    
    void writePingFrame() {
        final WebSocketWriter writer;
        final int failedPing;
        synchronized (this) {
            if (this.failed) {
                return;
            }
            writer = this.writer;
            failedPing = (this.awaitingPong ? this.sentPingCount : -1);
            ++this.sentPingCount;
            this.awaitingPong = true;
        }
        if (failedPing != -1) {
            this.failWebSocket(new SocketTimeoutException("sent ping but didn't receive pong within " + this.pingIntervalMillis + "ms (after " + (failedPing - 1) + " successful ping/pongs)"), null);
            return;
        }
        try {
            writer.writePing(ByteString.EMPTY);
        }
        catch (IOException e) {
            this.failWebSocket(e, null);
        }
    }
    
    public void failWebSocket(final Exception e, @Nullable final Response response) {
        final Streams streamsToClose;
        synchronized (this) {
            if (this.failed) {
                return;
            }
            this.failed = true;
            streamsToClose = this.streams;
            this.streams = null;
            if (this.cancelFuture != null) {
                this.cancelFuture.cancel(false);
            }
            if (this.executor != null) {
                this.executor.shutdown();
            }
        }
        try {
            this.listener.onFailure(this, e, response);
        }
        finally {
            Util.closeQuietly((Closeable)streamsToClose);
        }
    }
    
    static {
        ONLY_HTTP1 = Collections.singletonList(Protocol.HTTP_1_1);
    }
    
    private final class PingRunnable implements Runnable
    {
        PingRunnable() {
        }
        
        @Override
        public void run() {
            RealWebSocket.this.writePingFrame();
        }
    }
    
    static final class Message
    {
        final int formatOpcode;
        final ByteString data;
        
        Message(final int formatOpcode, final ByteString data) {
            this.formatOpcode = formatOpcode;
            this.data = data;
        }
    }
    
    static final class Close
    {
        final int code;
        final ByteString reason;
        final long cancelAfterCloseMillis;
        
        Close(final int code, final ByteString reason, final long cancelAfterCloseMillis) {
            this.code = code;
            this.reason = reason;
            this.cancelAfterCloseMillis = cancelAfterCloseMillis;
        }
    }
    
    public abstract static class Streams implements Closeable
    {
        public final boolean client;
        public final BufferedSource source;
        public final BufferedSink sink;
        
        public Streams(final boolean client, final BufferedSource source, final BufferedSink sink) {
            this.client = client;
            this.source = source;
            this.sink = sink;
        }
    }
    
    final class CancelRunnable implements Runnable
    {
        @Override
        public void run() {
            RealWebSocket.this.cancel();
        }
    }
}
