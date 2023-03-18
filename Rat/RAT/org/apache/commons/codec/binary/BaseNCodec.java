//Raddon On Top!

package org.apache.commons.codec.binary;

import org.apache.commons.codec.*;
import java.util.*;

public abstract class BaseNCodec implements BinaryEncoder, BinaryDecoder
{
    static final int EOF = -1;
    public static final int MIME_CHUNK_SIZE = 76;
    public static final int PEM_CHUNK_SIZE = 64;
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 2147483639;
    protected static final int MASK_8BITS = 255;
    protected static final byte PAD_DEFAULT = 61;
    protected static final CodecPolicy DECODING_POLICY_DEFAULT;
    static final byte[] CHUNK_SEPARATOR;
    @Deprecated
    protected final byte PAD = 61;
    protected final byte pad;
    private final int unencodedBlockSize;
    private final int encodedBlockSize;
    protected final int lineLength;
    private final int chunkSeparatorLength;
    private final CodecPolicy decodingPolicy;
    
    private static int compareUnsigned(final int x, final int y) {
        return Integer.compare(x + Integer.MIN_VALUE, y + Integer.MIN_VALUE);
    }
    
    private static int createPositiveCapacity(final int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError("Unable to allocate array size: " + ((long)minCapacity & 0xFFFFFFFFL));
        }
        return (minCapacity > 2147483639) ? minCapacity : 2147483639;
    }
    
    public static byte[] getChunkSeparator() {
        return BaseNCodec.CHUNK_SEPARATOR.clone();
    }
    
    protected static boolean isWhiteSpace(final byte byteToCheck) {
        switch (byteToCheck) {
            case 9:
            case 10:
            case 13:
            case 32: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static byte[] resizeBuffer(final Context context, final int minCapacity) {
        final int oldCapacity = context.buffer.length;
        int newCapacity = oldCapacity * 2;
        if (compareUnsigned(newCapacity, minCapacity) < 0) {
            newCapacity = minCapacity;
        }
        if (compareUnsigned(newCapacity, 2147483639) > 0) {
            newCapacity = createPositiveCapacity(minCapacity);
        }
        final byte[] b = new byte[newCapacity];
        System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
        return context.buffer = b;
    }
    
    protected BaseNCodec(final int unencodedBlockSize, final int encodedBlockSize, final int lineLength, final int chunkSeparatorLength) {
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, (byte)61);
    }
    
    protected BaseNCodec(final int unencodedBlockSize, final int encodedBlockSize, final int lineLength, final int chunkSeparatorLength, final byte pad) {
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, pad, BaseNCodec.DECODING_POLICY_DEFAULT);
    }
    
    protected BaseNCodec(final int unencodedBlockSize, final int encodedBlockSize, final int lineLength, final int chunkSeparatorLength, final byte pad, final CodecPolicy decodingPolicy) {
        this.unencodedBlockSize = unencodedBlockSize;
        this.encodedBlockSize = encodedBlockSize;
        final boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
        this.lineLength = (useChunking ? (lineLength / encodedBlockSize * encodedBlockSize) : 0);
        this.chunkSeparatorLength = chunkSeparatorLength;
        this.pad = pad;
        this.decodingPolicy = Objects.requireNonNull(decodingPolicy, "codecPolicy");
    }
    
    int available(final Context context) {
        return (context.buffer != null) ? (context.pos - context.readPos) : 0;
    }
    
    protected boolean containsAlphabetOrPad(final byte[] arrayOctet) {
        if (arrayOctet == null) {
            return false;
        }
        for (final byte element : arrayOctet) {
            if (this.pad == element || this.isInAlphabet(element)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public byte[] decode(final byte[] pArray) {
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        final Context context = new Context();
        this.decode(pArray, 0, pArray.length, context);
        this.decode(pArray, 0, -1, context);
        final byte[] result = new byte[context.pos];
        this.readResults(result, 0, result.length, context);
        return result;
    }
    
    abstract void decode(final byte[] p0, final int p1, final int p2, final Context p3);
    
    @Override
    public Object decode(final Object obj) throws DecoderException {
        if (obj instanceof byte[]) {
            return this.decode((byte[])obj);
        }
        if (obj instanceof String) {
            return this.decode((String)obj);
        }
        throw new DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String");
    }
    
    public byte[] decode(final String pArray) {
        return this.decode(StringUtils.getBytesUtf8(pArray));
    }
    
    @Override
    public byte[] encode(final byte[] pArray) {
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        return this.encode(pArray, 0, pArray.length);
    }
    
    public byte[] encode(final byte[] pArray, final int offset, final int length) {
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        final Context context = new Context();
        this.encode(pArray, offset, length, context);
        this.encode(pArray, offset, -1, context);
        final byte[] buf = new byte[context.pos - context.readPos];
        this.readResults(buf, 0, buf.length, context);
        return buf;
    }
    
    abstract void encode(final byte[] p0, final int p1, final int p2, final Context p3);
    
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (!(obj instanceof byte[])) {
            throw new EncoderException("Parameter supplied to Base-N encode is not a byte[]");
        }
        return this.encode((byte[])obj);
    }
    
    public String encodeAsString(final byte[] pArray) {
        return StringUtils.newStringUtf8(this.encode(pArray));
    }
    
    public String encodeToString(final byte[] pArray) {
        return StringUtils.newStringUtf8(this.encode(pArray));
    }
    
    protected byte[] ensureBufferSize(final int size, final Context context) {
        if (context.buffer == null) {
            context.buffer = new byte[Math.max(size, this.getDefaultBufferSize())];
            context.pos = 0;
            context.readPos = 0;
        }
        else if (context.pos + size - context.buffer.length > 0) {
            return resizeBuffer(context, context.pos + size);
        }
        return context.buffer;
    }
    
    public CodecPolicy getCodecPolicy() {
        return this.decodingPolicy;
    }
    
    protected int getDefaultBufferSize() {
        return 8192;
    }
    
    public long getEncodedLength(final byte[] pArray) {
        long len = (pArray.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize * (long)this.encodedBlockSize;
        if (this.lineLength > 0) {
            len += (len + this.lineLength - 1L) / this.lineLength * this.chunkSeparatorLength;
        }
        return len;
    }
    
    boolean hasData(final Context context) {
        return context.buffer != null;
    }
    
    protected abstract boolean isInAlphabet(final byte p0);
    
    public boolean isInAlphabet(final byte[] arrayOctet, final boolean allowWSPad) {
        for (final byte octet : arrayOctet) {
            if (!this.isInAlphabet(octet) && (!allowWSPad || (octet != this.pad && !isWhiteSpace(octet)))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isInAlphabet(final String basen) {
        return this.isInAlphabet(StringUtils.getBytesUtf8(basen), true);
    }
    
    public boolean isStrictDecoding() {
        return this.decodingPolicy == CodecPolicy.STRICT;
    }
    
    int readResults(final byte[] b, final int bPos, final int bAvail, final Context context) {
        if (context.buffer != null) {
            final int len = Math.min(this.available(context), bAvail);
            System.arraycopy(context.buffer, context.readPos, b, bPos, len);
            context.readPos += len;
            if (context.readPos >= context.pos) {
                context.buffer = null;
            }
            return len;
        }
        return context.eof ? -1 : 0;
    }
    
    static {
        DECODING_POLICY_DEFAULT = CodecPolicy.LENIENT;
        CHUNK_SEPARATOR = new byte[] { 13, 10 };
    }
    
    static class Context
    {
        int ibitWorkArea;
        long lbitWorkArea;
        byte[] buffer;
        int pos;
        int readPos;
        boolean eof;
        int currentLinePos;
        int modulus;
        
        @Override
        public String toString() {
            return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", this.getClass().getSimpleName(), Arrays.toString(this.buffer), this.currentLinePos, this.eof, this.ibitWorkArea, this.lbitWorkArea, this.modulus, this.pos, this.readPos);
        }
    }
}
