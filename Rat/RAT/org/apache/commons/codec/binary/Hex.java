//Raddon On Top!

package org.apache.commons.codec.binary;

import java.nio.*;
import org.apache.commons.codec.*;
import java.nio.charset.*;

public class Hex implements BinaryEncoder, BinaryDecoder
{
    public static final Charset DEFAULT_CHARSET;
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    private static final char[] DIGITS_LOWER;
    private static final char[] DIGITS_UPPER;
    private final Charset charset;
    
    public static byte[] decodeHex(final char[] data) throws DecoderException {
        final byte[] out = new byte[data.length >> 1];
        decodeHex(data, out, 0);
        return out;
    }
    
    public static int decodeHex(final char[] data, final byte[] out, final int outOffset) throws DecoderException {
        final int len = data.length;
        if ((len & 0x1) != 0x0) {
            throw new DecoderException("Odd number of characters.");
        }
        final int outLen = len >> 1;
        if (out.length - outOffset < outLen) {
            throw new DecoderException("Output array is not large enough to accommodate decoded data.");
        }
        int f;
        for (int i = outOffset, j = 0; j < len; ++j, f |= toDigit(data[j], j), ++j, out[i] = (byte)(f & 0xFF), ++i) {
            f = toDigit(data[j], j) << 4;
        }
        return outLen;
    }
    
    public static byte[] decodeHex(final String data) throws DecoderException {
        return decodeHex(data.toCharArray());
    }
    
    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }
    
    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? Hex.DIGITS_LOWER : Hex.DIGITS_UPPER);
    }
    
    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        encodeHex(data, 0, data.length, toDigits, out, 0);
        return out;
    }
    
    public static char[] encodeHex(final byte[] data, final int dataOffset, final int dataLen, final boolean toLowerCase) {
        final char[] out = new char[dataLen << 1];
        encodeHex(data, dataOffset, dataLen, toLowerCase ? Hex.DIGITS_LOWER : Hex.DIGITS_UPPER, out, 0);
        return out;
    }
    
    public static void encodeHex(final byte[] data, final int dataOffset, final int dataLen, final boolean toLowerCase, final char[] out, final int outOffset) {
        encodeHex(data, dataOffset, dataLen, toLowerCase ? Hex.DIGITS_LOWER : Hex.DIGITS_UPPER, out, outOffset);
    }
    
    private static void encodeHex(final byte[] data, final int dataOffset, final int dataLen, final char[] toDigits, final char[] out, final int outOffset) {
        int i = dataOffset;
        int j = outOffset;
        while (i < dataOffset + dataLen) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0xF & data[i]];
            ++i;
        }
    }
    
    public static char[] encodeHex(final ByteBuffer data) {
        return encodeHex(data, true);
    }
    
    public static char[] encodeHex(final ByteBuffer data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? Hex.DIGITS_LOWER : Hex.DIGITS_UPPER);
    }
    
    protected static char[] encodeHex(final ByteBuffer byteBuffer, final char[] toDigits) {
        return encodeHex(toByteArray(byteBuffer), toDigits);
    }
    
    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }
    
    public static String encodeHexString(final byte[] data, final boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase));
    }
    
    public static String encodeHexString(final ByteBuffer data) {
        return new String(encodeHex(data));
    }
    
    public static String encodeHexString(final ByteBuffer data, final boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase));
    }
    
    private static byte[] toByteArray(final ByteBuffer byteBuffer) {
        final int remaining = byteBuffer.remaining();
        if (byteBuffer.hasArray()) {
            final byte[] byteArray = byteBuffer.array();
            if (remaining == byteArray.length) {
                byteBuffer.position(remaining);
                return byteArray;
            }
        }
        final byte[] byteArray = new byte[remaining];
        byteBuffer.get(byteArray);
        return byteArray;
    }
    
    protected static int toDigit(final char ch, final int index) throws DecoderException {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
    
    public Hex() {
        this.charset = Hex.DEFAULT_CHARSET;
    }
    
    public Hex(final Charset charset) {
        this.charset = charset;
    }
    
    public Hex(final String charsetName) {
        this(Charset.forName(charsetName));
    }
    
    @Override
    public byte[] decode(final byte[] array) throws DecoderException {
        return decodeHex(new String(array, this.getCharset()).toCharArray());
    }
    
    public byte[] decode(final ByteBuffer buffer) throws DecoderException {
        return decodeHex(new String(toByteArray(buffer), this.getCharset()).toCharArray());
    }
    
    @Override
    public Object decode(final Object object) throws DecoderException {
        if (object instanceof String) {
            return this.decode(((String)object).toCharArray());
        }
        if (object instanceof byte[]) {
            return this.decode((byte[])object);
        }
        if (object instanceof ByteBuffer) {
            return this.decode((ByteBuffer)object);
        }
        try {
            return decodeHex((char[])object);
        }
        catch (ClassCastException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] encode(final byte[] array) {
        return encodeHexString(array).getBytes(this.getCharset());
    }
    
    public byte[] encode(final ByteBuffer array) {
        return encodeHexString(array).getBytes(this.getCharset());
    }
    
    @Override
    public Object encode(final Object object) throws EncoderException {
        byte[] byteArray;
        if (object instanceof String) {
            byteArray = ((String)object).getBytes(this.getCharset());
        }
        else if (object instanceof ByteBuffer) {
            byteArray = toByteArray((ByteBuffer)object);
        }
        else {
            try {
                byteArray = (byte[])object;
            }
            catch (ClassCastException e) {
                throw new EncoderException(e.getMessage(), e);
            }
        }
        return encodeHex(byteArray);
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public String getCharsetName() {
        return this.charset.name();
    }
    
    @Override
    public String toString() {
        return super.toString() + "[charsetName=" + this.charset + "]";
    }
    
    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
        DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
