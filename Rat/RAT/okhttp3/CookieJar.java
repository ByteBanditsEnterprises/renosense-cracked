//Raddon On Top!

package okhttp3;

import java.util.*;

public interface CookieJar
{
    public static final CookieJar NO_COOKIES = new CookieJar() {
        @Override
        public void saveFromResponse(final HttpUrl url, final List<Cookie> cookies) {
        }
        
        @Override
        public List<Cookie> loadForRequest(final HttpUrl url) {
            return Collections.emptyList();
        }
    };
    
    void saveFromResponse(final HttpUrl p0, final List<Cookie> p1);
    
    List<Cookie> loadForRequest(final HttpUrl p0);
}
