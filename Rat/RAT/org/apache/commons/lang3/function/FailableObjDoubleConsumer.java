//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableObjDoubleConsumer<T, E extends Throwable>
{
    public static final FailableObjDoubleConsumer NOP = (t, u) -> {};
    
    default <T, E extends Throwable> FailableObjDoubleConsumer<T, E> nop() {
        return (FailableObjDoubleConsumer<T, E>)FailableObjDoubleConsumer.NOP;
    }
    
    void accept(final T p0, final double p1) throws E, Throwable;
}
