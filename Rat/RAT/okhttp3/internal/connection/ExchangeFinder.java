//Raddon On Top!

package okhttp3.internal.connection;

import okhttp3.internal.http.*;
import java.io.*;
import java.util.*;
import okhttp3.internal.*;
import okhttp3.*;
import java.net.*;

final class ExchangeFinder
{
    private final Transmitter transmitter;
    private final Address address;
    private final RealConnectionPool connectionPool;
    private final Call call;
    private final EventListener eventListener;
    private RouteSelector.Selection routeSelection;
    private final RouteSelector routeSelector;
    private RealConnection connectingConnection;
    private boolean hasStreamFailure;
    private Route nextRouteToTry;
    
    ExchangeFinder(final Transmitter transmitter, final RealConnectionPool connectionPool, final Address address, final Call call, final EventListener eventListener) {
        this.transmitter = transmitter;
        this.connectionPool = connectionPool;
        this.address = address;
        this.call = call;
        this.eventListener = eventListener;
        this.routeSelector = new RouteSelector(address, connectionPool.routeDatabase, call, eventListener);
    }
    
    public ExchangeCodec find(final OkHttpClient client, final Interceptor.Chain chain, final boolean doExtensiveHealthChecks) {
        final int connectTimeout = chain.connectTimeoutMillis();
        final int readTimeout = chain.readTimeoutMillis();
        final int writeTimeout = chain.writeTimeoutMillis();
        final int pingIntervalMillis = client.pingIntervalMillis();
        final boolean connectionRetryEnabled = client.retryOnConnectionFailure();
        try {
            final RealConnection resultConnection = this.findHealthyConnection(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled, doExtensiveHealthChecks);
            return resultConnection.newCodec(client, chain);
        }
        catch (RouteException e) {
            this.trackFailure();
            throw e;
        }
        catch (IOException e2) {
            this.trackFailure();
            throw new RouteException(e2);
        }
    }
    
    private RealConnection findHealthyConnection(final int connectTimeout, final int readTimeout, final int writeTimeout, final int pingIntervalMillis, final boolean connectionRetryEnabled, final boolean doExtensiveHealthChecks) throws IOException {
        RealConnection candidate;
        while (true) {
            candidate = this.findConnection(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled);
            synchronized (this.connectionPool) {
                if (candidate.successCount == 0 && !candidate.isMultiplexed()) {
                    return candidate;
                }
            }
            if (candidate.isHealthy(doExtensiveHealthChecks)) {
                break;
            }
            candidate.noNewExchanges();
        }
        return candidate;
    }
    
    private RealConnection findConnection(final int connectTimeout, final int readTimeout, final int writeTimeout, final int pingIntervalMillis, final boolean connectionRetryEnabled) throws IOException {
        boolean foundPooledConnection = false;
        RealConnection result = null;
        Route selectedRoute = null;
        RealConnection releasedConnection;
        final Socket toClose;
        synchronized (this.connectionPool) {
            if (this.transmitter.isCanceled()) {
                throw new IOException("Canceled");
            }
            this.hasStreamFailure = false;
            releasedConnection = this.transmitter.connection;
            toClose = ((this.transmitter.connection != null && this.transmitter.connection.noNewExchanges) ? this.transmitter.releaseConnectionNoEvents() : null);
            if (this.transmitter.connection != null) {
                result = this.transmitter.connection;
                releasedConnection = null;
            }
            if (result == null) {
                if (this.connectionPool.transmitterAcquirePooledConnection(this.address, this.transmitter, null, false)) {
                    foundPooledConnection = true;
                    result = this.transmitter.connection;
                }
                else if (this.nextRouteToTry != null) {
                    selectedRoute = this.nextRouteToTry;
                    this.nextRouteToTry = null;
                }
                else if (this.retryCurrentRoute()) {
                    selectedRoute = this.transmitter.connection.route();
                }
            }
        }
        Util.closeQuietly(toClose);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, (Connection)releasedConnection);
        }
        if (foundPooledConnection) {
            this.eventListener.connectionAcquired(this.call, (Connection)result);
        }
        if (result != null) {
            return result;
        }
        boolean newRouteSelection = false;
        if (selectedRoute == null && (this.routeSelection == null || !this.routeSelection.hasNext())) {
            newRouteSelection = true;
            this.routeSelection = this.routeSelector.next();
        }
        List<Route> routes = null;
        synchronized (this.connectionPool) {
            if (this.transmitter.isCanceled()) {
                throw new IOException("Canceled");
            }
            if (newRouteSelection) {
                routes = this.routeSelection.getAll();
                if (this.connectionPool.transmitterAcquirePooledConnection(this.address, this.transmitter, routes, false)) {
                    foundPooledConnection = true;
                    result = this.transmitter.connection;
                }
            }
            if (!foundPooledConnection) {
                if (selectedRoute == null) {
                    selectedRoute = this.routeSelection.next();
                }
                result = new RealConnection(this.connectionPool, selectedRoute);
                this.connectingConnection = result;
            }
        }
        if (foundPooledConnection) {
            this.eventListener.connectionAcquired(this.call, (Connection)result);
            return result;
        }
        result.connect(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled, this.call, this.eventListener);
        this.connectionPool.routeDatabase.connected(result.route());
        Socket socket = null;
        synchronized (this.connectionPool) {
            this.connectingConnection = null;
            if (this.connectionPool.transmitterAcquirePooledConnection(this.address, this.transmitter, routes, true)) {
                result.noNewExchanges = true;
                socket = result.socket();
                result = this.transmitter.connection;
                this.nextRouteToTry = selectedRoute;
            }
            else {
                this.connectionPool.put(result);
                this.transmitter.acquireConnectionNoEvents(result);
            }
        }
        Util.closeQuietly(socket);
        this.eventListener.connectionAcquired(this.call, (Connection)result);
        return result;
    }
    
    RealConnection connectingConnection() {
        assert Thread.holdsLock(this.connectionPool);
        return this.connectingConnection;
    }
    
    void trackFailure() {
        assert !Thread.holdsLock(this.connectionPool);
        synchronized (this.connectionPool) {
            this.hasStreamFailure = true;
        }
    }
    
    boolean hasStreamFailure() {
        synchronized (this.connectionPool) {
            return this.hasStreamFailure;
        }
    }
    
    boolean hasRouteToTry() {
        synchronized (this.connectionPool) {
            if (this.nextRouteToTry != null) {
                return true;
            }
            if (this.retryCurrentRoute()) {
                this.nextRouteToTry = this.transmitter.connection.route();
                return true;
            }
            return (this.routeSelection != null && this.routeSelection.hasNext()) || this.routeSelector.hasNext();
        }
    }
    
    private boolean retryCurrentRoute() {
        return this.transmitter.connection != null && this.transmitter.connection.routeFailureCount == 0 && Util.sameConnection(this.transmitter.connection.route().address().url(), this.address.url());
    }
}
