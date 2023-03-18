//Raddon On Top!

package okhttp3.internal.http;

import okhttp3.internal.*;
import java.net.*;
import okhttp3.internal.connection.*;
import okhttp3.*;
import okio.*;
import java.io.*;

public final class CallServerInterceptor implements Interceptor
{
    private final boolean forWebSocket;
    
    public CallServerInterceptor(final boolean forWebSocket) {
        this.forWebSocket = forWebSocket;
    }
    
    public Response intercept(final Interceptor.Chain chain) throws IOException {
        final RealInterceptorChain realChain = (RealInterceptorChain)chain;
        final Exchange exchange = realChain.exchange();
        final Request request = realChain.request();
        final long sentRequestMillis = System.currentTimeMillis();
        exchange.writeRequestHeaders(request);
        boolean responseHeadersStarted = false;
        Response.Builder responseBuilder = null;
        if (HttpMethod.permitsRequestBody(request.method()) && request.body() != null) {
            if ("100-continue".equalsIgnoreCase(request.header("Expect"))) {
                exchange.flushRequest();
                responseHeadersStarted = true;
                exchange.responseHeadersStart();
                responseBuilder = exchange.readResponseHeaders(true);
            }
            if (responseBuilder == null) {
                if (request.body().isDuplex()) {
                    exchange.flushRequest();
                    final BufferedSink bufferedRequestBody = Okio.buffer(exchange.createRequestBody(request, true));
                    request.body().writeTo(bufferedRequestBody);
                }
                else {
                    final BufferedSink bufferedRequestBody = Okio.buffer(exchange.createRequestBody(request, false));
                    request.body().writeTo(bufferedRequestBody);
                    bufferedRequestBody.close();
                }
            }
            else {
                exchange.noRequestBody();
                if (!exchange.connection().isMultiplexed()) {
                    exchange.noNewExchangesOnConnection();
                }
            }
        }
        else {
            exchange.noRequestBody();
        }
        if (request.body() == null || !request.body().isDuplex()) {
            exchange.finishRequest();
        }
        if (!responseHeadersStarted) {
            exchange.responseHeadersStart();
        }
        if (responseBuilder == null) {
            responseBuilder = exchange.readResponseHeaders(false);
        }
        Response response = responseBuilder.request(request).handshake(exchange.connection().handshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
        int code = response.code();
        if (code == 100) {
            response = exchange.readResponseHeaders(false).request(request).handshake(exchange.connection().handshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
            code = response.code();
        }
        exchange.responseHeadersEnd(response);
        if (this.forWebSocket && code == 101) {
            response = response.newBuilder().body(Util.EMPTY_RESPONSE).build();
        }
        else {
            response = response.newBuilder().body(exchange.openResponseBody(response)).build();
        }
        if ("close".equalsIgnoreCase(response.request().header("Connection")) || "close".equalsIgnoreCase(response.header("Connection"))) {
            exchange.noNewExchangesOnConnection();
        }
        if ((code == 204 || code == 205) && response.body().contentLength() > 0L) {
            throw new ProtocolException("HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
        }
        return response;
    }
}
