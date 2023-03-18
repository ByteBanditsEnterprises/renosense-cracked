//Raddon On Top!

package okhttp3.internal.http;

import javax.annotation.*;
import okio.*;
import okhttp3.*;

public final class RealResponseBody extends ResponseBody
{
    @Nullable
    private final String contentTypeString;
    private final long contentLength;
    private final BufferedSource source;
    
    public RealResponseBody(@Nullable final String contentTypeString, final long contentLength, final BufferedSource source) {
        this.contentTypeString = contentTypeString;
        this.contentLength = contentLength;
        this.source = source;
    }
    
    @Override
    public MediaType contentType() {
        return (this.contentTypeString != null) ? MediaType.parse(this.contentTypeString) : null;
    }
    
    @Override
    public long contentLength() {
        return this.contentLength;
    }
    
    @Override
    public BufferedSource source() {
        return this.source;
    }
}
