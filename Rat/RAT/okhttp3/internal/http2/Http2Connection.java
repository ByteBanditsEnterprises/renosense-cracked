//Raddon On Top!

package okhttp3.internal.http2;

import java.util.*;
import okhttp3.*;
import java.io.*;
import okhttp3.internal.*;
import javax.annotation.*;
import java.util.concurrent.*;
import java.net.*;
import okhttp3.internal.platform.*;
import okio.*;

public final class Http2Connection implements Closeable
{
    static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
    static final int INTERVAL_PING = 1;
    static final int DEGRADED_PING = 2;
    static final int AWAIT_PING = 3;
    static final long DEGRADED_PONG_TIMEOUT_NS = 1000000000L;
    private static final ExecutorService listenerExecutor;
    final boolean client;
    final Listener listener;
    final Map<Integer, Http2Stream> streams;
    final String connectionName;
    int lastGoodStreamId;
    int nextStreamId;
    private boolean shutdown;
    private final ScheduledExecutorService writerExecutor;
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    private long intervalPingsSent;
    private long intervalPongsReceived;
    private long degradedPingsSent;
    private long degradedPongsReceived;
    private long awaitPingsSent;
    private long awaitPongsReceived;
    private long degradedPongDeadlineNs;
    long unacknowledgedBytesRead;
    long bytesLeftInWriteWindow;
    Settings okHttpSettings;
    final Settings peerSettings;
    final Socket socket;
    final Http2Writer writer;
    final ReaderRunnable readerRunnable;
    final Set<Integer> currentPushRequests;
    
