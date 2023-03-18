//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

@FunctionalInterface
public interface FailableConsumer<T, E extends Throwable>
{
    public static final FailableConsumer NOP = t -> {};
    
    default <T, E extends Throwable> FailableConsumer<T, E> nop() {
        return (FailableConsumer<T, E>)FailableConsumer.NOP;
    }
    
    void accept(final T p0) throws E, Throwable;
    
    default FailableConsumer<T, E> andThen(final FailableConsumer<? super T, E> after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept((T)t);
            after.accept((Object)t);
        };
    }
}
