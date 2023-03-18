//Raddon On Top!

package org.apache.commons.io;

import java.time.*;

class ThreadMonitor implements Runnable
{
    private final Thread thread;
    private final Duration timeout;
    
    static Thread start(final Duration timeout) {
        return start(Thread.currentThread(), timeout);
    }
    
    static Thread start(final Thread thread, final Duration timeout) {
        if (timeout.isZero() || timeout.isNegative()) {
            return null;
        }
        final ThreadMonitor timout = new ThreadMonitor(thread, timeout);
        final Thread monitor = new Thread(timout, ThreadMonitor.class.getSimpleName());
        monitor.setDaemon(true);
        monitor.start();
        return monitor;
    }
    
    static void stop(final Thread thread) {
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    private ThreadMonitor(final Thread thread, final Duration timeout) {
        this.thread = thread;
        this.timeout = timeout;
    }
    
    @Override
    public void run() {
        try {
            sleep(this.timeout);
            this.thread.interrupt();
        }
        catch (InterruptedException ex) {}
    }
    
    private static void sleep(final Duration duration) throws InterruptedException {
        final long millis = duration.toMillis();
        final long finishAtMillis = System.currentTimeMillis() + millis;
        long remainingMillis = millis;
        do {
            Thread.sleep(remainingMillis);
            remainingMillis = finishAtMillis - System.currentTimeMillis();
        } while (remainingMillis > 0L);
    }
}
