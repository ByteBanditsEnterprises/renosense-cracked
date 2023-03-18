//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableIntConsumer<E extends Throwable>
{
    public static final FailableIntConsumer NOP = t -> {};
    
    default <E extends Throwable> FailableIntConsumer<E> nop() {
        return (FailableIntConsumer<E>)FailableIntConsumer.NOP;
    }
    
    void accept(final int p0) throws E, Throwable;
    
    default FailableIntConsumer<E> andThen(final FailableIntConsumer<E> after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
