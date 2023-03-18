//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableLongConsumer<E extends Throwable>
{
    public static final FailableLongConsumer NOP = t -> {};
    
    default <E extends Throwable> FailableLongConsumer<E> nop() {
        return (FailableLongConsumer<E>)FailableLongConsumer.NOP;
    }
    
    void accept(final long p0) throws E, Throwable;
    
    default FailableLongConsumer<E> andThen(final FailableLongConsumer<E> after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
