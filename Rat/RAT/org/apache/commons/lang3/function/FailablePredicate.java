//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailablePredicate<T, E extends Throwable>
{
    public static final FailablePredicate FALSE = t -> false;
    public static final FailablePredicate TRUE = t -> true;
    
    default <T, E extends Throwable> FailablePredicate<T, E> falsePredicate() {
        return (FailablePredicate<T, E>)FailablePredicate.FALSE;
    }
    
    default <T, E extends Throwable> FailablePredicate<T, E> truePredicate() {
        return (FailablePredicate<T, E>)FailablePredicate.TRUE;
    }
    
    default FailablePredicate<T, E> and(final FailablePredicate<? super T, E> other) {
        Objects.requireNonNull(other);
        return t -> this.test((T)t) && other.test((Object)t);
    }
    
    default FailablePredicate<T, E> negate() {
        return t -> !this.test(t);
    }
    
    default FailablePredicate<T, E> or(final FailablePredicate<? super T, E> other) {
        Objects.requireNonNull(other);
        return t -> this.test((T)t) || other.test((Object)t);
    }
    
    boolean test(final T p0) throws E, Throwable;
}
