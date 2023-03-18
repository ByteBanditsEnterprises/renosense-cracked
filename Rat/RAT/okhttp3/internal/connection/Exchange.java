//Raddon On Top!

package okhttp3.internal.connection;

import java.io.*;
import okhttp3.internal.*;
import javax.annotation.*;
import okhttp3.internal.http.*;
import okhttp3.*;
import okhttp3.internal.ws.*;
import java.net.*;
import okio.*;

public final class Exchange
{
    final Transmitter transmitter;
    final Call call;
    final EventListener eventListener;
    final ExchangeFinder finder;
    final ExchangeCodec codec;
    private boolean duplex;
    
    public Exchange(final Transmitter transmitter, final Call call, final EventListener eventListener, final ExchangeFinder finder, final ExchangeCodec codec) {
        this.transmitter = transmitter;
        this.call = call;
        this.eventListener = eventListener;
        this.finder = finder;
        this.codec = codec;
    }
    
    public RealConnection connection() {
        return this.codec.connection();
    }
    
    public boolean isDuplex() {
        return this.duplex;
    }
    
    public void writeRequestHeaders(final Request request) throws IOException {
        try {
            this.eventListener.requestHeadersStart(this.call);
            this.codec.writeRequestHeaders(request);
            this.eventListener.requestHeadersEnd(this.call, request);
        }
        catch (IOException e) {
            this.eventListener.requestFailed(this.call, e);
            this.trackFailure(e);
            throw e;
        }
    }
    
    public Sink createRequestBody(final Request request, final boolean duplex) throws IOException {
        this.duplex = duplex;
        final long contentLength = request.body().contentLength();
        this.eventListener.requestBodyStart(this.call);
        final Sink rawRequestBody = this.codec.createRequestBody(request, contentLength);
        return (Sink)new RequestBodySink(rawRequestBody, contentLength);
    }
    
    public void flushRequest() throws IOException {
        try {
            this.codec.flushRequest();
        }
        catch (IOException e) {
            this.eventListener.requestFailed(this.call, e);
            this.trackFailure(e);
            throw e;
        }
    }
    
    public void finishRequest() throws IOException {
        try {
            this.codec.finishRequest();
        }
        catch (IOException e) {
            this.eventListener.requestFailed(this.call, e);
            this.trackFailure(e);
            throw e;
        }
    }
    
    public void responseHeadersStart() {
        this.eventListener.responseHeadersStart(this.call);
    }
    
    @Nullable
    public Response.Builder readResponseHeaders(final boolean expectContinue) throws IOException {
        try {
            final Response.Builder result = this.codec.readResponseHeaders(expectContinue);
            if (result != null) {
                Internal.instance.initExchange(result, this);
            }
            return result;
        }
        catch (IOException e) {
            this.eventListener.responseFailed(this.call, e);
            this.trackFailure(e);
            throw e;
        }
    }
    
    public void responseHeadersEnd(final Response response) {
        this.eventListener.responseHeadersEnd(this.call, response);
    }
    
    public ResponseBody openResponseBody(final Response response) throws IOException {
        try {
            this.eventListener.responseBodyStart(this.call);
            final String contentType = response.header("Content-Type");
            final long contentLength = this.codec.reportedContentLength(response);
            final Source rawSource = this.codec.openResponseBodySource(response);
            final ResponseBodySource source = new ResponseBodySource(rawSource, contentLength);
            return new RealResponseBody(contentType, contentLength, Okio.buffer((Source)source));
        }
        catch (IOException e) {
            this.eventListener.responseFailed(this.call, e);
            this.trackFailure(e);
            throw e;
        }
    }
    
    public Headers trailers() throws IOException {
        return this.codec.trailers();
    }
    
    public void timeoutEarlyExit() {
        this.transmitter.timeoutEarlyExit();
    }
    
    public RealWebSocket.Streams newWebSocketStreams() throws SocketException {
        this.transmitter.timeoutEarlyExit();
        return this.codec.connection().newWebSocketStreams(this);
    }
    
    public void webSocketUpgradeFailed() {
        this.bodyComplete(-1L, true, true, null);
    }
    
