//Raddon On Top!

package org.apache.commons.codec.binary;

import org.apache.commons.codec.*;

public class Base32 extends BaseNCodec
{
    private static final int BITS_PER_ENCODED_BYTE = 5;
    private static final int BYTES_PER_ENCODED_BLOCK = 8;
    private static final int BYTES_PER_UNENCODED_BLOCK = 5;
    private static final byte[] DECODE_TABLE;
    private static final byte[] ENCODE_TABLE;
    private static final byte[] HEX_DECODE_TABLE;
    private static final byte[] HEX_ENCODE_TABLE;
    private static final int MASK_5BITS = 31;
    private static final long MASK_4BITS = 15L;
    private static final long MASK_3BITS = 7L;
    private static final long MASK_2BITS = 3L;
    private static final long MASK_1BITS = 1L;
    private final int decodeSize;
    private final byte[] decodeTable;
    private final int encodeSize;
    private final byte[] encodeTable;
    private final byte[] lineSeparator;
    
    public Base32() {
        this(false);
    }
    
    public Base32(final boolean useHex) {
        this(0, null, useHex, (byte)61);
    }
    
    public Base32(final boolean useHex, final byte padding) {
        this(0, null, useHex, padding);
    }
    
    public Base32(final byte pad) {
        this(false, pad);
    }
    
    public Base32(final int lineLength) {
        this(lineLength, Base32.CHUNK_SEPARATOR);
    }
    
    public Base32(final int lineLength, final byte[] lineSeparator) {
        this(lineLength, lineSeparator, false, (byte)61);
    }
    
    public Base32(final int lineLength, final byte[] lineSeparator, final boolean useHex) {
        this(lineLength, lineSeparator, useHex, (byte)61);
    }
    
    public Base32(final int lineLength, final byte[] lineSeparator, final boolean useHex, final byte padding) {
        this(lineLength, lineSeparator, useHex, padding, Base32.DECODING_POLICY_DEFAULT);
    }
    
    public Base32(final int lineLength, final byte[] lineSeparator, final boolean useHex, final byte padding, final CodecPolicy decodingPolicy) {
        super(5, 8, lineLength, (lineSeparator == null) ? 0 : lineSeparator.length, padding, decodingPolicy);
        if (useHex) {
            this.encodeTable = Base32.HEX_ENCODE_TABLE;
            this.decodeTable = Base32.HEX_DECODE_TABLE;
        }
        else {
            this.encodeTable = Base32.ENCODE_TABLE;
            this.decodeTable = Base32.DECODE_TABLE;
        }
        if (lineLength > 0) {
            if (lineSeparator == null) {
                throw new IllegalArgumentException("lineLength " + lineLength + " > 0, but lineSeparator is null");
            }
            if (this.containsAlphabetOrPad(lineSeparator)) {
                final String sep = StringUtils.newStringUtf8(lineSeparator);
                throw new IllegalArgumentException("lineSeparator must not contain Base32 characters: [" + sep + "]");
            }
            this.encodeSize = 8 + lineSeparator.length;
            System.arraycopy(lineSeparator, 0, this.lineSeparator = new byte[lineSeparator.length], 0, lineSeparator.length);
        }
        else {
            this.encodeSize = 8;
            this.lineSeparator = null;
        }
        this.decodeSize = this.encodeSize - 1;
        if (this.isInAlphabet(padding) || BaseNCodec.isWhiteSpace(padding)) {
            throw new IllegalArgumentException("pad must not be in alphabet or whitespace");
        }
    }
    
