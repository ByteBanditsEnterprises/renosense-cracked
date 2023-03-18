//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableDoubleFunction<R, E extends Throwable>
{
    public static final FailableDoubleFunction NOP = t -> null;
    
    default <R, E extends Throwable> FailableDoubleFunction<R, E> nop() {
        return (FailableDoubleFunction<R, E>)FailableDoubleFunction.NOP;
    }
    
    R apply(final double p0) throws E, Throwable;
}
