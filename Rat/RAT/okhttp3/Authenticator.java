//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import java.io.*;

public interface Authenticator
{
    public static final Authenticator NONE = (route, response) -> null;
    
    @Nullable
    Request authenticate(@Nullable final Route p0, final Response p1) throws IOException;
}
