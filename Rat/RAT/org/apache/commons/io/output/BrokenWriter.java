//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class BrokenWriter extends Writer
{
    private final IOException exception;
    
    public BrokenWriter(final IOException exception) {
        this.exception = exception;
    }
    
    public BrokenWriter() {
        this(new IOException("Broken writer"));
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        throw this.exception;
    }
    
    @Override
    public void flush() throws IOException {
        throw this.exception;
    }
    
    @Override
    public void close() throws IOException {
        throw this.exception;
    }
}
