//Raddon On Top!

package okio;

import java.io.*;

public interface Source extends Closeable
{
    long read(final Buffer p0, final long p1) throws IOException;
    
    Timeout timeout();
    
    void close() throws IOException;
}
