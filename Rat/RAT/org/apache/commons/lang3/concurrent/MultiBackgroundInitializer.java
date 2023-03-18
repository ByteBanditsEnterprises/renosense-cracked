//Raddon On Top!

package org.apache.commons.lang3.concurrent;

import java.util.concurrent.*;
import org.apache.commons.lang3.*;
import java.util.*;

public class MultiBackgroundInitializer extends BackgroundInitializer<MultiBackgroundInitializerResults>
{
    private final Map<String, BackgroundInitializer<?>> childInitializers;
    
    public MultiBackgroundInitializer() {
        this.childInitializers = new HashMap<String, BackgroundInitializer<?>>();
    }
    
    public MultiBackgroundInitializer(final ExecutorService exec) {
        super(exec);
        this.childInitializers = new HashMap<String, BackgroundInitializer<?>>();
    }
    
    public void addInitializer(final String name, final BackgroundInitializer<?> backgroundInitializer) {
        Validate.notNull(name, "name", new Object[0]);
        Validate.notNull(backgroundInitializer, "backgroundInitializer", new Object[0]);
        synchronized (this) {
            if (this.isStarted()) {
                throw new IllegalStateException("addInitializer() must not be called after start()!");
            }
            this.childInitializers.put(name, backgroundInitializer);
        }
    }
    
    protected int getTaskCount() {
        int result = 1;
        for (final BackgroundInitializer<?> bi : this.childInitializers.values()) {
            result += bi.getTaskCount();
        }
        return result;
    }
    
    protected MultiBackgroundInitializerResults initialize() throws Exception {
        final Map<String, BackgroundInitializer<?>> inits;
        synchronized (this) {
            inits = new HashMap<String, BackgroundInitializer<?>>(this.childInitializers);
        }
        final ExecutorService exec = this.getActiveExecutor();
        for (final BackgroundInitializer<?> bi : inits.values()) {
            if (bi.getExternalExecutor() == null) {
                bi.setExternalExecutor(exec);
            }
            bi.start();
        }
        final Map<String, Object> results = new HashMap<String, Object>();
        final Map<String, ConcurrentException> excepts = new HashMap<String, ConcurrentException>();
        for (final Map.Entry<String, BackgroundInitializer<?>> e : inits.entrySet()) {
            try {
                results.put(e.getKey(), e.getValue().get());
            }
            catch (ConcurrentException cex) {
                excepts.put(e.getKey(), cex);
            }
        }
        return new MultiBackgroundInitializerResults((Map)inits, (Map)results, (Map)excepts);
    }
    
    public static class MultiBackgroundInitializerResults
    {
        private final Map<String, BackgroundInitializer<?>> initializers;
        private final Map<String, Object> resultObjects;
        private final Map<String, ConcurrentException> exceptions;
        
        private MultiBackgroundInitializerResults(final Map<String, BackgroundInitializer<?>> inits, final Map<String, Object> results, final Map<String, ConcurrentException> excepts) {
            this.initializers = inits;
            this.resultObjects = results;
            this.exceptions = excepts;
        }
        
        public BackgroundInitializer<?> getInitializer(final String name) {
            return this.checkName(name);
        }
        
        public Object getResultObject(final String name) {
            this.checkName(name);
            return this.resultObjects.get(name);
        }
        
        public boolean isException(final String name) {
            this.checkName(name);
            return this.exceptions.containsKey(name);
        }
        
        public ConcurrentException getException(final String name) {
            this.checkName(name);
            return this.exceptions.get(name);
        }
        
        public Set<String> initializerNames() {
            return Collections.unmodifiableSet((Set<? extends String>)this.initializers.keySet());
        }
        
        public boolean isSuccessful() {
            return this.exceptions.isEmpty();
        }
        
        private BackgroundInitializer<?> checkName(final String name) {
            final BackgroundInitializer<?> init = this.initializers.get(name);
            if (init == null) {
                throw new NoSuchElementException("No child initializer with name " + name);
            }
            return init;
        }
    }
}
