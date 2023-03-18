//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableFunction<T, R, E extends Throwable>
{
    public static final FailableFunction NOP = t -> null;
    
    default <T, E extends Throwable> FailableFunction<T, T, E> identity() {
        return t -> t;
    }
    
    default <T, R, E extends Throwable> FailableFunction<T, R, E> nop() {
        return (FailableFunction<T, R, E>)FailableFunction.NOP;
    }
    
    default <V> FailableFunction<T, V, E> andThen(final FailableFunction<? super R, ? extends V, E> after) {
        Objects.requireNonNull(after);
        return (FailableFunction<T, V, E>)(t -> after.apply(this.apply(t)));
    }
    
    R apply(final T p0) throws E, Throwable;
    
    default <V> FailableFunction<V, R, E> compose(final FailableFunction<? super V, ? extends T, E> before) {
        Objects.requireNonNull(before);
        return v -> this.apply(before.apply((Object)v));
    }
}