    @Override
    void decode(final byte[] input, int inPos, final int inAvail, final Context context) {
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
        }
        for (int i = 0; i < inAvail; ++i) {
            final byte b = input[inPos++];
            if (b == this.pad) {
                context.eof = true;
                break;
            }
            final byte[] buffer = this.ensureBufferSize(this.decodeSize, context);
            if (b >= 0 && b < this.decodeTable.length) {
                final int result = this.decodeTable[b];
                if (result >= 0) {
                    context.modulus = (context.modulus + 1) % 8;
                    context.lbitWorkArea = (context.lbitWorkArea << 5) + result;
                    if (context.modulus == 0) {
                        buffer[context.pos++] = (byte)(context.lbitWorkArea >> 32 & 0xFFL);
                        buffer[context.pos++] = (byte)(context.lbitWorkArea >> 24 & 0xFFL);
                        buffer[context.pos++] = (byte)(context.lbitWorkArea >> 16 & 0xFFL);
                        buffer[context.pos++] = (byte)(context.lbitWorkArea >> 8 & 0xFFL);
                        buffer[context.pos++] = (byte)(context.lbitWorkArea & 0xFFL);
                    }
                }
            }
        }
        if (context.eof && context.modulus > 0) {
            final byte[] buffer2 = this.ensureBufferSize(this.decodeSize, context);
            switch (context.modulus) {
                case 1: {
                    this.validateTrailingCharacters();
                }
                case 2: {
                    this.validateCharacter(3L, context);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 2 & 0xFFL);
                    break;
                }
                case 3: {
                    this.validateTrailingCharacters();
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 7 & 0xFFL);
                    break;
                }
                case 4: {
                    this.validateCharacter(15L, context);
                    context.lbitWorkArea >>= 4;
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 8 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea & 0xFFL);
                    break;
                }
                case 5: {
                    this.validateCharacter(1L, context);
                    context.lbitWorkArea >>= 1;
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 16 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 8 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea & 0xFFL);
                    break;
                }
                case 6: {
                    this.validateTrailingCharacters();
                    context.lbitWorkArea >>= 6;
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 16 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 8 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea & 0xFFL);
                    break;
                }
                case 7: {
                    this.validateCharacter(7L, context);
                    context.lbitWorkArea >>= 3;
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 24 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 16 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea >> 8 & 0xFFL);
                    buffer2[context.pos++] = (byte)(context.lbitWorkArea & 0xFFL);
                    break;
                }
                default: {
                    throw new IllegalStateException("Impossible modulus " + context.modulus);
                }
            }
        }
    }
    
    @Override
    void encode(final byte[] input, int inPos, final int inAvail, final Context context) {
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
            if (0 == context.modulus && this.lineLength == 0) {
                return;
            }
            final byte[] buffer = this.ensureBufferSize(this.encodeSize, context);
            final int savedPos = context.pos;
            switch (context.modulus) {
                case 0: {
                    break;
                }
                case 1: {
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 3) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 2) & 0x1F];
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    break;
                }
                case 2: {
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 11) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 6) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 1) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 4) & 0x1F];
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    break;
                }
                case 3: {
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 19) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 14) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 9) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 4) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 1) & 0x1F];
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    buffer[context.pos++] = this.pad;
                    break;
                }
                case 4: {
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 27) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 22) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 17) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 12) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 7) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 2) & 0x1F];
                    buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 3) & 0x1F];
                    buffer[context.pos++] = this.pad;
                    break;
                }
                default: {
                    throw new IllegalStateException("Impossible modulus " + context.modulus);
                }
            }
            context.currentLinePos += context.pos - savedPos;
            if (this.lineLength > 0 && context.currentLinePos > 0) {
                System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
                context.pos += this.lineSeparator.length;
            }
        }
        else {
            for (int i = 0; i < inAvail; ++i) {
                final byte[] buffer2 = this.ensureBufferSize(this.encodeSize, context);
                context.modulus = (context.modulus + 1) % 5;
                int b = input[inPos++];
                if (b < 0) {
                    b += 256;
                }
                context.lbitWorkArea = (context.lbitWorkArea << 8) + b;
                if (0 == context.modulus) {
                    buffer2[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 35) & 0x1F];
                    buffer2[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 30) & 0x1F];
                    buffer2[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 25) & 0x1F];
                    buffer2[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 20) & 0x1F];
                    buffer2[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 15) & 0x1F];
                    buffer2[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 10) & 0x1F];
                    buffer2[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 5) & 0x1F];
                    buffer2[context.pos++] = this.encodeTable[(int)context.lbitWorkArea & 0x1F];
                    context.currentLinePos += 8;
                    if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
                        System.arraycopy(this.lineSeparator, 0, buffer2, context.pos, this.lineSeparator.length);
                        context.pos += this.lineSeparator.length;
                        context.currentLinePos = 0;
                    }
                }
            }
        }
    }
    
    public boolean isInAlphabet(final byte octet) {
        return octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1;
    }
    
    private void validateCharacter(final long emptyBitsMask, final Context context) {
        if (this.isStrictDecoding() && (context.lbitWorkArea & emptyBitsMask) != 0x0L) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character (before the paddings if any) is a valid base 32 alphabet but not a possible encoding. Expected the discarded bits from the character to be zero.");
        }
    }
    
    private void validateTrailingCharacters() {
        if (this.isStrictDecoding()) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character(s) (before the paddings if any) are valid base 32 alphabet but not a possible encoding. Decoding requires either 2, 4, 5, or 7 trailing 5-bit characters to create bytes.");
        }
    }
    
    static {
        DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55 };
        HEX_DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
        HEX_ENCODE_TABLE = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86 };
    }
}
