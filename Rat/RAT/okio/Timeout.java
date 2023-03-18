//Raddon On Top!

package okio;

import java.util.concurrent.*;
import java.io.*;

public class Timeout
{
    public static final Timeout NONE;
    private boolean hasDeadline;
    private long deadlineNanoTime;
    private long timeoutNanos;
    
    public Timeout timeout(final long timeout, final TimeUnit unit) {
        if (timeout < 0L) {
            throw new IllegalArgumentException("timeout < 0: " + timeout);
        }
        if (unit == null) {
            throw new IllegalArgumentException("unit == null");
        }
        this.timeoutNanos = unit.toNanos(timeout);
        return this;
    }
    
    public long timeoutNanos() {
        return this.timeoutNanos;
    }
    
    public boolean hasDeadline() {
        return this.hasDeadline;
    }
    
    public long deadlineNanoTime() {
        if (!this.hasDeadline) {
            throw new IllegalStateException("No deadline");
        }
        return this.deadlineNanoTime;
    }
    
    public Timeout deadlineNanoTime(final long deadlineNanoTime) {
        this.hasDeadline = true;
        this.deadlineNanoTime = deadlineNanoTime;
        return this;
    }
    
    public final Timeout deadline(final long duration, final TimeUnit unit) {
        if (duration <= 0L) {
            throw new IllegalArgumentException("duration <= 0: " + duration);
        }
        if (unit == null) {
            throw new IllegalArgumentException("unit == null");
        }
        return this.deadlineNanoTime(System.nanoTime() + unit.toNanos(duration));
    }
    
    public Timeout clearTimeout() {
        this.timeoutNanos = 0L;
        return this;
    }
    
    public Timeout clearDeadline() {
        this.hasDeadline = false;
        return this;
    }
    
    public void throwIfReached() throws IOException {
        if (Thread.interrupted()) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("interrupted");
        }
        if (this.hasDeadline && this.deadlineNanoTime - System.nanoTime() <= 0L) {
            throw new InterruptedIOException("deadline reached");
        }
    }
    
    public final void waitUntilNotified(final Object monitor) throws InterruptedIOException {
        try {
            final boolean hasDeadline = this.hasDeadline();
            final long timeoutNanos = this.timeoutNanos();
            if (!hasDeadline && timeoutNanos == 0L) {
                monitor.wait();
                return;
            }
            final long start = System.nanoTime();
            long waitNanos;
            if (hasDeadline && timeoutNanos != 0L) {
                final long deadlineNanos = this.deadlineNanoTime() - start;
                waitNanos = Math.min(timeoutNanos, deadlineNanos);
            }
            else if (hasDeadline) {
                waitNanos = this.deadlineNanoTime() - start;
            }
            else {
                waitNanos = timeoutNanos;
            }
            long elapsedNanos = 0L;
            if (waitNanos > 0L) {
                final long waitMillis = waitNanos / 1000000L;
                monitor.wait(waitMillis, (int)(waitNanos - waitMillis * 1000000L));
                elapsedNanos = System.nanoTime() - start;
            }
            if (elapsedNanos >= waitNanos) {
                throw new InterruptedIOException("timeout");
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("interrupted");
        }
    }
    
    static long minTimeout(final long aNanos, final long bNanos) {
        if (aNanos == 0L) {
            return bNanos;
        }
        if (bNanos == 0L) {
            return aNanos;
        }
        if (aNanos < bNanos) {
            return aNanos;
        }
        return bNanos;
    }
    
    static {
        NONE = new Timeout() {
            @Override
            public Timeout timeout(final long timeout, final TimeUnit unit) {
                return this;
            }
            
            @Override
            public Timeout deadlineNanoTime(final long deadlineNanoTime) {
                return this;
            }
            
            @Override
            public void throwIfReached() throws IOException {
            }
        };
    }
}
