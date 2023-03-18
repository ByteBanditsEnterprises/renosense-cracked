//Raddon On Top!

package okhttp3;

import okio.*;
import javax.annotation.*;

public interface WebSocket
{
    Request request();
    
    long queueSize();
    
    boolean send(final String p0);
    
    boolean send(final ByteString p0);
    
    boolean close(final int p0, @Nullable final String p1);
    
    void cancel();
    
    public interface Factory
    {
        WebSocket newWebSocket(final Request p0, final WebSocketListener p1);
    }
}
