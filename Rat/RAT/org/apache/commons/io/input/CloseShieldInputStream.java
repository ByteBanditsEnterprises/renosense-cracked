//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;

public class CloseShieldInputStream extends ProxyInputStream
{
    public static CloseShieldInputStream wrap(final InputStream inputStream) {
        return new CloseShieldInputStream(inputStream);
    }
    
    @Deprecated
    public CloseShieldInputStream(final InputStream inputStream) {
        super(inputStream);
    }
    
    @Override
    public void close() {
        this.in = (InputStream)ClosedInputStream.CLOSED_INPUT_STREAM;
    }
}
