//Raddon On Top!

package okhttp3;

import okio.*;
import okhttp3.internal.cache.*;
import okhttp3.internal.http.*;
import java.util.*;
import okhttp3.internal.connection.*;
import okhttp3.internal.*;
import java.util.concurrent.atomic.*;
import java.io.*;
import java.util.concurrent.*;
import okhttp3.internal.platform.*;

final class RealCall implements Call
{
    final OkHttpClient client;
    private Transmitter transmitter;
    final Request originalRequest;
    final boolean forWebSocket;
    private boolean executed;
    
    private RealCall(final OkHttpClient client, final Request originalRequest, final boolean forWebSocket) {
        this.client = client;
        this.originalRequest = originalRequest;
        this.forWebSocket = forWebSocket;
    }
    
    static RealCall newRealCall(final OkHttpClient client, final Request originalRequest, final boolean forWebSocket) {
        final RealCall call = new RealCall(client, originalRequest, forWebSocket);
        call.transmitter = new Transmitter(client, (Call)call);
        return call;
    }
    
    public Request request() {
        return this.originalRequest;
    }
    
    public Response execute() throws IOException {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        this.transmitter.timeoutEnter();
        this.transmitter.callStart();
        try {
            this.client.dispatcher().executed(this);
            return this.getResponseWithInterceptorChain();
        }
        finally {
            this.client.dispatcher().finished(this);
        }
    }
    
    public void enqueue(final Callback responseCallback) {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        this.transmitter.callStart();
        this.client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }
    
    public void cancel() {
        this.transmitter.cancel();
    }
    
    public Timeout timeout() {
        return this.transmitter.timeout();
    }
    
    public synchronized boolean isExecuted() {
        return this.executed;
    }
    
    public boolean isCanceled() {
        return this.transmitter.isCanceled();
    }
    
    public RealCall clone() {
        return newRealCall(this.client, this.originalRequest, this.forWebSocket);
    }
    
    String toLoggableString() {
        return (this.isCanceled() ? "canceled " : "") + (this.forWebSocket ? "web socket" : "call") + " to " + this.redactedUrl();
    }
    
    String redactedUrl() {
        return this.originalRequest.url().redact();
    }
    
    Response getResponseWithInterceptorChain() throws IOException {
        final List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.addAll(this.client.interceptors());
        interceptors.add((Interceptor)new RetryAndFollowUpInterceptor(this.client));
        interceptors.add((Interceptor)new BridgeInterceptor(this.client.cookieJar()));
        interceptors.add((Interceptor)new CacheInterceptor(this.client.internalCache()));
        interceptors.add((Interceptor)new ConnectInterceptor(this.client));
        if (!this.forWebSocket) {
            interceptors.addAll(this.client.networkInterceptors());
        }
        interceptors.add((Interceptor)new CallServerInterceptor(this.forWebSocket));
        final Interceptor.Chain chain = (Interceptor.Chain)new RealInterceptorChain((List)interceptors, this.transmitter, (Exchange)null, 0, this.originalRequest, (Call)this, this.client.connectTimeoutMillis(), this.client.readTimeoutMillis(), this.client.writeTimeoutMillis());
        boolean calledNoMoreExchanges = false;
        try {
            final Response response = chain.proceed(this.originalRequest);
            if (this.transmitter.isCanceled()) {
                Util.closeQuietly((Closeable)response);
                throw new IOException("Canceled");
            }
            return response;
        }
        catch (IOException e) {
            calledNoMoreExchanges = true;
            throw this.transmitter.noMoreExchanges(e);
        }
        finally {
            if (!calledNoMoreExchanges) {
                this.transmitter.noMoreExchanges((IOException)null);
            }
        }
    }
    
    final class AsyncCall extends NamedRunnable
    {
        private final Callback responseCallback;
        private volatile AtomicInteger callsPerHost;
        
        AsyncCall(final Callback responseCallback) {
            super("OkHttp %s", new Object[] { RealCall.this.redactedUrl() });
            this.callsPerHost = new AtomicInteger(0);
            this.responseCallback = responseCallback;
        }
        
        AtomicInteger callsPerHost() {
            return this.callsPerHost;
        }
        
        void reuseCallsPerHostFrom(final AsyncCall other) {
            this.callsPerHost = other.callsPerHost;
        }
        
        String host() {
            return RealCall.this.originalRequest.url().host();
        }
        
        Request request() {
            return RealCall.this.originalRequest;
        }
        
        RealCall get() {
            return RealCall.this;
        }
        
        void executeOn(final ExecutorService executorService) {
            assert !Thread.holdsLock(RealCall.this.client.dispatcher());
            boolean success = false;
            try {
                executorService.execute((Runnable)this);
                success = true;
            }
            catch (RejectedExecutionException e) {
                final InterruptedIOException ioException = new InterruptedIOException("executor rejected");
                ioException.initCause(e);
                RealCall.this.transmitter.noMoreExchanges((IOException)ioException);
                this.responseCallback.onFailure((Call)RealCall.this, (IOException)ioException);
            }
            finally {
                if (!success) {
                    RealCall.this.client.dispatcher().finished(this);
                }
            }
        }
        
        protected void execute() {
            boolean signalledCallback = false;
            RealCall.this.transmitter.timeoutEnter();
            try {
                final Response response = RealCall.this.getResponseWithInterceptorChain();
                signalledCallback = true;
                this.responseCallback.onResponse((Call)RealCall.this, response);
            }
            catch (IOException e) {
                if (signalledCallback) {
                    Platform.get().log(4, "Callback failure for " + RealCall.this.toLoggableString(), (Throwable)e);
                }
                else {
                    this.responseCallback.onFailure((Call)RealCall.this, e);
                }
            }
            catch (Throwable t) {
                RealCall.this.cancel();
                if (!signalledCallback) {
                    final IOException canceledException = new IOException("canceled due to " + t);
                    canceledException.addSuppressed(t);
                    this.responseCallback.onFailure((Call)RealCall.this, canceledException);
                }
                throw t;
            }
            finally {
                RealCall.this.client.dispatcher().finished(this);
            }
        }
    }
}
