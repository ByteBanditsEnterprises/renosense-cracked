//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import java.net.*;
import okhttp3.internal.*;
import java.util.*;
import okhttp3.internal.publicsuffix.*;
import okio.*;
import java.nio.charset.*;

public final class HttpUrl
{
    private static final char[] HEX_DIGITS;
    static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
    static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
    static final String QUERY_ENCODE_SET = " \"'<>#";
    static final String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
    static final String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
    static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    static final String FRAGMENT_ENCODE_SET = "";
    static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
    final String scheme;
    private final String username;
    private final String password;
    final String host;
    final int port;
    private final List<String> pathSegments;
    @Nullable
    private final List<String> queryNamesAndValues;
    @Nullable
    private final String fragment;
    private final String url;
    
    HttpUrl(final Builder builder) {
        this.scheme = builder.scheme;
        this.username = percentDecode(builder.encodedUsername, false);
        this.password = percentDecode(builder.encodedPassword, false);
        this.host = builder.host;
        this.port = builder.effectivePort();
        this.pathSegments = this.percentDecode(builder.encodedPathSegments, false);
        this.queryNamesAndValues = ((builder.encodedQueryNamesAndValues != null) ? this.percentDecode(builder.encodedQueryNamesAndValues, true) : null);
        this.fragment = ((builder.encodedFragment != null) ? percentDecode(builder.encodedFragment, false) : null);
        this.url = builder.toString();
    }
    
