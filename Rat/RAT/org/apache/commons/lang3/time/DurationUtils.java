//Raddon On Top!

package org.apache.commons.lang3.time;

import org.apache.commons.lang3.function.*;
import java.time.*;
import java.util.concurrent.*;
import java.util.*;
import java.time.temporal.*;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.*;

public class DurationUtils
{
    static final Range<Long> LONG_TO_INT_RANGE;
    
    public static <T extends Throwable> void accept(final FailableBiConsumer<Long, Integer, T> consumer, final Duration duration) throws T, Throwable {
        if (consumer != null && duration != null) {
            consumer.accept((Object)duration.toMillis(), (Object)getNanosOfMiili(duration));
        }
    }
    
    public static int getNanosOfMiili(final Duration duration) {
        return duration.getNano() % 1000000;
    }
    
    public static boolean isPositive(final Duration duration) {
        return !duration.isNegative() && !duration.isZero();
    }
    
    static ChronoUnit toChronoUnit(final TimeUnit timeUnit) {
        switch (Objects.requireNonNull(timeUnit)) {
            case NANOSECONDS: {
                return ChronoUnit.NANOS;
            }
            case MICROSECONDS: {
                return ChronoUnit.MICROS;
            }
            case MILLISECONDS: {
                return ChronoUnit.MILLIS;
            }
            case SECONDS: {
                return ChronoUnit.SECONDS;
            }
            case MINUTES: {
                return ChronoUnit.MINUTES;
            }
            case HOURS: {
                return ChronoUnit.HOURS;
            }
            case DAYS: {
                return ChronoUnit.DAYS;
            }
            default: {
                throw new IllegalArgumentException(timeUnit.toString());
            }
        }
    }
    
    public static Duration toDuration(final long amount, final TimeUnit timeUnit) {
        return Duration.of(amount, toChronoUnit(timeUnit));
    }
    
    public static int toMillisInt(final Duration duration) {
        Objects.requireNonNull(duration, "duration");
        return ((Long)DurationUtils.LONG_TO_INT_RANGE.fit((Object)duration.toMillis())).intValue();
    }
    
    public static Duration zeroIfNull(final Duration duration) {
        return (Duration)ObjectUtils.defaultIfNull((Object)duration, (Object)Duration.ZERO);
    }
    
    static {
        LONG_TO_INT_RANGE = Range.between((Comparable)NumberUtils.LONG_INT_MIN_VALUE, (Comparable)NumberUtils.LONG_INT_MAX_VALUE);
    }
}
