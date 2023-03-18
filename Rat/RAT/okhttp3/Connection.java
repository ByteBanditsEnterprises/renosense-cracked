//Raddon On Top!

package okhttp3;

import java.net.*;
import javax.annotation.*;

public interface Connection
{
    Route route();
    
    Socket socket();
    
    @Nullable
    Handshake handshake();
    
    Protocol protocol();
}
