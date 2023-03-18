//Raddon On Top!

package okhttp3.internal;

import java.util.regex.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.concurrent.*;
import java.nio.charset.*;
import java.net.*;
import javax.annotation.*;
import javax.net.ssl.*;
import okhttp3.internal.http2.*;
import java.util.*;
import java.security.*;
import okhttp3.*;
import okio.*;

public final class Util
{
    public static final byte[] EMPTY_BYTE_ARRAY;
    public static final String[] EMPTY_STRING_ARRAY;
    public static final Headers EMPTY_HEADERS;
    public static final ResponseBody EMPTY_RESPONSE;
    public static final RequestBody EMPTY_REQUEST;
    private static final Options UNICODE_BOMS;
    private static final Charset UTF_32BE;
    private static final Charset UTF_32LE;
    public static final TimeZone UTC;
    public static final Comparator<String> NATURAL_ORDER;
    private static final Method addSuppressedExceptionMethod;
    private static final Pattern VERIFY_AS_IP_ADDRESS;
    
    public static void addSuppressedIfPossible(final Throwable e, final Throwable suppressed) {
        if (Util.addSuppressedExceptionMethod != null) {
            try {
                Util.addSuppressedExceptionMethod.invoke(e, suppressed);
            }
            catch (InvocationTargetException ex) {}
            catch (IllegalAccessException ex2) {}
        }
    }
    
    private Util() {
    }
    
    public static void checkOffsetAndCount(final long arrayLength, final long offset, final long count) {
        if ((offset | count) < 0L || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
    
    public static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (RuntimeException rethrown) {
                throw rethrown;
            }
            catch (Exception ex) {}
        }
    }
    