    public URL url() {
        try {
            return new URL(this.url);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public URI uri() {
        final String uri = this.newBuilder().reencodeForUri().toString();
        try {
            return new URI(uri);
        }
        catch (URISyntaxException e) {
            try {
                final String stripped = uri.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", "");
                return URI.create(stripped);
            }
            catch (Exception e2) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public String scheme() {
        return this.scheme;
    }
    
    public boolean isHttps() {
        return this.scheme.equals("https");
    }
    
    public String encodedUsername() {
        if (this.username.isEmpty()) {
            return "";
        }
        final int usernameStart = this.scheme.length() + 3;
        final int usernameEnd = Util.delimiterOffset(this.url, usernameStart, this.url.length(), ":@");
        return this.url.substring(usernameStart, usernameEnd);
    }
    
    public String username() {
        return this.username;
    }
    
    public String encodedPassword() {
        if (this.password.isEmpty()) {
            return "";
        }
        final int passwordStart = this.url.indexOf(58, this.scheme.length() + 3) + 1;
        final int passwordEnd = this.url.indexOf(64);
        return this.url.substring(passwordStart, passwordEnd);
    }
    
    public String password() {
        return this.password;
    }
    
    public String host() {
        return this.host;
    }
    
    public int port() {
        return this.port;
    }
    
    public static int defaultPort(final String scheme) {
        if (scheme.equals("http")) {
            return 80;
        }
        if (scheme.equals("https")) {
            return 443;
        }
        return -1;
    }
    
    public int pathSize() {
        return this.pathSegments.size();
    }
    
    public String encodedPath() {
        final int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        final int pathEnd = Util.delimiterOffset(this.url, pathStart, this.url.length(), "?#");
        return this.url.substring(pathStart, pathEnd);
    }
    
    static void pathSegmentsToString(final StringBuilder out, final List<String> pathSegments) {
        for (int i = 0, size = pathSegments.size(); i < size; ++i) {
            out.append('/');
            out.append(pathSegments.get(i));
        }
    }
    
    public List<String> encodedPathSegments() {
        final int pathStart = this.url.indexOf(47, this.scheme.length() + 3);
        final int pathEnd = Util.delimiterOffset(this.url, pathStart, this.url.length(), "?#");
        final List<String> result = new ArrayList<String>();
        int segmentEnd;
        for (int i = pathStart; i < pathEnd; i = segmentEnd) {
            ++i;
            segmentEnd = Util.delimiterOffset(this.url, i, pathEnd, '/');
            result.add(this.url.substring(i, segmentEnd));
        }
        return result;
    }
    
    public List<String> pathSegments() {
        return this.pathSegments;
    }
    
    @Nullable
    public String encodedQuery() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        final int queryStart = this.url.indexOf(63) + 1;
        final int queryEnd = Util.delimiterOffset(this.url, queryStart, this.url.length(), '#');
        return this.url.substring(queryStart, queryEnd);
    }
    
    static void namesAndValuesToQueryString(final StringBuilder out, final List<String> namesAndValues) {
        for (int i = 0, size = namesAndValues.size(); i < size; i += 2) {
            final String name = namesAndValues.get(i);
            final String value = namesAndValues.get(i + 1);
            if (i > 0) {
                out.append('&');
            }
            out.append(name);
            if (value != null) {
                out.append('=');
                out.append(value);
            }
        }
    }
    
    static List<String> queryStringToNamesAndValues(final String encodedQuery) {
        final List<String> result = new ArrayList<String>();
        int ampersandOffset;
        for (int pos = 0; pos <= encodedQuery.length(); pos = ampersandOffset + 1) {
            ampersandOffset = encodedQuery.indexOf(38, pos);
            if (ampersandOffset == -1) {
                ampersandOffset = encodedQuery.length();
            }
            final int equalsOffset = encodedQuery.indexOf(61, pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(encodedQuery.substring(pos, ampersandOffset));
                result.add(null);
            }
            else {
                result.add(encodedQuery.substring(pos, equalsOffset));
                result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
            }
        }
        return result;
    }
    
    @Nullable
    public String query() {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        namesAndValuesToQueryString(result, this.queryNamesAndValues);
        return result.toString();
    }
    
    public int querySize() {
        return (this.queryNamesAndValues != null) ? (this.queryNamesAndValues.size() / 2) : 0;
    }
    
    @Nullable
    public String queryParameter(final String name) {
        if (this.queryNamesAndValues == null) {
            return null;
        }
        for (int i = 0, size = this.queryNamesAndValues.size(); i < size; i += 2) {
            if (name.equals(this.queryNamesAndValues.get(i))) {
                return this.queryNamesAndValues.get(i + 1);
            }
        }
        return null;
    }
    
    public Set<String> queryParameterNames() {
        if (this.queryNamesAndValues == null) {
            return Collections.emptySet();
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (int i = 0, size = this.queryNamesAndValues.size(); i < size; i += 2) {
            result.add(this.queryNamesAndValues.get(i));
        }
        return Collections.unmodifiableSet((Set<? extends String>)result);
    }
    
    public List<String> queryParameterValues(final String name) {
        if (this.queryNamesAndValues == null) {
            return Collections.emptyList();
        }
        final List<String> result = new ArrayList<String>();
        for (int i = 0, size = this.queryNamesAndValues.size(); i < size; i += 2) {
            if (name.equals(this.queryNamesAndValues.get(i))) {
                result.add(this.queryNamesAndValues.get(i + 1));
            }
        }
        return Collections.unmodifiableList((List<? extends String>)result);
    }
    
    public String queryParameterName(final int index) {
        if (this.queryNamesAndValues == null) {
            throw new IndexOutOfBoundsException();
        }
        return this.queryNamesAndValues.get(index * 2);
    }
    
    public String queryParameterValue(final int index) {
        if (this.queryNamesAndValues == null) {
            throw new IndexOutOfBoundsException();
        }
        return this.queryNamesAndValues.get(index * 2 + 1);
    }
    
    @Nullable
    public String encodedFragment() {
        if (this.fragment == null) {
            return null;
        }
        final int fragmentStart = this.url.indexOf(35) + 1;
        return this.url.substring(fragmentStart);
    }
    
    @Nullable
    public String fragment() {
        return this.fragment;
    }
    
    public String redact() {
        return this.newBuilder("/...").username("").password("").build().toString();
    }
    
    @Nullable
    public HttpUrl resolve(final String link) {
        final Builder builder = this.newBuilder(link);
        return (builder != null) ? builder.build() : null;
    }
    
    public Builder newBuilder() {
        final Builder result = new Builder();
        result.scheme = this.scheme;
        result.encodedUsername = this.encodedUsername();
        result.encodedPassword = this.encodedPassword();
        result.host = this.host;
        result.port = ((this.port != defaultPort(this.scheme)) ? this.port : -1);
        result.encodedPathSegments.clear();
        result.encodedPathSegments.addAll(this.encodedPathSegments());
        result.encodedQuery(this.encodedQuery());
        result.encodedFragment = this.encodedFragment();
        return result;
    }
    
    @Nullable
    public Builder newBuilder(final String link) {
        try {
            return new Builder().parse(this, link);
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }
    
    @Nullable
    public static HttpUrl parse(final String url) {
        try {
            return get(url);
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }
    
    public static HttpUrl get(final String url) {
        return new Builder().parse(null, url).build();
    }
    
    @Nullable
    public static HttpUrl get(final URL url) {
        return parse(url.toString());
    }
    
    @Nullable
    public static HttpUrl get(final URI uri) {
        return parse(uri.toString());
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        return other instanceof HttpUrl && ((HttpUrl)other).url.equals(this.url);
    }
    
    @Override
    public int hashCode() {
        return this.url.hashCode();
    }
    
    @Override
    public String toString() {
        return this.url;
    }
    
    @Nullable
    public String topPrivateDomain() {
        if (Util.verifyAsIpAddress(this.host)) {
            return null;
        }
        return PublicSuffixDatabase.get().getEffectiveTldPlusOne(this.host);
    }
    
    static String percentDecode(final String encoded, final boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }
    
    private List<String> percentDecode(final List<String> list, final boolean plusIsSpace) {
        final int size = list.size();
        final List<String> result = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            final String s = list.get(i);
            result.add((s != null) ? percentDecode(s, plusIsSpace) : null);
        }
        return Collections.unmodifiableList((List<? extends String>)result);
    }
    
    static String percentDecode(final String encoded, final int pos, final int limit, final boolean plusIsSpace) {
        for (int i = pos; i < limit; ++i) {
            final char c = encoded.charAt(i);
            if (c == '%' || (c == '+' && plusIsSpace)) {
                final Buffer out = new Buffer();
                out.writeUtf8(encoded, pos, i);
                percentDecode(out, encoded, i, limit, plusIsSpace);
                return out.readUtf8();
            }
        }
        return encoded.substring(pos, limit);
    }
    
    static void percentDecode(final Buffer out, final String encoded, final int pos, final int limit, final boolean plusIsSpace) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = encoded.codePointAt(i);
            if (codePoint == 37 && i + 2 < limit) {
                final int d1 = Util.decodeHexDigit(encoded.charAt(i + 1));
                final int d2 = Util.decodeHexDigit(encoded.charAt(i + 2));
                if (d1 != -1 && d2 != -1) {
                    out.writeByte((d1 << 4) + d2);
                    i += 2;
                    continue;
                }
            }
            else if (codePoint == 43 && plusIsSpace) {
                out.writeByte(32);
                continue;
            }
            out.writeUtf8CodePoint(codePoint);
        }
    }
    
    static boolean percentEncoded(final String encoded, final int pos, final int limit) {
        return pos + 2 < limit && encoded.charAt(pos) == '%' && Util.decodeHexDigit(encoded.charAt(pos + 1)) != -1 && Util.decodeHexDigit(encoded.charAt(pos + 2)) != -1;
    }
    
    static String canonicalize(final String input, final int pos, final int limit, final String encodeSet, final boolean alreadyEncoded, final boolean strict, final boolean plusIsSpace, final boolean asciiOnly, @Nullable final Charset charset) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (codePoint < 32 || codePoint == 127 || (codePoint >= 128 && asciiOnly) || encodeSet.indexOf(codePoint) != -1 || (codePoint == 37 && (!alreadyEncoded || (strict && !percentEncoded(input, i, limit)))) || (codePoint == 43 && plusIsSpace)) {
                final Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
                return out.readUtf8();
            }
        }
        return input.substring(pos, limit);
    }
    
    static void canonicalize(final Buffer out, final String input, final int pos, final int limit, final String encodeSet, final boolean alreadyEncoded, final boolean strict, final boolean plusIsSpace, final boolean asciiOnly, @Nullable final Charset charset) {
        Buffer encodedCharBuffer = null;
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (alreadyEncoded) {
                if (codePoint == 9 || codePoint == 10 || codePoint == 12) {
                    continue;
                }
                if (codePoint == 13) {
                    continue;
                }
            }
            if (codePoint == 43 && plusIsSpace) {
                out.writeUtf8(alreadyEncoded ? "+" : "%2B");
            }
            else if (codePoint < 32 || codePoint == 127 || (codePoint >= 128 && asciiOnly) || encodeSet.indexOf(codePoint) != -1 || (codePoint == 37 && (!alreadyEncoded || (strict && !percentEncoded(input, i, limit))))) {
                if (encodedCharBuffer == null) {
                    encodedCharBuffer = new Buffer();
                }
                if (charset == null || charset.equals(StandardCharsets.UTF_8)) {
                    encodedCharBuffer.writeUtf8CodePoint(codePoint);
                }
                else {
                    encodedCharBuffer.writeString(input, i, i + Character.charCount(codePoint), charset);
                }
                while (!encodedCharBuffer.exhausted()) {
                    final int b = encodedCharBuffer.readByte() & 0xFF;
                    out.writeByte(37);
                    out.writeByte((int)HttpUrl.HEX_DIGITS[b >> 4 & 0xF]);
                    out.writeByte((int)HttpUrl.HEX_DIGITS[b & 0xF]);
                }
            }
            else {
                out.writeUtf8CodePoint(codePoint);
            }
        }
    }
    
