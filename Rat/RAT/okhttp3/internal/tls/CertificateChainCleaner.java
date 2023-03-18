//Raddon On Top!

package okhttp3.internal.tls;

import java.util.*;
import javax.net.ssl.*;
import okhttp3.internal.platform.*;
import java.security.cert.*;

public abstract class CertificateChainCleaner
{
    public abstract List<Certificate> clean(final List<Certificate> p0, final String p1) throws SSLPeerUnverifiedException;
    
    public static CertificateChainCleaner get(final X509TrustManager trustManager) {
        return Platform.get().buildCertificateChainCleaner(trustManager);
    }
    
    public static CertificateChainCleaner get(final X509Certificate... caCerts) {
        return (CertificateChainCleaner)new BasicCertificateChainCleaner((TrustRootIndex)new BasicTrustRootIndex(caCerts));
    }
}
