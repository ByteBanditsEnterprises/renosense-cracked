//Raddon On Top!

package okhttp3.internal.http2;

import okhttp3.*;
import javax.annotation.*;
import okhttp3.internal.*;
import java.util.*;
import java.io.*;
import okio.*;
import java.net.*;

public final class Http2Stream
{
    long unacknowledgedBytesRead;
    long bytesLeftInWriteWindow;
    final int id;
    final Http2Connection connection;
    private final Deque<Headers> headersQueue;
    private boolean hasResponseHeaders;
    private final FramingSource source;
    final FramingSink sink;
    final StreamTimeout readTimeout;
    final StreamTimeout writeTimeout;
    @Nullable
    ErrorCode errorCode;
    @Nullable
    IOException errorException;
    
    Http2Stream(final int id, final Http2Connection connection, final boolean outFinished, final boolean inFinished, @Nullable final Headers headers) {
        this.unacknowledgedBytesRead = 0L;
        this.headersQueue = new ArrayDeque<Headers>();
        this.readTimeout = new StreamTimeout();
        this.writeTimeout = new StreamTimeout();
        if (connection == null) {
            throw new NullPointerException("connection == null");
        }
        this.id = id;
        this.connection = connection;
        this.bytesLeftInWriteWindow = connection.peerSettings.getInitialWindowSize();
        this.source = new FramingSource(connection.okHttpSettings.getInitialWindowSize());
        this.sink = new FramingSink();
        this.source.finished = inFinished;
        this.sink.finished = outFinished;
        if (headers != null) {
            this.headersQueue.add(headers);
        }
        if (this.isLocallyInitiated() && headers != null) {
            throw new IllegalStateException("locally-initiated streams shouldn't have headers yet");
        }
        if (!this.isLocallyInitiated() && headers == null) {
            throw new IllegalStateException("remotely-initiated streams should have headers");
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    public synchronized boolean isOpen() {
        return this.errorCode == null && ((!this.source.finished && !this.source.closed) || (!this.sink.finished && !this.sink.closed) || !this.hasResponseHeaders);
    }
    
    public boolean isLocallyInitiated() {
        final boolean streamIsClient = (this.id & 0x1) == 0x1;
        return this.connection.client == streamIsClient;
    }
    
    public Http2Connection getConnection() {
        return this.connection;
    }
    
    public synchronized Headers takeHeaders() throws IOException {
        this.readTimeout.enter();
        try {
            while (this.headersQueue.isEmpty() && this.errorCode == null) {
                this.waitForIo();
            }
        }
        finally {
            this.readTimeout.exitAndThrowIfTimedOut();
        }
        if (!this.headersQueue.isEmpty()) {
            return this.headersQueue.removeFirst();
        }
        throw (this.errorException != null) ? this.errorException : new StreamResetException(this.errorCode);
    }
    
    public synchronized Headers trailers() throws IOException {
        if (this.errorCode != null) {
            throw (this.errorException != null) ? this.errorException : new StreamResetException(this.errorCode);
        }
        if (!this.source.finished || !this.source.receiveBuffer.exhausted() || !this.source.readBuffer.exhausted()) {
            throw new IllegalStateException("too early; can't read the trailers yet");
        }
        return (this.source.trailers != null) ? this.source.trailers : Util.EMPTY_HEADERS;
    }
    
    public synchronized ErrorCode getErrorCode() {
        return this.errorCode;
    }
    
    public void writeHeaders(final List<Header> responseHeaders, final boolean outFinished, boolean flushHeaders) throws IOException {
        assert !Thread.holdsLock(this);
        if (responseHeaders == null) {
            throw new NullPointerException("headers == null");
        }
        synchronized (this) {
            this.hasResponseHeaders = true;
            if (outFinished) {
                this.sink.finished = true;
            }
        }
        if (!flushHeaders) {
            synchronized (this.connection) {
                flushHeaders = (this.connection.bytesLeftInWriteWindow == 0L);
            }
        }
        this.connection.writeHeaders(this.id, outFinished, (List)responseHeaders);
        if (flushHeaders) {
            this.connection.flush();
        }
    }
    
    public void enqueueTrailers(final Headers trailers) {
        synchronized (this) {
            if (this.sink.finished) {
                throw new IllegalStateException("already finished");
            }
            if (trailers.size() == 0) {
                throw new IllegalArgumentException("trailers.size() == 0");
            }
            this.sink.trailers = trailers;
        }
    }
    
    public Timeout readTimeout() {
        return (Timeout)this.readTimeout;
    }
    
    public Timeout writeTimeout() {
        return (Timeout)this.writeTimeout;
    }
    
    public Source getSource() {
        return (Source)this.source;
    }
    
    public Sink getSink() {
        synchronized (this) {
            if (!this.hasResponseHeaders && !this.isLocallyInitiated()) {
                throw new IllegalStateException("reply before requesting the sink");
            }
        }
        return (Sink)this.sink;
    }
    
    public void close(final ErrorCode rstStatusCode, @Nullable final IOException errorException) throws IOException {
        if (!this.closeInternal(rstStatusCode, errorException)) {
            return;
        }
        this.connection.writeSynReset(this.id, rstStatusCode);
    }
    
    public void closeLater(final ErrorCode errorCode) {
        if (!this.closeInternal(errorCode, null)) {
            return;
        }
        this.connection.writeSynResetLater(this.id, errorCode);
    }
    
    private boolean closeInternal(final ErrorCode errorCode, @Nullable final IOException errorException) {
        assert !Thread.holdsLock(this);
        synchronized (this) {
            if (this.errorCode != null) {
                return false;
            }
            if (this.source.finished && this.sink.finished) {
                return false;
            }
            this.errorCode = errorCode;
            this.errorException = errorException;
            this.notifyAll();
        }
        this.connection.removeStream(this.id);
        return true;
    }
    
    void receiveData(final BufferedSource in, final int length) throws IOException {
        assert !Thread.holdsLock(this);
        this.source.receive(in, length);
    }
    
    void receiveHeaders(final Headers headers, final boolean inFinished) {
        assert !Thread.holdsLock(this);
        final boolean open;
        synchronized (this) {
            if (!this.hasResponseHeaders || !inFinished) {
                this.hasResponseHeaders = true;
                this.headersQueue.add(headers);
            }
            else {
                this.source.trailers = headers;
            }
            if (inFinished) {
                this.source.finished = true;
            }
            open = this.isOpen();
            this.notifyAll();
        }
        if (!open) {
            this.connection.removeStream(this.id);
        }
    }
    
    synchronized void receiveRstStream(final ErrorCode errorCode) {
        if (this.errorCode == null) {
            this.errorCode = errorCode;
            this.notifyAll();
        }
    }
    
    void cancelStreamIfNecessary() throws IOException {
        assert !Thread.holdsLock(this);
        final boolean cancel;
        final boolean open;
        synchronized (this) {
            cancel = (!this.source.finished && this.source.closed && (this.sink.finished || this.sink.closed));
            open = this.isOpen();
        }
        if (cancel) {
            this.close(ErrorCode.CANCEL, null);
        }
        else if (!open) {
            this.connection.removeStream(this.id);
        }
    }
    
    void addBytesToWriteWindow(final long delta) {
        this.bytesLeftInWriteWindow += delta;
        if (delta > 0L) {
            this.notifyAll();
        }
    }
    
    void checkOutNotClosed() throws IOException {
        if (this.sink.closed) {
            throw new IOException("stream closed");
        }
        if (this.sink.finished) {
            throw new IOException("stream finished");
        }
        if (this.errorCode != null) {
            throw (this.errorException != null) ? this.errorException : new StreamResetException(this.errorCode);
        }
    }
    
    void waitForIo() throws InterruptedIOException {
        try {
            this.wait();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        }
    }
    
    private final class FramingSource implements Source
    {
        private final Buffer receiveBuffer;
        private final Buffer readBuffer;
        private final long maxByteCount;
        private Headers trailers;
        boolean closed;
        boolean finished;
        
        FramingSource(final long maxByteCount) {
            this.receiveBuffer = new Buffer();
            this.readBuffer = new Buffer();
            this.maxByteCount = maxByteCount;
        }
        
        public long read(final Buffer sink, final long byteCount) throws IOException {
            if (byteCount < 0L) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            }
            long readBytesDelivered;
            IOException errorExceptionToDeliver;
            while (true) {
                readBytesDelivered = -1L;
                errorExceptionToDeliver = null;
                synchronized (Http2Stream.this) {
                    Http2Stream.this.readTimeout.enter();
                    try {
                        if (Http2Stream.this.errorCode != null) {
                            errorExceptionToDeliver = ((Http2Stream.this.errorException != null) ? Http2Stream.this.errorException : new StreamResetException(Http2Stream.this.errorCode));
                        }
                        if (this.closed) {
                            throw new IOException("stream closed");
                        }
                        if (this.readBuffer.size() > 0L) {
                            readBytesDelivered = this.readBuffer.read(sink, Math.min(byteCount, this.readBuffer.size()));
                            final Http2Stream this$0 = Http2Stream.this;
                            this$0.unacknowledgedBytesRead += readBytesDelivered;
                            if (errorExceptionToDeliver == null && Http2Stream.this.unacknowledgedBytesRead >= Http2Stream.this.connection.okHttpSettings.getInitialWindowSize() / 2) {
                                Http2Stream.this.connection.writeWindowUpdateLater(Http2Stream.this.id, Http2Stream.this.unacknowledgedBytesRead);
                                Http2Stream.this.unacknowledgedBytesRead = 0L;
                            }
                        }
                        else if (!this.finished && errorExceptionToDeliver == null) {
                            Http2Stream.this.waitForIo();
                            continue;
                        }
                    }
                    finally {
                        Http2Stream.this.readTimeout.exitAndThrowIfTimedOut();
                    }
                }
                break;
            }
            if (readBytesDelivered != -1L) {
                this.updateConnectionFlowControl(readBytesDelivered);
                return readBytesDelivered;
            }
            if (errorExceptionToDeliver != null) {
                throw errorExceptionToDeliver;
            }
            return -1L;
        }
        
        private void updateConnectionFlowControl(final long read) {
            assert !Thread.holdsLock(Http2Stream.this);
            Http2Stream.this.connection.updateConnectionFlowControl(read);
        }
        
        void receive(final BufferedSource in, long byteCount) throws IOException {
            assert !Thread.holdsLock(Http2Stream.this);
            while (byteCount > 0L) {
                final boolean finished;
                final boolean flowControlError;
                synchronized (Http2Stream.this) {
                    finished = this.finished;
                    flowControlError = (byteCount + this.readBuffer.size() > this.maxByteCount);
                }
                if (flowControlError) {
                    in.skip(byteCount);
                    Http2Stream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                    return;
                }
                if (finished) {
                    in.skip(byteCount);
                    return;
                }
                final long read = in.read(this.receiveBuffer, byteCount);
                if (read == -1L) {
                    throw new EOFException();
                }
                byteCount -= read;
                long bytesDiscarded = 0L;
                synchronized (Http2Stream.this) {
                    if (this.closed) {
                        bytesDiscarded = this.receiveBuffer.size();
                        this.receiveBuffer.clear();
                    }
                    else {
                        final boolean wasEmpty = this.readBuffer.size() == 0L;
                        this.readBuffer.writeAll((Source)this.receiveBuffer);
                        if (wasEmpty) {
                            Http2Stream.this.notifyAll();
                        }
                    }
                }
                if (bytesDiscarded <= 0L) {
                    continue;
                }
                this.updateConnectionFlowControl(bytesDiscarded);
            }
        }
        
        public Timeout timeout() {
            return (Timeout)Http2Stream.this.readTimeout;
        }
        
        public void close() throws IOException {
            final long bytesDiscarded;
            synchronized (Http2Stream.this) {
                this.closed = true;
                bytesDiscarded = this.readBuffer.size();
                this.readBuffer.clear();
                Http2Stream.this.notifyAll();
            }
            if (bytesDiscarded > 0L) {
                this.updateConnectionFlowControl(bytesDiscarded);
            }
            Http2Stream.this.cancelStreamIfNecessary();
        }
    }
    
    final class FramingSink implements Sink
    {
        private static final long EMIT_BUFFER_SIZE = 16384L;
        private final Buffer sendBuffer;
        private Headers trailers;
        boolean closed;
        boolean finished;
        
        FramingSink() {
            this.sendBuffer = new Buffer();
        }
        
        public void write(final Buffer source, final long byteCount) throws IOException {
            assert !Thread.holdsLock(Http2Stream.this);
            this.sendBuffer.write(source, byteCount);
            while (this.sendBuffer.size() >= 16384L) {
                this.emitFrame(false);
            }
        }
        
        private void emitFrame(final boolean outFinishedOnLastFrame) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //     4: dup            
            //     5: astore          4
            //     7: monitorenter   
            //     8: aload_0         /* this */
            //     9: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    12: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //    15: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.enter:()V
            //    18: aload_0         /* this */
            //    19: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    22: getfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //    25: lconst_0       
            //    26: lcmp           
            //    27: ifgt            64
            //    30: aload_0         /* this */
            //    31: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.finished:Z
            //    34: ifne            64
            //    37: aload_0         /* this */
            //    38: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.closed:Z
            //    41: ifne            64
            //    44: aload_0         /* this */
            //    45: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    48: getfield        okhttp3/internal/http2/Http2Stream.errorCode:Lokhttp3/internal/http2/ErrorCode;
            //    51: ifnonnull       64
            //    54: aload_0         /* this */
            //    55: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    58: invokevirtual   okhttp3/internal/http2/Http2Stream.waitForIo:()V
            //    61: goto            18
            //    64: aload_0         /* this */
            //    65: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    68: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //    71: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //    74: goto            92
            //    77: astore          5
            //    79: aload_0         /* this */
            //    80: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    83: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //    86: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //    89: aload           5
            //    91: athrow         
            //    92: aload_0         /* this */
            //    93: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    96: invokevirtual   okhttp3/internal/http2/Http2Stream.checkOutNotClosed:()V
            //    99: aload_0         /* this */
            //   100: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   103: getfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //   106: aload_0         /* this */
            //   107: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer:Lokio/Buffer;
            //   110: invokevirtual   okio/Buffer.size:()J
            //   113: invokestatic    java/lang/Math.min:(JJ)J
            //   116: lstore_2        /* toWrite */
            //   117: aload_0         /* this */
            //   118: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   121: dup            
            //   122: getfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //   125: lload_2         /* toWrite */
            //   126: lsub           
            //   127: putfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //   130: aload           4
            //   132: monitorexit    
            //   133: goto            144
            //   136: astore          6
            //   138: aload           4
            //   140: monitorexit    
            //   141: aload           6
            //   143: athrow         
            //   144: aload_0         /* this */
            //   145: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   148: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //   151: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.enter:()V
            //   154: iload_1         /* outFinishedOnLastFrame */
            //   155: ifeq            174
            //   158: lload_2         /* toWrite */
            //   159: aload_0         /* this */
            //   160: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer:Lokio/Buffer;
            //   163: invokevirtual   okio/Buffer.size:()J
            //   166: lcmp           
            //   167: ifne            174
            //   170: iconst_1       
            //   171: goto            175
            //   174: iconst_0       
            //   175: istore          outFinished
            //   177: aload_0         /* this */
            //   178: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   181: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   184: aload_0         /* this */
            //   185: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   188: getfield        okhttp3/internal/http2/Http2Stream.id:I
            //   191: iload           outFinished
            //   193: aload_0         /* this */
            //   194: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer:Lokio/Buffer;
            //   197: lload_2         /* toWrite */
            //   198: invokevirtual   okhttp3/internal/http2/Http2Connection.writeData:(IZLokio/Buffer;J)V
            //   201: aload_0         /* this */
            //   202: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   205: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //   208: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //   211: goto            229
            //   214: astore          7
            //   216: aload_0         /* this */
            //   217: getfield        okhttp3/internal/http2/Http2Stream$FramingSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   220: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //   223: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //   226: aload           7
            //   228: athrow         
            //   229: return         
            //    Exceptions:
            //  throws java.io.IOException
            //    StackMapTable: 00 0A FE 00 12 00 00 07 00 25 2D 4C 07 00 49 0E 6B 07 00 49 FF 00 07 00 03 07 00 0D 01 04 00 00 1D 40 01 66 07 00 49 0E
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  18     64     77     92     Any
            //  77     79     77     92     Any
            //  8      133    136    144    Any
            //  136    141    136    144    Any
            //  154    201    214    229    Any
            //  214    216    214    229    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.ArrayIndexOutOfBoundsException
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        public void flush() throws IOException {
            assert !Thread.holdsLock(Http2Stream.this);
            synchronized (Http2Stream.this) {
                Http2Stream.this.checkOutNotClosed();
            }
            while (this.sendBuffer.size() > 0L) {
                this.emitFrame(false);
                Http2Stream.this.connection.flush();
            }
        }
        
        public Timeout timeout() {
            return (Timeout)Http2Stream.this.writeTimeout;
        }
        
        public void close() throws IOException {
            assert !Thread.holdsLock(Http2Stream.this);
            synchronized (Http2Stream.this) {
                if (this.closed) {
                    return;
                }
            }
            if (!Http2Stream.this.sink.finished) {
                final boolean hasData = this.sendBuffer.size() > 0L;
                final boolean hasTrailers = this.trailers != null;
                if (hasTrailers) {
                    while (this.sendBuffer.size() > 0L) {
                        this.emitFrame(false);
                    }
                    Http2Stream.this.connection.writeHeaders(Http2Stream.this.id, true, (List)Util.toHeaderBlock(this.trailers));
                }
                else if (hasData) {
                    while (this.sendBuffer.size() > 0L) {
                        this.emitFrame(true);
                    }
                }
                else {
                    Http2Stream.this.connection.writeData(Http2Stream.this.id, true, (Buffer)null, 0L);
                }
            }
            synchronized (Http2Stream.this) {
                this.closed = true;
            }
            Http2Stream.this.connection.flush();
            Http2Stream.this.cancelStreamIfNecessary();
        }
    }
    
    class StreamTimeout extends AsyncTimeout
    {
        protected void timedOut() {
            Http2Stream.this.closeLater(ErrorCode.CANCEL);
            Http2Stream.this.connection.sendDegradedPingLater();
        }
        
        protected IOException newTimeoutException(final IOException cause) {
            final SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
            if (cause != null) {
                socketTimeoutException.initCause(cause);
            }
            return socketTimeoutException;
        }
        
        public void exitAndThrowIfTimedOut() throws IOException {
            if (this.exit()) {
                throw this.newTimeoutException(null);
            }
        }
    }
}
