//Raddon On Top!

package okio;

import java.nio.*;
import java.nio.charset.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import javax.annotation.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class ByteString implements Serializable, Comparable<ByteString>
{
    static final char[] HEX_DIGITS;
    private static final long serialVersionUID = 1L;
    public static final ByteString EMPTY;
    final byte[] data;
    transient int hashCode;
    transient String utf8;
    
    ByteString(final byte[] data) {
        this.data = data;
    }
    
    public static ByteString of(final byte... data) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        return new ByteString(data.clone());
    }
    
    public static ByteString of(final byte[] data, final int offset, final int byteCount) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        Util.checkOffsetAndCount(data.length, offset, byteCount);
        final byte[] copy = new byte[byteCount];
        System.arraycopy(data, offset, copy, 0, byteCount);
        return new ByteString(copy);
    }
    
    public static ByteString of(final ByteBuffer data) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        final byte[] copy = new byte[data.remaining()];
        data.get(copy);
        return new ByteString(copy);
    }
    
    public static ByteString encodeUtf8(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("s == null");
        }
        final ByteString byteString = new ByteString(s.getBytes(Util.UTF_8));
        byteString.utf8 = s;
        return byteString;
    }
    
    public static ByteString encodeString(final String s, final Charset charset) {
        if (s == null) {
            throw new IllegalArgumentException("s == null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        }
        return new ByteString(s.getBytes(charset));
    }
    
    public String utf8() {
        final String result = this.utf8;
        return (result != null) ? result : (this.utf8 = new String(this.data, Util.UTF_8));
    }
    
    public String string(final Charset charset) {
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        }
        return new String(this.data, charset);
    }
    
    public String base64() {
        return Base64.encode(this.data);
    }
    
    public ByteString md5() {
        return this.digest("MD5");
    }
    
    public ByteString sha1() {
        return this.digest("SHA-1");
    }
    
    public ByteString sha256() {
        return this.digest("SHA-256");
    }
    
    public ByteString sha512() {
        return this.digest("SHA-512");
    }
    
    private ByteString digest(final String algorithm) {
        try {
            return of(MessageDigest.getInstance(algorithm).digest(this.data));
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    public ByteString hmacSha1(final ByteString key) {
        return this.hmac("HmacSHA1", key);
    }
    
    public ByteString hmacSha256(final ByteString key) {
        return this.hmac("HmacSHA256", key);
    }
    
    public ByteString hmacSha512(final ByteString key) {
        return this.hmac("HmacSHA512", key);
    }
    
    private ByteString hmac(final String algorithm, final ByteString key) {
        try {
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.toByteArray(), algorithm));
            return of(mac.doFinal(this.data));
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError((Object)e);
        }
        catch (InvalidKeyException e2) {
            throw new IllegalArgumentException(e2);
        }
    }
    
    public String base64Url() {
        return Base64.encodeUrl(this.data);
    }
    
    @Nullable
    public static ByteString decodeBase64(final String base64) {
        if (base64 == null) {
            throw new IllegalArgumentException("base64 == null");
        }
        final byte[] decoded = Base64.decode(base64);
        return (decoded != null) ? new ByteString(decoded) : null;
    }
    
    public String hex() {
        final char[] result = new char[this.data.length * 2];
        int c = 0;
        for (final byte b : this.data) {
            result[c++] = ByteString.HEX_DIGITS[b >> 4 & 0xF];
            result[c++] = ByteString.HEX_DIGITS[b & 0xF];
        }
        return new String(result);
    }
    
    public static ByteString decodeHex(final String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("hex == null");
        }
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Unexpected hex string: " + hex);
        }
        final byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; ++i) {
            final int d1 = decodeHexDigit(hex.charAt(i * 2)) << 4;
            final int d2 = decodeHexDigit(hex.charAt(i * 2 + 1));
            result[i] = (byte)(d1 + d2);
        }
        return of(result);
    }
    
    private static int decodeHexDigit(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        throw new IllegalArgumentException("Unexpected hex digit: " + c);
    }
    
    public static ByteString read(final InputStream in, final int byteCount) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("in == null");
        }
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        }
        final byte[] result = new byte[byteCount];
        int read;
        for (int offset = 0; offset < byteCount; offset += read) {
            read = in.read(result, offset, byteCount - offset);
            if (read == -1) {
                throw new EOFException();
            }
        }
        return new ByteString(result);
    }
    
    public ByteString toAsciiLowercase() {
        for (int i = 0; i < this.data.length; ++i) {
            byte c = this.data[i];
            if (c >= 65 && c <= 90) {
                final byte[] lowercase = this.data.clone();
                lowercase[i++] = (byte)(c + 32);
                while (i < lowercase.length) {
                    c = lowercase[i];
                    if (c >= 65) {
                        if (c <= 90) {
                            lowercase[i] = (byte)(c + 32);
                        }
                    }
                    ++i;
                }
                return new ByteString(lowercase);
            }
        }
        return this;
    }
    
    public ByteString toAsciiUppercase() {
        for (int i = 0; i < this.data.length; ++i) {
            byte c = this.data[i];
            if (c >= 97 && c <= 122) {
                final byte[] lowercase = this.data.clone();
                lowercase[i++] = (byte)(c - 32);
                while (i < lowercase.length) {
                    c = lowercase[i];
                    if (c >= 97) {
                        if (c <= 122) {
                            lowercase[i] = (byte)(c - 32);
                        }
                    }
                    ++i;
                }
                return new ByteString(lowercase);
            }
        }
        return this;
    }
    
    public ByteString substring(final int beginIndex) {
        return this.substring(beginIndex, this.data.length);
    }
    
    public ByteString substring(final int beginIndex, final int endIndex) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException("beginIndex < 0");
        }
        if (endIndex > this.data.length) {
            throw new IllegalArgumentException("endIndex > length(" + this.data.length + ")");
        }
        final int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new IllegalArgumentException("endIndex < beginIndex");
        }
        if (beginIndex == 0 && endIndex == this.data.length) {
            return this;
        }
        final byte[] copy = new byte[subLen];
        System.arraycopy(this.data, beginIndex, copy, 0, subLen);
        return new ByteString(copy);
    }
    
    public byte getByte(final int pos) {
        return this.data[pos];
    }
    
    public int size() {
        return this.data.length;
    }
    
    public byte[] toByteArray() {
        return this.data.clone();
    }
    
    byte[] internalArray() {
        return this.data;
    }
    
    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.data).asReadOnlyBuffer();
    }
    
    public void write(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        out.write(this.data);
    }
    
    void write(final Buffer buffer) {
        buffer.write(this.data, 0, this.data.length);
    }
    
    public boolean rangeEquals(final int offset, final ByteString other, final int otherOffset, final int byteCount) {
        return other.rangeEquals(otherOffset, this.data, offset, byteCount);
    }
    
    public boolean rangeEquals(final int offset, final byte[] other, final int otherOffset, final int byteCount) {
        return offset >= 0 && offset <= this.data.length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && Util.arrayRangeEquals(this.data, offset, other, otherOffset, byteCount);
    }
    
    public final boolean startsWith(final ByteString prefix) {
        return this.rangeEquals(0, prefix, 0, prefix.size());
    }
    
    public final boolean startsWith(final byte[] prefix) {
        return this.rangeEquals(0, prefix, 0, prefix.length);
    }
    
    public final boolean endsWith(final ByteString suffix) {
        return this.rangeEquals(this.size() - suffix.size(), suffix, 0, suffix.size());
    }
    
    public final boolean endsWith(final byte[] suffix) {
        return this.rangeEquals(this.size() - suffix.length, suffix, 0, suffix.length);
    }
    
    public final int indexOf(final ByteString other) {
        return this.indexOf(other.internalArray(), 0);
    }
    
    public final int indexOf(final ByteString other, final int fromIndex) {
        return this.indexOf(other.internalArray(), fromIndex);
    }
    
    public final int indexOf(final byte[] other) {
        return this.indexOf(other, 0);
    }
    
    public int indexOf(final byte[] other, int fromIndex) {
        int i;
        fromIndex = (i = Math.max(fromIndex, 0));
        for (int limit = this.data.length - other.length; i <= limit; ++i) {
            if (Util.arrayRangeEquals(this.data, i, other, 0, other.length)) {
                return i;
            }
        }
        return -1;
    }
    
    public final int lastIndexOf(final ByteString other) {
        return this.lastIndexOf(other.internalArray(), this.size());
    }
    
    public final int lastIndexOf(final ByteString other, final int fromIndex) {
        return this.lastIndexOf(other.internalArray(), fromIndex);
    }
    
    public final int lastIndexOf(final byte[] other) {
        return this.lastIndexOf(other, this.size());
    }
    
    public int lastIndexOf(final byte[] other, int fromIndex) {
        int i;
        for (fromIndex = (i = Math.min(fromIndex, this.data.length - other.length)); i >= 0; --i) {
            if (Util.arrayRangeEquals(this.data, i, other, 0, other.length)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof ByteString && ((ByteString)o).size() == this.data.length && ((ByteString)o).rangeEquals(0, this.data, 0, this.data.length));
    }
    
    @Override
    public int hashCode() {
        final int result = this.hashCode;
        return (result != 0) ? result : (this.hashCode = Arrays.hashCode(this.data));
    }
    
    @Override
    public int compareTo(final ByteString byteString) {
        final int sizeA = this.size();
        final int sizeB = byteString.size();
        for (int i = 0, size = Math.min(sizeA, sizeB); i < size; ++i) {
            final int byteA = this.getByte(i) & 0xFF;
            final int byteB = byteString.getByte(i) & 0xFF;
            if (byteA != byteB) {
                return (byteA < byteB) ? -1 : 1;
            }
        }
        if (sizeA == sizeB) {
            return 0;
        }
        return (sizeA < sizeB) ? -1 : 1;
    }
    
    @Override
    public String toString() {
        if (this.data.length == 0) {
            return "[size=0]";
        }
        final String text = this.utf8();
        final int i = codePointIndexToCharIndex(text, 64);
        if (i == -1) {
            return (this.data.length <= 64) ? ("[hex=" + this.hex() + "]") : ("[size=" + this.data.length + " hex=" + this.substring(0, 64).hex() + "\u2026]");
        }
        final String safeText = text.substring(0, i).replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r");
        return (i < text.length()) ? ("[size=" + this.data.length + " text=" + safeText + "\u2026]") : ("[text=" + safeText + "]");
    }
    
    static int codePointIndexToCharIndex(final String s, final int codePointCount) {
        int i = 0;
        int j = 0;
        int c;
        for (int length = s.length(); i < length; i += Character.charCount(c)) {
            if (j == codePointCount) {
                return i;
            }
            c = s.codePointAt(i);
            if ((Character.isISOControl(c) && c != 10 && c != 13) || c == 65533) {
                return -1;
            }
            ++j;
        }
        return s.length();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException {
        final int dataLength = in.readInt();
        final ByteString byteString = read(in, dataLength);
        try {
            final Field field = ByteString.class.getDeclaredField("data");
            field.setAccessible(true);
            field.set(this, byteString.data);
        }
        catch (NoSuchFieldException e) {
            throw new AssertionError();
        }
        catch (IllegalAccessException e2) {
            throw new AssertionError();
        }
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.data.length);
        out.write(this.data);
    }
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        EMPTY = of(new byte[0]);
    }
}
