//Raddon On Top!

package okhttp3.internal.http1;

import okhttp3.internal.connection.*;
import java.net.*;
import okhttp3.internal.http.*;
import java.io.*;
import okhttp3.internal.*;
import okhttp3.*;
import java.util.concurrent.*;
import okio.*;

public final class Http1ExchangeCodec implements ExchangeCodec
{
    private static final int STATE_IDLE = 0;
    private static final int STATE_OPEN_REQUEST_BODY = 1;
    private static final int STATE_WRITING_REQUEST_BODY = 2;
    private static final int STATE_READ_RESPONSE_HEADERS = 3;
    private static final int STATE_OPEN_RESPONSE_BODY = 4;
    private static final int STATE_READING_RESPONSE_BODY = 5;
    private static final int STATE_CLOSED = 6;
    private static final int HEADER_LIMIT = 262144;
    private final OkHttpClient client;
    private final RealConnection realConnection;
    private final BufferedSource source;
    private final BufferedSink sink;
    private int state;
    private long headerLimit;
    private Headers trailers;
    
    public Http1ExchangeCodec(final OkHttpClient client, final RealConnection realConnection, final BufferedSource source, final BufferedSink sink) {
        this.state = 0;
        this.headerLimit = 262144L;
        this.client = client;
        this.realConnection = realConnection;
        this.source = source;
        this.sink = sink;
    }
    
    public RealConnection connection() {
        return this.realConnection;
    }
    
