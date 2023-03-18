//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import okhttp3.internal.connection.*;
import java.io.*;
import okio.*;
import java.util.*;
import okhttp3.internal.http.*;

public final class Response implements Closeable
{
    final Request request;
    final Protocol protocol;
    final int code;
    final String message;
    @Nullable
    final Handshake handshake;
    final Headers headers;
    @Nullable
    final ResponseBody body;
    @Nullable
    final Response networkResponse;
    @Nullable
    final Response cacheResponse;
    @Nullable
    final Response priorResponse;
    final long sentRequestAtMillis;
    final long receivedResponseAtMillis;
    @Nullable
    final Exchange exchange;
    @Nullable
    private volatile CacheControl cacheControl;
    
    Response(final Builder builder) {
        this.request = builder.request;
        this.protocol = builder.protocol;
        this.code = builder.code;
        this.message = builder.message;
        this.handshake = builder.handshake;
        this.headers = builder.headers.build();
        this.body = builder.body;
        this.networkResponse = builder.networkResponse;
        this.cacheResponse = builder.cacheResponse;
        this.priorResponse = builder.priorResponse;
        this.sentRequestAtMillis = builder.sentRequestAtMillis;
        this.receivedResponseAtMillis = builder.receivedResponseAtMillis;
        this.exchange = builder.exchange;
    }
    
    public Request request() {
        return this.request;
    }
    
    public Protocol protocol() {
        return this.protocol;
    }
    
    public int code() {
        return this.code;
    }
    
    public boolean isSuccessful() {
        return this.code >= 200 && this.code < 300;
    }
    
    public String message() {
        return this.message;
    }
    
    @Nullable
    public Handshake handshake() {
        return this.handshake;
    }
    
    public List<String> headers(final String name) {
        return (List<String>)this.headers.values(name);
    }
    
    @Nullable
    public String header(final String name) {
        return this.header(name, null);
    }
    
    @Nullable
    public String header(final String name, @Nullable final String defaultValue) {
        final String result = this.headers.get(name);
        return (result != null) ? result : defaultValue;
    }
    
    public Headers headers() {
        return this.headers;
    }
    
    public Headers trailers() throws IOException {
        if (this.exchange == null) {
            throw new IllegalStateException("trailers not available");
        }
        return this.exchange.trailers();
    }
    
    public ResponseBody peekBody(final long byteCount) throws IOException {
        final BufferedSource peeked = this.body.source().peek();
        final Buffer buffer = new Buffer();
        peeked.request(byteCount);
        buffer.write((Source)peeked, Math.min(byteCount, peeked.getBuffer().size()));
        return ResponseBody.create(this.body.contentType(), buffer.size(), (BufferedSource)buffer);
    }
    
    @Nullable
    public ResponseBody body() {
        return this.body;
    }
    
    public Builder newBuilder() {
        return new Builder(this);
    }
    
