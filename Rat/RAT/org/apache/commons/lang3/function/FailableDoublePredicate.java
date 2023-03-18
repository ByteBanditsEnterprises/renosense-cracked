//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableDoublePredicate<E extends Throwable>
{
    public static final FailableDoublePredicate FALSE = t -> false;
    public static final FailableDoublePredicate TRUE = t -> true;
    
    default <E extends Throwable> FailableDoublePredicate<E> falsePredicate() {
        return (FailableDoublePredicate<E>)FailableDoublePredicate.FALSE;
    }
    
    default <E extends Throwable> FailableDoublePredicate<E> truePredicate() {
        return (FailableDoublePredicate<E>)FailableDoublePredicate.TRUE;
    }
    
    default FailableDoublePredicate<E> and(final FailableDoublePredicate<E> other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }
    
    default FailableDoublePredicate<E> negate() {
        return t -> !this.test(t);
    }
    
    default FailableDoublePredicate<E> or(final FailableDoublePredicate<E> other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }
    
    boolean test(final double p0) throws E, Throwable;
}
