//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToDoubleBiFunction<T, U, E extends Throwable>
{
    public static final FailableToDoubleBiFunction NOP = (t, u) -> 0.0;
    
    default <T, U, E extends Throwable> FailableToDoubleBiFunction<T, U, E> nop() {
        return (FailableToDoubleBiFunction<T, U, E>)FailableToDoubleBiFunction.NOP;
    }
    
    double applyAsDouble(final T p0, final U p1) throws E, Throwable;
}
