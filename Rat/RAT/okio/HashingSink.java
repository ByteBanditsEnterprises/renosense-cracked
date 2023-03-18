//Raddon On Top!

package okio;

import javax.annotation.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.io.*;

public final class HashingSink extends ForwardingSink
{
    @Nullable
    private final MessageDigest messageDigest;
    @Nullable
    private final Mac mac;
    
    public static HashingSink md5(final Sink sink) {
        return new HashingSink(sink, "MD5");
    }
    
    public static HashingSink sha1(final Sink sink) {
        return new HashingSink(sink, "SHA-1");
    }
    
    public static HashingSink sha256(final Sink sink) {
        return new HashingSink(sink, "SHA-256");
    }
    
    public static HashingSink sha512(final Sink sink) {
        return new HashingSink(sink, "SHA-512");
    }
    
    public static HashingSink hmacSha1(final Sink sink, final ByteString key) {
        return new HashingSink(sink, key, "HmacSHA1");
    }
    
    public static HashingSink hmacSha256(final Sink sink, final ByteString key) {
        return new HashingSink(sink, key, "HmacSHA256");
    }
    
    public static HashingSink hmacSha512(final Sink sink, final ByteString key) {
        return new HashingSink(sink, key, "HmacSHA512");
    }
    
    private HashingSink(final Sink sink, final String algorithm) {
        super(sink);
        try {
            this.messageDigest = MessageDigest.getInstance(algorithm);
            this.mac = null;
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }
    
    private HashingSink(final Sink sink, final ByteString key, final String algorithm) {
        super(sink);
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
    
    public void write(final Buffer source, final long byteCount) throws IOException {
        Util.checkOffsetAndCount(source.size, 0L, byteCount);
        long hashedCount = 0L;
        int toHash;
        for (Segment s = source.head; hashedCount < byteCount; hashedCount += toHash, s = s.next) {
            toHash = (int)Math.min(byteCount - hashedCount, s.limit - s.pos);
            if (this.messageDigest != null) {
                this.messageDigest.update(s.data, s.pos, toHash);
            }
            else {
                this.mac.update(s.data, s.pos, toHash);
            }
        }
        super.write(source, byteCount);
    }
    
    public final ByteString hash() {
        final byte[] result = (this.messageDigest != null) ? this.messageDigest.digest() : this.mac.doFinal();
        return ByteString.of(result);
    }
}
