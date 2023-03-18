//Raddon On Top!

package okhttp3.internal.http;

import java.net.*;
import okhttp3.*;

public final class RequestLine
{
    private RequestLine() {
    }
    
    public static String get(final Request request, final Proxy.Type proxyType) {
        final StringBuilder result = new StringBuilder();
        result.append(request.method());
        result.append(' ');
        if (includeAuthorityInRequestLine(request, proxyType)) {
            result.append(request.url());
        }
        else {
            result.append(requestPath(request.url()));
        }
        result.append(" HTTP/1.1");
        return result.toString();
    }
    
    private static boolean includeAuthorityInRequestLine(final Request request, final Proxy.Type proxyType) {
        return !request.isHttps() && proxyType == Proxy.Type.HTTP;
    }
    
    public static String requestPath(final HttpUrl url) {
        final String path = url.encodedPath();
        final String query = url.encodedQuery();
        return (query != null) ? (path + '?' + query) : path;
    }
}
