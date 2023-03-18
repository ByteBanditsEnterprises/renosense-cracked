//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToIntFunction<T, E extends Throwable>
{
    public static final FailableToIntFunction NOP = t -> 0;
    
    default <T, E extends Throwable> FailableToIntFunction<T, E> nop() {
        return (FailableToIntFunction<T, E>)FailableToIntFunction.NOP;
    }
    
    int applyAsInt(final T p0) throws E, Throwable;
}
