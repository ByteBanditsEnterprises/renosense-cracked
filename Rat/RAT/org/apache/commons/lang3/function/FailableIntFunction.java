//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableIntFunction<R, E extends Throwable>
{
    public static final FailableIntFunction NOP = t -> null;
    
    default <R, E extends Throwable> FailableIntFunction<R, E> nop() {
        return (FailableIntFunction<R, E>)FailableIntFunction.NOP;
    }
    
    R apply(final int p0) throws E, Throwable;
}
