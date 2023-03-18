//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableBiFunction<T, U, R, E extends Throwable>
{
    public static final FailableBiFunction NOP = (t, u) -> null;
    
    default <T, U, R, E extends Throwable> FailableBiFunction<T, U, R, E> nop() {
        return (FailableBiFunction<T, U, R, E>)FailableBiFunction.NOP;
    }
    
    default <V> FailableBiFunction<T, U, V, E> andThen(final FailableFunction<? super R, ? extends V, E> after) {
        Objects.requireNonNull(after);
        return (FailableBiFunction<T, U, V, E>)((t, u) -> after.apply((Object)this.apply(t, u)));
    }
    
    R apply(final T p0, final U p1) throws E, Throwable;
}
