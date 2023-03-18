//Raddon On Top!

package okhttp3.internal.proxy;

import java.util.*;
import java.net.*;
import java.io.*;

public class NullProxySelector extends ProxySelector
{
    @Override
    public List<Proxy> select(final URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null");
        }
        return Collections.singletonList(Proxy.NO_PROXY);
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
    }
}
