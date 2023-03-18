//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableDoubleToIntFunction<E extends Throwable>
{
    public static final FailableDoubleToIntFunction NOP = t -> 0;
    
    default <E extends Throwable> FailableDoubleToIntFunction<E> nop() {
        return (FailableDoubleToIntFunction<E>)FailableDoubleToIntFunction.NOP;
    }
    
    int applyAsInt(final double p0) throws E, Throwable;
}
