//Raddon On Top!

package okio;

import java.io.*;

public interface Sink extends Closeable, Flushable
{
    void write(final Buffer p0, final long p1) throws IOException;
    
    void flush() throws IOException;
    
    Timeout timeout();
    
    void close() throws IOException;
}
