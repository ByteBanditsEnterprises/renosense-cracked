//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToIntBiFunction<T, U, E extends Throwable>
{
    public static final FailableToIntBiFunction NOP = (t, u) -> 0;
    
    default <T, U, E extends Throwable> FailableToIntBiFunction<T, U, E> nop() {
        return (FailableToIntBiFunction<T, U, E>)FailableToIntBiFunction.NOP;
    }
    
    int applyAsInt(final T p0, final U p1) throws E, Throwable;
}
