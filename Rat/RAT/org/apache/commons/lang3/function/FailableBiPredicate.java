//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableBiPredicate<T, U, E extends Throwable>
{
    public static final FailableBiPredicate FALSE = (t, u) -> false;
    public static final FailableBiPredicate TRUE = (t, u) -> true;
    
    default <T, U, E extends Throwable> FailableBiPredicate<T, U, E> falsePredicate() {
        return (FailableBiPredicate<T, U, E>)FailableBiPredicate.FALSE;
    }
    
    default <T, U, E extends Throwable> FailableBiPredicate<T, U, E> truePredicate() {
        return (FailableBiPredicate<T, U, E>)FailableBiPredicate.TRUE;
    }
    
    default FailableBiPredicate<T, U, E> and(final FailableBiPredicate<? super T, ? super U, E> other) {
        Objects.requireNonNull(other);
        return (t, u) -> this.test((T)t, (U)u) && other.test((Object)t, (Object)u);
    }
    
    default FailableBiPredicate<T, U, E> negate() {
        return (t, u) -> !this.test(t, u);
    }
    
    default FailableBiPredicate<T, U, E> or(final FailableBiPredicate<? super T, ? super U, E> other) {
        Objects.requireNonNull(other);
        return (t, u) -> this.test((T)t, (U)u) || other.test((Object)t, (Object)u);
    }
    
    boolean test(final T p0, final U p1) throws E, Throwable;
}
