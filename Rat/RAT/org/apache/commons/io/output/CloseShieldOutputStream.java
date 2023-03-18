//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class CloseShieldOutputStream extends ProxyOutputStream
{
    public static CloseShieldOutputStream wrap(final OutputStream outputStream) {
        return new CloseShieldOutputStream(outputStream);
    }
    
    @Deprecated
    public CloseShieldOutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    @Override
    public void close() {
        this.out = (OutputStream)ClosedOutputStream.CLOSED_OUTPUT_STREAM;
    }
}
