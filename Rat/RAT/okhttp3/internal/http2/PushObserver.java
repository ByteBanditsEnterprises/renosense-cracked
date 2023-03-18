//Raddon On Top!

package okhttp3.internal.http2;

import java.util.*;
import okio.*;
import java.io.*;

public interface PushObserver
{
    public static final PushObserver CANCEL = new PushObserver() {
        @Override
        public boolean onRequest(final int streamId, final List<Header> requestHeaders) {
            return true;
        }
        
        @Override
        public boolean onHeaders(final int streamId, final List<Header> responseHeaders, final boolean last) {
            return true;
        }
        
        @Override
        public boolean onData(final int streamId, final BufferedSource source, final int byteCount, final boolean last) throws IOException {
            source.skip((long)byteCount);
            return true;
        }
        
        @Override
        public void onReset(final int streamId, final ErrorCode errorCode) {
        }
    };
    
    boolean onRequest(final int p0, final List<Header> p1);
    
    boolean onHeaders(final int p0, final List<Header> p1, final boolean p2);
    
    boolean onData(final int p0, final BufferedSource p1, final int p2, final boolean p3) throws IOException;
    
    void onReset(final int p0, final ErrorCode p1);
}
