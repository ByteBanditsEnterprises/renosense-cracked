//Raddon On Top!

package okhttp3.internal.connection;

import okhttp3.*;
import java.util.*;

final class RouteDatabase
{
    private final Set<Route> failedRoutes;
    
    RouteDatabase() {
        this.failedRoutes = new LinkedHashSet<Route>();
    }
    
    public synchronized void failed(final Route failedRoute) {
        this.failedRoutes.add(failedRoute);
    }
    
    public synchronized void connected(final Route route) {
        this.failedRoutes.remove(route);
    }
    
    public synchronized boolean shouldPostpone(final Route route) {
        return this.failedRoutes.contains(route);
    }
}
