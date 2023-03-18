//Raddon On Top!

package org.apache.commons.codec.digest;

import java.nio.*;
import java.security.*;
import org.apache.commons.codec.binary.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;

public class DigestUtils
{
    private static final int STREAM_BUFFER_LENGTH = 1024;
    private final MessageDigest messageDigest;
    
    public static byte[] digest(final MessageDigest messageDigest, final byte[] data) {
        return messageDigest.digest(data);
    }
    
    public static byte[] digest(final MessageDigest messageDigest, final ByteBuffer data) {
        messageDigest.update(data);
        return messageDigest.digest();
    }
    
    public static byte[] digest(final MessageDigest messageDigest, final File data) throws IOException {
        return updateDigest(messageDigest, data).digest();
    }
    
    public static byte[] digest(final MessageDigest messageDigest, final InputStream data) throws IOException {
        return updateDigest(messageDigest, data).digest();
    }
    
    public static byte[] digest(final MessageDigest messageDigest, final Path data, final OpenOption... options) throws IOException {
        return updateDigest(messageDigest, data, options).digest();
    }
    
    public static byte[] digest(final MessageDigest messageDigest, final RandomAccessFile data) throws IOException {
        return updateDigest(messageDigest, data).digest();
    }
    
    public static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static MessageDigest getDigest(final String algorithm, final MessageDigest defaultMessageDigest) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (Exception e) {
            return defaultMessageDigest;
        }
    }
    
    public static MessageDigest getMd2Digest() {
        return getDigest("MD2");
    }
    
    public static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }
    
    public static MessageDigest getSha1Digest() {
        return getDigest("SHA-1");
    }
    
    public static MessageDigest getSha256Digest() {
        return getDigest("SHA-256");
    }
    
    public static MessageDigest getSha3_224Digest() {
        return getDigest("SHA3-224");
    }
    
    public static MessageDigest getSha3_256Digest() {
        return getDigest("SHA3-256");
    }
    
    public static MessageDigest getSha3_384Digest() {
        return getDigest("SHA3-384");
    }
    
    public static MessageDigest getSha3_512Digest() {
        return getDigest("SHA3-512");
    }
    
    public static MessageDigest getSha384Digest() {
        return getDigest("SHA-384");
    }
    
    public static MessageDigest getSha512_224Digest() {
        return getDigest("SHA-512/224");
    }
    
    public static MessageDigest getSha512_256Digest() {
        return getDigest("SHA-512/256");
    }
    
    public static MessageDigest getSha512Digest() {
        return getDigest("SHA-512");
    }
    
    @Deprecated
    public static MessageDigest getShaDigest() {
        return getSha1Digest();
    }
    
    public static boolean isAvailable(final String messageDigestAlgorithm) {
        return getDigest(messageDigestAlgorithm, null) != null;
    }
    
    public static byte[] md2(final byte[] data) {
        return getMd2Digest().digest(data);
    }
    
    public static byte[] md2(final InputStream data) throws IOException {
        return digest(getMd2Digest(), data);
    }
    
    public static byte[] md2(final String data) {
        return md2(StringUtils.getBytesUtf8(data));
    }
    
    public static String md2Hex(final byte[] data) {
        return Hex.encodeHexString(md2(data));
    }
    
    public static String md2Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(md2(data));
    }
    
    public static String md2Hex(final String data) {
        return Hex.encodeHexString(md2(data));
    }
    
    public static byte[] md5(final byte[] data) {
        return getMd5Digest().digest(data);
    }
    
    public static byte[] md5(final InputStream data) throws IOException {
        return digest(getMd5Digest(), data);
    }
    
    public static byte[] md5(final String data) {
        return md5(StringUtils.getBytesUtf8(data));
    }
    
    public static String md5Hex(final byte[] data) {
        return Hex.encodeHexString(md5(data));
    }
    
    public static String md5Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(md5(data));
    }
    
    public static String md5Hex(final String data) {
        return Hex.encodeHexString(md5(data));
    }
    
    @Deprecated
    public static byte[] sha(final byte[] data) {
        return sha1(data);
    }
    
    @Deprecated
    public static byte[] sha(final InputStream data) throws IOException {
        return sha1(data);
    }
    
    @Deprecated
    public static byte[] sha(final String data) {
        return sha1(data);
    }
    
    public static byte[] sha1(final byte[] data) {
        return getSha1Digest().digest(data);
    }
    
    public static byte[] sha1(final InputStream data) throws IOException {
        return digest(getSha1Digest(), data);
    }
    
    public static byte[] sha1(final String data) {
        return sha1(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha1Hex(final byte[] data) {
        return Hex.encodeHexString(sha1(data));
    }
    
    public static String sha1Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha1(data));
    }
    
    public static String sha1Hex(final String data) {
        return Hex.encodeHexString(sha1(data));
    }
    
    public static byte[] sha256(final byte[] data) {
        return getSha256Digest().digest(data);
    }
    
    public static byte[] sha256(final InputStream data) throws IOException {
        return digest(getSha256Digest(), data);
    }
    
    public static byte[] sha256(final String data) {
        return sha256(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha256Hex(final byte[] data) {
        return Hex.encodeHexString(sha256(data));
    }
    
    public static String sha256Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha256(data));
    }
    
    public static String sha256Hex(final String data) {
        return Hex.encodeHexString(sha256(data));
    }
    
    public static byte[] sha3_224(final byte[] data) {
        return getSha3_224Digest().digest(data);
    }
    
    public static byte[] sha3_224(final InputStream data) throws IOException {
        return digest(getSha3_224Digest(), data);
    }
    
    public static byte[] sha3_224(final String data) {
        return sha3_224(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha3_224Hex(final byte[] data) {
        return Hex.encodeHexString(sha3_224(data));
    }
    
    public static String sha3_224Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha3_224(data));
    }
    
    public static String sha3_224Hex(final String data) {
        return Hex.encodeHexString(sha3_224(data));
    }
    
    public static byte[] sha3_256(final byte[] data) {
        return getSha3_256Digest().digest(data);
    }
    
    public static byte[] sha3_256(final InputStream data) throws IOException {
        return digest(getSha3_256Digest(), data);
    }
    
    public static byte[] sha3_256(final String data) {
        return sha3_256(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha3_256Hex(final byte[] data) {
        return Hex.encodeHexString(sha3_256(data));
    }
    
    public static String sha3_256Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha3_256(data));
    }
    
    public static String sha3_256Hex(final String data) {
        return Hex.encodeHexString(sha3_256(data));
    }
    
    public static byte[] sha3_384(final byte[] data) {
        return getSha3_384Digest().digest(data);
    }
    
    public static byte[] sha3_384(final InputStream data) throws IOException {
        return digest(getSha3_384Digest(), data);
    }
    
    public static byte[] sha3_384(final String data) {
        return sha3_384(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha3_384Hex(final byte[] data) {
        return Hex.encodeHexString(sha3_384(data));
    }
    
    public static String sha3_384Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha3_384(data));
    }
    
    public static String sha3_384Hex(final String data) {
        return Hex.encodeHexString(sha3_384(data));
    }
    
    public static byte[] sha3_512(final byte[] data) {
        return getSha3_512Digest().digest(data);
    }
    
    public static byte[] sha3_512(final InputStream data) throws IOException {
        return digest(getSha3_512Digest(), data);
    }
    
    public static byte[] sha3_512(final String data) {
        return sha3_512(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha3_512Hex(final byte[] data) {
        return Hex.encodeHexString(sha3_512(data));
    }
    
    public static String sha3_512Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha3_512(data));
    }
    
    public static String sha3_512Hex(final String data) {
        return Hex.encodeHexString(sha3_512(data));
    }
    
    public static byte[] sha384(final byte[] data) {
        return getSha384Digest().digest(data);
    }
    
    public static byte[] sha384(final InputStream data) throws IOException {
        return digest(getSha384Digest(), data);
    }
    
    public static byte[] sha384(final String data) {
        return sha384(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha384Hex(final byte[] data) {
        return Hex.encodeHexString(sha384(data));
    }
    
    public static String sha384Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha384(data));
    }
    
    public static String sha384Hex(final String data) {
        return Hex.encodeHexString(sha384(data));
    }
    
    public static byte[] sha512(final byte[] data) {
        return getSha512Digest().digest(data);
    }
    
    public static byte[] sha512(final InputStream data) throws IOException {
        return digest(getSha512Digest(), data);
    }
    
    public static byte[] sha512(final String data) {
        return sha512(StringUtils.getBytesUtf8(data));
    }
    
    public static byte[] sha512_224(final byte[] data) {
        return getSha512_224Digest().digest(data);
    }
    
    public static byte[] sha512_224(final InputStream data) throws IOException {
        return digest(getSha512_224Digest(), data);
    }
    
    public static byte[] sha512_224(final String data) {
        return sha512_224(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha512_224Hex(final byte[] data) {
        return Hex.encodeHexString(sha512_224(data));
    }
    
    public static String sha512_224Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha512_224(data));
    }
    
    public static String sha512_224Hex(final String data) {
        return Hex.encodeHexString(sha512_224(data));
    }
    
    public static byte[] sha512_256(final byte[] data) {
        return getSha512_256Digest().digest(data);
    }
    
    public static byte[] sha512_256(final InputStream data) throws IOException {
        return digest(getSha512_256Digest(), data);
    }
    
    public static byte[] sha512_256(final String data) {
        return sha512_256(StringUtils.getBytesUtf8(data));
    }
    
    public static String sha512_256Hex(final byte[] data) {
        return Hex.encodeHexString(sha512_256(data));
    }
    
    public static String sha512_256Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha512_256(data));
    }
    
    public static String sha512_256Hex(final String data) {
        return Hex.encodeHexString(sha512_256(data));
    }
    
    public static String sha512Hex(final byte[] data) {
        return Hex.encodeHexString(sha512(data));
    }
    
    public static String sha512Hex(final InputStream data) throws IOException {
        return Hex.encodeHexString(sha512(data));
    }
    
    public static String sha512Hex(final String data) {
        return Hex.encodeHexString(sha512(data));
    }
    
    @Deprecated
    public static String shaHex(final byte[] data) {
        return sha1Hex(data);
    }
    
    @Deprecated
    public static String shaHex(final InputStream data) throws IOException {
        return sha1Hex(data);
    }
    
    @Deprecated
    public static String shaHex(final String data) {
        return sha1Hex(data);
    }
    
    public static MessageDigest updateDigest(final MessageDigest messageDigest, final byte[] valueToDigest) {
        messageDigest.update(valueToDigest);
        return messageDigest;
    }
    
    public static MessageDigest updateDigest(final MessageDigest messageDigest, final ByteBuffer valueToDigest) {
        messageDigest.update(valueToDigest);
        return messageDigest;
    }
    
    public static MessageDigest updateDigest(final MessageDigest digest, final File data) throws IOException {
        try (final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(data))) {
            return updateDigest(digest, inputStream);
        }
    }
    
    private static MessageDigest updateDigest(final MessageDigest digest, final FileChannel data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (data.read(buffer) > 0) {
            buffer.flip();
            digest.update(buffer);
            buffer.clear();
        }
        return digest;
    }
    
    public static MessageDigest updateDigest(final MessageDigest digest, final InputStream inputStream) throws IOException {
        final byte[] buffer = new byte[1024];
        for (int read = inputStream.read(buffer, 0, 1024); read > -1; read = inputStream.read(buffer, 0, 1024)) {
            digest.update(buffer, 0, read);
        }
        return digest;
    }
    
    public static MessageDigest updateDigest(final MessageDigest digest, final Path path, final OpenOption... options) throws IOException {
        try (final BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(path, options))) {
            return updateDigest(digest, inputStream);
        }
    }
    
    public static MessageDigest updateDigest(final MessageDigest digest, final RandomAccessFile data) throws IOException {
        return updateDigest(digest, data.getChannel());
    }
    
    public static MessageDigest updateDigest(final MessageDigest messageDigest, final String valueToDigest) {
        messageDigest.update(StringUtils.getBytesUtf8(valueToDigest));
        return messageDigest;
    }
    
    @Deprecated
    public DigestUtils() {
        this.messageDigest = null;
    }
    
    public DigestUtils(final MessageDigest digest) {
        this.messageDigest = digest;
    }
    
    public DigestUtils(final String name) {
        this(getDigest(name));
    }
    
    public byte[] digest(final byte[] data) {
        return updateDigest(this.messageDigest, data).digest();
    }
    
    public byte[] digest(final ByteBuffer data) {
        return updateDigest(this.messageDigest, data).digest();
    }
    
    public byte[] digest(final File data) throws IOException {
        return updateDigest(this.messageDigest, data).digest();
    }
    
    public byte[] digest(final InputStream data) throws IOException {
        return updateDigest(this.messageDigest, data).digest();
    }
    
    public byte[] digest(final Path data, final OpenOption... options) throws IOException {
        return updateDigest(this.messageDigest, data, options).digest();
    }
    
    public byte[] digest(final String data) {
        return updateDigest(this.messageDigest, data).digest();
    }
    
    public String digestAsHex(final byte[] data) {
        return Hex.encodeHexString(this.digest(data));
    }
    
    public String digestAsHex(final ByteBuffer data) {
        return Hex.encodeHexString(this.digest(data));
    }
    
    public String digestAsHex(final File data) throws IOException {
        return Hex.encodeHexString(this.digest(data));
    }
    
    public String digestAsHex(final InputStream data) throws IOException {
        return Hex.encodeHexString(this.digest(data));
    }
    
    public String digestAsHex(final Path data, final OpenOption... options) throws IOException {
        return Hex.encodeHexString(this.digest(data, options));
    }
    
    public String digestAsHex(final String data) {
        return Hex.encodeHexString(this.digest(data));
    }
    
    public MessageDigest getMessageDigest() {
        return this.messageDigest;
    }
}
