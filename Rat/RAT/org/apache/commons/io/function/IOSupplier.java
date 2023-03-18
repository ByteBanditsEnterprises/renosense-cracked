//Raddon On Top!

package org.apache.commons.io.function;

import java.io.*;

@FunctionalInterface
public interface IOSupplier<T>
{
    T get() throws IOException;
}
