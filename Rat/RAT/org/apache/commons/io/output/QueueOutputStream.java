//Raddon On Top!

package org.apache.commons.io.output;

import java.util.concurrent.*;
import java.util.*;
import org.apache.commons.io.input.*;
import java.io.*;

public class QueueOutputStream extends OutputStream
{
    private final BlockingQueue<Integer> blockingQueue;
    
    public QueueOutputStream() {
        this(new LinkedBlockingQueue<Integer>());
    }
    
    public QueueOutputStream(final BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = Objects.requireNonNull(blockingQueue, "blockingQueue");
    }
    
    public QueueInputStream newQueueInputStream() {
        return new QueueInputStream((BlockingQueue)this.blockingQueue);
    }
    
    @Override
    public void write(final int b) throws InterruptedIOException {
        try {
            this.blockingQueue.put(0xFF & b);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            final InterruptedIOException interruptedIoException = new InterruptedIOException();
            interruptedIoException.initCause(e);
            throw interruptedIoException;
        }
    }
}
