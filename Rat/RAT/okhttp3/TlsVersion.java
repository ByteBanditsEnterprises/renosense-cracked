//Raddon On Top!

package okhttp3;

import java.util.*;

public enum TlsVersion
{
    TLS_1_3("TLSv1.3"), 
    TLS_1_2("TLSv1.2"), 
    TLS_1_1("TLSv1.1"), 
    TLS_1_0("TLSv1"), 
    SSL_3_0("SSLv3");
    
    final String javaName;
    
    private TlsVersion(final String javaName) {
        this.javaName = javaName;
    }
    
    public static TlsVersion forJavaName(final String javaName) {
        switch (javaName) {
            case "TLSv1.3": {
                return TlsVersion.TLS_1_3;
            }
            case "TLSv1.2": {
                return TlsVersion.TLS_1_2;
            }
            case "TLSv1.1": {
                return TlsVersion.TLS_1_1;
            }
            case "TLSv1": {
                return TlsVersion.TLS_1_0;
            }
            case "SSLv3": {
                return TlsVersion.SSL_3_0;
            }
            default: {
                throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
            }
        }
    }
    
    static List<TlsVersion> forJavaNames(final String... tlsVersions) {
        final List<TlsVersion> result = new ArrayList<TlsVersion>(tlsVersions.length);
        for (final String tlsVersion : tlsVersions) {
            result.add(forJavaName(tlsVersion));
        }
        return Collections.unmodifiableList((List<? extends TlsVersion>)result);
    }
    
    public String javaName() {
        return this.javaName;
    }
}
