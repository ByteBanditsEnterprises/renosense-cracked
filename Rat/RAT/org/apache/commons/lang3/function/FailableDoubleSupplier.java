//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableDoubleSupplier<E extends Throwable>
{
    double getAsDouble() throws E, Throwable;
}
