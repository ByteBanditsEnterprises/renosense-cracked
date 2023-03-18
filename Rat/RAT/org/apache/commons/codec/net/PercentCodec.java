//Raddon On Top!

package org.apache.commons.codec.net;

import java.util.*;
import java.nio.*;
import org.apache.commons.codec.*;

public class PercentCodec implements BinaryEncoder, BinaryDecoder
{
    private static final byte ESCAPE_CHAR = 37;
    private final BitSet alwaysEncodeChars;
    private final boolean plusForSpace;
    private int alwaysEncodeCharsMin;
    private int alwaysEncodeCharsMax;
    
    public PercentCodec() {
        this.alwaysEncodeChars = new BitSet();
        this.alwaysEncodeCharsMin = Integer.MAX_VALUE;
        this.alwaysEncodeCharsMax = Integer.MIN_VALUE;
        this.plusForSpace = false;
        this.insertAlwaysEncodeChar((byte)37);
    }
    
    public PercentCodec(final byte[] alwaysEncodeChars, final boolean plusForSpace) {
        this.alwaysEncodeChars = new BitSet();
        this.alwaysEncodeCharsMin = Integer.MAX_VALUE;
        this.alwaysEncodeCharsMax = Integer.MIN_VALUE;
        this.plusForSpace = plusForSpace;
        this.insertAlwaysEncodeChars(alwaysEncodeChars);
    }
    
    private void insertAlwaysEncodeChars(final byte[] alwaysEncodeCharsArray) {
        if (alwaysEncodeCharsArray != null) {
            for (final byte b : alwaysEncodeCharsArray) {
                this.insertAlwaysEncodeChar(b);
            }
        }
        this.insertAlwaysEncodeChar((byte)37);
    }
    
    private void insertAlwaysEncodeChar(final byte b) {
        this.alwaysEncodeChars.set(b);
        if (b < this.alwaysEncodeCharsMin) {
            this.alwaysEncodeCharsMin = b;
        }
        if (b > this.alwaysEncodeCharsMax) {
            this.alwaysEncodeCharsMax = b;
        }
    }
    
    public byte[] encode(final byte[] bytes) throws EncoderException {
        if (bytes == null) {
            return null;
        }
        final int expectedEncodingBytes = this.expectedEncodingBytes(bytes);
        final boolean willEncode = expectedEncodingBytes != bytes.length;
        if (willEncode || (this.plusForSpace && this.containsSpace(bytes))) {
            return this.doEncode(bytes, expectedEncodingBytes, willEncode);
        }
        return bytes;
    }
    
    private byte[] doEncode(final byte[] bytes, final int expectedLength, final boolean willEncode) {
        final ByteBuffer buffer = ByteBuffer.allocate(expectedLength);
        for (final byte b : bytes) {
            if (willEncode && this.canEncode(b)) {
                byte bb = b;
                if (bb < 0) {
                    bb += 256;
                }
                final char hex1 = Utils.hexDigit(bb >> 4);
                final char hex2 = Utils.hexDigit(bb);
                buffer.put((byte)37);
                buffer.put((byte)hex1);
                buffer.put((byte)hex2);
            }
            else if (this.plusForSpace && b == 32) {
                buffer.put((byte)43);
            }
            else {
                buffer.put(b);
            }
        }
        return buffer.array();
    }
    
    private int expectedEncodingBytes(final byte[] bytes) {
        int byteCount = 0;
        for (final byte b : bytes) {
            byteCount += (this.canEncode(b) ? 3 : 1);
        }
        return byteCount;
    }
    
    private boolean containsSpace(final byte[] bytes) {
        for (final byte b : bytes) {
            if (b == 32) {
                return true;
            }
        }
        return false;
    }
    
    private boolean canEncode(final byte c) {
        return !this.isAsciiChar(c) || (this.inAlwaysEncodeCharsRange(c) && this.alwaysEncodeChars.get(c));
    }
    
    private boolean inAlwaysEncodeCharsRange(final byte c) {
        return c >= this.alwaysEncodeCharsMin && c <= this.alwaysEncodeCharsMax;
    }
    
    private boolean isAsciiChar(final byte c) {
        return c >= 0;
    }
    
    public byte[] decode(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        final ByteBuffer buffer = ByteBuffer.allocate(this.expectedDecodingBytes(bytes));
        for (int i = 0; i < bytes.length; ++i) {
            final byte b = bytes[i];
            if (b == 37) {
                try {
                    final int u = Utils.digit16(bytes[++i]);
                    final int l = Utils.digit16(bytes[++i]);
                    buffer.put((byte)((u << 4) + l));
                    continue;
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid percent decoding: ", (Throwable)e);
                }
            }
            if (this.plusForSpace && b == 43) {
                buffer.put((byte)32);
            }
            else {
                buffer.put(b);
            }
        }
        return buffer.array();
    }
    
    private int expectedDecodingBytes(final byte[] bytes) {
        int byteCount = 0;
        byte b;
        for (int i = 0; i < bytes.length; i += ((b == 37) ? 3 : 1), ++byteCount) {
            b = bytes[i];
        }
        return byteCount;
    }
    
    public Object encode(final Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return this.encode((byte[])obj);
        }
        throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent encoded");
    }
    
    public Object decode(final Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return this.decode((byte[])obj);
        }
        throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent decoded");
    }
}
