//Raddon On Top!

package org.apache.commons.codec.digest;

import org.apache.commons.codec.binary.*;

public final class MurmurHash2
{
    private static final int M32 = 1540483477;
    private static final int R32 = 24;
    private static final long M64 = -4132994306676758123L;
    private static final int R64 = 47;
    
    private MurmurHash2() {
    }
    
    public static int hash32(final byte[] data, final int length, final int seed) {
        int h = seed ^ length;
        final int nblocks = length >> 2;
        for (int i = 0; i < nblocks; ++i) {
            final int index = i << 2;
            int k = getLittleEndianInt(data, index);
            k *= 1540483477;
            k ^= k >>> 24;
            k *= 1540483477;
            h *= 1540483477;
            h ^= k;
        }
        final int index2 = nblocks << 2;
        switch (length - index2) {
            case 3: {
                h ^= (data[index2 + 2] & 0xFF) << 16;
            }
            case 2: {
                h ^= (data[index2 + 1] & 0xFF) << 8;
            }
            case 1: {
                h ^= (data[index2] & 0xFF);
                h *= 1540483477;
                break;
            }
        }
        h ^= h >>> 13;
        h *= 1540483477;
        h ^= h >>> 15;
        return h;
    }
    
    public static int hash32(final byte[] data, final int length) {
        return hash32(data, length, -1756908916);
    }
    
    public static int hash32(final String text) {
        final byte[] bytes = StringUtils.getBytesUtf8(text);
        return hash32(bytes, bytes.length);
    }
    
    public static int hash32(final String text, final int from, final int length) {
        return hash32(text.substring(from, from + length));
    }
    
    public static long hash64(final byte[] data, final int length, final int seed) {
        long h = ((long)seed & 0xFFFFFFFFL) ^ length * -4132994306676758123L;
        final int nblocks = length >> 3;
        for (int i = 0; i < nblocks; ++i) {
            final int index = i << 3;
            long k = getLittleEndianLong(data, index);
            k *= -4132994306676758123L;
            k ^= k >>> 47;
            k *= -4132994306676758123L;
            h ^= k;
            h *= -4132994306676758123L;
        }
        final int index2 = nblocks << 3;
        switch (length - index2) {
            case 7: {
                h ^= ((long)data[index2 + 6] & 0xFFL) << 48;
            }
            case 6: {
                h ^= ((long)data[index2 + 5] & 0xFFL) << 40;
            }
            case 5: {
                h ^= ((long)data[index2 + 4] & 0xFFL) << 32;
            }
            case 4: {
                h ^= ((long)data[index2 + 3] & 0xFFL) << 24;
            }
            case 3: {
                h ^= ((long)data[index2 + 2] & 0xFFL) << 16;
            }
            case 2: {
                h ^= ((long)data[index2 + 1] & 0xFFL) << 8;
            }
            case 1: {
                h ^= ((long)data[index2] & 0xFFL);
                h *= -4132994306676758123L;
                break;
            }
        }
        h ^= h >>> 47;
        h *= -4132994306676758123L;
        h ^= h >>> 47;
        return h;
    }
    
    public static long hash64(final byte[] data, final int length) {
        return hash64(data, length, -512093083);
    }
    
    public static long hash64(final String text) {
        final byte[] bytes = StringUtils.getBytesUtf8(text);
        return hash64(bytes, bytes.length);
    }
    
    public static long hash64(final String text, final int from, final int length) {
        return hash64(text.substring(from, from + length));
    }
    
    private static int getLittleEndianInt(final byte[] data, final int index) {
        return (data[index] & 0xFF) | (data[index + 1] & 0xFF) << 8 | (data[index + 2] & 0xFF) << 16 | (data[index + 3] & 0xFF) << 24;
    }
    
    private static long getLittleEndianLong(final byte[] data, final int index) {
        return ((long)data[index] & 0xFFL) | ((long)data[index + 1] & 0xFFL) << 8 | ((long)data[index + 2] & 0xFFL) << 16 | ((long)data[index + 3] & 0xFFL) << 24 | ((long)data[index + 4] & 0xFFL) << 32 | ((long)data[index + 5] & 0xFFL) << 40 | ((long)data[index + 6] & 0xFFL) << 48 | ((long)data[index + 7] & 0xFFL) << 56;
    }
}
