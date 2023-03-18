//Raddon On Top!

package okhttp3.internal.cache;

import okhttp3.*;
import java.io.*;
import javax.annotation.*;

public interface InternalCache
{
    @Nullable
    Response get(final Request p0) throws IOException;
    
    @Nullable
    CacheRequest put(final Response p0) throws IOException;
    
    void remove(final Request p0) throws IOException;
    
    void update(final Response p0, final Response p1);
    
    void trackConditionalCacheHit();
    
    void trackResponse(final CacheStrategy p0);
}