    public Sink createRequestBody(final Request request, final long contentLength) throws IOException {
        if (request.body() != null && request.body().isDuplex()) {
            throw new ProtocolException("Duplex connections are not supported for HTTP/1");
        }
        if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding"))) {
            return this.newChunkedSink();
        }
        if (contentLength != -1L) {
            return this.newKnownLengthSink();
        }
        throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
    }
    
    public void cancel() {
        if (this.realConnection != null) {
            this.realConnection.cancel();
        }
    }
    
    public void writeRequestHeaders(final Request request) throws IOException {
        final String requestLine = RequestLine.get(request, this.realConnection.route().proxy().type());
        this.writeRequest(request.headers(), requestLine);
    }
    
    public long reportedContentLength(final Response response) {
        if (!HttpHeaders.hasBody(response)) {
            return 0L;
        }
        if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            return -1L;
        }
        return HttpHeaders.contentLength(response);
    }
    
    public Source openResponseBodySource(final Response response) {
        if (!HttpHeaders.hasBody(response)) {
            return this.newFixedLengthSource(0L);
        }
        if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            return this.newChunkedSource(response.request().url());
        }
        final long contentLength = HttpHeaders.contentLength(response);
        if (contentLength != -1L) {
            return this.newFixedLengthSource(contentLength);
        }
        return this.newUnknownLengthSource();
    }
    
    public Headers trailers() {
        if (this.state != 6) {
            throw new IllegalStateException("too early; can't read the trailers yet");
        }
        return (this.trailers != null) ? this.trailers : Util.EMPTY_HEADERS;
    }
    
    public boolean isClosed() {
        return this.state == 6;
    }
    
    public void flushRequest() throws IOException {
        this.sink.flush();
    }
    
    public void finishRequest() throws IOException {
        this.sink.flush();
    }
    
    public void writeRequest(final Headers headers, final String requestLine) throws IOException {
        if (this.state != 0) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.sink.writeUtf8(requestLine).writeUtf8("\r\n");
        for (int i = 0, size = headers.size(); i < size; ++i) {
            this.sink.writeUtf8(headers.name(i)).writeUtf8(": ").writeUtf8(headers.value(i)).writeUtf8("\r\n");
        }
        this.sink.writeUtf8("\r\n");
        this.state = 1;
    }
    
    public Response.Builder readResponseHeaders(final boolean expectContinue) throws IOException {
        if (this.state != 1 && this.state != 3) {
            throw new IllegalStateException("state: " + this.state);
        }
        try {
            final StatusLine statusLine = StatusLine.parse(this.readHeaderLine());
            final Response.Builder responseBuilder = new Response.Builder().protocol(statusLine.protocol).code(statusLine.code).message(statusLine.message).headers(this.readHeaders());
            if (expectContinue && statusLine.code == 100) {
                return null;
            }
            if (statusLine.code == 100) {
                this.state = 3;
                return responseBuilder;
            }
            this.state = 4;
            return responseBuilder;
        }
        catch (EOFException e) {
            String address = "unknown";
            if (this.realConnection != null) {
                address = this.realConnection.route().address().url().redact();
            }
            throw new IOException("unexpected end of stream on " + address, e);
        }
    }
    
    private String readHeaderLine() throws IOException {
        final String line = this.source.readUtf8LineStrict(this.headerLimit);
        this.headerLimit -= line.length();
        return line;
    }
    
    private Headers readHeaders() throws IOException {
        final Headers.Builder headers = new Headers.Builder();
        String line;
        while ((line = this.readHeaderLine()).length() != 0) {
            Internal.instance.addLenient(headers, line);
        }
        return headers.build();
    }
    
    private Sink newChunkedSink() {
        if (this.state != 1) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 2;
        return (Sink)new ChunkedSink();
    }
    
    private Sink newKnownLengthSink() {
        if (this.state != 1) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 2;
        return (Sink)new KnownLengthSink();
    }
    
    private Source newFixedLengthSource(final long length) {
        if (this.state != 4) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 5;
        return (Source)new FixedLengthSource(length);
    }
    
    private Source newChunkedSource(final HttpUrl url) {
        if (this.state != 4) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 5;
        return (Source)new ChunkedSource(url);
    }
    
    private Source newUnknownLengthSource() {
        if (this.state != 4) {
            throw new IllegalStateException("state: " + this.state);
        }
        this.state = 5;
        this.realConnection.noNewExchanges();
        return (Source)new UnknownLengthSource();
    }
    
    private void detachTimeout(final ForwardingTimeout timeout) {
        final Timeout oldDelegate = timeout.delegate();
        timeout.setDelegate(Timeout.NONE);
        oldDelegate.clearDeadline();
        oldDelegate.clearTimeout();
    }
    
    public void skipConnectBody(final Response response) throws IOException {
        final long contentLength = HttpHeaders.contentLength(response);
        if (contentLength == -1L) {
            return;
        }
        final Source body = this.newFixedLengthSource(contentLength);
        Util.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
        body.close();
    }
    
    private final class KnownLengthSink implements Sink
    {
        private final ForwardingTimeout timeout;
        private boolean closed;
        
        private KnownLengthSink() {
            this.timeout = new ForwardingTimeout(Http1ExchangeCodec.this.sink.timeout());
        }
        
        public Timeout timeout() {
            return (Timeout)this.timeout;
        }
        
        public void write(final Buffer source, final long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            Util.checkOffsetAndCount(source.size(), 0L, byteCount);
            Http1ExchangeCodec.this.sink.write(source, byteCount);
        }
        
        public void flush() throws IOException {
            if (this.closed) {
                return;
            }
            Http1ExchangeCodec.this.sink.flush();
        }
        
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            Http1ExchangeCodec.this.detachTimeout(this.timeout);
            Http1ExchangeCodec.this.state = 3;
        }
    }
    
    private final class ChunkedSink implements Sink
    {
        private final ForwardingTimeout timeout;
        private boolean closed;
        
        ChunkedSink() {
            this.timeout = new ForwardingTimeout(Http1ExchangeCodec.this.sink.timeout());
        }
        
        public Timeout timeout() {
            return (Timeout)this.timeout;
        }
        
        public void write(final Buffer source, final long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            if (byteCount == 0L) {
                return;
            }
            Http1ExchangeCodec.this.sink.writeHexadecimalUnsignedLong(byteCount);
            Http1ExchangeCodec.this.sink.writeUtf8("\r\n");
            Http1ExchangeCodec.this.sink.write(source, byteCount);
            Http1ExchangeCodec.this.sink.writeUtf8("\r\n");
        }
        
        public synchronized void flush() throws IOException {
            if (this.closed) {
                return;
            }
            Http1ExchangeCodec.this.sink.flush();
        }
        
        public synchronized void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            Http1ExchangeCodec.this.sink.writeUtf8("0\r\n\r\n");
            Http1ExchangeCodec.this.detachTimeout(this.timeout);
            Http1ExchangeCodec.this.state = 3;
        }
    }
    
    private abstract class AbstractSource implements Source
    {
        protected final ForwardingTimeout timeout;
        protected boolean closed;
        
        private AbstractSource() {
            this.timeout = new ForwardingTimeout(Http1ExchangeCodec.this.source.timeout());
        }
        
        public Timeout timeout() {
            return (Timeout)this.timeout;
        }
        
        public long read(final Buffer sink, final long byteCount) throws IOException {
            try {
                return Http1ExchangeCodec.this.source.read(sink, byteCount);
            }
            catch (IOException e) {
                Http1ExchangeCodec.this.realConnection.noNewExchanges();
                this.responseBodyComplete();
                throw e;
            }
        }
        
        final void responseBodyComplete() {
            if (Http1ExchangeCodec.this.state == 6) {
                return;
            }
            if (Http1ExchangeCodec.this.state != 5) {
                throw new IllegalStateException("state: " + Http1ExchangeCodec.this.state);
            }
            Http1ExchangeCodec.this.detachTimeout(this.timeout);
            Http1ExchangeCodec.this.state = 6;
        }
    }
    
    private class FixedLengthSource extends AbstractSource
    {
        private long bytesRemaining;
        
        FixedLengthSource(final long length) {
            this.bytesRemaining = length;
            if (this.bytesRemaining == 0L) {
                this.responseBodyComplete();
            }
        }
        
        @Override
        public long read(final Buffer sink, final long byteCount) throws IOException {
            if (byteCount < 0L) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            }
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            if (this.bytesRemaining == 0L) {
                return -1L;
            }
            final long read = super.read(sink, Math.min(this.bytesRemaining, byteCount));
            if (read == -1L) {
                Http1ExchangeCodec.this.realConnection.noNewExchanges();
                final ProtocolException e = new ProtocolException("unexpected end of stream");
                this.responseBodyComplete();
                throw e;
            }
            this.bytesRemaining -= read;
            if (this.bytesRemaining == 0L) {
                this.responseBodyComplete();
            }
            return read;
        }
        
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            if (this.bytesRemaining != 0L && !Util.discard((Source)this, 100, TimeUnit.MILLISECONDS)) {
                Http1ExchangeCodec.this.realConnection.noNewExchanges();
                this.responseBodyComplete();
            }
            this.closed = true;
        }
    }
    
    private class ChunkedSource extends AbstractSource
    {
        private static final long NO_CHUNK_YET = -1L;
        private final HttpUrl url;
        private long bytesRemainingInChunk;
        private boolean hasMoreChunks;
        
        ChunkedSource(final HttpUrl url) {
            this.bytesRemainingInChunk = -1L;
            this.hasMoreChunks = true;
            this.url = url;
        }
        
        @Override
        public long read(final Buffer sink, final long byteCount) throws IOException {
            if (byteCount < 0L) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            }
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            if (!this.hasMoreChunks) {
                return -1L;
            }
            if (this.bytesRemainingInChunk == 0L || this.bytesRemainingInChunk == -1L) {
                this.readChunkSize();
                if (!this.hasMoreChunks) {
                    return -1L;
                }
            }
            final long read = super.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
            if (read == -1L) {
                Http1ExchangeCodec.this.realConnection.noNewExchanges();
                final ProtocolException e = new ProtocolException("unexpected end of stream");
                this.responseBodyComplete();
                throw e;
            }
            this.bytesRemainingInChunk -= read;
            return read;
        }
        
        private void readChunkSize() throws IOException {
            if (this.bytesRemainingInChunk != -1L) {
                Http1ExchangeCodec.this.source.readUtf8LineStrict();
            }
            try {
                this.bytesRemainingInChunk = Http1ExchangeCodec.this.source.readHexadecimalUnsignedLong();
                final String extensions = Http1ExchangeCodec.this.source.readUtf8LineStrict().trim();
                if (this.bytesRemainingInChunk < 0L || (!extensions.isEmpty() && !extensions.startsWith(";"))) {
                    throw new ProtocolException("expected chunk size and optional extensions but was \"" + this.bytesRemainingInChunk + extensions + "\"");
                }
            }
            catch (NumberFormatException e) {
                throw new ProtocolException(e.getMessage());
            }
            if (this.bytesRemainingInChunk == 0L) {
                this.hasMoreChunks = false;
                Http1ExchangeCodec.this.trailers = Http1ExchangeCodec.this.readHeaders();
                HttpHeaders.receiveHeaders(Http1ExchangeCodec.this.client.cookieJar(), this.url, Http1ExchangeCodec.this.trailers);
                this.responseBodyComplete();
            }
        }
        
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            if (this.hasMoreChunks && !Util.discard((Source)this, 100, TimeUnit.MILLISECONDS)) {
                Http1ExchangeCodec.this.realConnection.noNewExchanges();
                this.responseBodyComplete();
            }
            this.closed = true;
        }
    }
    
    private class UnknownLengthSource extends AbstractSource
    {
        private boolean inputExhausted;
        
        @Override
        public long read(final Buffer sink, final long byteCount) throws IOException {
            if (byteCount < 0L) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            }
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            if (this.inputExhausted) {
                return -1L;
            }
            final long read = super.read(sink, byteCount);
            if (read == -1L) {
                this.inputExhausted = true;
                this.responseBodyComplete();
                return -1L;
            }
            return read;
        }
        
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            if (!this.inputExhausted) {
                this.responseBodyComplete();
            }
            this.closed = true;
        }
    }
}
