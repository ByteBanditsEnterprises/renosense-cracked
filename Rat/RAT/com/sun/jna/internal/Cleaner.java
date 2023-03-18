//Raddon On Top!

package com.sun.jna.internal;

import java.util.logging.*;
import java.lang.ref.*;

public class Cleaner
{
    private static final Cleaner INSTANCE;
    private final ReferenceQueue<Object> referenceQueue;
    private final Thread cleanerThread;
    private CleanerRef firstCleanable;
    
    public static Cleaner getCleaner() {
        return Cleaner.INSTANCE;
    }
    
    private Cleaner() {
        this.referenceQueue = new ReferenceQueue<Object>();
        (this.cleanerThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            final Reference<?> ref = (Reference<?>)Cleaner.this.referenceQueue.remove();
                            if (ref instanceof CleanerRef) {
                                ((CleanerRef)ref).clean();
                            }
                        }
                    }
                    catch (InterruptedException ex2) {}
                    catch (Exception ex) {
                        Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, null, ex);
                        continue;
                    }
                    break;
                }
            }
        }).setName("JNA Cleaner");
        this.cleanerThread.setDaemon(true);
        this.cleanerThread.start();
    }
    
    public synchronized Cleanable register(final Object obj, final Runnable cleanupTask) {
        return this.add(new CleanerRef(this, obj, this.referenceQueue, cleanupTask));
    }
    
    private synchronized CleanerRef add(final CleanerRef ref) {
        if (this.firstCleanable == null) {
            this.firstCleanable = ref;
        }
        else {
            ref.setNext(this.firstCleanable);
            this.firstCleanable.setPrevious(ref);
            this.firstCleanable = ref;
        }
        return ref;
    }
    
    private synchronized boolean remove(final CleanerRef ref) {
        boolean inChain = false;
        if (ref == this.firstCleanable) {
            this.firstCleanable = ref.getNext();
            inChain = true;
        }
        if (ref.getPrevious() != null) {
            ref.getPrevious().setNext(ref.getNext());
        }
        if (ref.getNext() != null) {
            ref.getNext().setPrevious(ref.getPrevious());
        }
        if (ref.getPrevious() != null || ref.getNext() != null) {
            inChain = true;
        }
        ref.setNext(null);
        ref.setPrevious(null);
        return inChain;
    }
    
    static {
        INSTANCE = new Cleaner();
    }
    
    private static class CleanerRef extends PhantomReference<Object> implements Cleanable
    {
        private final Cleaner cleaner;
        private final Runnable cleanupTask;
        private CleanerRef previous;
        private CleanerRef next;
        
        public CleanerRef(final Cleaner cleaner, final Object referent, final ReferenceQueue<? super Object> q, final Runnable cleanupTask) {
            super(referent, q);
            this.cleaner = cleaner;
            this.cleanupTask = cleanupTask;
        }
        
        @Override
        public void clean() {
            if (this.cleaner.remove(this)) {
                this.cleanupTask.run();
            }
        }
        
        CleanerRef getPrevious() {
            return this.previous;
        }
        
        void setPrevious(final CleanerRef previous) {
            this.previous = previous;
        }
        
        CleanerRef getNext() {
            return this.next;
        }
        
        void setNext(final CleanerRef next) {
            this.next = next;
        }
    }
    
    public interface Cleanable
    {
        void clean();
    }
}
