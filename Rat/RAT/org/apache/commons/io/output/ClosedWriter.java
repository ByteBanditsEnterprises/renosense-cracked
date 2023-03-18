//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class ClosedWriter extends Writer
{
    public static final ClosedWriter CLOSED_WRITER;
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        throw new IOException("write(" + new String(cbuf) + ", " + off + ", " + len + ") failed: stream is closed");
    }
    
    @Override
    public void flush() throws IOException {
        throw new IOException("flush() failed: stream is closed");
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        CLOSED_WRITER = new ClosedWriter();
    }
}