    public boolean isRedirect() {
        switch (this.code) {
            case 300:
            case 301:
            case 302:
            case 303:
            case 307:
            case 308: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Nullable
    public Response networkResponse() {
        return this.networkResponse;
    }
    
    @Nullable
    public Response cacheResponse() {
        return this.cacheResponse;
    }
    
    @Nullable
    public Response priorResponse() {
        return this.priorResponse;
    }
    
    public List<Challenge> challenges() {
        String responseField;
        if (this.code == 401) {
            responseField = "WWW-Authenticate";
        }
        else {
            if (this.code != 407) {
                return Collections.emptyList();
            }
            responseField = "Proxy-Authenticate";
        }
        return (List<Challenge>)HttpHeaders.parseChallenges(this.headers(), responseField);
    }
    
    public CacheControl cacheControl() {
        final CacheControl result = this.cacheControl;
        return (result != null) ? result : (this.cacheControl = CacheControl.parse(this.headers));
    }
    
    public long sentRequestAtMillis() {
        return this.sentRequestAtMillis;
    }
    
    public long receivedResponseAtMillis() {
        return this.receivedResponseAtMillis;
    }
    
    @Override
    public void close() {
        if (this.body == null) {
            throw new IllegalStateException("response is not eligible for a body and must not be closed");
        }
        this.body.close();
    }
    
    @Override
    public String toString() {
        return "Response{protocol=" + this.protocol + ", code=" + this.code + ", message=" + this.message + ", url=" + this.request.url() + '}';
    }
    
    public static class Builder
    {
        @Nullable
        Request request;
        @Nullable
        Protocol protocol;
        int code;
        String message;
        @Nullable
        Handshake handshake;
        Headers.Builder headers;
        @Nullable
        ResponseBody body;
        @Nullable
        Response networkResponse;
        @Nullable
        Response cacheResponse;
        @Nullable
        Response priorResponse;
        long sentRequestAtMillis;
        long receivedResponseAtMillis;
        @Nullable
        Exchange exchange;
        
        public Builder() {
            this.code = -1;
            this.headers = new Headers.Builder();
        }
        
        Builder(final Response response) {
            this.code = -1;
            this.request = response.request;
            this.protocol = response.protocol;
            this.code = response.code;
            this.message = response.message;
            this.handshake = response.handshake;
            this.headers = response.headers.newBuilder();
            this.body = response.body;
            this.networkResponse = response.networkResponse;
            this.cacheResponse = response.cacheResponse;
            this.priorResponse = response.priorResponse;
            this.sentRequestAtMillis = response.sentRequestAtMillis;
            this.receivedResponseAtMillis = response.receivedResponseAtMillis;
            this.exchange = response.exchange;
        }
        
        public Builder request(final Request request) {
            this.request = request;
            return this;
        }
        
        public Builder protocol(final Protocol protocol) {
            this.protocol = protocol;
            return this;
        }
        
        public Builder code(final int code) {
            this.code = code;
            return this;
        }
        
        public Builder message(final String message) {
            this.message = message;
            return this;
        }
        
        public Builder handshake(@Nullable final Handshake handshake) {
            this.handshake = handshake;
            return this;
        }
        
        public Builder header(final String name, final String value) {
            this.headers.set(name, value);
            return this;
        }
        
        public Builder addHeader(final String name, final String value) {
            this.headers.add(name, value);
            return this;
        }
        
        public Builder removeHeader(final String name) {
            this.headers.removeAll(name);
            return this;
        }
        
        public Builder headers(final Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }
        
        public Builder body(@Nullable final ResponseBody body) {
            this.body = body;
            return this;
        }
        
        public Builder networkResponse(@Nullable final Response networkResponse) {
            if (networkResponse != null) {
                this.checkSupportResponse("networkResponse", networkResponse);
            }
            this.networkResponse = networkResponse;
            return this;
        }
        
        public Builder cacheResponse(@Nullable final Response cacheResponse) {
            if (cacheResponse != null) {
                this.checkSupportResponse("cacheResponse", cacheResponse);
            }
            this.cacheResponse = cacheResponse;
            return this;
        }
        
        private void checkSupportResponse(final String name, final Response response) {
            if (response.body != null) {
                throw new IllegalArgumentException(name + ".body != null");
            }
            if (response.networkResponse != null) {
                throw new IllegalArgumentException(name + ".networkResponse != null");
            }
            if (response.cacheResponse != null) {
                throw new IllegalArgumentException(name + ".cacheResponse != null");
            }
            if (response.priorResponse != null) {
                throw new IllegalArgumentException(name + ".priorResponse != null");
            }
        }
        
        public Builder priorResponse(@Nullable final Response priorResponse) {
            if (priorResponse != null) {
                this.checkPriorResponse(priorResponse);
            }
            this.priorResponse = priorResponse;
            return this;
        }
        
        private void checkPriorResponse(final Response response) {
            if (response.body != null) {
                throw new IllegalArgumentException("priorResponse.body != null");
            }
        }
        
        public Builder sentRequestAtMillis(final long sentRequestAtMillis) {
            this.sentRequestAtMillis = sentRequestAtMillis;
            return this;
        }
        
        public Builder receivedResponseAtMillis(final long receivedResponseAtMillis) {
            this.receivedResponseAtMillis = receivedResponseAtMillis;
            return this;
        }
        
        void initExchange(final Exchange deferredTrailers) {
            this.exchange = deferredTrailers;
        }
        
        public Response build() {
            if (this.request == null) {
                throw new IllegalStateException("request == null");
            }
            if (this.protocol == null) {
                throw new IllegalStateException("protocol == null");
            }
            if (this.code < 0) {
                throw new IllegalStateException("code < 0: " + this.code);
            }
            if (this.message == null) {
                throw new IllegalStateException("message == null");
            }
            return new Response(this);
        }
    }
}
