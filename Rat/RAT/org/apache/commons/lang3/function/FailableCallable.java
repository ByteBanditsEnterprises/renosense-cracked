//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableCallable<R, E extends Throwable>
{
    R call() throws E, Throwable;
}
