//Raddon On Top!

package okio;

import java.io.*;

public abstract class ForwardingSink implements Sink
{
    private final Sink delegate;
    
    public ForwardingSink(final Sink delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate == null");
        }
        this.delegate = delegate;
    }
    
    public final Sink delegate() {
        return this.delegate;
    }
    
    @Override
    public void write(final Buffer source, final long byteCount) throws IOException {
        this.delegate.write(source, byteCount);
    }
    
    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }
    
    @Override
    public Timeout timeout() {
        return this.delegate.timeout();
    }
    
    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.delegate.toString() + ")";
    }
}
