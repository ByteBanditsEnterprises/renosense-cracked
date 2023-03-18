//Raddon On Top!

package okhttp3.internal.connection;

import okhttp3.*;
import javax.annotation.*;
import java.util.*;
import okhttp3.internal.*;
import java.lang.ref.*;
import okhttp3.internal.platform.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public final class RealConnectionPool
{
    private static final Executor executor;
    private final int maxIdleConnections;
    private final long keepAliveDurationNs;
    private final Runnable cleanupRunnable;
    private final Deque<RealConnection> connections;
    final RouteDatabase routeDatabase;
    boolean cleanupRunning;
    
    public RealConnectionPool(final int maxIdleConnections, final long keepAliveDuration, final TimeUnit timeUnit) {
        long waitNanos;
        long waitMillis;
        long waitNanos2;
        this.cleanupRunnable = (() -> {
            while (true) {
                waitNanos = this.cleanup(System.nanoTime());
                if (waitNanos == -1L) {
                    break;
                }
                else if (waitNanos > 0L) {
                    waitMillis = waitNanos / 1000000L;
                    waitNanos2 = waitNanos - waitMillis * 1000000L;
                    synchronized (this) {
                        try {
                            this.wait(waitMillis, (int)waitNanos2);
                        }
                        catch (InterruptedException ex) {}
                    }
                }
                else {
                    continue;
                }
            }
            return;
        });
        this.connections = new ArrayDeque<RealConnection>();
        this.routeDatabase = new RouteDatabase();
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
        if (keepAliveDuration <= 0L) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDuration);
        }
    }
    
    public synchronized int idleConnectionCount() {
        int total = 0;
        for (final RealConnection connection : this.connections) {
            if (connection.transmitters.isEmpty()) {
                ++total;
            }
        }
        return total;
    }
    
    public synchronized int connectionCount() {
        return this.connections.size();
    }
    
    boolean transmitterAcquirePooledConnection(final Address address, final Transmitter transmitter, @Nullable final List<Route> routes, final boolean requireMultiplexed) {
        assert Thread.holdsLock(this);
        for (final RealConnection connection : this.connections) {
            if (requireMultiplexed && !connection.isMultiplexed()) {
                continue;
            }
            if (!connection.isEligible(address, (List)routes)) {
                continue;
            }
            transmitter.acquireConnectionNoEvents(connection);
            return true;
        }
        return false;
    }
    
    void put(final RealConnection connection) {
        assert Thread.holdsLock(this);
        if (!this.cleanupRunning) {
            this.cleanupRunning = true;
            RealConnectionPool.executor.execute(this.cleanupRunnable);
        }
        this.connections.add(connection);
    }
    
    boolean connectionBecameIdle(final RealConnection connection) {
        assert Thread.holdsLock(this);
        if (connection.noNewExchanges || this.maxIdleConnections == 0) {
            this.connections.remove(connection);
            return true;
        }
        this.notifyAll();
        return false;
    }
    
    public void evictAll() {
        final List<RealConnection> evictedConnections = new ArrayList<RealConnection>();
        synchronized (this) {
            final Iterator<RealConnection> i = this.connections.iterator();
            while (i.hasNext()) {
                final RealConnection connection = i.next();
                if (connection.transmitters.isEmpty()) {
                    connection.noNewExchanges = true;
                    evictedConnections.add(connection);
                    i.remove();
                }
            }
        }
        for (final RealConnection connection2 : evictedConnections) {
            Util.closeQuietly(connection2.socket());
        }
    }
    
    long cleanup(final long now) {
        int inUseConnectionCount = 0;
        int idleConnectionCount = 0;
        RealConnection longestIdleConnection = null;
        long longestIdleDurationNs = Long.MIN_VALUE;
        synchronized (this) {
            for (final RealConnection connection : this.connections) {
                if (this.pruneAndGetAllocationCount(connection, now) > 0) {
                    ++inUseConnectionCount;
                }
                else {
                    ++idleConnectionCount;
                    final long idleDurationNs = now - connection.idleAtNanos;
                    if (idleDurationNs <= longestIdleDurationNs) {
                        continue;
                    }
                    longestIdleDurationNs = idleDurationNs;
                    longestIdleConnection = connection;
                }
            }
            if (longestIdleDurationNs >= this.keepAliveDurationNs || idleConnectionCount > this.maxIdleConnections) {
                this.connections.remove(longestIdleConnection);
            }
            else {
                if (idleConnectionCount > 0) {
                    return this.keepAliveDurationNs - longestIdleDurationNs;
                }
                if (inUseConnectionCount > 0) {
                    return this.keepAliveDurationNs;
                }
                this.cleanupRunning = false;
                return -1L;
            }
        }
        Util.closeQuietly(longestIdleConnection.socket());
        return 0L;
    }
    
    private int pruneAndGetAllocationCount(final RealConnection connection, final long now) {
        final List<Reference<Transmitter>> references = (List<Reference<Transmitter>>)connection.transmitters;
        int i = 0;
        while (i < references.size()) {
            final Reference<Transmitter> reference = references.get(i);
            if (reference.get() != null) {
                ++i;
            }
            else {
                final Transmitter.TransmitterReference transmitterRef = (Transmitter.TransmitterReference)reference;
                final String message = "A connection to " + connection.route().address().url() + " was leaked. Did you forget to close a response body?";
                Platform.get().logCloseableLeak(message, transmitterRef.callStackTrace);
                references.remove(i);
                connection.noNewExchanges = true;
                if (references.isEmpty()) {
                    connection.idleAtNanos = now - this.keepAliveDurationNs;
                    return 0;
                }
                continue;
            }
        }
        return references.size();
    }
    
    public void connectFailed(final Route failedRoute, final IOException failure) {
        if (failedRoute.proxy().type() != Proxy.Type.DIRECT) {
            final Address address = failedRoute.address();
            address.proxySelector().connectFailed(address.url().uri(), failedRoute.proxy().address(), failure);
        }
        this.routeDatabase.failed(failedRoute);
    }
    
    static {
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp ConnectionPool", true));
    }
}
