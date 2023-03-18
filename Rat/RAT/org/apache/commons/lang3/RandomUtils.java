//Raddon On Top!

package org.apache.commons.lang3;

import java.util.*;

public class RandomUtils
{
    private static final Random RANDOM;
    
    public static boolean nextBoolean() {
        return RandomUtils.RANDOM.nextBoolean();
    }
    
    public static byte[] nextBytes(final int count) {
        Validate.isTrue(count >= 0, "Count cannot be negative.", new Object[0]);
        final byte[] result = new byte[count];
        RandomUtils.RANDOM.nextBytes(result);
        return result;
    }
    
    public static int nextInt(final int startInclusive, final int endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + RandomUtils.RANDOM.nextInt(endExclusive - startInclusive);
    }
    
    public static int nextInt() {
        return nextInt(0, Integer.MAX_VALUE);
    }
    
    public static long nextLong(final long startInclusive, final long endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0L, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + nextLong(endExclusive - startInclusive);
    }
    
    public static long nextLong() {
        return nextLong(Long.MAX_VALUE);
    }
    
    private static long nextLong(final long n) {
        long bits;
        long val;
        do {
            bits = RandomUtils.RANDOM.nextLong() >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1L) < 0L);
        return val;
    }
    
    public static double nextDouble(final double startInclusive, final double endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0.0, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + (endExclusive - startInclusive) * RandomUtils.RANDOM.nextDouble();
    }
    
    public static double nextDouble() {
        return nextDouble(0.0, Double.MAX_VALUE);
    }
    
    public static float nextFloat(final float startInclusive, final float endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0.0f, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + (endExclusive - startInclusive) * RandomUtils.RANDOM.nextFloat();
    }
    
    public static float nextFloat() {
        return nextFloat(0.0f, Float.MAX_VALUE);
    }
    
    static {
        RANDOM = new Random();
    }
}
