//Raddon On Top!

package okhttp3;

import okhttp3.internal.*;
import javax.annotation.*;
import okhttp3.internal.publicsuffix.*;
import java.util.regex.*;
import java.util.*;
import okhttp3.internal.http.*;

public final class Cookie
{
    private static final Pattern YEAR_PATTERN;
    private static final Pattern MONTH_PATTERN;
    private static final Pattern DAY_OF_MONTH_PATTERN;
    private static final Pattern TIME_PATTERN;
    private final String name;
    private final String value;
    private final long expiresAt;
    private final String domain;
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;
    private final boolean persistent;
    private final boolean hostOnly;
    
    private Cookie(final String name, final String value, final long expiresAt, final String domain, final String path, final boolean secure, final boolean httpOnly, final boolean hostOnly, final boolean persistent) {
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
        this.persistent = persistent;
    }
    
    Cookie(final Builder builder) {
        if (builder.name == null) {
            throw new NullPointerException("builder.name == null");
        }
        if (builder.value == null) {
            throw new NullPointerException("builder.value == null");
        }
        if (builder.domain == null) {
            throw new NullPointerException("builder.domain == null");
        }
        this.name = builder.name;
        this.value = builder.value;
        this.expiresAt = builder.expiresAt;
        this.domain = builder.domain;
        this.path = builder.path;
        this.secure = builder.secure;
        this.httpOnly = builder.httpOnly;
        this.persistent = builder.persistent;
        this.hostOnly = builder.hostOnly;
    }
    
    public String name() {
        return this.name;
    }
    
    public String value() {
        return this.value;
    }
    
    public boolean persistent() {
        return this.persistent;
    }
    
    public long expiresAt() {
        return this.expiresAt;
    }
    
    public boolean hostOnly() {
        return this.hostOnly;
    }
    
    public String domain() {
        return this.domain;
    }
    
    public String path() {
        return this.path;
    }
    
    public boolean httpOnly() {
        return this.httpOnly;
    }
    
    public boolean secure() {
        return this.secure;
    }
    
    public boolean matches(final HttpUrl url) {
        final boolean domainMatch = this.hostOnly ? url.host().equals(this.domain) : domainMatch(url.host(), this.domain);
        return domainMatch && pathMatch(url, this.path) && (!this.secure || url.isHttps());
    }
    
    private static boolean domainMatch(final String urlHost, final String domain) {
        return urlHost.equals(domain) || (urlHost.endsWith(domain) && urlHost.charAt(urlHost.length() - domain.length() - 1) == '.' && !Util.verifyAsIpAddress(urlHost));
    }
    
