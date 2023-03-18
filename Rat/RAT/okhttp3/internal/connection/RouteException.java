//Raddon On Top!

package okhttp3.internal.connection;

import java.io.*;
import okhttp3.internal.*;

public final class RouteException extends RuntimeException
{
    private IOException firstException;
    private IOException lastException;
    
    RouteException(final IOException cause) {
        super(cause);
        this.firstException = cause;
        this.lastException = cause;
    }
    
    public IOException getFirstConnectException() {
        return this.firstException;
    }
    
    public IOException getLastConnectException() {
        return this.lastException;
    }
    
    void addConnectException(final IOException e) {
        Util.addSuppressedIfPossible(this.firstException, e);
        this.lastException = e;
    }
}
