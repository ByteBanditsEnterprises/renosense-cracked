//Raddon On Top!

package org.apache.commons.codec.digest;

import org.apache.commons.codec.binary.*;

public final class MurmurHash3
{
    @Deprecated
    public static final long NULL_HASHCODE = 2862933555777941757L;
    public static final int DEFAULT_SEED = 104729;
    static final int LONG_BYTES = 8;
    static final int INTEGER_BYTES = 4;
    static final int SHORT_BYTES = 2;
    private static final int C1_32 = -862048943;
    private static final int C2_32 = 461845907;
    private static final int R1_32 = 15;
    private static final int R2_32 = 13;
    private static final int M_32 = 5;
    private static final int N_32 = -430675100;
    private static final long C1 = -8663945395140668459L;
    private static final long C2 = 5545529020109919103L;
    private static final int R1 = 31;
    private static final int R2 = 27;
    private static final int R3 = 33;
    private static final int M = 5;
    private static final int N1 = 1390208809;
    private static final int N2 = 944331445;
    
    private MurmurHash3() {
    }
    
    public static int hash32(final long data1, final long data2) {
        return hash32(data1, data2, 104729);
    }
    
    public static int hash32(final long data1, final long data2, final int seed) {
        int hash = seed;
        final long r0 = Long.reverseBytes(data1);
        final long r2 = Long.reverseBytes(data2);
        hash = mix32((int)r0, hash);
        hash = mix32((int)(r0 >>> 32), hash);
        hash = mix32((int)r2, hash);
        hash = mix32((int)(r2 >>> 32), hash);
        hash ^= 0x10;
        return fmix32(hash);
    }
    
    public static int hash32(final long data) {
        return hash32(data, 104729);
    }
    
    public static int hash32(final long data, final int seed) {
        int hash = seed;
        final long r0 = Long.reverseBytes(data);
        hash = mix32((int)r0, hash);
        hash = mix32((int)(r0 >>> 32), hash);
        hash ^= 0x8;
        return fmix32(hash);
    }
    
    @Deprecated
    public static int hash32(final byte[] data) {
        return hash32(data, 0, data.length, 104729);
    }
    
    @Deprecated
    public static int hash32(final String data) {
        final byte[] bytes = StringUtils.getBytesUtf8(data);
        return hash32(bytes, 0, bytes.length, 104729);
    }
    
    @Deprecated
    public static int hash32(final byte[] data, final int length) {
        return hash32(data, length, 104729);
    }
    
    @Deprecated
    public static int hash32(final byte[] data, final int length, final int seed) {
        return hash32(data, 0, length, seed);
    }
    
    @Deprecated
    public static int hash32(final byte[] data, final int offset, final int length, final int seed) {
        int hash = seed;
        final int nblocks = length >> 2;
        for (int i = 0; i < nblocks; ++i) {
            final int index = offset + (i << 2);
            final int k = getLittleEndianInt(data, index);
            hash = mix32(k, hash);
        }
        final int index2 = offset + (nblocks << 2);
        int k2 = 0;
        switch (offset + length - index2) {
            case 3: {
                k2 ^= data[index2 + 2] << 16;
            }
            case 2: {
                k2 ^= data[index2 + 1] << 8;
            }
            case 1: {
                k2 ^= data[index2];
                k2 *= -862048943;
                k2 = Integer.rotateLeft(k2, 15);
                k2 *= 461845907;
                hash ^= k2;
                break;
            }
        }
        hash ^= length;
        return fmix32(hash);
    }
    
    public static int hash32x86(final byte[] data) {
        return hash32x86(data, 0, data.length, 0);
    }
    
    public static int hash32x86(final byte[] data, final int offset, final int length, final int seed) {
        int hash = seed;
        final int nblocks = length >> 2;
        for (int i = 0; i < nblocks; ++i) {
            final int index = offset + (i << 2);
            final int k = getLittleEndianInt(data, index);
            hash = mix32(k, hash);
        }
        final int index2 = offset + (nblocks << 2);
        int k2 = 0;
        switch (offset + length - index2) {
            case 3: {
                k2 ^= (data[index2 + 2] & 0xFF) << 16;
            }
            case 2: {
                k2 ^= (data[index2 + 1] & 0xFF) << 8;
            }
            case 1: {
                k2 ^= (data[index2] & 0xFF);
                k2 *= -862048943;
                k2 = Integer.rotateLeft(k2, 15);
                k2 *= 461845907;
                hash ^= k2;
                break;
            }
        }
        hash ^= length;
        return fmix32(hash);
    }
    
