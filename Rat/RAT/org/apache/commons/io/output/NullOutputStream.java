//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class NullOutputStream extends OutputStream
{
    public static final NullOutputStream NULL_OUTPUT_STREAM;
    
    @Deprecated
    public NullOutputStream() {
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
    }
    
    @Override
    public void write(final int b) {
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
    }
    
    static {
        NULL_OUTPUT_STREAM = new NullOutputStream();
    }
}
