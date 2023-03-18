//Raddon On Top!

package okhttp3.internal.connection;

import java.util.*;
import java.io.*;
import okhttp3.*;
import okhttp3.internal.*;
import java.net.*;

final class RouteSelector
{
    private final Address address;
    private final RouteDatabase routeDatabase;
    private final Call call;
    private final EventListener eventListener;
    private List<Proxy> proxies;
    private int nextProxyIndex;
    private List<InetSocketAddress> inetSocketAddresses;
    private final List<Route> postponedRoutes;
    
    RouteSelector(final Address address, final RouteDatabase routeDatabase, final Call call, final EventListener eventListener) {
        this.proxies = Collections.emptyList();
        this.inetSocketAddresses = Collections.emptyList();
        this.postponedRoutes = new ArrayList<Route>();
        this.address = address;
        this.routeDatabase = routeDatabase;
        this.call = call;
        this.eventListener = eventListener;
        this.resetNextProxy(address.url(), address.proxy());
    }
    
    public boolean hasNext() {
        return this.hasNextProxy() || !this.postponedRoutes.isEmpty();
    }
    
    public Selection next() throws IOException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final List<Route> routes = new ArrayList<Route>();
        while (this.hasNextProxy()) {
            final Proxy proxy = this.nextProxy();
            for (int i = 0, size = this.inetSocketAddresses.size(); i < size; ++i) {
                final Route route = new Route(this.address, proxy, this.inetSocketAddresses.get(i));
                if (this.routeDatabase.shouldPostpone(route)) {
                    this.postponedRoutes.add(route);
                }
                else {
                    routes.add(route);
                }
            }
            if (!routes.isEmpty()) {
                break;
            }
        }
        if (routes.isEmpty()) {
            routes.addAll(this.postponedRoutes);
            this.postponedRoutes.clear();
        }
        return new Selection(routes);
    }
    
    private void resetNextProxy(final HttpUrl url, final Proxy proxy) {
        if (proxy != null) {
            this.proxies = Collections.singletonList(proxy);
        }
        else {
            final List<Proxy> proxiesOrNull = this.address.proxySelector().select(url.uri());
            this.proxies = ((proxiesOrNull != null && !proxiesOrNull.isEmpty()) ? Util.immutableList(proxiesOrNull) : Util.immutableList(Proxy.NO_PROXY));
        }
        this.nextProxyIndex = 0;
    }
    
    private boolean hasNextProxy() {
        return this.nextProxyIndex < this.proxies.size();
    }
    
    private Proxy nextProxy() throws IOException {
        if (!this.hasNextProxy()) {
            throw new SocketException("No route to " + this.address.url().host() + "; exhausted proxy configurations: " + this.proxies);
        }
        final Proxy result = this.proxies.get(this.nextProxyIndex++);
        this.resetNextInetSocketAddress(result);
        return result;
    }
    
    private void resetNextInetSocketAddress(final Proxy proxy) throws IOException {
        this.inetSocketAddresses = new ArrayList<InetSocketAddress>();
        String socketHost;
        int socketPort;
        if (proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.SOCKS) {
            socketHost = this.address.url().host();
            socketPort = this.address.url().port();
        }
        else {
            final SocketAddress proxyAddress = proxy.address();
            if (!(proxyAddress instanceof InetSocketAddress)) {
                throw new IllegalArgumentException("Proxy.address() is not an InetSocketAddress: " + proxyAddress.getClass());
            }
            final InetSocketAddress proxySocketAddress = (InetSocketAddress)proxyAddress;
            socketHost = getHostString(proxySocketAddress);
            socketPort = proxySocketAddress.getPort();
        }
        if (socketPort < 1 || socketPort > 65535) {
            throw new SocketException("No route to " + socketHost + ":" + socketPort + "; port is out of range");
        }
        if (proxy.type() == Proxy.Type.SOCKS) {
            this.inetSocketAddresses.add(InetSocketAddress.createUnresolved(socketHost, socketPort));
        }
        else {
            this.eventListener.dnsStart(this.call, socketHost);
            final List<InetAddress> addresses = (List<InetAddress>)this.address.dns().lookup(socketHost);
            if (addresses.isEmpty()) {
                throw new UnknownHostException(this.address.dns() + " returned no addresses for " + socketHost);
            }
            this.eventListener.dnsEnd(this.call, socketHost, (List)addresses);
            for (int i = 0, size = addresses.size(); i < size; ++i) {
                final InetAddress inetAddress = addresses.get(i);
                this.inetSocketAddresses.add(new InetSocketAddress(inetAddress, socketPort));
            }
        }
    }
    
    static String getHostString(final InetSocketAddress socketAddress) {
        final InetAddress address = socketAddress.getAddress();
        if (address == null) {
            return socketAddress.getHostName();
        }
        return address.getHostAddress();
    }
    
    public static final class Selection
    {
        private final List<Route> routes;
        private int nextRouteIndex;
        
        Selection(final List<Route> routes) {
            this.nextRouteIndex = 0;
            this.routes = routes;
        }
        
        public boolean hasNext() {
            return this.nextRouteIndex < this.routes.size();
        }
        
        public Route next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.routes.get(this.nextRouteIndex++);
        }
        
        public List<Route> getAll() {
            return new ArrayList<Route>(this.routes);
        }
    }
}
