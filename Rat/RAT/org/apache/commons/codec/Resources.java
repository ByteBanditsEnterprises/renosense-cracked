//Raddon On Top!

package org.apache.commons.codec;

import java.io.*;

public class Resources
{
    public static InputStream getInputStream(final String name) {
        final InputStream inputStream = Resources.class.getClassLoader().getResourceAsStream(name);
        if (inputStream == null) {
            throw new IllegalArgumentException("Unable to resolve required resource: " + name);
        }
        return inputStream;
    }
}