    private static boolean pathMatch(final HttpUrl url, final String path) {
        final String urlPath = url.encodedPath();
        if (urlPath.equals(path)) {
            return true;
        }
        if (urlPath.startsWith(path)) {
            if (path.endsWith("/")) {
                return true;
            }
            if (urlPath.charAt(path.length()) == '/') {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    public static Cookie parse(final HttpUrl url, final String setCookie) {
        return parse(System.currentTimeMillis(), url, setCookie);
    }
    
    @Nullable
    static Cookie parse(final long currentTimeMillis, final HttpUrl url, final String setCookie) {
        int pos = 0;
        final int limit = setCookie.length();
        final int cookiePairEnd = Util.delimiterOffset(setCookie, pos, limit, ';');
        final int pairEqualsSign = Util.delimiterOffset(setCookie, pos, cookiePairEnd, '=');
        if (pairEqualsSign == cookiePairEnd) {
            return null;
        }
        final String cookieName = Util.trimSubstring(setCookie, pos, pairEqualsSign);
        if (cookieName.isEmpty() || Util.indexOfControlOrNonAscii(cookieName) != -1) {
            return null;
        }
        final String cookieValue = Util.trimSubstring(setCookie, pairEqualsSign + 1, cookiePairEnd);
        if (Util.indexOfControlOrNonAscii(cookieValue) != -1) {
            return null;
        }
        long expiresAt = 253402300799999L;
        long deltaSeconds = -1L;
        String domain = null;
        String path = null;
        boolean secureOnly = false;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;
        int attributePairEnd;
        for (pos = cookiePairEnd + 1; pos < limit; pos = attributePairEnd + 1) {
            attributePairEnd = Util.delimiterOffset(setCookie, pos, limit, ';');
            final int attributeEqualsSign = Util.delimiterOffset(setCookie, pos, attributePairEnd, '=');
            final String attributeName = Util.trimSubstring(setCookie, pos, attributeEqualsSign);
            final String attributeValue = (attributeEqualsSign < attributePairEnd) ? Util.trimSubstring(setCookie, attributeEqualsSign + 1, attributePairEnd) : "";
            if (attributeName.equalsIgnoreCase("expires")) {
                try {
                    expiresAt = parseExpires(attributeValue, 0, attributeValue.length());
                    persistent = true;
                }
                catch (IllegalArgumentException ex) {}
            }
            else if (attributeName.equalsIgnoreCase("max-age")) {
                try {
                    deltaSeconds = parseMaxAge(attributeValue);
                    persistent = true;
                }
                catch (NumberFormatException ex2) {}
            }
            else if (attributeName.equalsIgnoreCase("domain")) {
                try {
                    domain = parseDomain(attributeValue);
                    hostOnly = false;
                }
                catch (IllegalArgumentException ex3) {}
            }
            else if (attributeName.equalsIgnoreCase("path")) {
                path = attributeValue;
            }
            else if (attributeName.equalsIgnoreCase("secure")) {
                secureOnly = true;
            }
            else if (attributeName.equalsIgnoreCase("httponly")) {
                httpOnly = true;
            }
        }
        if (deltaSeconds == Long.MIN_VALUE) {
            expiresAt = Long.MIN_VALUE;
        }
        else if (deltaSeconds != -1L) {
            final long deltaMilliseconds = (deltaSeconds <= 9223372036854775L) ? (deltaSeconds * 1000L) : Long.MAX_VALUE;
            expiresAt = currentTimeMillis + deltaMilliseconds;
            if (expiresAt < currentTimeMillis || expiresAt > 253402300799999L) {
                expiresAt = 253402300799999L;
            }
        }
        final String urlHost = url.host();
        if (domain == null) {
            domain = urlHost;
        }
        else if (!domainMatch(urlHost, domain)) {
            return null;
        }
        if (urlHost.length() != domain.length() && PublicSuffixDatabase.get().getEffectiveTldPlusOne(domain) == null) {
            return null;
        }
        if (path == null || !path.startsWith("/")) {
            final String encodedPath = url.encodedPath();
            final int lastSlash = encodedPath.lastIndexOf(47);
            path = ((lastSlash != 0) ? encodedPath.substring(0, lastSlash) : "/");
        }
        return new Cookie(cookieName, cookieValue, expiresAt, domain, path, secureOnly, httpOnly, hostOnly, persistent);
    }
    
    private static long parseExpires(final String s, int pos, final int limit) {
        pos = dateCharacterOffset(s, pos, limit, false);
        int hour = -1;
        int minute = -1;
        int second = -1;
        int dayOfMonth = -1;
        int month = -1;
        int year = -1;
        final Matcher matcher = Cookie.TIME_PATTERN.matcher(s);
        while (pos < limit) {
            final int end = dateCharacterOffset(s, pos + 1, limit, true);
            matcher.region(pos, end);
            if (hour == -1 && matcher.usePattern(Cookie.TIME_PATTERN).matches()) {
                hour = Integer.parseInt(matcher.group(1));
                minute = Integer.parseInt(matcher.group(2));
                second = Integer.parseInt(matcher.group(3));
            }
            else if (dayOfMonth == -1 && matcher.usePattern(Cookie.DAY_OF_MONTH_PATTERN).matches()) {
                dayOfMonth = Integer.parseInt(matcher.group(1));
            }
            else if (month == -1 && matcher.usePattern(Cookie.MONTH_PATTERN).matches()) {
                final String monthString = matcher.group(1).toLowerCase(Locale.US);
                month = Cookie.MONTH_PATTERN.pattern().indexOf(monthString) / 4;
            }
            else if (year == -1 && matcher.usePattern(Cookie.YEAR_PATTERN).matches()) {
                year = Integer.parseInt(matcher.group(1));
            }
            pos = dateCharacterOffset(s, end + 1, limit, false);
        }
        if (year >= 70 && year <= 99) {
            year += 1900;
        }
        if (year >= 0 && year <= 69) {
            year += 2000;
        }
        if (year < 1601) {
            throw new IllegalArgumentException();
        }
        if (month == -1) {
            throw new IllegalArgumentException();
        }
        if (dayOfMonth < 1 || dayOfMonth > 31) {
            throw new IllegalArgumentException();
        }
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException();
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException();
        }
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException();
        }
        final Calendar calendar = new GregorianCalendar(Util.UTC);
        calendar.setLenient(false);
        calendar.set(1, year);
        calendar.set(2, month - 1);
        calendar.set(5, dayOfMonth);
        calendar.set(11, hour);
        calendar.set(12, minute);
        calendar.set(13, second);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }
    
    private static int dateCharacterOffset(final String input, final int pos, final int limit, final boolean invert) {
        for (int i = pos; i < limit; ++i) {
            final int c = input.charAt(i);
            final boolean dateCharacter = (c < 32 && c != 9) || c >= 127 || (c >= 48 && c <= 57) || (c >= 97 && c <= 122) || (c >= 65 && c <= 90) || c == 58;
            if (dateCharacter == !invert) {
                return i;
            }
        }
        return limit;
    }
    
    private static long parseMaxAge(final String s) {
        try {
            final long parsed = Long.parseLong(s);
            return (parsed <= 0L) ? Long.MIN_VALUE : parsed;
        }
        catch (NumberFormatException e) {
            if (s.matches("-?\\d+")) {
                return s.startsWith("-") ? Long.MIN_VALUE : Long.MAX_VALUE;
            }
            throw e;
        }
    }
    
    private static String parseDomain(String s) {
        if (s.endsWith(".")) {
            throw new IllegalArgumentException();
        }
        if (s.startsWith(".")) {
            s = s.substring(1);
        }
        final String canonicalDomain = Util.canonicalizeHost(s);
        if (canonicalDomain == null) {
            throw new IllegalArgumentException();
        }
        return canonicalDomain;
    }
    
    public static List<Cookie> parseAll(final HttpUrl url, final Headers headers) {
        final List<String> cookieStrings = headers.values("Set-Cookie");
        List<Cookie> cookies = null;
        for (int i = 0, size = cookieStrings.size(); i < size; ++i) {
            final Cookie cookie = parse(url, cookieStrings.get(i));
            if (cookie != null) {
                if (cookies == null) {
                    cookies = new ArrayList<Cookie>();
                }
                cookies.add(cookie);
            }
        }
        return (cookies != null) ? Collections.unmodifiableList((List<? extends Cookie>)cookies) : Collections.emptyList();
    }
    
    @Override
    public String toString() {
        return this.toString(false);
    }
    
    String toString(final boolean forObsoleteRfc2965) {
        final StringBuilder result = new StringBuilder();
        result.append(this.name);
        result.append('=');
        result.append(this.value);
        if (this.persistent) {
            if (this.expiresAt == Long.MIN_VALUE) {
                result.append("; max-age=0");
            }
            else {
                result.append("; expires=").append(HttpDate.format(new Date(this.expiresAt)));
            }
        }
        if (!this.hostOnly) {
            result.append("; domain=");
            if (forObsoleteRfc2965) {
                result.append(".");
            }
            result.append(this.domain);
        }
        result.append("; path=").append(this.path);
        if (this.secure) {
            result.append("; secure");
        }
        if (this.httpOnly) {
            result.append("; httponly");
        }
        return result.toString();
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        if (!(other instanceof Cookie)) {
            return false;
        }
        final Cookie that = (Cookie)other;
        return that.name.equals(this.name) && that.value.equals(this.value) && that.domain.equals(this.domain) && that.path.equals(this.path) && that.expiresAt == this.expiresAt && that.secure == this.secure && that.httpOnly == this.httpOnly && that.persistent == this.persistent && that.hostOnly == this.hostOnly;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + this.name.hashCode();
        hash = 31 * hash + this.value.hashCode();
        hash = 31 * hash + this.domain.hashCode();
        hash = 31 * hash + this.path.hashCode();
        hash = 31 * hash + (int)(this.expiresAt ^ this.expiresAt >>> 32);
        hash = 31 * hash + (this.secure ? 0 : 1);
        hash = 31 * hash + (this.httpOnly ? 0 : 1);
        hash = 31 * hash + (this.persistent ? 0 : 1);
        hash = 31 * hash + (this.hostOnly ? 0 : 1);
        return hash;
    }
    
    static {
        YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
        MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
        DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
        TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    }
    
    public static final class Builder
    {
        @Nullable
        String name;
        @Nullable
        String value;
        long expiresAt;
        @Nullable
        String domain;
        String path;
        boolean secure;
        boolean httpOnly;
        boolean persistent;
        boolean hostOnly;
        
        public Builder() {
            this.expiresAt = 253402300799999L;
            this.path = "/";
        }
        
        public Builder name(final String name) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            if (!name.trim().equals(name)) {
                throw new IllegalArgumentException("name is not trimmed");
            }
            this.name = name;
            return this;
        }
        
        public Builder value(final String value) {
            if (value == null) {
                throw new NullPointerException("value == null");
            }
            if (!value.trim().equals(value)) {
                throw new IllegalArgumentException("value is not trimmed");
            }
            this.value = value;
            return this;
        }
        
        public Builder expiresAt(long expiresAt) {
            if (expiresAt <= 0L) {
                expiresAt = Long.MIN_VALUE;
            }
            if (expiresAt > 253402300799999L) {
                expiresAt = 253402300799999L;
            }
            this.expiresAt = expiresAt;
            this.persistent = true;
            return this;
        }
        
        public Builder domain(final String domain) {
            return this.domain(domain, false);
        }
        
        public Builder hostOnlyDomain(final String domain) {
            return this.domain(domain, true);
        }
        
        private Builder domain(final String domain, final boolean hostOnly) {
            if (domain == null) {
                throw new NullPointerException("domain == null");
            }
            final String canonicalDomain = Util.canonicalizeHost(domain);
            if (canonicalDomain == null) {
                throw new IllegalArgumentException("unexpected domain: " + domain);
            }
            this.domain = canonicalDomain;
            this.hostOnly = hostOnly;
            return this;
        }
        
        public Builder path(final String path) {
            if (!path.startsWith("/")) {
                throw new IllegalArgumentException("path must start with '/'");
            }
            this.path = path;
            return this;
        }
        
        public Builder secure() {
            this.secure = true;
            return this;
        }
        
        public Builder httpOnly() {
            this.httpOnly = true;
            return this;
        }
        
        public Cookie build() {
            return new Cookie(this);
        }
    }
}
