//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import okhttp3.internal.*;
import java.util.*;
import java.net.*;
import okhttp3.internal.http.*;

public final class Request
{
    final HttpUrl url;
    final String method;
    final Headers headers;
    @Nullable
    final RequestBody body;
    final Map<Class<?>, Object> tags;
    @Nullable
    private volatile CacheControl cacheControl;
    
    Request(final Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.body = builder.body;
        this.tags = (Map<Class<?>, Object>)Util.immutableMap((Map)builder.tags);
    }
    
    public HttpUrl url() {
        return this.url;
    }
    
    public String method() {
        return this.method;
    }
    
    public Headers headers() {
        return this.headers;
    }
    
    @Nullable
    public String header(final String name) {
        return this.headers.get(name);
    }
    
    public List<String> headers(final String name) {
        return (List<String>)this.headers.values(name);
    }
    
    @Nullable
    public RequestBody body() {
        return this.body;
    }
    
    @Nullable
    public Object tag() {
        return this.tag((Class<?>)Object.class);
    }
    
    @Nullable
    public <T> T tag(final Class<? extends T> type) {
        return (T)type.cast(this.tags.get(type));
    }
    
    public Builder newBuilder() {
        return new Builder(this);
    }
    
    public CacheControl cacheControl() {
        final CacheControl result = this.cacheControl;
        return (result != null) ? result : (this.cacheControl = CacheControl.parse(this.headers));
    }
    
    public boolean isHttps() {
        return this.url.isHttps();
    }
    
    @Override
    public String toString() {
        return "Request{method=" + this.method + ", url=" + this.url + ", tags=" + this.tags + '}';
    }
    
    public static class Builder
    {
        @Nullable
        HttpUrl url;
        String method;
        Headers.Builder headers;
        @Nullable
        RequestBody body;
        Map<Class<?>, Object> tags;
        
        public Builder() {
            this.tags = Collections.emptyMap();
            this.method = "GET";
            this.headers = new Headers.Builder();
        }
        
        Builder(final Request request) {
            this.tags = Collections.emptyMap();
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
            this.tags = (request.tags.isEmpty() ? Collections.emptyMap() : new LinkedHashMap<Class<?>, Object>(request.tags));
            this.headers = request.headers.newBuilder();
        }
        
        public Builder url(final HttpUrl url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            this.url = url;
            return this;
        }
        
        public Builder url(String url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            if (url.regionMatches(true, 0, "ws:", 0, 3)) {
                url = "http:" + url.substring(3);
            }
            else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
                url = "https:" + url.substring(4);
            }
            return this.url(HttpUrl.get(url));
        }
        
        public Builder url(final URL url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            return this.url(HttpUrl.get(url.toString()));
        }
        
        public Builder header(final String name, final String value) {
            this.headers.set(name, value);
            return this;
        }
        
        public Builder addHeader(final String name, final String value) {
            this.headers.add(name, value);
            return this;
        }
        
        public Builder removeHeader(final String name) {
            this.headers.removeAll(name);
            return this;
        }
        
        public Builder headers(final Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }
        
        public Builder cacheControl(final CacheControl cacheControl) {
            final String value = cacheControl.toString();
            if (value.isEmpty()) {
                return this.removeHeader("Cache-Control");
            }
            return this.header("Cache-Control", value);
        }
        
        public Builder get() {
            return this.method("GET", null);
        }
        
        public Builder head() {
            return this.method("HEAD", null);
        }
        
        public Builder post(final RequestBody body) {
            return this.method("POST", body);
        }
        
        public Builder delete(@Nullable final RequestBody body) {
            return this.method("DELETE", body);
        }
        
        public Builder delete() {
            return this.delete(Util.EMPTY_REQUEST);
        }
        
        public Builder put(final RequestBody body) {
            return this.method("PUT", body);
        }
        
        public Builder patch(final RequestBody body) {
            return this.method("PATCH", body);
        }
        
        public Builder method(final String method, @Nullable final RequestBody body) {
            if (method == null) {
                throw new NullPointerException("method == null");
            }
            if (method.length() == 0) {
                throw new IllegalArgumentException("method.length() == 0");
            }
            if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must not have a request body.");
            }
            if (body == null && HttpMethod.requiresRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must have a request body.");
            }
            this.method = method;
            this.body = body;
            return this;
        }
        
        public Builder tag(@Nullable final Object tag) {
            return this.tag(Object.class, tag);
        }
        
        public <T> Builder tag(final Class<? super T> type, @Nullable final T tag) {
            if (type == null) {
                throw new NullPointerException("type == null");
            }
            if (tag == null) {
                this.tags.remove(type);
            }
            else {
                if (this.tags.isEmpty()) {
                    this.tags = new LinkedHashMap<Class<?>, Object>();
                }
                this.tags.put(type, type.cast(tag));
            }
            return this;
        }
        
        public Request build() {
            if (this.url == null) {
                throw new IllegalStateException("url == null");
            }
            return new Request(this);
        }
    }
}
