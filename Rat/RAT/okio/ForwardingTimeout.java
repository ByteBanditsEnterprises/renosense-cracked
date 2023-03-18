//Raddon On Top!

package okio;

import java.util.concurrent.*;
import java.io.*;

public class ForwardingTimeout extends Timeout
{
    private Timeout delegate;
    
    public ForwardingTimeout(final Timeout delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate == null");
        }
        this.delegate = delegate;
    }
    
    public final Timeout delegate() {
        return this.delegate;
    }
    
    public final ForwardingTimeout setDelegate(final Timeout delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate == null");
        }
        this.delegate = delegate;
        return this;
    }
    
    @Override
    public Timeout timeout(final long timeout, final TimeUnit unit) {
        return this.delegate.timeout(timeout, unit);
    }
    
    @Override
    public long timeoutNanos() {
        return this.delegate.timeoutNanos();
    }
    
    @Override
    public boolean hasDeadline() {
        return this.delegate.hasDeadline();
    }
    
    @Override
    public long deadlineNanoTime() {
        return this.delegate.deadlineNanoTime();
    }
    
    @Override
    public Timeout deadlineNanoTime(final long deadlineNanoTime) {
        return this.delegate.deadlineNanoTime(deadlineNanoTime);
    }
    
    @Override
    public Timeout clearTimeout() {
        return this.delegate.clearTimeout();
    }
    
    @Override
    public Timeout clearDeadline() {
        return this.delegate.clearDeadline();
    }
    
    @Override
    public void throwIfReached() throws IOException {
        this.delegate.throwIfReached();
    }
}
