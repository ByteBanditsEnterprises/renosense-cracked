//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import okhttp3.internal.*;
import java.util.concurrent.*;
import java.util.*;

public final class Dispatcher
{
    private int maxRequests;
    private int maxRequestsPerHost;
    @Nullable
    private Runnable idleCallback;
    @Nullable
    private ExecutorService executorService;
    private final Deque<RealCall.AsyncCall> readyAsyncCalls;
    private final Deque<RealCall.AsyncCall> runningAsyncCalls;
    private final Deque<RealCall> runningSyncCalls;
    
    public Dispatcher(final ExecutorService executorService) {
        this.maxRequests = 64;
        this.maxRequestsPerHost = 5;
        this.readyAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningSyncCalls = new ArrayDeque<RealCall>();
        this.executorService = executorService;
    }
    
    public Dispatcher() {
        this.maxRequests = 64;
        this.maxRequestsPerHost = 5;
        this.readyAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningSyncCalls = new ArrayDeque<RealCall>();
    }
    
    public synchronized ExecutorService executorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
        }
        return this.executorService;
    }
    
    public void setMaxRequests(final int maxRequests) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }
        synchronized (this) {
            this.maxRequests = maxRequests;
        }
        this.promoteAndExecute();
    }
    
    public synchronized int getMaxRequests() {
        return this.maxRequests;
    }
    
    public void setMaxRequestsPerHost(final int maxRequestsPerHost) {
        if (maxRequestsPerHost < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequestsPerHost);
        }
        synchronized (this) {
            this.maxRequestsPerHost = maxRequestsPerHost;
        }
        this.promoteAndExecute();
    }
    
    public synchronized int getMaxRequestsPerHost() {
        return this.maxRequestsPerHost;
    }
    
    public synchronized void setIdleCallback(@Nullable final Runnable idleCallback) {
        this.idleCallback = idleCallback;
    }
    
    void enqueue(final RealCall.AsyncCall call) {
        synchronized (this) {
            this.readyAsyncCalls.add(call);
            if (!call.get().forWebSocket) {
                final RealCall.AsyncCall existingCall = this.findExistingCallWithHost(call.host());
                if (existingCall != null) {
                    call.reuseCallsPerHostFrom(existingCall);
                }
            }
        }
        this.promoteAndExecute();
    }
    
    @Nullable
    private RealCall.AsyncCall findExistingCallWithHost(final String host) {
        for (final RealCall.AsyncCall existingCall : this.runningAsyncCalls) {
            if (existingCall.host().equals(host)) {
                return existingCall;
            }
        }
        for (final RealCall.AsyncCall existingCall : this.readyAsyncCalls) {
            if (existingCall.host().equals(host)) {
                return existingCall;
            }
        }
        return null;
    }
    
    public synchronized void cancelAll() {
        for (final RealCall.AsyncCall call : this.readyAsyncCalls) {
            call.get().cancel();
        }
        for (final RealCall.AsyncCall call : this.runningAsyncCalls) {
            call.get().cancel();
        }
        for (final RealCall call2 : this.runningSyncCalls) {
            call2.cancel();
        }
    }
    
    private boolean promoteAndExecute() {
        assert !Thread.holdsLock(this);
        final List<RealCall.AsyncCall> executableCalls = new ArrayList<RealCall.AsyncCall>();
        final boolean isRunning;
        synchronized (this) {
            final Iterator<RealCall.AsyncCall> i = this.readyAsyncCalls.iterator();
            while (i.hasNext()) {
                final RealCall.AsyncCall asyncCall = i.next();
                if (this.runningAsyncCalls.size() >= this.maxRequests) {
                    break;
                }
                if (asyncCall.callsPerHost().get() >= this.maxRequestsPerHost) {
                    continue;
                }
                i.remove();
                asyncCall.callsPerHost().incrementAndGet();
                executableCalls.add(asyncCall);
                this.runningAsyncCalls.add(asyncCall);
            }
            isRunning = (this.runningCallsCount() > 0);
        }
        for (int j = 0, size = executableCalls.size(); j < size; ++j) {
            final RealCall.AsyncCall asyncCall = executableCalls.get(j);
            asyncCall.executeOn(this.executorService());
        }
        return isRunning;
    }
    
    synchronized void executed(final RealCall call) {
        this.runningSyncCalls.add(call);
    }
    
    void finished(final RealCall.AsyncCall call) {
        call.callsPerHost().decrementAndGet();
        this.finished(this.runningAsyncCalls, call);
    }
    
    void finished(final RealCall call) {
        this.finished(this.runningSyncCalls, call);
    }
    
    private <T> void finished(final Deque<T> calls, final T call) {
        final Runnable idleCallback;
        synchronized (this) {
            if (!calls.remove(call)) {
                throw new AssertionError((Object)"Call wasn't in-flight!");
            }
            idleCallback = this.idleCallback;
        }
        final boolean isRunning = this.promoteAndExecute();
        if (!isRunning && idleCallback != null) {
            idleCallback.run();
        }
    }
    
    public synchronized List<Call> queuedCalls() {
        final List<Call> result = new ArrayList<Call>();
        for (final RealCall.AsyncCall asyncCall : this.readyAsyncCalls) {
            result.add((Call)asyncCall.get());
        }
        return Collections.unmodifiableList((List<? extends Call>)result);
    }
    
    public synchronized List<Call> runningCalls() {
        final List<Call> result = new ArrayList<Call>();
        result.addAll((Collection<? extends Call>)this.runningSyncCalls);
        for (final RealCall.AsyncCall asyncCall : this.runningAsyncCalls) {
            result.add((Call)asyncCall.get());
        }
        return Collections.unmodifiableList((List<? extends Call>)result);
    }
    
    public synchronized int queuedCallsCount() {
        return this.readyAsyncCalls.size();
    }
    
    public synchronized int runningCallsCount() {
        return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
    }
}
