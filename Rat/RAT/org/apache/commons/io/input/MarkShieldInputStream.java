//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;

public class MarkShieldInputStream extends ProxyInputStream
{
    public MarkShieldInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public void mark(final int readlimit) {
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void reset() throws IOException {
        throw UnsupportedOperationExceptions.reset();
    }
}
