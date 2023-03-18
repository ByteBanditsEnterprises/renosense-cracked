//Raddon On Top!

package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.*;

public abstract class AtomicInitializer<T> implements ConcurrentInitializer<T>
{
    private final AtomicReference<T> reference;
    
    public AtomicInitializer() {
        this.reference = new AtomicReference<T>();
    }
    
    @Override
    public T get() throws ConcurrentException {
        T result = this.reference.get();
        if (result == null) {
            result = this.initialize();
            if (!this.reference.compareAndSet(null, result)) {
                result = this.reference.get();
            }
        }
        return result;
    }
    
    protected abstract T initialize() throws ConcurrentException;
}
