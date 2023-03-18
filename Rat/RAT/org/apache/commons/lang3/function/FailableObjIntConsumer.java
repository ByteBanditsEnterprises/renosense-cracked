//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableObjIntConsumer<T, E extends Throwable>
{
    public static final FailableObjIntConsumer NOP = (t, u) -> {};
    
    default <T, E extends Throwable> FailableObjIntConsumer<T, E> nop() {
        return (FailableObjIntConsumer<T, E>)FailableObjIntConsumer.NOP;
    }
    
    void accept(final T p0, final int p1) throws E, Throwable;
}
