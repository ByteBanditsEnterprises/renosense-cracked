//Raddon On Top!

package org.apache.commons.io.function;

import java.io.*;
import java.util.*;

@FunctionalInterface
public interface IOConsumer<T>
{
    public static final IOConsumer<?> NOOP_IO_CONSUMER = t -> {};
    
    default <T> IOConsumer<T> noop() {
        return (IOConsumer<T>)IOConsumer.NOOP_IO_CONSUMER;
    }
    
    void accept(final T p0) throws IOException;
    
    default IOConsumer<T> andThen(final IOConsumer<? super T> after) {
        Objects.requireNonNull(after, "after");
        return t -> {
            this.accept((T)t);
            after.accept((Object)t);
        };
    }
}
