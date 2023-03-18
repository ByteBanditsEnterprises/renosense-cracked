//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableIntSupplier<E extends Throwable>
{
    int getAsInt() throws E, Throwable;
}
