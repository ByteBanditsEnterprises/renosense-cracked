//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableObjLongConsumer<T, E extends Throwable>
{
    public static final FailableObjLongConsumer NOP = (t, u) -> {};
    
    default <T, E extends Throwable> FailableObjLongConsumer<T, E> nop() {
        return (FailableObjLongConsumer<T, E>)FailableObjLongConsumer.NOP;
    }
    
    void accept(final T p0, final long p1) throws E, Throwable;
}
