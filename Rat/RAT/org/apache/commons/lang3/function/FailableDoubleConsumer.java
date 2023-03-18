//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableDoubleConsumer<E extends Throwable>
{
    public static final FailableDoubleConsumer NOP = t -> {};
    
    default <E extends Throwable> FailableDoubleConsumer<E> nop() {
        return (FailableDoubleConsumer<E>)FailableDoubleConsumer.NOP;
    }
    
    void accept(final double p0) throws E, Throwable;
    
    default FailableDoubleConsumer<E> andThen(final FailableDoubleConsumer<E> after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
