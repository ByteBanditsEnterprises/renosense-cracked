//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableDoubleToLongFunction<E extends Throwable>
{
    public static final FailableDoubleToLongFunction NOP = t -> 0;
    
    default <E extends Throwable> FailableDoubleToLongFunction<E> nop() {
        return (FailableDoubleToLongFunction<E>)FailableDoubleToLongFunction.NOP;
    }
    
    int applyAsLong(final double p0) throws E, Throwable;
}
