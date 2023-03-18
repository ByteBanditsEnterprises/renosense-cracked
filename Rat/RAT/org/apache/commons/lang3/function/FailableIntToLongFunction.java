//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableIntToLongFunction<E extends Throwable>
{
    public static final FailableIntToLongFunction NOP = t -> 0L;
    
    default <E extends Throwable> FailableIntToLongFunction<E> nop() {
        return (FailableIntToLongFunction<E>)FailableIntToLongFunction.NOP;
    }
    
    long applyAsLong(final int p0) throws E, Throwable;
}
