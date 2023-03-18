//Raddon On Top!

package okhttp3;

import java.net.*;
import java.util.*;

public interface Dns
{
    public static final Dns SYSTEM;
    
    List<InetAddress> lookup(final String p0) throws UnknownHostException;
    
    default static {
        // This method could not be decompiled.
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
