//Raddon On Top!

package okhttp3;

import okhttp3.internal.connection.*;
import java.util.concurrent.*;

public final class ConnectionPool
{
    final RealConnectionPool delegate;
    
    public ConnectionPool() {
        this(5, 5L, TimeUnit.MINUTES);
    }
    
    public ConnectionPool(final int maxIdleConnections, final long keepAliveDuration, final TimeUnit timeUnit) {
        this.delegate = new RealConnectionPool(maxIdleConnections, keepAliveDuration, timeUnit);
    }
    
    public int idleConnectionCount() {
        return this.delegate.idleConnectionCount();
    }
    
    public int connectionCount() {
        return this.delegate.connectionCount();
    }
    
    public void evictAll() {
        this.delegate.evictAll();
    }
}
