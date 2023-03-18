//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;

public class AutoCloseInputStream extends ProxyInputStream
{
    public AutoCloseInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
        this.in = ClosedInputStream.CLOSED_INPUT_STREAM;
    }
    
    @Override
    protected void afterRead(final int n) throws IOException {
        if (n == -1) {
            this.close();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
