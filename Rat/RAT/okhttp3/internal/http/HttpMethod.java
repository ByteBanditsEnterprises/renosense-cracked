//Raddon On Top!

package okhttp3.internal.http;

public final class HttpMethod
{
    public static boolean invalidatesCache(final String method) {
        return method.equals("POST") || method.equals("PATCH") || method.equals("PUT") || method.equals("DELETE") || method.equals("MOVE");
    }
    
    public static boolean requiresRequestBody(final String method) {
        return method.equals("POST") || method.equals("PUT") || method.equals("PATCH") || method.equals("PROPPATCH") || method.equals("REPORT");
    }
    
    public static boolean permitsRequestBody(final String method) {
        return !method.equals("GET") && !method.equals("HEAD");
    }
    
    public static boolean redirectsWithBody(final String method) {
        return method.equals("PROPFIND");
    }
    
    public static boolean redirectsToGet(final String method) {
        return !method.equals("PROPFIND");
    }
    
    private HttpMethod() {
    }
}
