//Raddon On Top!

package okhttp3;

import java.io.*;
import javax.net.ssl.*;
import okhttp3.internal.*;
import java.security.*;
import java.security.cert.*;
import javax.annotation.*;
import java.util.*;

public final class Handshake
{
    private final TlsVersion tlsVersion;
    private final CipherSuite cipherSuite;
    private final List<Certificate> peerCertificates;
    private final List<Certificate> localCertificates;
    
    private Handshake(final TlsVersion tlsVersion, final CipherSuite cipherSuite, final List<Certificate> peerCertificates, final List<Certificate> localCertificates) {
        this.tlsVersion = tlsVersion;
        this.cipherSuite = cipherSuite;
        this.peerCertificates = peerCertificates;
        this.localCertificates = localCertificates;
    }
    
    public static Handshake get(final SSLSession session) throws IOException {
        final String cipherSuiteString = session.getCipherSuite();
        if (cipherSuiteString == null) {
            throw new IllegalStateException("cipherSuite == null");
        }
        if ("SSL_NULL_WITH_NULL_NULL".equals(cipherSuiteString)) {
            throw new IOException("cipherSuite == SSL_NULL_WITH_NULL_NULL");
        }
        final CipherSuite cipherSuite = CipherSuite.forJavaName(cipherSuiteString);
        final String tlsVersionString = session.getProtocol();
        if (tlsVersionString == null) {
            throw new IllegalStateException("tlsVersion == null");
        }
        if ("NONE".equals(tlsVersionString)) {
            throw new IOException("tlsVersion == NONE");
        }
        final TlsVersion tlsVersion = TlsVersion.forJavaName(tlsVersionString);
        Certificate[] peerCertificates;
        try {
            peerCertificates = session.getPeerCertificates();
        }
        catch (SSLPeerUnverifiedException ignored) {
            peerCertificates = null;
        }
        final List<Certificate> peerCertificatesList = (peerCertificates != null) ? Util.immutableList(peerCertificates) : Collections.emptyList();
        final Certificate[] localCertificates = session.getLocalCertificates();
        final List<Certificate> localCertificatesList = (localCertificates != null) ? Util.immutableList(localCertificates) : Collections.emptyList();
        return new Handshake(tlsVersion, cipherSuite, peerCertificatesList, localCertificatesList);
    }
    
    public static Handshake get(final TlsVersion tlsVersion, final CipherSuite cipherSuite, final List<Certificate> peerCertificates, final List<Certificate> localCertificates) {
        if (tlsVersion == null) {
            throw new NullPointerException("tlsVersion == null");
        }
        if (cipherSuite == null) {
            throw new NullPointerException("cipherSuite == null");
        }
        return new Handshake(tlsVersion, cipherSuite, Util.immutableList(peerCertificates), Util.immutableList(localCertificates));
    }
    
    public TlsVersion tlsVersion() {
        return this.tlsVersion;
    }
    
    public CipherSuite cipherSuite() {
        return this.cipherSuite;
    }
    
    public List<Certificate> peerCertificates() {
        return this.peerCertificates;
    }
    
    @Nullable
    public Principal peerPrincipal() {
        return this.peerCertificates.isEmpty() ? null : this.peerCertificates.get(0).getSubjectX500Principal();
    }
    
    public List<Certificate> localCertificates() {
        return this.localCertificates;
    }
    
    @Nullable
    public Principal localPrincipal() {
        return this.localCertificates.isEmpty() ? null : this.localCertificates.get(0).getSubjectX500Principal();
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        if (!(other instanceof Handshake)) {
            return false;
        }
        final Handshake that = (Handshake)other;
        return this.tlsVersion.equals(that.tlsVersion) && this.cipherSuite.equals(that.cipherSuite) && this.peerCertificates.equals(that.peerCertificates) && this.localCertificates.equals(that.localCertificates);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.tlsVersion.hashCode();
        result = 31 * result + this.cipherSuite.hashCode();
        result = 31 * result + this.peerCertificates.hashCode();
        result = 31 * result + this.localCertificates.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "Handshake{tlsVersion=" + this.tlsVersion + " cipherSuite=" + this.cipherSuite + " peerCertificates=" + this.names(this.peerCertificates) + " localCertificates=" + this.names(this.localCertificates) + '}';
    }
    
    private List<String> names(final List<Certificate> certificates) {
        final ArrayList<String> strings = new ArrayList<String>();
        for (final Certificate cert : certificates) {
            if (cert instanceof X509Certificate) {
                strings.add(String.valueOf(((X509Certificate)cert).getSubjectDN()));
            }
            else {
                strings.add(cert.getType());
            }
        }
        return strings;
    }
}