    @Deprecated
    public static long hash64(final long data) {
        long hash = 104729L;
        long k = Long.reverseBytes(data);
        final int length = 8;
        k *= -8663945395140668459L;
        k = Long.rotateLeft(k, 31);
        k *= 5545529020109919103L;
        hash ^= k;
        hash = Long.rotateLeft(hash, 27) * 5L + 1390208809L;
        hash ^= 0x8L;
        hash = fmix64(hash);
        return hash;
    }
    
    @Deprecated
    public static long hash64(final int data) {
        long k1 = (long)Integer.reverseBytes(data) & 0xFFFFFFFFL;
        final int length = 4;
        long hash = 104729L;
        k1 *= -8663945395140668459L;
        k1 = Long.rotateLeft(k1, 31);
        k1 *= 5545529020109919103L;
        hash ^= k1;
        hash ^= 0x4L;
        hash = fmix64(hash);
        return hash;
    }
    
    @Deprecated
    public static long hash64(final short data) {
        long hash = 104729L;
        long k1 = 0L;
        k1 ^= ((long)data & 0xFFL) << 8;
        k1 ^= ((long)((data & 0xFF00) >> 8) & 0xFFL);
        k1 *= -8663945395140668459L;
        k1 = Long.rotateLeft(k1, 31);
        k1 *= 5545529020109919103L;
        hash ^= k1;
        hash ^= 0x2L;
        hash = fmix64(hash);
        return hash;
    }
    
    @Deprecated
    public static long hash64(final byte[] data) {
        return hash64(data, 0, data.length, 104729);
    }
    
    @Deprecated
    public static long hash64(final byte[] data, final int offset, final int length) {
        return hash64(data, offset, length, 104729);
    }
    
    @Deprecated
    public static long hash64(final byte[] data, final int offset, final int length, final int seed) {
        long hash = seed;
        final int nblocks = length >> 3;
        for (int i = 0; i < nblocks; ++i) {
            final int index = offset + (i << 3);
            long k = getLittleEndianLong(data, index);
            k *= -8663945395140668459L;
            k = Long.rotateLeft(k, 31);
            k *= 5545529020109919103L;
            hash ^= k;
            hash = Long.rotateLeft(hash, 27) * 5L + 1390208809L;
        }
        long k2 = 0L;
        final int index2 = offset + (nblocks << 3);
        switch (offset + length - index2) {
            case 7: {
                k2 ^= ((long)data[index2 + 6] & 0xFFL) << 48;
            }
            case 6: {
                k2 ^= ((long)data[index2 + 5] & 0xFFL) << 40;
            }
            case 5: {
                k2 ^= ((long)data[index2 + 4] & 0xFFL) << 32;
            }
            case 4: {
                k2 ^= ((long)data[index2 + 3] & 0xFFL) << 24;
            }
            case 3: {
                k2 ^= ((long)data[index2 + 2] & 0xFFL) << 16;
            }
            case 2: {
                k2 ^= ((long)data[index2 + 1] & 0xFFL) << 8;
            }
            case 1: {
                k2 ^= ((long)data[index2] & 0xFFL);
                k2 *= -8663945395140668459L;
                k2 = Long.rotateLeft(k2, 31);
                k2 *= 5545529020109919103L;
                hash ^= k2;
                break;
            }
        }
        hash ^= length;
        hash = fmix64(hash);
        return hash;
    }
    
    public static long[] hash128(final byte[] data) {
        return hash128(data, 0, data.length, 104729);
    }
    
    public static long[] hash128x64(final byte[] data) {
        return hash128x64(data, 0, data.length, 0);
    }
    
    @Deprecated
    public static long[] hash128(final String data) {
        final byte[] bytes = StringUtils.getBytesUtf8(data);
        return hash128(bytes, 0, bytes.length, 104729);
    }
    
    @Deprecated
    public static long[] hash128(final byte[] data, final int offset, final int length, final int seed) {
        return hash128x64Internal(data, offset, length, seed);
    }
    
    public static long[] hash128x64(final byte[] data, final int offset, final int length, final int seed) {
        return hash128x64Internal(data, offset, length, (long)seed & 0xFFFFFFFFL);
    }
    
