//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongSupplier<E extends Throwable>
{
    long getAsLong() throws E, Throwable;
}
