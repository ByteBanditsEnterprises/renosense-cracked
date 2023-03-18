//Raddon On Top!

package okio;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.io.*;

public final class HashingSource extends ForwardingSource
{
    private final MessageDigest messageDigest;
    private final Mac mac;
    
    public static HashingSource md5(final Source source) {
        return new HashingSource(source, "MD5");
    }
    
    public static HashingSource sha1(final Source source) {
        return new HashingSource(source, "SHA-1");
    }
    
    public static HashingSource sha256(final Source source) {
        return new HashingSource(source, "SHA-256");
    }
    
    public static HashingSource hmacSha1(final Source source, final ByteString key) {
        return new HashingSource(source, key, "HmacSHA1");
    }
    
    public static HashingSource hmacSha256(final Source source, final ByteString key) {
        return new HashingSource(source, key, "HmacSHA256");
    }
    
    private HashingSource(final Source source, final String algorithm) {
        super(source);
        try {
            this.messageDigest = MessageDigest.getInstance(algorithm);
            this.mac = null;
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }
    
    private HashingSource(final Source source, final ByteString key, final String algorithm) {
        super(source);
        try {
            (this.mac = Mac.getInstance(algorithm)).init(new SecretKeySpec(key.toByteArray(), algorithm));
            this.messageDigest = null;
        }
        catch (NoSuchAlgorithmException e2) {
            throw new AssertionError();
        }
        catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public long read(final Buffer sink, final long byteCount) throws IOException {
        final long result = super.read(sink, byteCount);
        if (result != -1L) {
            long start;
            long offset;
            Segment s;
            for (start = sink.size - result, offset = sink.size, s = sink.head; offset > start; offset -= s.limit - s.pos) {
                s = s.prev;
            }
            while (offset < sink.size) {
                final int pos = (int)(s.pos + start - offset);
                if (this.messageDigest != null) {
                    this.messageDigest.update(s.data, pos, s.limit - pos);
                }
                else {
                    this.mac.update(s.data, pos, s.limit - pos);
                }
                offset = (start = offset + (s.limit - s.pos));
                s = s.next;
            }
        }
        return result;
    }
    
    public final ByteString hash() {
        final byte[] result = (this.messageDigest != null) ? this.messageDigest.digest() : this.mac.doFinal();
        return ByteString.of(result);
    }
}
