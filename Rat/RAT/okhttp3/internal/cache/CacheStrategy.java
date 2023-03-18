//Raddon On Top!

package okhttp3.internal.cache;

import javax.annotation.*;
import java.util.*;
import okhttp3.internal.http.*;
import java.util.concurrent.*;
import okhttp3.internal.*;
import okhttp3.*;

public final class CacheStrategy
{
    @Nullable
    public final Request networkRequest;
    @Nullable
    public final Response cacheResponse;
    
    CacheStrategy(final Request networkRequest, final Response cacheResponse) {
        this.networkRequest = networkRequest;
        this.cacheResponse = cacheResponse;
    }
    
    public static boolean isCacheable(final Response response, final Request request) {
        switch (response.code()) {
            case 200:
            case 203:
            case 204:
            case 300:
            case 301:
            case 308:
            case 404:
            case 405:
            case 410:
            case 414:
            case 501: {
                return !response.cacheControl().noStore() && !request.cacheControl().noStore();
            }
            case 302:
            case 307: {
                if (response.header("Expires") != null || response.cacheControl().maxAgeSeconds() != -1 || response.cacheControl().isPublic()) {
                    return !response.cacheControl().noStore() && !request.cacheControl().noStore();
                }
                if (response.cacheControl().isPrivate()) {
                    return !response.cacheControl().noStore() && !request.cacheControl().noStore();
                }
                break;
            }
        }
        return false;
    }
    
    public static class Factory
    {
        final long nowMillis;
        final Request request;
        final Response cacheResponse;
        private Date servedDate;
        private String servedDateString;
        private Date lastModified;
        private String lastModifiedString;
        private Date expires;
        private long sentRequestMillis;
        private long receivedResponseMillis;
        private String etag;
        private int ageSeconds;
        
        public Factory(final long nowMillis, final Request request, final Response cacheResponse) {
            this.ageSeconds = -1;
            this.nowMillis = nowMillis;
            this.request = request;
            this.cacheResponse = cacheResponse;
            if (cacheResponse != null) {
                this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
                this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
                final Headers headers = cacheResponse.headers();
                for (int i = 0, size = headers.size(); i < size; ++i) {
                    final String fieldName = headers.name(i);
                    final String value = headers.value(i);
                    if ("Date".equalsIgnoreCase(fieldName)) {
                        this.servedDate = HttpDate.parse(value);
                        this.servedDateString = value;
                    }
                    else if ("Expires".equalsIgnoreCase(fieldName)) {
                        this.expires = HttpDate.parse(value);
                    }
                    else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
                        this.lastModified = HttpDate.parse(value);
                        this.lastModifiedString = value;
                    }
                    else if ("ETag".equalsIgnoreCase(fieldName)) {
                        this.etag = value;
                    }
                    else if ("Age".equalsIgnoreCase(fieldName)) {
                        this.ageSeconds = HttpHeaders.parseSeconds(value, -1);
                    }
                }
            }
        }
        
        public CacheStrategy get() {
            final CacheStrategy candidate = this.getCandidate();
            if (candidate.networkRequest != null && this.request.cacheControl().onlyIfCached()) {
                return new CacheStrategy(null, null);
            }
            return candidate;
        }
        
        private CacheStrategy getCandidate() {
            if (this.cacheResponse == null) {
                return new CacheStrategy(this.request, null);
            }
            if (this.request.isHttps() && this.cacheResponse.handshake() == null) {
                return new CacheStrategy(this.request, null);
            }
            if (!CacheStrategy.isCacheable(this.cacheResponse, this.request)) {
                return new CacheStrategy(this.request, null);
            }
            final CacheControl requestCaching = this.request.cacheControl();
            if (requestCaching.noCache() || hasConditions(this.request)) {
                return new CacheStrategy(this.request, null);
            }
            final CacheControl responseCaching = this.cacheResponse.cacheControl();
            final long ageMillis = this.cacheResponseAge();
            long freshMillis = this.computeFreshnessLifetime();
            if (requestCaching.maxAgeSeconds() != -1) {
                freshMillis = Math.min(freshMillis, TimeUnit.SECONDS.toMillis(requestCaching.maxAgeSeconds()));
            }
            long minFreshMillis = 0L;
            if (requestCaching.minFreshSeconds() != -1) {
                minFreshMillis = TimeUnit.SECONDS.toMillis(requestCaching.minFreshSeconds());
            }
            long maxStaleMillis = 0L;
            if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
                maxStaleMillis = TimeUnit.SECONDS.toMillis(requestCaching.maxStaleSeconds());
            }
            if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
                final Response.Builder builder = this.cacheResponse.newBuilder();
                if (ageMillis + minFreshMillis >= freshMillis) {
                    builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
                }
                final long oneDayMillis = 86400000L;
                if (ageMillis > oneDayMillis && this.isFreshnessLifetimeHeuristic()) {
                    builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
                }
                return new CacheStrategy(null, builder.build());
            }
            String conditionName;
            String conditionValue;
            if (this.etag != null) {
                conditionName = "If-None-Match";
                conditionValue = this.etag;
            }
            else if (this.lastModified != null) {
                conditionName = "If-Modified-Since";
                conditionValue = this.lastModifiedString;
            }
            else {
                if (this.servedDate == null) {
                    return new CacheStrategy(this.request, null);
                }
                conditionName = "If-Modified-Since";
                conditionValue = this.servedDateString;
            }
            final Headers.Builder conditionalRequestHeaders = this.request.headers().newBuilder();
            Internal.instance.addLenient(conditionalRequestHeaders, conditionName, conditionValue);
            final Request conditionalRequest = this.request.newBuilder().headers(conditionalRequestHeaders.build()).build();
            return new CacheStrategy(conditionalRequest, this.cacheResponse);
        }
        
        private long computeFreshnessLifetime() {
            final CacheControl responseCaching = this.cacheResponse.cacheControl();
            if (responseCaching.maxAgeSeconds() != -1) {
                return TimeUnit.SECONDS.toMillis(responseCaching.maxAgeSeconds());
            }
            if (this.expires != null) {
                final long servedMillis = (this.servedDate != null) ? this.servedDate.getTime() : this.receivedResponseMillis;
                final long delta = this.expires.getTime() - servedMillis;
                return (delta > 0L) ? delta : 0L;
            }
            if (this.lastModified != null && this.cacheResponse.request().url().query() == null) {
                final long servedMillis = (this.servedDate != null) ? this.servedDate.getTime() : this.sentRequestMillis;
                final long delta = servedMillis - this.lastModified.getTime();
                return (delta > 0L) ? (delta / 10L) : 0L;
            }
            return 0L;
        }
        
        private long cacheResponseAge() {
            final long apparentReceivedAge = (this.servedDate != null) ? Math.max(0L, this.receivedResponseMillis - this.servedDate.getTime()) : 0L;
            final long receivedAge = (this.ageSeconds != -1) ? Math.max(apparentReceivedAge, TimeUnit.SECONDS.toMillis(this.ageSeconds)) : apparentReceivedAge;
            final long responseDuration = this.receivedResponseMillis - this.sentRequestMillis;
            final long residentDuration = this.nowMillis - this.receivedResponseMillis;
            return receivedAge + responseDuration + residentDuration;
        }
        
        private boolean isFreshnessLifetimeHeuristic() {
            return this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
        }
        
        private static boolean hasConditions(final Request request) {
            return request.header("If-Modified-Since") != null || request.header("If-None-Match") != null;
        }
    }
}
