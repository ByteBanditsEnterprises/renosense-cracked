//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import java.util.concurrent.*;
import java.util.*;
import org.apache.commons.io.output.*;

public class QueueInputStream extends InputStream
{
    private final BlockingQueue<Integer> blockingQueue;
    
    public QueueInputStream() {
        this(new LinkedBlockingQueue<Integer>());
    }
    
    public QueueInputStream(final BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = Objects.requireNonNull(blockingQueue, "blockingQueue");
    }
    
    public QueueOutputStream newQueueOutputStream() {
        return new QueueOutputStream(this.blockingQueue);
    }
    
    @Override
    public int read() {
        final Integer value = this.blockingQueue.poll();
        return (value == null) ? -1 : (0xFF & value);
    }
}