    private static long[] hash128x64Internal(final byte[] data, final int offset, final int length, final long seed) {
        long h1 = seed;
        long h2 = seed;
        final int nblocks = length >> 4;
        for (int i = 0; i < nblocks; ++i) {
            final int index = offset + (i << 4);
            long k1 = getLittleEndianLong(data, index);
            long k2 = getLittleEndianLong(data, index + 8);
            k1 *= -8663945395140668459L;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= 5545529020109919103L;
            h1 ^= k1;
            h1 = Long.rotateLeft(h1, 27);
            h1 += h2;
            h1 = h1 * 5L + 1390208809L;
            k2 *= 5545529020109919103L;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= -8663945395140668459L;
            h2 ^= k2;
            h2 = Long.rotateLeft(h2, 31);
            h2 += h1;
            h2 = h2 * 5L + 944331445L;
        }
        long k3 = 0L;
        long k4 = 0L;
        final int index2 = offset + (nblocks << 4);
        switch (offset + length - index2) {
            case 15: {
                k4 ^= ((long)data[index2 + 14] & 0xFFL) << 48;
            }
            case 14: {
                k4 ^= ((long)data[index2 + 13] & 0xFFL) << 40;
            }
            case 13: {
                k4 ^= ((long)data[index2 + 12] & 0xFFL) << 32;
            }
            case 12: {
                k4 ^= ((long)data[index2 + 11] & 0xFFL) << 24;
            }
            case 11: {
                k4 ^= ((long)data[index2 + 10] & 0xFFL) << 16;
            }
            case 10: {
                k4 ^= ((long)data[index2 + 9] & 0xFFL) << 8;
            }
            case 9: {
                k4 ^= (data[index2 + 8] & 0xFF);
                k4 *= 5545529020109919103L;
                k4 = Long.rotateLeft(k4, 33);
                k4 *= -8663945395140668459L;
                h2 ^= k4;
            }
            case 8: {
                k3 ^= ((long)data[index2 + 7] & 0xFFL) << 56;
            }
            case 7: {
                k3 ^= ((long)data[index2 + 6] & 0xFFL) << 48;
            }
            case 6: {
                k3 ^= ((long)data[index2 + 5] & 0xFFL) << 40;
            }
            case 5: {
                k3 ^= ((long)data[index2 + 4] & 0xFFL) << 32;
            }
            case 4: {
                k3 ^= ((long)data[index2 + 3] & 0xFFL) << 24;
            }
            case 3: {
                k3 ^= ((long)data[index2 + 2] & 0xFFL) << 16;
            }
            case 2: {
                k3 ^= ((long)data[index2 + 1] & 0xFFL) << 8;
            }
            case 1: {
                k3 ^= (data[index2] & 0xFF);
                k3 *= -8663945395140668459L;
                k3 = Long.rotateLeft(k3, 31);
                k3 *= 5545529020109919103L;
                h1 ^= k3;
                break;
            }
        }
        h1 ^= length;
        h2 ^= length;
        h1 += h2;
        h2 += h1;
        h1 = fmix64(h1);
        h2 = fmix64(h2);
        h1 += h2;
        h2 += h1;
        return new long[] { h1, h2 };
    }
    
    private static long getLittleEndianLong(final byte[] data, final int index) {
        return ((long)data[index] & 0xFFL) | ((long)data[index + 1] & 0xFFL) << 8 | ((long)data[index + 2] & 0xFFL) << 16 | ((long)data[index + 3] & 0xFFL) << 24 | ((long)data[index + 4] & 0xFFL) << 32 | ((long)data[index + 5] & 0xFFL) << 40 | ((long)data[index + 6] & 0xFFL) << 48 | ((long)data[index + 7] & 0xFFL) << 56;
    }
    
    private static int getLittleEndianInt(final byte[] data, final int index) {
        return (data[index] & 0xFF) | (data[index + 1] & 0xFF) << 8 | (data[index + 2] & 0xFF) << 16 | (data[index + 3] & 0xFF) << 24;
    }
    
    private static int mix32(int k, int hash) {
        k *= -862048943;
        k = Integer.rotateLeft(k, 15);
        k *= 461845907;
        hash ^= k;
        return Integer.rotateLeft(hash, 13) * 5 - 430675100;
    }
    
