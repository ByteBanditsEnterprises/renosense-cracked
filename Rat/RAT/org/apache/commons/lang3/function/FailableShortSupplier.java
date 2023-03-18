//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableShortSupplier<E extends Throwable>
{
    short getAsShort() throws E, Throwable;
}
