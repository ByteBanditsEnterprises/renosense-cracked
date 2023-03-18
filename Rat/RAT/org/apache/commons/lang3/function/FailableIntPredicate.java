//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableIntPredicate<E extends Throwable>
{
    public static final FailableIntPredicate FALSE = t -> false;
    public static final FailableIntPredicate TRUE = t -> true;
    
    default <E extends Throwable> FailableIntPredicate<E> falsePredicate() {
        return (FailableIntPredicate<E>)FailableIntPredicate.FALSE;
    }
    
    default <E extends Throwable> FailableIntPredicate<E> truePredicate() {
        return (FailableIntPredicate<E>)FailableIntPredicate.TRUE;
    }
    
    default FailableIntPredicate<E> and(final FailableIntPredicate<E> other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) && other.test(t);
    }
    
    default FailableIntPredicate<E> negate() {
        return t -> !this.test(t);
    }
    
    default FailableIntPredicate<E> or(final FailableIntPredicate<E> other) {
        Objects.requireNonNull(other);
        return t -> this.test(t) || other.test(t);
    }
    
    boolean test(final int p0) throws E, Throwable;
}