    private static int fmix32(int hash) {
        hash ^= hash >>> 16;
        hash *= -2048144789;
        hash ^= hash >>> 13;
        hash *= -1028477387;
        hash ^= hash >>> 16;
        return hash;
    }
    
    private static long fmix64(long hash) {
        hash ^= hash >>> 33;
        hash *= -49064778989728563L;
        hash ^= hash >>> 33;
        hash *= -4265267296055464877L;
        hash ^= hash >>> 33;
        return hash;
    }
    
    public static class IncrementalHash32x86
    {
        private static final int BLOCK_SIZE = 4;
        private final byte[] unprocessed;
        private int unprocessedLength;
        private int totalLen;
        private int hash;
        
        public IncrementalHash32x86() {
            this.unprocessed = new byte[3];
        }
        
        public final void start(final int seed) {
            final int n = 0;
            this.totalLen = n;
            this.unprocessedLength = n;
            this.hash = seed;
        }
        
        public final void add(final byte[] data, final int offset, final int length) {
            if (length <= 0) {
                return;
            }
            this.totalLen += length;
            if (this.unprocessedLength + length - 4 < 0) {
                System.arraycopy(data, offset, this.unprocessed, this.unprocessedLength, length);
                this.unprocessedLength += length;
                return;
            }
            int newOffset;
            int newLength;
            if (this.unprocessedLength > 0) {
                int k = -1;
                switch (this.unprocessedLength) {
                    case 1: {
                        k = orBytes(this.unprocessed[0], data[offset], data[offset + 1], data[offset + 2]);
                        break;
                    }
                    case 2: {
                        k = orBytes(this.unprocessed[0], this.unprocessed[1], data[offset], data[offset + 1]);
                        break;
                    }
                    case 3: {
                        k = orBytes(this.unprocessed[0], this.unprocessed[1], this.unprocessed[2], data[offset]);
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unprocessed length should be 1, 2, or 3: " + this.unprocessedLength);
                    }
                }
                this.hash = mix32(k, this.hash);
                final int consumed = 4 - this.unprocessedLength;
                newOffset = offset + consumed;
                newLength = length - consumed;
            }
            else {
                newOffset = offset;
                newLength = length;
            }
            final int nblocks = newLength >> 2;
            for (int i = 0; i < nblocks; ++i) {
                final int index = newOffset + (i << 2);
                final int j = getLittleEndianInt(data, index);
                this.hash = mix32(j, this.hash);
            }
            final int consumed = nblocks << 2;
            this.unprocessedLength = newLength - consumed;
            if (this.unprocessedLength != 0) {
                System.arraycopy(data, newOffset + consumed, this.unprocessed, 0, this.unprocessedLength);
            }
        }
        
        public final int end() {
            return this.finalise(this.hash, this.unprocessedLength, this.unprocessed, this.totalLen);
        }
        
        int finalise(final int hash, final int unprocessedLength, final byte[] unprocessed, final int totalLen) {
            int result = hash;
            int k1 = 0;
            switch (unprocessedLength) {
                case 3: {
                    k1 ^= (unprocessed[2] & 0xFF) << 16;
                }
                case 2: {
                    k1 ^= (unprocessed[1] & 0xFF) << 8;
                }
                case 1: {
                    k1 ^= (unprocessed[0] & 0xFF);
                    k1 *= -862048943;
                    k1 = Integer.rotateLeft(k1, 15);
                    k1 *= 461845907;
                    result ^= k1;
                    break;
                }
            }
            result ^= totalLen;
            return fmix32(result);
        }
        
        private static int orBytes(final byte b1, final byte b2, final byte b3, final byte b4) {
            return (b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24;
        }
    }
    
    @Deprecated
    public static class IncrementalHash32 extends IncrementalHash32x86
    {
        @Deprecated
        @Override
        int finalise(final int hash, final int unprocessedLength, final byte[] unprocessed, final int totalLen) {
            int result = hash;
            int k1 = 0;
            switch (unprocessedLength) {
                case 3: {
                    k1 ^= unprocessed[2] << 16;
                }
                case 2: {
                    k1 ^= unprocessed[1] << 8;
                }
                case 1: {
                    k1 ^= unprocessed[0];
                    k1 *= -862048943;
                    k1 = Integer.rotateLeft(k1, 15);
                    k1 *= 461845907;
                    result ^= k1;
                    break;
                }
            }
            result ^= totalLen;
            return fmix32(result);
        }
    }
}
