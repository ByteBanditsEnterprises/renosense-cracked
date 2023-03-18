//Raddon On Top!

package okhttp3.internal.http;

import okhttp3.internal.*;
import okio.*;
import java.util.*;
import okhttp3.*;
import java.io.*;

public final class BridgeInterceptor implements Interceptor
{
    private final CookieJar cookieJar;
    
    public BridgeInterceptor(final CookieJar cookieJar) {
        this.cookieJar = cookieJar;
    }
    
    public Response intercept(final Interceptor.Chain chain) throws IOException {
        final Request userRequest = chain.request();
        final Request.Builder requestBuilder = userRequest.newBuilder();
        final RequestBody body = userRequest.body();
        if (body != null) {
            final MediaType contentType = body.contentType();
            if (contentType != null) {
                requestBuilder.header("Content-Type", contentType.toString());
            }
            final long contentLength = body.contentLength();
            if (contentLength != -1L) {
                requestBuilder.header("Content-Length", Long.toString(contentLength));
                requestBuilder.removeHeader("Transfer-Encoding");
            }
            else {
                requestBuilder.header("Transfer-Encoding", "chunked");
                requestBuilder.removeHeader("Content-Length");
            }
        }
        if (userRequest.header("Host") == null) {
            requestBuilder.header("Host", Util.hostHeader(userRequest.url(), false));
        }
        if (userRequest.header("Connection") == null) {
            requestBuilder.header("Connection", "Keep-Alive");
        }
        boolean transparentGzip = false;
        if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
            transparentGzip = true;
            requestBuilder.header("Accept-Encoding", "gzip");
        }
        final List<Cookie> cookies = (List<Cookie>)this.cookieJar.loadForRequest(userRequest.url());
        if (!cookies.isEmpty()) {
            requestBuilder.header("Cookie", this.cookieHeader(cookies));
        }
        if (userRequest.header("User-Agent") == null) {
            requestBuilder.header("User-Agent", Version.userAgent());
        }
        final Response networkResponse = chain.proceed(requestBuilder.build());
        HttpHeaders.receiveHeaders(this.cookieJar, userRequest.url(), networkResponse.headers());
        final Response.Builder responseBuilder = networkResponse.newBuilder().request(userRequest);
        if (transparentGzip && "gzip".equalsIgnoreCase(networkResponse.header("Content-Encoding")) && HttpHeaders.hasBody(networkResponse)) {
            final GzipSource responseBody = new GzipSource((Source)networkResponse.body().source());
            final Headers strippedHeaders = networkResponse.headers().newBuilder().removeAll("Content-Encoding").removeAll("Content-Length").build();
            responseBuilder.headers(strippedHeaders);
            final String contentType2 = networkResponse.header("Content-Type");
            responseBuilder.body(new RealResponseBody(contentType2, -1L, Okio.buffer((Source)responseBody)));
        }
        return responseBuilder.build();
    }
    
    private String cookieHeader(final List<Cookie> cookies) {
        final StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; ++i) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            final Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }
        return cookieHeader.toString();
    }
}
