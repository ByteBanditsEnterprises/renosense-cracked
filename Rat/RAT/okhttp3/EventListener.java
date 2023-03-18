//Raddon On Top!

package okhttp3;

import java.util.*;
import java.net.*;
import javax.annotation.*;
import java.io.*;

public abstract class EventListener
{
    public static final EventListener NONE;
    
    static Factory factory(final EventListener listener) {
        return call -> listener;
    }
    
    public void callStart(final Call call) {
    }
    
    public void dnsStart(final Call call, final String domainName) {
    }
    
    public void dnsEnd(final Call call, final String domainName, final List<InetAddress> inetAddressList) {
    }
    
    public void connectStart(final Call call, final InetSocketAddress inetSocketAddress, final Proxy proxy) {
    }
    
    public void secureConnectStart(final Call call) {
    }
    
    public void secureConnectEnd(final Call call, @Nullable final Handshake handshake) {
    }
    
    public void connectEnd(final Call call, final InetSocketAddress inetSocketAddress, final Proxy proxy, @Nullable final Protocol protocol) {
    }
    
    public void connectFailed(final Call call, final InetSocketAddress inetSocketAddress, final Proxy proxy, @Nullable final Protocol protocol, final IOException ioe) {
    }
    
    public void connectionAcquired(final Call call, final Connection connection) {
    }
    
    public void connectionReleased(final Call call, final Connection connection) {
    }
    
    public void requestHeadersStart(final Call call) {
    }
    
    public void requestHeadersEnd(final Call call, final Request request) {
    }
    
    public void requestBodyStart(final Call call) {
    }
    
    public void requestBodyEnd(final Call call, final long byteCount) {
    }
    
    public void requestFailed(final Call call, final IOException ioe) {
    }
    
    public void responseHeadersStart(final Call call) {
    }
    
    public void responseHeadersEnd(final Call call, final Response response) {
    }
    
    public void responseBodyStart(final Call call) {
    }
    
    public void responseBodyEnd(final Call call, final long byteCount) {
    }
    
    public void responseFailed(final Call call, final IOException ioe) {
    }
    
    public void callEnd(final Call call) {
    }
    
    public void callFailed(final Call call, final IOException ioe) {
    }
    
    static {
        NONE = new EventListener() {};
    }
    
    public interface Factory
    {
        EventListener create(final Call p0);
    }
}
