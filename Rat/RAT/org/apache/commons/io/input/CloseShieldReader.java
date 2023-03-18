//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;

public class CloseShieldReader extends ProxyReader
{
    public static CloseShieldReader wrap(final Reader reader) {
        return new CloseShieldReader(reader);
    }
    
    @Deprecated
    public CloseShieldReader(final Reader reader) {
        super(reader);
    }
    
    @Override
    public void close() {
        this.in = (Reader)ClosedReader.CLOSED_READER;
    }
}
