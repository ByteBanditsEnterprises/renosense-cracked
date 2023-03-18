//Raddon On Top!

package okhttp3.internal.http2;

import okio.*;
import okhttp3.internal.*;

public final class Header
{
    public static final ByteString PSEUDO_PREFIX;
    public static final String RESPONSE_STATUS_UTF8 = ":status";
    public static final String TARGET_METHOD_UTF8 = ":method";
    public static final String TARGET_PATH_UTF8 = ":path";
    public static final String TARGET_SCHEME_UTF8 = ":scheme";
    public static final String TARGET_AUTHORITY_UTF8 = ":authority";
    public static final ByteString RESPONSE_STATUS;
    public static final ByteString TARGET_METHOD;
    public static final ByteString TARGET_PATH;
    public static final ByteString TARGET_SCHEME;
    public static final ByteString TARGET_AUTHORITY;
    public final ByteString name;
    public final ByteString value;
    final int hpackSize;
    
    public Header(final String name, final String value) {
        this(ByteString.encodeUtf8(name), ByteString.encodeUtf8(value));
    }
    
    public Header(final ByteString name, final String value) {
        this(name, ByteString.encodeUtf8(value));
    }
    
    public Header(final ByteString name, final ByteString value) {
        this.name = name;
        this.value = value;
        this.hpackSize = 32 + name.size() + value.size();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Header) {
            final Header that = (Header)other;
            return this.name.equals((Object)that.name) && this.value.equals((Object)that.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.name.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return Util.format("%s: %s", this.name.utf8(), this.value.utf8());
    }
    
    static {
        PSEUDO_PREFIX = ByteString.encodeUtf8(":");
        RESPONSE_STATUS = ByteString.encodeUtf8(":status");
        TARGET_METHOD = ByteString.encodeUtf8(":method");
        TARGET_PATH = ByteString.encodeUtf8(":path");
        TARGET_SCHEME = ByteString.encodeUtf8(":scheme");
        TARGET_AUTHORITY = ByteString.encodeUtf8(":authority");
    }
}
