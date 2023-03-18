//Raddon On Top!

package okhttp3.internal.cache;

import okio.*;
import java.io.*;

public interface CacheRequest
{
    Sink body() throws IOException;
    
    void abort();
}
