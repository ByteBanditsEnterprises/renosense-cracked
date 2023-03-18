//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToLongBiFunction<T, U, E extends Throwable>
{
    public static final FailableToLongBiFunction NOP = (t, u) -> 0L;
    
    default <T, U, E extends Throwable> FailableToLongBiFunction<T, U, E> nop() {
        return (FailableToLongBiFunction<T, U, E>)FailableToLongBiFunction.NOP;
    }
    
    long applyAsLong(final T p0, final U p1) throws E, Throwable;
}
