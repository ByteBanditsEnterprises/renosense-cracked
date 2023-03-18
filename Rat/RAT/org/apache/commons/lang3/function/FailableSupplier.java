//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableSupplier<R, E extends Throwable>
{
    R get() throws E, Throwable;
}
