//Raddon On Top!

package okhttp3.internal.connection;

import okhttp3.internal.http.*;
import okhttp3.*;
import java.io.*;

public final class ConnectInterceptor implements Interceptor
{
    public final OkHttpClient client;
    
    public ConnectInterceptor(final OkHttpClient client) {
        this.client = client;
    }
    
    public Response intercept(final Interceptor.Chain chain) throws IOException {
        final RealInterceptorChain realChain = (RealInterceptorChain)chain;
        final Request request = realChain.request();
        final Transmitter transmitter = realChain.transmitter();
        final boolean doExtensiveHealthChecks = !request.method().equals("GET");
        final Exchange exchange = transmitter.newExchange(chain, doExtensiveHealthChecks);
        return realChain.proceed(request, transmitter, exchange);
    }
}
