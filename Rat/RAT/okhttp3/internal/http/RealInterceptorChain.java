//Raddon On Top!

package okhttp3.internal.http;

import java.util.*;
import okhttp3.internal.connection.*;
import javax.annotation.*;
import java.util.concurrent.*;
import okhttp3.internal.*;
import okhttp3.*;
import java.io.*;

public final class RealInterceptorChain implements Interceptor.Chain
{
    private final List<Interceptor> interceptors;
    private final Transmitter transmitter;
    @Nullable
    private final Exchange exchange;
    private final int index;
    private final Request request;
    private final Call call;
    private final int connectTimeout;
    private final int readTimeout;
    private final int writeTimeout;
    private int calls;
    
    public RealInterceptorChain(final List<Interceptor> interceptors, final Transmitter transmitter, @Nullable final Exchange exchange, final int index, final Request request, final Call call, final int connectTimeout, final int readTimeout, final int writeTimeout) {
        this.interceptors = interceptors;
        this.transmitter = transmitter;
        this.exchange = exchange;
        this.index = index;
        this.request = request;
        this.call = call;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }
    
    @Nullable
    public Connection connection() {
        return (Connection)((this.exchange != null) ? this.exchange.connection() : null);
    }
    
    public int connectTimeoutMillis() {
        return this.connectTimeout;
    }
    
    public Interceptor.Chain withConnectTimeout(final int timeout, final TimeUnit unit) {
        final int millis = Util.checkDuration("timeout", timeout, unit);
        return (Interceptor.Chain)new RealInterceptorChain(this.interceptors, this.transmitter, this.exchange, this.index, this.request, this.call, millis, this.readTimeout, this.writeTimeout);
    }
    
    public int readTimeoutMillis() {
        return this.readTimeout;
    }
    
    public Interceptor.Chain withReadTimeout(final int timeout, final TimeUnit unit) {
        final int millis = Util.checkDuration("timeout", timeout, unit);
        return (Interceptor.Chain)new RealInterceptorChain(this.interceptors, this.transmitter, this.exchange, this.index, this.request, this.call, this.connectTimeout, millis, this.writeTimeout);
    }
    
    public int writeTimeoutMillis() {
        return this.writeTimeout;
    }
    
    public Interceptor.Chain withWriteTimeout(final int timeout, final TimeUnit unit) {
        final int millis = Util.checkDuration("timeout", timeout, unit);
        return (Interceptor.Chain)new RealInterceptorChain(this.interceptors, this.transmitter, this.exchange, this.index, this.request, this.call, this.connectTimeout, this.readTimeout, millis);
    }
    
    public Transmitter transmitter() {
        return this.transmitter;
    }
    
    public Exchange exchange() {
        if (this.exchange == null) {
            throw new IllegalStateException();
        }
        return this.exchange;
    }
    
    public Call call() {
        return this.call;
    }
    
    public Request request() {
        return this.request;
    }
    
    public Response proceed(final Request request) throws IOException {
        return this.proceed(request, this.transmitter, this.exchange);
    }
    
    public Response proceed(final Request request, final Transmitter transmitter, @Nullable final Exchange exchange) throws IOException {
        if (this.index >= this.interceptors.size()) {
            throw new AssertionError();
        }
        ++this.calls;
        if (this.exchange != null && !this.exchange.connection().supportsUrl(request.url())) {
            throw new IllegalStateException("network interceptor " + this.interceptors.get(this.index - 1) + " must retain the same host and port");
        }
        if (this.exchange != null && this.calls > 1) {
            throw new IllegalStateException("network interceptor " + this.interceptors.get(this.index - 1) + " must call proceed() exactly once");
        }
        final RealInterceptorChain next = new RealInterceptorChain(this.interceptors, transmitter, exchange, this.index + 1, request, this.call, this.connectTimeout, this.readTimeout, this.writeTimeout);
        final Interceptor interceptor = this.interceptors.get(this.index);
        final Response response = interceptor.intercept((Interceptor.Chain)next);
        if (exchange != null && this.index + 1 < this.interceptors.size() && next.calls != 1) {
            throw new IllegalStateException("network interceptor " + interceptor + " must call proceed() exactly once");
        }
        if (response == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
        }
        if (response.body() == null) {
            throw new IllegalStateException("interceptor " + interceptor + " returned a response with no body");
        }
        return response;
    }
}
