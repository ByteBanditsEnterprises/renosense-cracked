//Raddon On Top!

package org.apache.commons.lang3.builder;

@FunctionalInterface
public interface Diffable<T>
{
    DiffResult<T> diff(final T p0);
}
