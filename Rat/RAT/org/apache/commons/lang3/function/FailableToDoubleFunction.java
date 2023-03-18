//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToDoubleFunction<T, E extends Throwable>
{
    public static final FailableToDoubleFunction NOP = t -> 0.0;
    
    default <T, E extends Throwable> FailableToDoubleFunction<T, E> nop() {
        return (FailableToDoubleFunction<T, E>)FailableToDoubleFunction.NOP;
    }
    
    double applyAsDouble(final T p0) throws E, Throwable;
}
