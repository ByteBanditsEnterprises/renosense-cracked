//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;

public class ClosedReader extends Reader
{
    public static final ClosedReader CLOSED_READER;
    
    @Override
    public int read(final char[] cbuf, final int off, final int len) {
        return -1;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        CLOSED_READER = new ClosedReader();
    }
}
