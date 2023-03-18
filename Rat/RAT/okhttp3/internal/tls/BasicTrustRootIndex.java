//Raddon On Top!

package okhttp3.internal.tls;

import javax.security.auth.x500.*;
import java.security.cert.*;
import java.util.*;
import java.security.*;

public final class BasicTrustRootIndex implements TrustRootIndex
{
    private final Map<X500Principal, Set<X509Certificate>> subjectToCaCerts;
    
    public BasicTrustRootIndex(final X509Certificate... caCerts) {
        this.subjectToCaCerts = new LinkedHashMap<X500Principal, Set<X509Certificate>>();
        for (final X509Certificate caCert : caCerts) {
            final X500Principal subject = caCert.getSubjectX500Principal();
            Set<X509Certificate> subjectCaCerts = this.subjectToCaCerts.get(subject);
            if (subjectCaCerts == null) {
                subjectCaCerts = new LinkedHashSet<X509Certificate>(1);
                this.subjectToCaCerts.put(subject, subjectCaCerts);
            }
            subjectCaCerts.add(caCert);
        }
    }
    
    @Override
    public X509Certificate findByIssuerAndSignature(final X509Certificate cert) {
        final X500Principal issuer = cert.getIssuerX500Principal();
        final Set<X509Certificate> subjectCaCerts = this.subjectToCaCerts.get(issuer);
        if (subjectCaCerts == null) {
            return null;
        }
        for (final X509Certificate caCert : subjectCaCerts) {
            final PublicKey publicKey = caCert.getPublicKey();
            try {
                cert.verify(publicKey);
                return caCert;
            }
            catch (Exception ex) {
                continue;
            }
            break;
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other == this || (other instanceof BasicTrustRootIndex && ((BasicTrustRootIndex)other).subjectToCaCerts.equals(this.subjectToCaCerts));
    }
    
    @Override
    public int hashCode() {
        return this.subjectToCaCerts.hashCode();
    }
}
