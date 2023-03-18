//Raddon On Top!

package okhttp3;

import java.nio.charset.*;
import okio.*;

public final class Credentials
{
    private Credentials() {
    }
    
    public static String basic(final String username, final String password) {
        return basic(username, password, StandardCharsets.ISO_8859_1);
    }
    
    public static String basic(final String username, final String password, final Charset charset) {
        final String usernameAndPassword = username + ":" + password;
        final String encoded = ByteString.encodeString(usernameAndPassword, charset).base64();
        return "Basic " + encoded;
    }
}
