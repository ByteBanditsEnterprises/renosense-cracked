//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class CloseShieldWriter extends ProxyWriter
{
    public static CloseShieldWriter wrap(final Writer writer) {
        return new CloseShieldWriter(writer);
    }
    
    @Deprecated
    public CloseShieldWriter(final Writer writer) {
        super(writer);
    }
    
    @Override
    public void close() {
        this.out = (Writer)ClosedWriter.CLOSED_WRITER;
    }
}
