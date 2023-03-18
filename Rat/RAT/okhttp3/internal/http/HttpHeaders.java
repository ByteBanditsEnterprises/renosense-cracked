//Raddon On Top!

package okhttp3.internal.http;

import okhttp3.internal.*;
import okio.*;
import java.io.*;
import java.util.*;
import okhttp3.*;

public final class HttpHeaders
{
    private static final ByteString QUOTED_STRING_DELIMITERS;
    private static final ByteString TOKEN_DELIMITERS;
    
    private HttpHeaders() {
    }
    
    public static long contentLength(final Response response) {
        return contentLength(response.headers());
    }
    
    public static long contentLength(final Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }
    
    private static long stringToLong(final String s) {
        if (s == null) {
            return -1L;
        }
        try {
            return Long.parseLong(s);
        }
        catch (NumberFormatException e) {
            return -1L;
        }
    }
    
    public static boolean varyMatches(final Response cachedResponse, final Headers cachedRequest, final Request newRequest) {
        for (final String field : varyFields(cachedResponse)) {
            if (!Objects.equals(cachedRequest.values(field), newRequest.headers(field))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean hasVaryAll(final Response response) {
        return hasVaryAll(response.headers());
    }
    
    public static boolean hasVaryAll(final Headers responseHeaders) {
        return varyFields(responseHeaders).contains("*");
    }
    
    private static Set<String> varyFields(final Response response) {
        return varyFields(response.headers());
    }
    
    public static Set<String> varyFields(final Headers responseHeaders) {
        Set<String> result = Collections.emptySet();
        for (int i = 0, size = responseHeaders.size(); i < size; ++i) {
            if ("Vary".equalsIgnoreCase(responseHeaders.name(i))) {
                final String value = responseHeaders.value(i);
                if (result.isEmpty()) {
                    result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                }
                for (final String varyField : value.split(",")) {
                    result.add(varyField.trim());
                }
            }
        }
        return result;
    }
    
    public static Headers varyHeaders(final Response response) {
        final Headers requestHeaders = response.networkResponse().request().headers();
        final Headers responseHeaders = response.headers();
        return varyHeaders(requestHeaders, responseHeaders);
    }
    
    public static Headers varyHeaders(final Headers requestHeaders, final Headers responseHeaders) {
        final Set<String> varyFields = varyFields(responseHeaders);
        if (varyFields.isEmpty()) {
            return Util.EMPTY_HEADERS;
        }
        final Headers.Builder result = new Headers.Builder();
        for (int i = 0, size = requestHeaders.size(); i < size; ++i) {
            final String fieldName = requestHeaders.name(i);
            if (varyFields.contains(fieldName)) {
                result.add(fieldName, requestHeaders.value(i));
            }
        }
        return result.build();
    }
    
    public static List<Challenge> parseChallenges(final Headers responseHeaders, final String headerName) {
        final List<Challenge> result = new ArrayList<Challenge>();
        for (int h = 0; h < responseHeaders.size(); ++h) {
            if (headerName.equalsIgnoreCase(responseHeaders.name(h))) {
                final Buffer header = new Buffer().writeUtf8(responseHeaders.value(h));
                parseChallengeHeader(result, header);
            }
        }
        return result;
    }
    
    private static void parseChallengeHeader(final List<Challenge> result, final Buffer header) {
        String peek = null;
        while (true) {
            if (peek == null) {
                skipWhitespaceAndCommas(header);
                peek = readToken(header);
                if (peek == null) {
                    return;
                }
            }
            final String schemeName = peek;
            final boolean commaPrefixed = skipWhitespaceAndCommas(header);
            peek = readToken(header);
            if (peek == null) {
                if (!header.exhausted()) {
                    return;
                }
                result.add(new Challenge(schemeName, (Map)Collections.emptyMap()));
            }
            else {
                int eqCount = skipAll(header, (byte)61);
                final boolean commaSuffixed = skipWhitespaceAndCommas(header);
                if (!commaPrefixed && (commaSuffixed || header.exhausted())) {
                    result.add(new Challenge(schemeName, (Map)Collections.singletonMap((Object)null, peek + repeat('=', eqCount))));
                    peek = null;
                }
                else {
                    final Map<String, String> parameters = new LinkedHashMap<String, String>();
                    eqCount += skipAll(header, (byte)61);
                    while (true) {
                        if (peek == null) {
                            peek = readToken(header);
                            if (skipWhitespaceAndCommas(header)) {
                                break;
                            }
                            eqCount = skipAll(header, (byte)61);
                        }
                        if (eqCount == 0) {
                            break;
                        }
                        if (eqCount > 1) {
                            return;
                        }
                        if (skipWhitespaceAndCommas(header)) {
                            return;
                        }
                        final String parameterValue = (!header.exhausted() && header.getByte(0L) == 34) ? readQuotedString(header) : readToken(header);
                        if (parameterValue == null) {
                            return;
                        }
                        final String replaced = parameters.put(peek, parameterValue);
                        peek = null;
                        if (replaced != null) {
                            return;
                        }
                        if (!skipWhitespaceAndCommas(header) && !header.exhausted()) {
                            return;
                        }
                    }
                    result.add(new Challenge(schemeName, (Map)parameters));
                }
            }
        }
    }
    
    private static boolean skipWhitespaceAndCommas(final Buffer buffer) {
        boolean commaFound = false;
        while (!buffer.exhausted()) {
            final byte b = buffer.getByte(0L);
            if (b == 44) {
                buffer.readByte();
                commaFound = true;
            }
            else {
                if (b != 32 && b != 9) {
                    break;
                }
                buffer.readByte();
            }
        }
        return commaFound;
    }
    
    private static int skipAll(final Buffer buffer, final byte b) {
        int count = 0;
        while (!buffer.exhausted() && buffer.getByte(0L) == b) {
            ++count;
            buffer.readByte();
        }
        return count;
    }
    
    private static String readQuotedString(final Buffer buffer) {
        if (buffer.readByte() != 34) {
            throw new IllegalArgumentException();
        }
        final Buffer result = new Buffer();
        while (true) {
            final long i = buffer.indexOfElement(HttpHeaders.QUOTED_STRING_DELIMITERS);
            if (i == -1L) {
                return null;
            }
            if (buffer.getByte(i) == 34) {
                result.write(buffer, i);
                buffer.readByte();
                return result.readUtf8();
            }
            if (buffer.size() == i + 1L) {
                return null;
            }
            result.write(buffer, i);
            buffer.readByte();
            result.write(buffer, 1L);
        }
    }
    
    private static String readToken(final Buffer buffer) {
        try {
            long tokenSize = buffer.indexOfElement(HttpHeaders.TOKEN_DELIMITERS);
            if (tokenSize == -1L) {
                tokenSize = buffer.size();
            }
            return (tokenSize != 0L) ? buffer.readUtf8(tokenSize) : null;
        }
        catch (EOFException e) {
            throw new AssertionError();
        }
    }
    
    private static String repeat(final char c, final int count) {
        final char[] array = new char[count];
        Arrays.fill(array, c);
        return new String(array);
    }
    
    public static void receiveHeaders(final CookieJar cookieJar, final HttpUrl url, final Headers headers) {
        if (cookieJar == CookieJar.NO_COOKIES) {
            return;
        }
        final List<Cookie> cookies = (List<Cookie>)Cookie.parseAll(url, headers);
        if (cookies.isEmpty()) {
            return;
        }
        cookieJar.saveFromResponse(url, (List)cookies);
    }
    
    public static boolean hasBody(final Response response) {
        if (response.request().method().equals("HEAD")) {
            return false;
        }
        final int responseCode = response.code();
        return ((responseCode < 100 || responseCode >= 200) && responseCode != 204 && responseCode != 304) || (contentLength(response) != -1L || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding")));
    }
    
    public static int skipUntil(final String input, int pos, final String characters) {
        while (pos < input.length() && characters.indexOf(input.charAt(pos)) == -1) {
            ++pos;
        }
        return pos;
    }
    
    public static int skipWhitespace(final String input, int pos) {
        while (pos < input.length()) {
            final char c = input.charAt(pos);
            if (c != ' ' && c != '\t') {
                break;
            }
            ++pos;
        }
        return pos;
    }
    
    public static int parseSeconds(final String value, final int defaultValue) {
        try {
            final long seconds = Long.parseLong(value);
            if (seconds > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            if (seconds < 0L) {
                return 0;
            }
            return (int)seconds;
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    static {
        QUOTED_STRING_DELIMITERS = ByteString.encodeUtf8("\"\\");
        TOKEN_DELIMITERS = ByteString.encodeUtf8("\t ,=");
    }
}