    static String canonicalize(final String input, final String encodeSet, final boolean alreadyEncoded, final boolean strict, final boolean plusIsSpace, final boolean asciiOnly, @Nullable final Charset charset) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
    }
    
    static String canonicalize(final String input, final String encodeSet, final boolean alreadyEncoded, final boolean strict, final boolean plusIsSpace, final boolean asciiOnly) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, null);
    }
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
    
    public static final class Builder
    {
        @Nullable
        String scheme;
        String encodedUsername;
        String encodedPassword;
        @Nullable
        String host;
        int port;
        final List<String> encodedPathSegments;
        @Nullable
        List<String> encodedQueryNamesAndValues;
        @Nullable
        String encodedFragment;
        static final String INVALID_HOST = "Invalid URL host";
        
        public Builder() {
            this.encodedUsername = "";
            this.encodedPassword = "";
            this.port = -1;
            (this.encodedPathSegments = new ArrayList<String>()).add("");
        }
        
        public Builder scheme(final String scheme) {
            if (scheme == null) {
                throw new NullPointerException("scheme == null");
            }
            if (scheme.equalsIgnoreCase("http")) {
                this.scheme = "http";
            }
            else {
                if (!scheme.equalsIgnoreCase("https")) {
                    throw new IllegalArgumentException("unexpected scheme: " + scheme);
                }
                this.scheme = "https";
            }
            return this;
        }
        
        public Builder username(final String username) {
            if (username == null) {
                throw new NullPointerException("username == null");
            }
            this.encodedUsername = HttpUrl.canonicalize(username, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
            return this;
        }
        
        public Builder encodedUsername(final String encodedUsername) {
            if (encodedUsername == null) {
                throw new NullPointerException("encodedUsername == null");
            }
            this.encodedUsername = HttpUrl.canonicalize(encodedUsername, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
            return this;
        }
        
        public Builder password(final String password) {
            if (password == null) {
                throw new NullPointerException("password == null");
            }
            this.encodedPassword = HttpUrl.canonicalize(password, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
            return this;
        }
        
        public Builder encodedPassword(final String encodedPassword) {
            if (encodedPassword == null) {
                throw new NullPointerException("encodedPassword == null");
            }
            this.encodedPassword = HttpUrl.canonicalize(encodedPassword, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
            return this;
        }
        
        public Builder host(final String host) {
            if (host == null) {
                throw new NullPointerException("host == null");
            }
            final String encoded = canonicalizeHost(host, 0, host.length());
            if (encoded == null) {
                throw new IllegalArgumentException("unexpected host: " + host);
            }
            this.host = encoded;
            return this;
        }
        
        public Builder port(final int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("unexpected port: " + port);
            }
            this.port = port;
            return this;
        }
        
        int effectivePort() {
            return (this.port != -1) ? this.port : HttpUrl.defaultPort(this.scheme);
        }
        
        public Builder addPathSegment(final String pathSegment) {
            if (pathSegment == null) {
                throw new NullPointerException("pathSegment == null");
            }
            this.push(pathSegment, 0, pathSegment.length(), false, false);
            return this;
        }
        
        public Builder addPathSegments(final String pathSegments) {
            if (pathSegments == null) {
                throw new NullPointerException("pathSegments == null");
            }
            return this.addPathSegments(pathSegments, false);
        }
        
        public Builder addEncodedPathSegment(final String encodedPathSegment) {
            if (encodedPathSegment == null) {
                throw new NullPointerException("encodedPathSegment == null");
            }
            this.push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
            return this;
        }
        
        public Builder addEncodedPathSegments(final String encodedPathSegments) {
            if (encodedPathSegments == null) {
                throw new NullPointerException("encodedPathSegments == null");
            }
            return this.addPathSegments(encodedPathSegments, true);
        }
        
        private Builder addPathSegments(final String pathSegments, final boolean alreadyEncoded) {
            int offset = 0;
            do {
                final int segmentEnd = Util.delimiterOffset(pathSegments, offset, pathSegments.length(), "/\\");
                final boolean addTrailingSlash = segmentEnd < pathSegments.length();
                this.push(pathSegments, offset, segmentEnd, addTrailingSlash, alreadyEncoded);
                offset = segmentEnd + 1;
            } while (offset <= pathSegments.length());
            return this;
        }
        
        public Builder setPathSegment(final int index, final String pathSegment) {
            if (pathSegment == null) {
                throw new NullPointerException("pathSegment == null");
            }
            final String canonicalPathSegment = HttpUrl.canonicalize(pathSegment, 0, pathSegment.length(), " \"<>^`{}|/\\?#", false, false, false, true, null);
            if (this.isDot(canonicalPathSegment) || this.isDotDot(canonicalPathSegment)) {
                throw new IllegalArgumentException("unexpected path segment: " + pathSegment);
            }
            this.encodedPathSegments.set(index, canonicalPathSegment);
            return this;
        }
        
        public Builder setEncodedPathSegment(final int index, final String encodedPathSegment) {
            if (encodedPathSegment == null) {
                throw new NullPointerException("encodedPathSegment == null");
            }
            final String canonicalPathSegment = HttpUrl.canonicalize(encodedPathSegment, 0, encodedPathSegment.length(), " \"<>^`{}|/\\?#", true, false, false, true, null);
            this.encodedPathSegments.set(index, canonicalPathSegment);
            if (this.isDot(canonicalPathSegment) || this.isDotDot(canonicalPathSegment)) {
                throw new IllegalArgumentException("unexpected path segment: " + encodedPathSegment);
            }
            return this;
        }
        
        public Builder removePathSegment(final int index) {
            this.encodedPathSegments.remove(index);
            if (this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.add("");
            }
            return this;
        }
        
        public Builder encodedPath(final String encodedPath) {
            if (encodedPath == null) {
                throw new NullPointerException("encodedPath == null");
            }
            if (!encodedPath.startsWith("/")) {
                throw new IllegalArgumentException("unexpected encodedPath: " + encodedPath);
            }
            this.resolvePath(encodedPath, 0, encodedPath.length());
            return this;
        }
        
        public Builder query(@Nullable final String query) {
            this.encodedQueryNamesAndValues = ((query != null) ? HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(query, " \"'<>#", false, false, true, true)) : null);
            return this;
        }
        
        public Builder encodedQuery(@Nullable final String encodedQuery) {
            this.encodedQueryNamesAndValues = ((encodedQuery != null) ? HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(encodedQuery, " \"'<>#", true, false, true, true)) : null);
            return this;
        }
        
        public Builder addQueryParameter(final String name, @Nullable final String value) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            if (this.encodedQueryNamesAndValues == null) {
                this.encodedQueryNamesAndValues = new ArrayList<String>();
            }
            this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(name, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, true));
            this.encodedQueryNamesAndValues.add((value != null) ? HttpUrl.canonicalize(value, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, true) : null);
            return this;
        }
        
        public Builder addEncodedQueryParameter(final String encodedName, @Nullable final String encodedValue) {
            if (encodedName == null) {
                throw new NullPointerException("encodedName == null");
            }
            if (this.encodedQueryNamesAndValues == null) {
                this.encodedQueryNamesAndValues = new ArrayList<String>();
            }
            this.encodedQueryNamesAndValues.add(HttpUrl.canonicalize(encodedName, " \"'<>#&=", true, false, true, true));
            this.encodedQueryNamesAndValues.add((encodedValue != null) ? HttpUrl.canonicalize(encodedValue, " \"'<>#&=", true, false, true, true) : null);
            return this;
        }
        
        public Builder setQueryParameter(final String name, @Nullable final String value) {
            this.removeAllQueryParameters(name);
            this.addQueryParameter(name, value);
            return this;
        }
        
        public Builder setEncodedQueryParameter(final String encodedName, @Nullable final String encodedValue) {
            this.removeAllEncodedQueryParameters(encodedName);
            this.addEncodedQueryParameter(encodedName, encodedValue);
            return this;
        }
        
        public Builder removeAllQueryParameters(final String name) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            if (this.encodedQueryNamesAndValues == null) {
                return this;
            }
            final String nameToRemove = HttpUrl.canonicalize(name, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, true);
            this.removeAllCanonicalQueryParameters(nameToRemove);
            return this;
        }
        
        public Builder removeAllEncodedQueryParameters(final String encodedName) {
            if (encodedName == null) {
                throw new NullPointerException("encodedName == null");
            }
            if (this.encodedQueryNamesAndValues == null) {
                return this;
            }
            this.removeAllCanonicalQueryParameters(HttpUrl.canonicalize(encodedName, " \"'<>#&=", true, false, true, true));
            return this;
        }
        
        private void removeAllCanonicalQueryParameters(final String canonicalName) {
            for (int i = this.encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
                if (canonicalName.equals(this.encodedQueryNamesAndValues.get(i))) {
                    this.encodedQueryNamesAndValues.remove(i + 1);
                    this.encodedQueryNamesAndValues.remove(i);
                    if (this.encodedQueryNamesAndValues.isEmpty()) {
                        this.encodedQueryNamesAndValues = null;
                        return;
                    }
                }
            }
        }
        
        public Builder fragment(@Nullable final String fragment) {
            this.encodedFragment = ((fragment != null) ? HttpUrl.canonicalize(fragment, "", false, false, false, false) : null);
            return this;
        }
        
        public Builder encodedFragment(@Nullable final String encodedFragment) {
            this.encodedFragment = ((encodedFragment != null) ? HttpUrl.canonicalize(encodedFragment, "", true, false, false, false) : null);
            return this;
        }
        
        Builder reencodeForUri() {
            for (int i = 0, size = this.encodedPathSegments.size(); i < size; ++i) {
                final String pathSegment = this.encodedPathSegments.get(i);
                this.encodedPathSegments.set(i, HttpUrl.canonicalize(pathSegment, "[]", true, true, false, true));
            }
            if (this.encodedQueryNamesAndValues != null) {
                for (int i = 0, size = this.encodedQueryNamesAndValues.size(); i < size; ++i) {
                    final String component = this.encodedQueryNamesAndValues.get(i);
                    if (component != null) {
                        this.encodedQueryNamesAndValues.set(i, HttpUrl.canonicalize(component, "\\^`{|}", true, true, true, true));
                    }
                }
            }
            if (this.encodedFragment != null) {
                this.encodedFragment = HttpUrl.canonicalize(this.encodedFragment, " \"#<>\\^`{|}", true, true, false, false);
            }
            return this;
        }
        
        public HttpUrl build() {
            if (this.scheme == null) {
                throw new IllegalStateException("scheme == null");
            }
            if (this.host == null) {
                throw new IllegalStateException("host == null");
            }
            return new HttpUrl(this);
        }
        
        @Override
        public String toString() {
            final StringBuilder result = new StringBuilder();
            if (this.scheme != null) {
                result.append(this.scheme);
                result.append("://");
            }
            else {
                result.append("//");
            }
            if (!this.encodedUsername.isEmpty() || !this.encodedPassword.isEmpty()) {
                result.append(this.encodedUsername);
                if (!this.encodedPassword.isEmpty()) {
                    result.append(':');
                    result.append(this.encodedPassword);
                }
                result.append('@');
            }
            if (this.host != null) {
                if (this.host.indexOf(58) != -1) {
                    result.append('[');
                    result.append(this.host);
                    result.append(']');
                }
                else {
                    result.append(this.host);
                }
            }
            if (this.port != -1 || this.scheme != null) {
                final int effectivePort = this.effectivePort();
                if (this.scheme == null || effectivePort != HttpUrl.defaultPort(this.scheme)) {
                    result.append(':');
                    result.append(effectivePort);
                }
            }
            HttpUrl.pathSegmentsToString(result, this.encodedPathSegments);
            if (this.encodedQueryNamesAndValues != null) {
                result.append('?');
                HttpUrl.namesAndValuesToQueryString(result, this.encodedQueryNamesAndValues);
            }
            if (this.encodedFragment != null) {
                result.append('#');
                result.append(this.encodedFragment);
            }
            return result.toString();
        }
        
        Builder parse(@Nullable final HttpUrl base, final String input) {
            int pos = Util.skipLeadingAsciiWhitespace(input, 0, input.length());
            final int limit = Util.skipTrailingAsciiWhitespace(input, pos, input.length());
            final int schemeDelimiterOffset = schemeDelimiterOffset(input, pos, limit);
            if (schemeDelimiterOffset != -1) {
                if (input.regionMatches(true, pos, "https:", 0, 6)) {
                    this.scheme = "https";
                    pos += "https:".length();
                }
                else {
                    if (!input.regionMatches(true, pos, "http:", 0, 5)) {
                        throw new IllegalArgumentException("Expected URL scheme 'http' or 'https' but was '" + input.substring(0, schemeDelimiterOffset) + "'");
                    }
                    this.scheme = "http";
                    pos += "http:".length();
                }
            }
            else {
                if (base == null) {
                    throw new IllegalArgumentException("Expected URL scheme 'http' or 'https' but no colon was found");
                }
                this.scheme = base.scheme;
            }
            boolean hasUsername = false;
            boolean hasPassword = false;
            final int slashCount = slashCount(input, pos, limit);
            if (slashCount >= 2 || base == null || !base.scheme.equals(this.scheme)) {
                pos += slashCount;
                int componentDelimiterOffset = 0;
            Label_0457:
                while (true) {
                    componentDelimiterOffset = Util.delimiterOffset(input, pos, limit, "@/\\?#");
                    final int c = (componentDelimiterOffset != limit) ? input.charAt(componentDelimiterOffset) : -1;
                    switch (c) {
                        case 64: {
                            if (!hasPassword) {
                                final int passwordColonOffset = Util.delimiterOffset(input, pos, componentDelimiterOffset, ':');
                                final String canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null);
                                this.encodedUsername = (hasUsername ? (this.encodedUsername + "%40" + canonicalUsername) : canonicalUsername);
                                if (passwordColonOffset != componentDelimiterOffset) {
                                    hasPassword = true;
                                    this.encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null);
                                }
                                hasUsername = true;
                            }
                            else {
                                this.encodedPassword = this.encodedPassword + "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null);
                            }
                            pos = componentDelimiterOffset + 1;
                            continue;
                        }
                        case -1:
                        case 35:
                        case 47:
                        case 63:
                        case 92: {
                            break Label_0457;
                        }
                    }
                }
                final int portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
                if (portColonOffset + 1 < componentDelimiterOffset) {
                    this.host = canonicalizeHost(input, pos, portColonOffset);
                    this.port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
                    if (this.port == -1) {
                        throw new IllegalArgumentException("Invalid URL port: \"" + input.substring(portColonOffset + 1, componentDelimiterOffset) + '\"');
                    }
                }
                else {
                    this.host = canonicalizeHost(input, pos, portColonOffset);
                    this.port = HttpUrl.defaultPort(this.scheme);
                }
                if (this.host == null) {
                    throw new IllegalArgumentException("Invalid URL host: \"" + input.substring(pos, portColonOffset) + '\"');
                }
                pos = componentDelimiterOffset;
            }
            else {
                this.encodedUsername = base.encodedUsername();
                this.encodedPassword = base.encodedPassword();
                this.host = base.host;
                this.port = base.port;
                this.encodedPathSegments.clear();
                this.encodedPathSegments.addAll(base.encodedPathSegments());
                if (pos == limit || input.charAt(pos) == '#') {
                    this.encodedQuery(base.encodedQuery());
                }
            }
            final int pathDelimiterOffset = Util.delimiterOffset(input, pos, limit, "?#");
            this.resolvePath(input, pos, pathDelimiterOffset);
            pos = pathDelimiterOffset;
            if (pos < limit && input.charAt(pos) == '?') {
                final int queryDelimiterOffset = Util.delimiterOffset(input, pos, limit, '#');
                this.encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, false, true, true, null));
                pos = queryDelimiterOffset;
            }
            if (pos < limit && input.charAt(pos) == '#') {
                this.encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false, false, null);
            }
            return this;
        }
        
        private void resolvePath(final String input, int pos, final int limit) {
            if (pos == limit) {
                return;
            }
            final char c = input.charAt(pos);
            if (c == '/' || c == '\\') {
                this.encodedPathSegments.clear();
                this.encodedPathSegments.add("");
                ++pos;
            }
            else {
                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
            }
            for (int i = pos; i < limit; ++i) {
                final int pathSegmentDelimiterOffset = Util.delimiterOffset(input, i, limit, "/\\");
                final boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
                this.push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
                i = pathSegmentDelimiterOffset;
                if (segmentHasTrailingSlash) {}
            }
        }
        
        private void push(final String input, final int pos, final int limit, final boolean addTrailingSlash, final boolean alreadyEncoded) {
            final String segment = HttpUrl.canonicalize(input, pos, limit, " \"<>^`{}|/\\?#", alreadyEncoded, false, false, true, null);
            if (this.isDot(segment)) {
                return;
            }
            if (this.isDotDot(segment)) {
                this.pop();
                return;
            }
            if (this.encodedPathSegments.get(this.encodedPathSegments.size() - 1).isEmpty()) {
                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, segment);
            }
            else {
                this.encodedPathSegments.add(segment);
            }
            if (addTrailingSlash) {
                this.encodedPathSegments.add("");
            }
        }
        
        private boolean isDot(final String input) {
            return input.equals(".") || input.equalsIgnoreCase("%2e");
        }
        
        private boolean isDotDot(final String input) {
            return input.equals("..") || input.equalsIgnoreCase("%2e.") || input.equalsIgnoreCase(".%2e") || input.equalsIgnoreCase("%2e%2e");
        }
        
        private void pop() {
            final String removed = this.encodedPathSegments.remove(this.encodedPathSegments.size() - 1);
            if (removed.isEmpty() && !this.encodedPathSegments.isEmpty()) {
                this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
            }
            else {
                this.encodedPathSegments.add("");
            }
        }
        
        private static int schemeDelimiterOffset(final String input, final int pos, final int limit) {
            if (limit - pos < 2) {
                return -1;
            }
            final char c0 = input.charAt(pos);
            if ((c0 < 'a' || c0 > 'z') && (c0 < 'A' || c0 > 'Z')) {
                return -1;
            }
            int i = pos + 1;
            while (i < limit) {
                final char c2 = input.charAt(i);
                if ((c2 < 'a' || c2 > 'z') && (c2 < 'A' || c2 > 'Z') && (c2 < '0' || c2 > '9') && c2 != '+' && c2 != '-' && c2 != '.') {
                    if (c2 == ':') {
                        return i;
                    }
                    return -1;
                }
                else {
                    ++i;
                }
            }
            return -1;
        }
        
        private static int slashCount(final String input, int pos, final int limit) {
            int slashCount = 0;
            while (pos < limit) {
                final char c = input.charAt(pos);
                if (c != '\\' && c != '/') {
                    break;
                }
                ++slashCount;
                ++pos;
            }
            return slashCount;
        }
        
        private static int portColonOffset(final String input, final int pos, final int limit) {
            for (int i = pos; i < limit; ++i) {
                switch (input.charAt(i)) {
                    case '[': {
                        while (++i < limit) {
                            if (input.charAt(i) == ']') {
                                break;
                            }
                        }
                        break;
                    }
                    case ':': {
                        return i;
                    }
                }
            }
            return limit;
        }
        
        @Nullable
        private static String canonicalizeHost(final String input, final int pos, final int limit) {
            final String percentDecoded = HttpUrl.percentDecode(input, pos, limit, false);
            return Util.canonicalizeHost(percentDecoded);
        }
        
        private static int parsePort(final String input, final int pos, final int limit) {
            try {
                final String portString = HttpUrl.canonicalize(input, pos, limit, "", false, false, false, true, null);
                final int i = Integer.parseInt(portString);
                if (i > 0 && i <= 65535) {
                    return i;
                }
                return -1;
            }
            catch (NumberFormatException e) {
                return -1;
            }
        }
    }
}
