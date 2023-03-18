//Raddon On Top!

package okhttp3;

import java.util.*;
import java.nio.charset.*;
import javax.annotation.*;

public final class Challenge
{
    private final String scheme;
    private final Map<String, String> authParams;
    
    public Challenge(final String scheme, final Map<String, String> authParams) {
        if (scheme == null) {
            throw new NullPointerException("scheme == null");
        }
        if (authParams == null) {
            throw new NullPointerException("authParams == null");
        }
        this.scheme = scheme;
        final Map<String, String> newAuthParams = new LinkedHashMap<String, String>();
        for (final Map.Entry<String, String> authParam : authParams.entrySet()) {
            final String key = (authParam.getKey() == null) ? null : authParam.getKey().toLowerCase(Locale.US);
            newAuthParams.put(key, authParam.getValue());
        }
        this.authParams = Collections.unmodifiableMap((Map<? extends String, ? extends String>)newAuthParams);
    }
    
    public Challenge(final String scheme, final String realm) {
        if (scheme == null) {
            throw new NullPointerException("scheme == null");
        }
        if (realm == null) {
            throw new NullPointerException("realm == null");
        }
        this.scheme = scheme;
        this.authParams = Collections.singletonMap("realm", realm);
    }
    
    public Challenge withCharset(final Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset == null");
        }
        final Map<String, String> authParams = new LinkedHashMap<String, String>(this.authParams);
        authParams.put("charset", charset.name());
        return new Challenge(this.scheme, authParams);
    }
    
    public String scheme() {
        return this.scheme;
    }
    
    public Map<String, String> authParams() {
        return this.authParams;
    }
    
    public String realm() {
        return this.authParams.get("realm");
    }
    
    public Charset charset() {
        final String charset = this.authParams.get("charset");
        if (charset != null) {
            try {
                return Charset.forName(charset);
            }
            catch (Exception ex) {}
        }
        return StandardCharsets.ISO_8859_1;
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        return other instanceof Challenge && ((Challenge)other).scheme.equals(this.scheme) && ((Challenge)other).authParams.equals(this.authParams);
    }
    
    @Override
    public int hashCode() {
        int result = 29;
        result = 31 * result + this.scheme.hashCode();
        result = 31 * result + this.authParams.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return this.scheme + " authParams=" + this.authParams;
    }
}
