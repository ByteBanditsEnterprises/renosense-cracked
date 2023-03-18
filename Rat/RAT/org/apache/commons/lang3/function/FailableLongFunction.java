//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongFunction<R, E extends Throwable>
{
    public static final FailableLongFunction NOP = t -> null;
    
    default <R, E extends Throwable> FailableLongFunction<R, E> nop() {
        return (FailableLongFunction<R, E>)FailableLongFunction.NOP;
    }
    
    R apply(final long p0) throws E, Throwable;
}
