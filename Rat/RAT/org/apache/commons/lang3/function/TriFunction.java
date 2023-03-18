//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.function.*;
import java.util.*;

@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    R apply(final T p0, final U p1, final V p2);
    
    default <W> TriFunction<T, U, V, W> andThen(final Function<? super R, ? extends W> after) {
        Objects.requireNonNull(after);
        return (TriFunction<T, U, V, W>)((t, u, v) -> after.apply((Object)this.apply(t, u, v)));
    }
}
