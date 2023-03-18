//Raddon On Top!

package org.apache.commons.lang3.concurrent.locks;

import java.util.function.*;
import java.util.concurrent.locks.*;
import java.util.*;
import org.apache.commons.lang3.function.*;

public class LockingVisitors
{
    public static <O> ReadWriteLockVisitor<O> reentrantReadWriteLockVisitor(final O object) {
        return new ReadWriteLockVisitor<O>(object, new ReentrantReadWriteLock());
    }
    
    public static <O> StampedLockVisitor<O> stampedLockVisitor(final O object) {
        return new StampedLockVisitor<O>(object, new StampedLock());
    }
    
    public static class LockVisitor<O, L>
    {
        private final L lock;
        private final O object;
        private final Supplier<Lock> readLockSupplier;
        private final Supplier<Lock> writeLockSupplier;
        
        protected LockVisitor(final O object, final L lock, final Supplier<Lock> readLockSupplier, final Supplier<Lock> writeLockSupplier) {
            this.object = Objects.requireNonNull(object, "object");
            this.lock = Objects.requireNonNull(lock, "lock");
            this.readLockSupplier = Objects.requireNonNull(readLockSupplier, "readLockSupplier");
            this.writeLockSupplier = Objects.requireNonNull(writeLockSupplier, "writeLockSupplier");
        }
        
        public void acceptReadLocked(final FailableConsumer<O, ?> consumer) {
            this.lockAcceptUnlock(this.readLockSupplier, consumer);
        }
        
        public void acceptWriteLocked(final FailableConsumer<O, ?> consumer) {
            this.lockAcceptUnlock(this.writeLockSupplier, consumer);
        }
        
        public <T> T applyReadLocked(final FailableFunction<O, T, ?> function) {
            return this.lockApplyUnlock(this.readLockSupplier, function);
        }
        
        public <T> T applyWriteLocked(final FailableFunction<O, T, ?> function) {
            return this.lockApplyUnlock(this.writeLockSupplier, function);
        }
        
        public L getLock() {
            return this.lock;
        }
        
        public O getObject() {
            return this.object;
        }
        
        protected void lockAcceptUnlock(final Supplier<Lock> lockSupplier, final FailableConsumer<O, ?> consumer) {
            final Lock lock = lockSupplier.get();
            lock.lock();
            try {
                consumer.accept(this.object);
            }
            catch (Throwable t) {
                throw Failable.rethrow(t);
            }
            finally {
                lock.unlock();
            }
        }
        
        protected <T> T lockApplyUnlock(final Supplier<Lock> lockSupplier, final FailableFunction<O, T, ?> function) {
            final Lock lock = lockSupplier.get();
            lock.lock();
            try {
                return function.apply(this.object);
            }
            catch (Throwable t) {
                throw Failable.rethrow(t);
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    public static class ReadWriteLockVisitor<O> extends LockVisitor<O, ReadWriteLock>
    {
        protected ReadWriteLockVisitor(final O object, final ReadWriteLock readWriteLock) {
            super(object, readWriteLock, readWriteLock::readLock, readWriteLock::writeLock);
        }
    }
    
    public static class StampedLockVisitor<O> extends LockVisitor<O, StampedLock>
    {
        protected StampedLockVisitor(final O object, final StampedLock stampedLock) {
            super(object, stampedLock, stampedLock::asReadLock, stampedLock::asWriteLock);
        }
    }
}
