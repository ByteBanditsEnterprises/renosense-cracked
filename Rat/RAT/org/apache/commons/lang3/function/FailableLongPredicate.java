//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableLongPredicate<E extends Throwable>
{
    public static final FailableLongPredicate FALSE = t -> false;
    public static final FailableLongPredicate TRUE = t -> true;
    
    default <E extends Throwable> FailableLongPredicate<E> falsePredicate() {
        return (FailableLongPredicate<E>)FailableLongPredicate.FALSE;
    }
    
    default <E extends Throwable> FailableLongPredicate<E> truePredicate() {
        return (FailableLongPredicate<E>)FailableLongPredicate.TRUE;
    }
    
    default FailableLongPredicate<E> and(final FailableLongPredicate<E> other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }
    
    default FailableLongPredicate<E> negate() {
        return t -> !this.test(t);
    }
    
    default FailableLongPredicate<E> or(final FailableLongPredicate<E> other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }
    
    boolean test(final long p0) throws E, Throwable;
}
