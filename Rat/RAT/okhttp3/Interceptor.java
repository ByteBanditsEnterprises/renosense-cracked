//Raddon On Top!

package okhttp3;

import java.io.*;
import javax.annotation.*;
import java.util.concurrent.*;

public interface Interceptor
{
    Response intercept(final Chain p0) throws IOException;
    
    public interface Chain
    {
        Request request();
        
        Response proceed(final Request p0) throws IOException;
        
        @Nullable
        Connection connection();
        
        Call call();
        
        int connectTimeoutMillis();
        
        Chain withConnectTimeout(final int p0, final TimeUnit p1);
        
        int readTimeoutMillis();
        
        Chain withReadTimeout(final int p0, final TimeUnit p1);
        
        int writeTimeoutMillis();
        
        Chain withWriteTimeout(final int p0, final TimeUnit p1);
    }
}