    public static void closeQuietly(final Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (AssertionError e) {
                if (!isAndroidGetsocknameError(e)) {
                    throw e;
                }
            }
            catch (RuntimeException rethrown) {
                throw rethrown;
            }
            catch (Exception ex) {}
        }
    }
    
    public static void closeQuietly(final ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (RuntimeException rethrown) {
                throw rethrown;
            }
            catch (Exception ex) {}
        }
    }
    
    public static boolean discard(final Source source, final int timeout, final TimeUnit timeUnit) {
        try {
            return skipAll(source, timeout, timeUnit);
        }
        catch (IOException e) {
            return false;
        }
    }
    
    public static boolean skipAll(final Source source, final int duration, final TimeUnit timeUnit) throws IOException {
        final long now = System.nanoTime();
        final long originalDuration = source.timeout().hasDeadline() ? (source.timeout().deadlineNanoTime() - now) : Long.MAX_VALUE;
        source.timeout().deadlineNanoTime(now + Math.min(originalDuration, timeUnit.toNanos(duration)));
        try {
            final Buffer skipBuffer = new Buffer();
            while (source.read(skipBuffer, 8192L) != -1L) {
                skipBuffer.clear();
            }
            return true;
        }
        catch (InterruptedIOException e) {
            return false;
        }
        finally {
            if (originalDuration == Long.MAX_VALUE) {
                source.timeout().clearDeadline();
            }
            else {
                source.timeout().deadlineNanoTime(now + originalDuration);
            }
        }
    }
    
    public static <T> List<T> immutableList(final List<T> list) {
        return Collections.unmodifiableList((List<? extends T>)new ArrayList<T>((Collection<? extends T>)list));
    }
    
    public static <K, V> Map<K, V> immutableMap(final Map<K, V> map) {
        return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap((Map<? extends K, ? extends V>)new LinkedHashMap<K, V>((Map<? extends K, ? extends V>)map));
    }
    
    @SafeVarargs
    public static <T> List<T> immutableList(final T... elements) {
        return Collections.unmodifiableList((List<? extends T>)Arrays.asList((T[])elements.clone()));
    }
    
    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        final Thread result;
        return runnable -> {
            result = new Thread(runnable, name);
            result.setDaemon(daemon);
            return result;
        };
    }
    
    public static String[] intersect(final Comparator<? super String> comparator, final String[] first, final String[] second) {
        final List<String> result = new ArrayList<String>();
        for (final String a : first) {
            for (final String b : second) {
                if (comparator.compare(a, b) == 0) {
                    result.add(a);
                    break;
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }
    
    public static boolean nonEmptyIntersection(final Comparator<String> comparator, final String[] first, final String[] second) {
        if (first == null || second == null || first.length == 0 || second.length == 0) {
            return false;
        }
        for (final String a : first) {
            for (final String b : second) {
                if (comparator.compare(a, b) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String hostHeader(final HttpUrl url, final boolean includeDefaultPort) {
        final String host = url.host().contains(":") ? ("[" + url.host() + "]") : url.host();
        return (includeDefaultPort || url.port() != HttpUrl.defaultPort(url.scheme())) ? (host + ":" + url.port()) : host;
    }
    
    public static boolean isAndroidGetsocknameError(final AssertionError e) {
        return e.getCause() != null && e.getMessage() != null && e.getMessage().contains("getsockname failed");
    }
    
    public static int indexOf(final Comparator<String> comparator, final String[] array, final String value) {
        for (int i = 0, size = array.length; i < size; ++i) {
            if (comparator.compare(array[i], value) == 0) {
                return i;
            }
        }
        return -1;
    }
    
    public static String[] concat(final String[] array, final String value) {
        final String[] result = new String[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[result.length - 1] = value;
        return result;
    }
    
    public static int skipLeadingAsciiWhitespace(final String input, final int pos, final int limit) {
        int i = pos;
        while (i < limit) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ': {
                    ++i;
                    continue;
                }
                default: {
                    return i;
                }
            }
        }
        return limit;
    }
    
    public static int skipTrailingAsciiWhitespace(final String input, final int pos, final int limit) {
        int i = limit - 1;
        while (i >= pos) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ': {
                    --i;
                    continue;
                }
                default: {
                    return i + 1;
                }
            }
        }
        return pos;
    }
    
    public static String trimSubstring(final String string, final int pos, final int limit) {
        final int start = skipLeadingAsciiWhitespace(string, pos, limit);
        final int end = skipTrailingAsciiWhitespace(string, start, limit);
        return string.substring(start, end);
    }
    
    public static int delimiterOffset(final String input, final int pos, final int limit, final String delimiters) {
        for (int i = pos; i < limit; ++i) {
            if (delimiters.indexOf(input.charAt(i)) != -1) {
                return i;
            }
        }
        return limit;
    }
    
    public static int delimiterOffset(final String input, final int pos, final int limit, final char delimiter) {
        for (int i = pos; i < limit; ++i) {
            if (input.charAt(i) == delimiter) {
                return i;
            }
        }
        return limit;
    }
    
    public static String canonicalizeHost(final String host) {
        if (host.contains(":")) {
            final InetAddress inetAddress = (host.startsWith("[") && host.endsWith("]")) ? decodeIpv6(host, 1, host.length() - 1) : decodeIpv6(host, 0, host.length());
            if (inetAddress == null) {
                return null;
            }
            final byte[] address = inetAddress.getAddress();
            if (address.length == 16) {
                return inet6AddressToAscii(address);
            }
            if (address.length == 4) {
                return inetAddress.getHostAddress();
            }
            throw new AssertionError((Object)("Invalid IPv6 address: '" + host + "'"));
        }
        else {
            try {
                final String result = IDN.toASCII(host).toLowerCase(Locale.US);
                if (result.isEmpty()) {
                    return null;
                }
                if (containsInvalidHostnameAsciiCodes(result)) {
                    return null;
                }
                return result;
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
    
    private static boolean containsInvalidHostnameAsciiCodes(final String hostnameAscii) {
        for (int i = 0; i < hostnameAscii.length(); ++i) {
            final char c = hostnameAscii.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                return true;
            }
            if (" #%/:?@[\\]".indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }
    
    public static int indexOfControlOrNonAscii(final String input) {
        for (int i = 0, length = input.length(); i < length; ++i) {
            final char c = input.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean verifyAsIpAddress(final String host) {
        return Util.VERIFY_AS_IP_ADDRESS.matcher(host).matches();
    }
    
    public static String format(final String format, final Object... args) {
        return String.format(Locale.US, format, args);
    }
    
    public static Charset bomAwareCharset(final BufferedSource source, final Charset charset) throws IOException {
        switch (source.select(Util.UNICODE_BOMS)) {
            case 0: {
                return StandardCharsets.UTF_8;
            }
            case 1: {
                return StandardCharsets.UTF_16BE;
            }
            case 2: {
                return StandardCharsets.UTF_16LE;
            }
            case 3: {
                return Util.UTF_32BE;
            }
            case 4: {
                return Util.UTF_32LE;
            }
            case -1: {
                return charset;
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public static int checkDuration(final String name, final long duration, final TimeUnit unit) {
        if (duration < 0L) {
            throw new IllegalArgumentException(name + " < 0");
        }
        if (unit == null) {
            throw new NullPointerException("unit == null");
        }
        final long millis = unit.toMillis(duration);
        if (millis > 2147483647L) {
            throw new IllegalArgumentException(name + " too large.");
        }
        if (millis == 0L && duration > 0L) {
            throw new IllegalArgumentException(name + " too small.");
        }
        return (int)millis;
    }
    
    public static int decodeHexDigit(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }
    
    @Nullable
    private static InetAddress decodeIpv6(final String input, final int pos, final int limit) {
        final byte[] address = new byte[16];
        int b = 0;
        int compress = -1;
        int groupOffset = -1;
        int i = pos;
        while (i < limit) {
            if (b == address.length) {
                return null;
            }
            if (i + 2 <= limit && input.regionMatches(i, "::", 0, 2)) {
                if (compress != -1) {
                    return null;
                }
                i += 2;
                b += 2;
                compress = b;
                if (i == limit) {
                    break;
                }
            }
            else if (b != 0) {
                if (input.regionMatches(i, ":", 0, 1)) {
                    ++i;
                }
                else {
                    if (!input.regionMatches(i, ".", 0, 1)) {
                        return null;
                    }
                    if (!decodeIpv4Suffix(input, groupOffset, limit, address, b - 2)) {
                        return null;
                    }
                    b += 2;
                    break;
                }
            }
            int value = 0;
            groupOffset = i;
            while (i < limit) {
                final char c = input.charAt(i);
                final int hexDigit = decodeHexDigit(c);
                if (hexDigit == -1) {
                    break;
                }
                value = (value << 4) + hexDigit;
                ++i;
            }
            final int groupLength = i - groupOffset;
            if (groupLength == 0 || groupLength > 4) {
                return null;
            }
            address[b++] = (byte)(value >>> 8 & 0xFF);
            address[b++] = (byte)(value & 0xFF);
        }
        if (b != address.length) {
            if (compress == -1) {
                return null;
            }
            System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
            Arrays.fill(address, compress, compress + (address.length - b), (byte)0);
        }
        try {
            return InetAddress.getByAddress(address);
        }
        catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }
    
    private static boolean decodeIpv4Suffix(final String input, final int pos, final int limit, final byte[] address, final int addressOffset) {
        int b = addressOffset;
        int i = pos;
        while (i < limit) {
            if (b == address.length) {
                return false;
            }
            if (b != addressOffset) {
                if (input.charAt(i) != '.') {
                    return false;
                }
                ++i;
            }
            int value = 0;
            final int groupOffset = i;
            while (i < limit) {
                final char c = input.charAt(i);
                if (c < '0') {
                    break;
                }
                if (c > '9') {
                    break;
                }
                if (value == 0 && groupOffset != i) {
                    return false;
                }
                value = value * 10 + c - 48;
                if (value > 255) {
                    return false;
                }
                ++i;
            }
            final int groupLength = i - groupOffset;
            if (groupLength == 0) {
                return false;
            }
            address[b++] = (byte)value;
        }
        return b == addressOffset + 4;
    }
    
    private static String inet6AddressToAscii(final byte[] address) {
        int longestRunOffset = -1;
        int longestRunLength = 0;
        for (int i = 0; i < address.length; i += 2) {
            final int currentRunOffset = i;
            while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
                i += 2;
            }
            final int currentRunLength = i - currentRunOffset;
            if (currentRunLength > longestRunLength && currentRunLength >= 4) {
                longestRunOffset = currentRunOffset;
                longestRunLength = currentRunLength;
            }
        }
        final Buffer result = new Buffer();
        int j = 0;
        while (j < address.length) {
            if (j == longestRunOffset) {
                result.writeByte(58);
                j += longestRunLength;
                if (j != 16) {
                    continue;
                }
                result.writeByte(58);
            }
            else {
                if (j > 0) {
                    result.writeByte(58);
                }
                final int group = (address[j] & 0xFF) << 8 | (address[j + 1] & 0xFF);
                result.writeHexadecimalUnsignedLong((long)group);
                j += 2;
            }
        }
        return result.readUtf8();
    }
    
    public static X509TrustManager platformTrustManager() {
        try {
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore)null);
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager)trustManagers[0];
        }
        catch (GeneralSecurityException e) {
            throw new AssertionError("No System TLS", e);
        }
    }
    
    public static Headers toHeaders(final List<Header> headerBlock) {
        final Headers.Builder builder = new Headers.Builder();
        for (final Header header : headerBlock) {
            Internal.instance.addLenient(builder, header.name.utf8(), header.value.utf8());
        }
        return builder.build();
    }
    
    public static List<Header> toHeaderBlock(final Headers headers) {
        final List<Header> result = new ArrayList<Header>();
        for (int i = 0; i < headers.size(); ++i) {
            result.add(new Header(headers.name(i), headers.value(i)));
        }
        return result;
    }
    
    public static String getSystemProperty(final String key, @Nullable final String defaultValue) {
        String value;
        try {
            value = System.getProperty(key);
        }
        catch (AccessControlException ex) {
            return defaultValue;
        }
        return (value != null) ? value : defaultValue;
    }
    
    public static boolean sameConnection(final HttpUrl a, final HttpUrl b) {
        return a.host().equals(b.host()) && a.port() == b.port() && a.scheme().equals(b.scheme());
    }
    
    static {
        EMPTY_BYTE_ARRAY = new byte[0];
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_HEADERS = Headers.of(new String[0]);
        EMPTY_RESPONSE = ResponseBody.create(null, Util.EMPTY_BYTE_ARRAY);
        EMPTY_REQUEST = RequestBody.create(null, Util.EMPTY_BYTE_ARRAY);
        UNICODE_BOMS = Options.of(new ByteString[] { ByteString.decodeHex("efbbbf"), ByteString.decodeHex("feff"), ByteString.decodeHex("fffe"), ByteString.decodeHex("0000ffff"), ByteString.decodeHex("ffff0000") });
        UTF_32BE = Charset.forName("UTF-32BE");
        UTF_32LE = Charset.forName("UTF-32LE");
        UTC = TimeZone.getTimeZone("GMT");
        NATURAL_ORDER = String::compareTo;
        Method m;
        try {
            m = Throwable.class.getDeclaredMethod("addSuppressed", Throwable.class);
        }
        catch (Exception e) {
            m = null;
        }
        addSuppressedExceptionMethod = m;
        VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");
    }
}
