//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface ToBooleanBiFunction<T, U>
{
    boolean applyAsBoolean(final T p0, final U p1);
}
