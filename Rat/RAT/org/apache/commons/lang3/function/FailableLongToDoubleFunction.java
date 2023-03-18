//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongToDoubleFunction<E extends Throwable>
{
    public static final FailableLongToDoubleFunction NOP = t -> 0.0;
    
    default <E extends Throwable> FailableLongToDoubleFunction<E> nop() {
        return (FailableLongToDoubleFunction<E>)FailableLongToDoubleFunction.NOP;
    }
    
    double applyAsDouble(final long p0) throws E, Throwable;
}
