//Raddon On Top!

package okio;

import java.util.concurrent.*;

final class PushableTimeout extends Timeout
{
    private Timeout pushed;
    private boolean originalHasDeadline;
    private long originalDeadlineNanoTime;
    private long originalTimeoutNanos;
    
    void push(final Timeout pushed) {
        this.pushed = pushed;
        this.originalHasDeadline = pushed.hasDeadline();
        this.originalDeadlineNanoTime = (this.originalHasDeadline ? pushed.deadlineNanoTime() : -1L);
        this.originalTimeoutNanos = pushed.timeoutNanos();
        pushed.timeout(Timeout.minTimeout(this.originalTimeoutNanos, this.timeoutNanos()), TimeUnit.NANOSECONDS);
        if (this.originalHasDeadline && this.hasDeadline()) {
            pushed.deadlineNanoTime(Math.min(this.deadlineNanoTime(), this.originalDeadlineNanoTime));
        }
        else if (this.hasDeadline()) {
            pushed.deadlineNanoTime(this.deadlineNanoTime());
        }
    }
    
    void pop() {
        this.pushed.timeout(this.originalTimeoutNanos, TimeUnit.NANOSECONDS);
        if (this.originalHasDeadline) {
            this.pushed.deadlineNanoTime(this.originalDeadlineNanoTime);
        }
        else {
            this.pushed.clearDeadline();
        }
    }
}
