//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableIntToDoubleFunction<E extends Throwable>
{
    public static final FailableIntToDoubleFunction NOP = t -> 0.0;
    
    default <E extends Throwable> FailableIntToDoubleFunction<E> nop() {
        return (FailableIntToDoubleFunction<E>)FailableIntToDoubleFunction.NOP;
    }
    
    double applyAsDouble(final int p0) throws E, Throwable;
}
