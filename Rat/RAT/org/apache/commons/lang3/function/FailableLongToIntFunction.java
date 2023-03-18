//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongToIntFunction<E extends Throwable>
{
    public static final FailableLongToIntFunction NOP = t -> 0;
    
    default <E extends Throwable> FailableLongToIntFunction<E> nop() {
        return (FailableLongToIntFunction<E>)FailableLongToIntFunction.NOP;
    }
    
    int applyAsInt(final long p0) throws E, Throwable;
}
