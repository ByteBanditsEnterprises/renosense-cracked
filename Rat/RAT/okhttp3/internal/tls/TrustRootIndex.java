//Raddon On Top!

package okhttp3.internal.tls;

import java.security.cert.*;

public interface TrustRootIndex
{
    X509Certificate findByIssuerAndSignature(final X509Certificate p0);
}
