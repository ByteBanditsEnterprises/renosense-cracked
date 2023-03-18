//Raddon On Top!

package okhttp3;

import java.io.*;
import okio.*;

public interface Call extends Cloneable
{
    Request request();
    
    Response execute() throws IOException;
    
    void enqueue(final Callback p0);
    
    void cancel();
    
    boolean isExecuted();
    
    boolean isCanceled();
    
    Timeout timeout();
    
    Call clone();
    
    public interface Factory
    {
        Call newCall(final Request p0);
    }
}
