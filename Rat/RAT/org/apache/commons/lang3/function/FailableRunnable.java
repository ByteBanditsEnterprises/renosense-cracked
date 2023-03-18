//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableRunnable<E extends Throwable>
{
    void run() throws E, Throwable;
}
