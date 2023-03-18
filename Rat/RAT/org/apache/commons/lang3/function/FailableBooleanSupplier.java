//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableBooleanSupplier<E extends Throwable>
{
    boolean getAsBoolean() throws E, Throwable;
}
