//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToLongFunction<T, E extends Throwable>
{
    public static final FailableToLongFunction NOP = t -> 0L;
    
    default <T, E extends Throwable> FailableToLongFunction<T, E> nop() {
        return (FailableToLongFunction<T, E>)FailableToLongFunction.NOP;
    }
    
    long applyAsLong(final T p0) throws E, Throwable;
}
