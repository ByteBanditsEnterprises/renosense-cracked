//Raddon On Top!

package okhttp3.internal;

import javax.net.ssl.*;
import okhttp3.*;
import okhttp3.internal.connection.*;
import javax.annotation.*;

public abstract class Internal
{
    public static Internal instance;
    
    public static void initializeInstanceForTests() {
        new OkHttpClient();
    }
    
    public abstract void addLenient(final Headers.Builder p0, final String p1);
    
    public abstract void addLenient(final Headers.Builder p0, final String p1, final String p2);
    
    public abstract RealConnectionPool realConnectionPool(final ConnectionPool p0);
    
    public abstract boolean equalsNonHost(final Address p0, final Address p1);
    
    public abstract int code(final Response.Builder p0);
    
    public abstract void apply(final ConnectionSpec p0, final SSLSocket p1, final boolean p2);
    
    public abstract Call newWebSocketCall(final OkHttpClient p0, final Request p1);
    
    public abstract void initExchange(final Response.Builder p0, final Exchange p1);
    
    @Nullable
    public abstract Exchange exchange(final Response p0);
}
