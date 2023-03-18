//Raddon On Top!

package org.apache.commons.lang3.concurrent;

import java.util.concurrent.*;
import org.apache.commons.lang3.*;

public class CallableBackgroundInitializer<T> extends BackgroundInitializer<T>
{
    private final Callable<T> callable;
    
    public CallableBackgroundInitializer(final Callable<T> call) {
        this.checkCallable(call);
        this.callable = call;
    }
    
    public CallableBackgroundInitializer(final Callable<T> call, final ExecutorService exec) {
        super(exec);
        this.checkCallable(call);
        this.callable = call;
    }
    
    protected T initialize() throws Exception {
        return this.callable.call();
    }
    
    private void checkCallable(final Callable<T> callable) {
        Validate.notNull(callable, "callable", new Object[0]);
    }
}
