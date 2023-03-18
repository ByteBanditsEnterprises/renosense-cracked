//Raddon On Top!

package okhttp3.internal.http;

import okhttp3.internal.http2.*;
import okhttp3.internal.*;
import okhttp3.internal.connection.*;
import java.io.*;
import java.security.cert.*;
import javax.net.ssl.*;
import javax.annotation.*;
import java.net.*;
import okhttp3.*;

public final class RetryAndFollowUpInterceptor implements Interceptor
{
    private static final int MAX_FOLLOW_UPS = 20;
    private final OkHttpClient client;
    
    public RetryAndFollowUpInterceptor(final OkHttpClient client) {
        this.client = client;
    }
    
    public Response intercept(final Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        final RealInterceptorChain realChain = (RealInterceptorChain)chain;
        final Transmitter transmitter = realChain.transmitter();
        int followUpCount = 0;
        Response priorResponse = null;
        while (true) {
            transmitter.prepareToConnect(request);
            if (transmitter.isCanceled()) {
                throw new IOException("Canceled");
            }
            boolean success = false;
            Response response;
            try {
                response = realChain.proceed(request, transmitter, (Exchange)null);
                success = true;
            }
            catch (RouteException e) {
                if (!this.recover(e.getLastConnectException(), transmitter, false, request)) {
                    throw e.getFirstConnectException();
                }
                continue;
            }
            catch (IOException e2) {
                final boolean requestSendStarted = !(e2 instanceof ConnectionShutdownException);
                if (!this.recover(e2, transmitter, requestSendStarted, request)) {
                    throw e2;
                }
                continue;
            }
            finally {
                if (!success) {
                    transmitter.exchangeDoneDueToException();
                }
            }
            if (priorResponse != null) {
                response = response.newBuilder().priorResponse(priorResponse.newBuilder().body(null).build()).build();
            }
            final Exchange exchange = Internal.instance.exchange(response);
            final Route route = (exchange != null) ? exchange.connection().route() : null;
            final Request followUp = this.followUpRequest(response, route);
            if (followUp == null) {
                if (exchange != null && exchange.isDuplex()) {
                    transmitter.timeoutEarlyExit();
                }
                return response;
            }
            final RequestBody followUpBody = followUp.body();
            if (followUpBody != null && followUpBody.isOneShot()) {
                return response;
            }
            Util.closeQuietly(response.body());
            if (transmitter.hasExchange()) {
                exchange.detachWithViolence();
            }
            if (++followUpCount > 20) {
                throw new ProtocolException("Too many follow-up requests: " + followUpCount);
            }
            request = followUp;
            priorResponse = response;
        }
    }
    
    private boolean recover(final IOException e, final Transmitter transmitter, final boolean requestSendStarted, final Request userRequest) {
        return this.client.retryOnConnectionFailure() && (!requestSendStarted || !this.requestIsOneShot(e, userRequest)) && this.isRecoverable(e, requestSendStarted) && transmitter.canRetry();
    }
    
    private boolean requestIsOneShot(final IOException e, final Request userRequest) {
        final RequestBody requestBody = userRequest.body();
        return (requestBody != null && requestBody.isOneShot()) || e instanceof FileNotFoundException;
    }
    
    private boolean isRecoverable(final IOException e, final boolean requestSendStarted) {
        if (e instanceof ProtocolException) {
            return false;
        }
        if (e instanceof InterruptedIOException) {
            return e instanceof SocketTimeoutException && !requestSendStarted;
        }
        return (!(e instanceof SSLHandshakeException) || !(e.getCause() instanceof CertificateException)) && !(e instanceof SSLPeerUnverifiedException);
    }
    
    private Request followUpRequest(final Response userResponse, @Nullable final Route route) throws IOException {
        if (userResponse == null) {
            throw new IllegalStateException();
        }
        final int responseCode = userResponse.code();
        final String method = userResponse.request().method();
        switch (responseCode) {
            case 407: {
                final Proxy selectedProxy = (route != null) ? route.proxy() : this.client.proxy();
                if (selectedProxy.type() != Proxy.Type.HTTP) {
                    throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
                }
                return this.client.proxyAuthenticator().authenticate(route, userResponse);
            }
            case 401: {
                return this.client.authenticator().authenticate(route, userResponse);
            }
            case 307:
            case 308: {
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    return null;
                }
            }
            case 300:
            case 301:
            case 302:
            case 303: {
                if (!this.client.followRedirects()) {
                    return null;
                }
                final String location = userResponse.header("Location");
                if (location == null) {
                    return null;
                }
                final HttpUrl url = userResponse.request().url().resolve(location);
                if (url == null) {
                    return null;
                }
                final boolean sameScheme = url.scheme().equals(userResponse.request().url().scheme());
                if (!sameScheme && !this.client.followSslRedirects()) {
                    return null;
                }
                final Request.Builder requestBuilder = userResponse.request().newBuilder();
                if (HttpMethod.permitsRequestBody(method)) {
                    final boolean maintainBody = HttpMethod.redirectsWithBody(method);
                    if (HttpMethod.redirectsToGet(method)) {
                        requestBuilder.method("GET", null);
                    }
                    else {
                        final RequestBody requestBody = maintainBody ? userResponse.request().body() : null;
                        requestBuilder.method(method, requestBody);
                    }
                    if (!maintainBody) {
                        requestBuilder.removeHeader("Transfer-Encoding");
                        requestBuilder.removeHeader("Content-Length");
                        requestBuilder.removeHeader("Content-Type");
                    }
                }
                if (!Util.sameConnection(userResponse.request().url(), url)) {
                    requestBuilder.removeHeader("Authorization");
                }
                return requestBuilder.url(url).build();
            }
            case 408: {
                if (!this.client.retryOnConnectionFailure()) {
                    return null;
                }
                final RequestBody requestBody2 = userResponse.request().body();
                if (requestBody2 != null && requestBody2.isOneShot()) {
                    return null;
                }
                if (userResponse.priorResponse() != null && userResponse.priorResponse().code() == 408) {
                    return null;
                }
                if (this.retryAfter(userResponse, 0) > 0) {
                    return null;
                }
                return userResponse.request();
            }
            case 503: {
                if (userResponse.priorResponse() != null && userResponse.priorResponse().code() == 503) {
                    return null;
                }
                if (this.retryAfter(userResponse, Integer.MAX_VALUE) == 0) {
                    return userResponse.request();
                }
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    private int retryAfter(final Response userResponse, final int defaultDelay) {
        final String header = userResponse.header("Retry-After");
        if (header == null) {
            return defaultDelay;
        }
        if (header.matches("\\d+")) {
            return Integer.valueOf(header);
        }
        return Integer.MAX_VALUE;
    }
}