    Http2Connection(final Builder builder) {
        this.streams = new LinkedHashMap<Integer, Http2Stream>();
        this.intervalPingsSent = 0L;
        this.intervalPongsReceived = 0L;
        this.degradedPingsSent = 0L;
        this.degradedPongsReceived = 0L;
        this.awaitPingsSent = 0L;
        this.awaitPongsReceived = 0L;
        this.degradedPongDeadlineNs = 0L;
        this.unacknowledgedBytesRead = 0L;
        this.okHttpSettings = new Settings();
        this.peerSettings = new Settings();
        this.currentPushRequests = new LinkedHashSet<Integer>();
        this.pushObserver = builder.pushObserver;
        this.client = builder.client;
        this.listener = builder.listener;
        this.nextStreamId = (builder.client ? 1 : 2);
        if (builder.client) {
            this.nextStreamId += 2;
        }
        if (builder.client) {
            this.okHttpSettings.set(7, 16777216);
        }
        this.connectionName = builder.connectionName;
        this.writerExecutor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(Util.format("OkHttp %s Writer", this.connectionName), false));
        if (builder.pingIntervalMillis != 0) {
            this.writerExecutor.scheduleAtFixedRate(new IntervalPingRunnable(), builder.pingIntervalMillis, builder.pingIntervalMillis, TimeUnit.MILLISECONDS);
        }
        this.pushExecutor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Util.threadFactory(Util.format("OkHttp %s Push Observer", this.connectionName), true));
        this.peerSettings.set(7, 65535);
        this.peerSettings.set(5, 16384);
        this.bytesLeftInWriteWindow = this.peerSettings.getInitialWindowSize();
        this.socket = builder.socket;
        this.writer = new Http2Writer(builder.sink, this.client);
        this.readerRunnable = new ReaderRunnable(new Http2Reader(builder.source, this.client));
    }
    
    public synchronized int openStreamCount() {
        return this.streams.size();
    }
    
    synchronized Http2Stream getStream(final int id) {
        return this.streams.get(id);
    }
    
    synchronized Http2Stream removeStream(final int streamId) {
        final Http2Stream stream = this.streams.remove(streamId);
        this.notifyAll();
        return stream;
    }
    
    public synchronized int maxConcurrentStreams() {
        return this.peerSettings.getMaxConcurrentStreams(Integer.MAX_VALUE);
    }
    
    synchronized void updateConnectionFlowControl(final long read) {
        this.unacknowledgedBytesRead += read;
        if (this.unacknowledgedBytesRead >= this.okHttpSettings.getInitialWindowSize() / 2) {
            this.writeWindowUpdateLater(0, this.unacknowledgedBytesRead);
            this.unacknowledgedBytesRead = 0L;
        }
    }
    
    public Http2Stream pushStream(final int associatedStreamId, final List<Header> requestHeaders, final boolean out) throws IOException {
        if (this.client) {
            throw new IllegalStateException("Client cannot push requests.");
        }
        return this.newStream(associatedStreamId, requestHeaders, out);
    }
    
    public Http2Stream newStream(final List<Header> requestHeaders, final boolean out) throws IOException {
        return this.newStream(0, requestHeaders, out);
    }
    
    private Http2Stream newStream(final int associatedStreamId, final List<Header> requestHeaders, final boolean out) throws IOException {
        final boolean outFinished = !out;
        final boolean inFinished = false;
        final Http2Stream stream;
        final boolean flushHeaders;
        synchronized (this.writer) {
            final int streamId;
            synchronized (this) {
                if (this.nextStreamId > 1073741823) {
                    this.shutdown(ErrorCode.REFUSED_STREAM);
                }
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                streamId = this.nextStreamId;
                this.nextStreamId += 2;
                stream = new Http2Stream(streamId, this, outFinished, inFinished, null);
                flushHeaders = (!out || this.bytesLeftInWriteWindow == 0L || stream.bytesLeftInWriteWindow == 0L);
                if (stream.isOpen()) {
                    this.streams.put(streamId, stream);
                }
            }
            if (associatedStreamId == 0) {
                this.writer.headers(outFinished, streamId, requestHeaders);
            }
            else {
                if (this.client) {
                    throw new IllegalArgumentException("client streams shouldn't have associated stream IDs");
                }
                this.writer.pushPromise(associatedStreamId, streamId, requestHeaders);
            }
        }
        if (flushHeaders) {
            this.writer.flush();
        }
        return stream;
    }
    
    void writeHeaders(final int streamId, final boolean outFinished, final List<Header> alternating) throws IOException {
        this.writer.headers(outFinished, streamId, alternating);
    }
    
    public void writeData(final int streamId, final boolean outFinished, final Buffer buffer, long byteCount) throws IOException {
        if (byteCount == 0L) {
            this.writer.data(outFinished, streamId, buffer, 0);
            return;
        }
        while (byteCount > 0L) {
            int toWrite;
            synchronized (this) {
                try {
                    while (this.bytesLeftInWriteWindow <= 0L) {
                        if (!this.streams.containsKey(streamId)) {
                            throw new IOException("stream closed");
                        }
                        this.wait();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException();
                }
                toWrite = (int)Math.min(byteCount, this.bytesLeftInWriteWindow);
                toWrite = Math.min(toWrite, this.writer.maxDataLength());
                this.bytesLeftInWriteWindow -= toWrite;
            }
            byteCount -= toWrite;
            this.writer.data(outFinished && byteCount == 0L, streamId, buffer, toWrite);
        }
    }
    
    void writeSynResetLater(final int streamId, final ErrorCode errorCode) {
        try {
            this.writerExecutor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[] { this.connectionName, streamId }) {
                public void execute() {
                    try {
                        Http2Connection.this.writeSynReset(streamId, errorCode);
                    }
                    catch (IOException e) {
                        Http2Connection.this.failConnection(e);
                    }
                }
            });
        }
        catch (RejectedExecutionException ex) {}
    }
    
    void writeSynReset(final int streamId, final ErrorCode statusCode) throws IOException {
        this.writer.rstStream(streamId, statusCode);
    }
    
    void writeWindowUpdateLater(final int streamId, final long unacknowledgedBytesRead) {
        try {
            this.writerExecutor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[] { this.connectionName, streamId }) {
                public void execute() {
                    try {
                        Http2Connection.this.writer.windowUpdate(streamId, unacknowledgedBytesRead);
                    }
                    catch (IOException e) {
                        Http2Connection.this.failConnection(e);
                    }
                }
            });
        }
        catch (RejectedExecutionException ex) {}
    }
    
    void writePing(final boolean reply, final int payload1, final int payload2) {
        try {
            this.writer.ping(reply, payload1, payload2);
        }
        catch (IOException e) {
            this.failConnection(e);
        }
    }
    
    void writePingAndAwaitPong() throws InterruptedException {
        this.writePing();
        this.awaitPong();
    }
    
    void writePing() {
        synchronized (this) {
            ++this.awaitPingsSent;
        }
        this.writePing(false, 3, 1330343787);
    }
    
    synchronized void awaitPong() throws InterruptedException {
        while (this.awaitPongsReceived < this.awaitPingsSent) {
            this.wait();
        }
    }
    
    public void flush() throws IOException {
        this.writer.flush();
    }
    
    public void shutdown(final ErrorCode statusCode) throws IOException {
        synchronized (this.writer) {
            final int lastGoodStreamId;
            synchronized (this) {
                if (this.shutdown) {
                    return;
                }
                this.shutdown = true;
                lastGoodStreamId = this.lastGoodStreamId;
            }
            this.writer.goAway(lastGoodStreamId, statusCode, Util.EMPTY_BYTE_ARRAY);
        }
    }
    
    @Override
    public void close() {
        this.close(ErrorCode.NO_ERROR, ErrorCode.CANCEL, null);
    }
    
    void close(final ErrorCode connectionCode, final ErrorCode streamCode, @Nullable final IOException cause) {
        assert !Thread.holdsLock(this);
        try {
            this.shutdown(connectionCode);
        }
        catch (IOException ex) {}
        Http2Stream[] streamsToClose = null;
        synchronized (this) {
            if (!this.streams.isEmpty()) {
                streamsToClose = this.streams.values().toArray(new Http2Stream[this.streams.size()]);
                this.streams.clear();
            }
        }
        if (streamsToClose != null) {
            for (final Http2Stream stream : streamsToClose) {
                try {
                    stream.close(streamCode, cause);
                }
                catch (IOException ex2) {}
            }
        }
        try {
            this.writer.close();
        }
        catch (IOException ex3) {}
        try {
            this.socket.close();
        }
        catch (IOException ex4) {}
        this.writerExecutor.shutdown();
        this.pushExecutor.shutdown();
    }
    
    private void failConnection(@Nullable final IOException e) {
        this.close(ErrorCode.PROTOCOL_ERROR, ErrorCode.PROTOCOL_ERROR, e);
    }
    
    public void start() throws IOException {
        this.start(true);
    }
    
    void start(final boolean sendConnectionPreface) throws IOException {
        if (sendConnectionPreface) {
            this.writer.connectionPreface();
            this.writer.settings(this.okHttpSettings);
            final int windowSize = this.okHttpSettings.getInitialWindowSize();
            if (windowSize != 65535) {
                this.writer.windowUpdate(0, windowSize - 65535);
            }
        }
        new Thread(this.readerRunnable).start();
    }
    
    public void setSettings(final Settings settings) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                this.okHttpSettings.merge(settings);
            }
            this.writer.settings(settings);
        }
    }
    
    public synchronized boolean isHealthy(final long nowNs) {
        return !this.shutdown && (this.degradedPongsReceived >= this.degradedPingsSent || nowNs < this.degradedPongDeadlineNs);
    }
    
    void sendDegradedPingLater() {
        synchronized (this) {
            if (this.degradedPongsReceived < this.degradedPingsSent) {
                return;
            }
            ++this.degradedPingsSent;
            this.degradedPongDeadlineNs = System.nanoTime() + 1000000000L;
        }
        try {
            this.writerExecutor.execute(new NamedRunnable("OkHttp %s ping", new Object[] { this.connectionName }) {
                public void execute() {
                    Http2Connection.this.writePing(false, 2, 0);
                }
            });
        }
        catch (RejectedExecutionException ex) {}
    }
    
    boolean pushedStream(final int streamId) {
        return streamId != 0 && (streamId & 0x1) == 0x0;
    }
    
    void pushRequestLater(final int streamId, final List<Header> requestHeaders) {
        synchronized (this) {
            if (this.currentPushRequests.contains(streamId)) {
                this.writeSynResetLater(streamId, ErrorCode.PROTOCOL_ERROR);
                return;
            }
            this.currentPushRequests.add(streamId);
        }
        try {
            this.pushExecutorExecute(new NamedRunnable("OkHttp %s Push Request[%s]", new Object[] { this.connectionName, streamId }) {
                public void execute() {
                    final boolean cancel = Http2Connection.this.pushObserver.onRequest(streamId, requestHeaders);
                    try {
                        if (cancel) {
                            Http2Connection.this.writer.rstStream(streamId, ErrorCode.CANCEL);
                            synchronized (Http2Connection.this) {
                                Http2Connection.this.currentPushRequests.remove(streamId);
                            }
                        }
                    }
                    catch (IOException ex) {}
                }
            });
        }
        catch (RejectedExecutionException ex) {}
    }
    
    void pushHeadersLater(final int streamId, final List<Header> requestHeaders, final boolean inFinished) {
        try {
            this.pushExecutorExecute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[] { this.connectionName, streamId }) {
                public void execute() {
                    final boolean cancel = Http2Connection.this.pushObserver.onHeaders(streamId, requestHeaders, inFinished);
                    try {
                        if (cancel) {
                            Http2Connection.this.writer.rstStream(streamId, ErrorCode.CANCEL);
                        }
                        if (cancel || inFinished) {
                            synchronized (Http2Connection.this) {
                                Http2Connection.this.currentPushRequests.remove(streamId);
                            }
                        }
                    }
                    catch (IOException ex) {}
                }
            });
        }
        catch (RejectedExecutionException ex) {}
    }
    
    void pushDataLater(final int streamId, final BufferedSource source, final int byteCount, final boolean inFinished) throws IOException {
        final Buffer buffer = new Buffer();
        source.require((long)byteCount);
        source.read(buffer, (long)byteCount);
        if (buffer.size() != byteCount) {
            throw new IOException(buffer.size() + " != " + byteCount);
        }
        this.pushExecutorExecute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[] { this.connectionName, streamId }) {
            public void execute() {
                try {
                    final boolean cancel = Http2Connection.this.pushObserver.onData(streamId, (BufferedSource)buffer, byteCount, inFinished);
                    if (cancel) {
                        Http2Connection.this.writer.rstStream(streamId, ErrorCode.CANCEL);
                    }
                    if (cancel || inFinished) {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(streamId);
                        }
                    }
                }
                catch (IOException ex) {}
            }
        });
    }
    
    void pushResetLater(final int streamId, final ErrorCode errorCode) {
        this.pushExecutorExecute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[] { this.connectionName, streamId }) {
            public void execute() {
                Http2Connection.this.pushObserver.onReset(streamId, errorCode);
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(streamId);
                }
            }
        });
    }
    
    private synchronized void pushExecutorExecute(final NamedRunnable namedRunnable) {
        if (!this.shutdown) {
            this.pushExecutor.execute(namedRunnable);
        }
    }
    
    static {
        listenerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Http2Connection", true));
    }
    
    final class PingRunnable extends NamedRunnable
    {
        final boolean reply;
        final int payload1;
        final int payload2;
        
        PingRunnable(final boolean reply, final int payload1, final int payload2) {
            super("OkHttp %s ping %08x%08x", new Object[] { Http2Connection.this.connectionName, payload1, payload2 });
            this.reply = reply;
            this.payload1 = payload1;
            this.payload2 = payload2;
        }
        
        public void execute() {
            Http2Connection.this.writePing(this.reply, this.payload1, this.payload2);
        }
    }
    
    final class IntervalPingRunnable extends NamedRunnable
    {
        IntervalPingRunnable() {
            super("OkHttp %s ping", new Object[] { Http2Connection.this.connectionName });
        }
        
        public void execute() {
            boolean failDueToMissingPong;
            synchronized (Http2Connection.this) {
                if (Http2Connection.this.intervalPongsReceived < Http2Connection.this.intervalPingsSent) {
                    failDueToMissingPong = true;
                }
                else {
                    Http2Connection.this.intervalPingsSent++;
                    failDueToMissingPong = false;
                }
            }
            if (failDueToMissingPong) {
                Http2Connection.this.failConnection(null);
            }
            else {
                Http2Connection.this.writePing(false, 1, 0);
            }
        }
    }
    
    public static class Builder
    {
        Socket socket;
        String connectionName;
        BufferedSource source;
        BufferedSink sink;
        Listener listener;
        PushObserver pushObserver;
        boolean client;
        int pingIntervalMillis;
        
        public Builder(final boolean client) {
            this.listener = Listener.REFUSE_INCOMING_STREAMS;
            this.pushObserver = PushObserver.CANCEL;
            this.client = client;
        }
        
        public Builder socket(final Socket socket) throws IOException {
            final SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
            final String connectionName = (remoteSocketAddress instanceof InetSocketAddress) ? ((InetSocketAddress)remoteSocketAddress).getHostName() : remoteSocketAddress.toString();
            return this.socket(socket, connectionName, Okio.buffer(Okio.source(socket)), Okio.buffer(Okio.sink(socket)));
        }
        
        public Builder socket(final Socket socket, final String connectionName, final BufferedSource source, final BufferedSink sink) {
            this.socket = socket;
            this.connectionName = connectionName;
            this.source = source;
            this.sink = sink;
            return this;
        }
        
        public Builder listener(final Listener listener) {
            this.listener = listener;
            return this;
        }
        
        public Builder pushObserver(final PushObserver pushObserver) {
            this.pushObserver = pushObserver;
            return this;
        }
        
        public Builder pingIntervalMillis(final int pingIntervalMillis) {
            this.pingIntervalMillis = pingIntervalMillis;
            return this;
        }
        
        public Http2Connection build() {
            return new Http2Connection(this);
        }
    }
    
    class ReaderRunnable extends NamedRunnable implements Http2Reader.Handler
    {
        final Http2Reader reader;
        
        ReaderRunnable(final Http2Reader reader) {
            super("OkHttp %s", new Object[] { Http2Connection.this.connectionName });
            this.reader = reader;
        }
        
        @Override
        protected void execute() {
            ErrorCode connectionErrorCode = ErrorCode.INTERNAL_ERROR;
            ErrorCode streamErrorCode = ErrorCode.INTERNAL_ERROR;
            IOException errorException = null;
            try {
                this.reader.readConnectionPreface(this);
                while (this.reader.nextFrame(false, this)) {}
                connectionErrorCode = ErrorCode.NO_ERROR;
                streamErrorCode = ErrorCode.CANCEL;
            }
            catch (IOException e) {
                errorException = e;
                connectionErrorCode = ErrorCode.PROTOCOL_ERROR;
                streamErrorCode = ErrorCode.PROTOCOL_ERROR;
            }
            finally {
                Http2Connection.this.close(connectionErrorCode, streamErrorCode, errorException);
                Util.closeQuietly(this.reader);
            }
        }
        
        @Override
        public void data(final boolean inFinished, final int streamId, final BufferedSource source, final int length) throws IOException {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushDataLater(streamId, source, length, inFinished);
                return;
            }
            final Http2Stream dataStream = Http2Connection.this.getStream(streamId);
            if (dataStream == null) {
                Http2Connection.this.writeSynResetLater(streamId, ErrorCode.PROTOCOL_ERROR);
                Http2Connection.this.updateConnectionFlowControl(length);
                source.skip((long)length);
                return;
            }
            dataStream.receiveData(source, length);
            if (inFinished) {
                dataStream.receiveHeaders(Util.EMPTY_HEADERS, true);
            }
        }
        
        @Override
        public void headers(final boolean inFinished, final int streamId, final int associatedStreamId, final List<Header> headerBlock) {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushHeadersLater(streamId, headerBlock, inFinished);
                return;
            }
            final Http2Stream stream;
            synchronized (Http2Connection.this) {
                stream = Http2Connection.this.getStream(streamId);
                if (stream == null) {
                    if (Http2Connection.this.shutdown) {
                        return;
                    }
                    if (streamId <= Http2Connection.this.lastGoodStreamId) {
                        return;
                    }
                    if (streamId % 2 == Http2Connection.this.nextStreamId % 2) {
                        return;
                    }
                    final Headers headers = Util.toHeaders(headerBlock);
                    final Http2Stream newStream = new Http2Stream(streamId, Http2Connection.this, false, inFinished, headers);
                    Http2Connection.this.lastGoodStreamId = streamId;
                    Http2Connection.this.streams.put(streamId, newStream);
                    Http2Connection.listenerExecutor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[] { Http2Connection.this.connectionName, streamId }) {
                        public void execute() {
                            try {
                                Http2Connection.this.listener.onStream(newStream);
                            }
                            catch (IOException e) {
                                Platform.get().log(4, "Http2Connection.Listener failure for " + Http2Connection.this.connectionName, e);
                                try {
                                    newStream.close(ErrorCode.PROTOCOL_ERROR, e);
                                }
                                catch (IOException ex) {}
                            }
                        }
                    });
                    return;
                }
            }
            stream.receiveHeaders(Util.toHeaders(headerBlock), inFinished);
        }
        
        @Override
        public void rstStream(final int streamId, final ErrorCode errorCode) {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushResetLater(streamId, errorCode);
                return;
            }
            final Http2Stream rstStream = Http2Connection.this.removeStream(streamId);
            if (rstStream != null) {
                rstStream.receiveRstStream(errorCode);
            }
        }
        
        @Override
        public void settings(final boolean clearPrevious, final Settings settings) {
            try {
                Http2Connection.this.writerExecutor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[] { Http2Connection.this.connectionName }) {
                    public void execute() {
                        ReaderRunnable.this.applyAndAckSettings(clearPrevious, settings);
                    }
                });
            }
            catch (RejectedExecutionException ex) {}
        }
        
        void applyAndAckSettings(final boolean clearPrevious, final Settings settings) {
            long delta = 0L;
            Http2Stream[] streamsToNotify = null;
            synchronized (Http2Connection.this.writer) {
                synchronized (Http2Connection.this) {
                    final int priorWriteWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                    if (clearPrevious) {
                        Http2Connection.this.peerSettings.clear();
                    }
                    Http2Connection.this.peerSettings.merge(settings);
                    final int peerInitialWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                    if (peerInitialWindowSize != -1 && peerInitialWindowSize != priorWriteWindowSize) {
                        delta = peerInitialWindowSize - priorWriteWindowSize;
                        streamsToNotify = (Http2Stream[])(Http2Connection.this.streams.isEmpty() ? null : ((Http2Stream[])Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()])));
                    }
                }
                try {
                    Http2Connection.this.writer.applyAndAckSettings(Http2Connection.this.peerSettings);
                }
                catch (IOException e) {
                    Http2Connection.this.failConnection(e);
                }
            }
            if (streamsToNotify != null) {
                for (final Http2Stream stream : streamsToNotify) {
                    synchronized (stream) {
                        stream.addBytesToWriteWindow(delta);
                    }
                }
            }
            Http2Connection.listenerExecutor.execute(new NamedRunnable("OkHttp %s settings", new Object[] { Http2Connection.this.connectionName }) {
                public void execute() {
                    Http2Connection.this.listener.onSettings(Http2Connection.this);
                }
            });
        }
        
        @Override
        public void ackSettings() {
        }
        
        @Override
        public void ping(final boolean reply, final int payload1, final int payload2) {
            if (reply) {
                synchronized (Http2Connection.this) {
                    if (payload1 == 1) {
                        Http2Connection.this.intervalPongsReceived++;
                    }
                    else if (payload1 == 2) {
                        Http2Connection.this.degradedPongsReceived++;
                    }
                    else if (payload1 == 3) {
                        Http2Connection.this.awaitPongsReceived++;
                        Http2Connection.this.notifyAll();
                    }
                }
            }
            else {
                try {
                    Http2Connection.this.writerExecutor.execute(new PingRunnable(true, payload1, payload2));
                }
                catch (RejectedExecutionException ex) {}
            }
        }
        
        @Override
        public void goAway(final int lastGoodStreamId, final ErrorCode errorCode, final ByteString debugData) {
            if (debugData.size() > 0) {}
            final Http2Stream[] streamsCopy;
            synchronized (Http2Connection.this) {
                streamsCopy = Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                Http2Connection.this.shutdown = true;
            }
            for (final Http2Stream http2Stream : streamsCopy) {
                if (http2Stream.getId() > lastGoodStreamId && http2Stream.isLocallyInitiated()) {
                    http2Stream.receiveRstStream(ErrorCode.REFUSED_STREAM);
                    Http2Connection.this.removeStream(http2Stream.getId());
                }
            }
        }
        
        @Override
        public void windowUpdate(final int streamId, final long windowSizeIncrement) {
            if (streamId == 0) {
                synchronized (Http2Connection.this) {
                    final Http2Connection this$0 = Http2Connection.this;
                    this$0.bytesLeftInWriteWindow += windowSizeIncrement;
                    Http2Connection.this.notifyAll();
                }
            }
            else {
                final Http2Stream stream = Http2Connection.this.getStream(streamId);
                if (stream != null) {
                    synchronized (stream) {
                        stream.addBytesToWriteWindow(windowSizeIncrement);
                    }
                }
            }
        }
        
        @Override
        public void priority(final int streamId, final int streamDependency, final int weight, final boolean exclusive) {
        }
        
        @Override
        public void pushPromise(final int streamId, final int promisedStreamId, final List<Header> requestHeaders) {
            Http2Connection.this.pushRequestLater(promisedStreamId, requestHeaders);
        }
        
        @Override
        public void alternateService(final int streamId, final String origin, final ByteString protocol, final String host, final int port, final long maxAge) {
        }
    }
    
    public abstract static class Listener
    {
        public static final Listener REFUSE_INCOMING_STREAMS;
        
        public abstract void onStream(final Http2Stream p0) throws IOException;
        
        public void onSettings(final Http2Connection connection) {
        }
        
        static {
            REFUSE_INCOMING_STREAMS = new Listener() {
                @Override
                public void onStream(final Http2Stream stream) throws IOException {
                    stream.close(ErrorCode.REFUSED_STREAM, null);
                }
            };
        }
    }
}
