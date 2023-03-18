//Raddon On Top!

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongBinaryOperator<E extends Throwable>
{
    long applyAsLong(final long p0, final long p1) throws E, Throwable;
}
