//Raddon On Top!

package okhttp3.internal.http;

import okhttp3.internal.connection.*;
import java.io.*;
import javax.annotation.*;
import okio.*;
import okhttp3.*;

public interface ExchangeCodec
{
    public static final int DISCARD_STREAM_TIMEOUT_MILLIS = 100;
    
    RealConnection connection();
    
    Sink createRequestBody(final Request p0, final long p1) throws IOException;
    
    void writeRequestHeaders(final Request p0) throws IOException;
    
    void flushRequest() throws IOException;
    
    void finishRequest() throws IOException;
    
    @Nullable
    Response.Builder readResponseHeaders(final boolean p0) throws IOException;
    
    long reportedContentLength(final Response p0) throws IOException;
    
    Source openResponseBodySource(final Response p0) throws IOException;
    
    Headers trailers() throws IOException;
    
    void cancel();
}