    public void noNewExchangesOnConnection() {
        this.codec.connection().noNewExchanges();
    }
    
    public void cancel() {
        this.codec.cancel();
    }
    
    public void detachWithViolence() {
        this.codec.cancel();
        this.transmitter.exchangeMessageDone(this, true, true, null);
    }
    
    void trackFailure(final IOException e) {
        this.finder.trackFailure();
        this.codec.connection().trackFailure(e);
    }
    
    @Nullable
    IOException bodyComplete(final long bytesRead, final boolean responseDone, final boolean requestDone, @Nullable final IOException e) {
        if (e != null) {
            this.trackFailure(e);
        }
        if (requestDone) {
            if (e != null) {
                this.eventListener.requestFailed(this.call, e);
            }
            else {
                this.eventListener.requestBodyEnd(this.call, bytesRead);
            }
        }
        if (responseDone) {
            if (e != null) {
                this.eventListener.responseFailed(this.call, e);
            }
            else {
                this.eventListener.responseBodyEnd(this.call, bytesRead);
            }
        }
        return this.transmitter.exchangeMessageDone(this, requestDone, responseDone, e);
    }
    
    public void noRequestBody() {
        this.transmitter.exchangeMessageDone(this, true, false, null);
    }
    
    private final class RequestBodySink extends ForwardingSink
    {
        private boolean completed;
        private long contentLength;
        private long bytesReceived;
        private boolean closed;
        
        RequestBodySink(final Sink delegate, final long contentLength) {
            super(delegate);
            this.contentLength = contentLength;
        }
        
        public void write(final Buffer source, final long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            if (this.contentLength != -1L && this.bytesReceived + byteCount > this.contentLength) {
                throw new ProtocolException("expected " + this.contentLength + " bytes but received " + (this.bytesReceived + byteCount));
            }
            try {
                super.write(source, byteCount);
                this.bytesReceived += byteCount;
            }
            catch (IOException e) {
                throw this.complete(e);
            }
        }
        
        public void flush() throws IOException {
            try {
                super.flush();
            }
            catch (IOException e) {
                throw this.complete(e);
            }
        }
        
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (this.contentLength != -1L && this.bytesReceived != this.contentLength) {
                throw new ProtocolException("unexpected end of stream");
            }
            try {
                super.close();
                this.complete(null);
            }
            catch (IOException e) {
                throw this.complete(e);
            }
        }
        
        @Nullable
        private IOException complete(@Nullable final IOException e) {
            if (this.completed) {
                return e;
            }
            this.completed = true;
            return Exchange.this.bodyComplete(this.bytesReceived, false, true, e);
        }
    }
    
    final class ResponseBodySource extends ForwardingSource
    {
        private final long contentLength;
        private long bytesReceived;
        private boolean completed;
        private boolean closed;
        
        ResponseBodySource(final Source delegate, final long contentLength) {
            super(delegate);
            this.contentLength = contentLength;
            if (contentLength == 0L) {
                this.complete(null);
            }
        }
        
        public long read(final Buffer sink, final long byteCount) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("closed");
            }
            try {
                final long read = this.delegate().read(sink, byteCount);
                if (read == -1L) {
                    this.complete(null);
                    return -1L;
                }
                final long newBytesReceived = this.bytesReceived + read;
                if (this.contentLength != -1L && newBytesReceived > this.contentLength) {
                    throw new ProtocolException("expected " + this.contentLength + " bytes but received " + newBytesReceived);
                }
                this.bytesReceived = newBytesReceived;
                if (newBytesReceived == this.contentLength) {
                    this.complete(null);
                }
                return read;
            }
            catch (IOException e) {
                throw this.complete(e);
            }
        }
        
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            try {
                super.close();
                this.complete(null);
            }
            catch (IOException e) {
                throw this.complete(e);
            }
        }
        
        @Nullable
        IOException complete(@Nullable final IOException e) {
            if (this.completed) {
                return e;
            }
            this.completed = true;
            return Exchange.this.bodyComplete(this.bytesReceived, true, false, e);
        }
    }
}
