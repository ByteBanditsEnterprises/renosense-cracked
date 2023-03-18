//Raddon On Top!

package okhttp3.internal.tls;

import javax.net.ssl.*;
import okhttp3.internal.*;
import java.util.*;
import java.security.cert.*;

public final class OkHostnameVerifier implements HostnameVerifier
{
    public static final OkHostnameVerifier INSTANCE;
    private static final int ALT_DNS_NAME = 2;
    private static final int ALT_IPA_NAME = 7;
    
    private OkHostnameVerifier() {
    }
    
    @Override
    public boolean verify(final String host, final SSLSession session) {
        try {
            final Certificate[] certificates = session.getPeerCertificates();
            return this.verify(host, (X509Certificate)certificates[0]);
        }
        catch (SSLException e) {
            return false;
        }
    }
    
    public boolean verify(final String host, final X509Certificate certificate) {
        return Util.verifyAsIpAddress(host) ? this.verifyIpAddress(host, certificate) : this.verifyHostname(host, certificate);
    }
    
    private boolean verifyIpAddress(final String ipAddress, final X509Certificate certificate) {
        final List<String> altNames = getSubjectAltNames(certificate, 7);
        for (int i = 0, size = altNames.size(); i < size; ++i) {
            if (ipAddress.equalsIgnoreCase(altNames.get(i))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean verifyHostname(String hostname, final X509Certificate certificate) {
        hostname = hostname.toLowerCase(Locale.US);
        final List<String> altNames = getSubjectAltNames(certificate, 2);
        for (final String altName : altNames) {
            if (this.verifyHostname(hostname, altName)) {
                return true;
            }
        }
        return false;
    }
    
    public static List<String> allSubjectAltNames(final X509Certificate certificate) {
        final List<String> altIpaNames = getSubjectAltNames(certificate, 7);
        final List<String> altDnsNames = getSubjectAltNames(certificate, 2);
        final List<String> result = new ArrayList<String>(altIpaNames.size() + altDnsNames.size());
        result.addAll(altIpaNames);
        result.addAll(altDnsNames);
        return result;
    }
    
    private static List<String> getSubjectAltNames(final X509Certificate certificate, final int type) {
        final List<String> result = new ArrayList<String>();
        try {
            final Collection<?> subjectAltNames = certificate.getSubjectAlternativeNames();
            if (subjectAltNames == null) {
                return Collections.emptyList();
            }
            for (final Object subjectAltName : subjectAltNames) {
                final List<?> entry = (List<?>)subjectAltName;
                if (entry != null) {
                    if (entry.size() < 2) {
                        continue;
                    }
                    final Integer altNameType = (Integer)entry.get(0);
                    if (altNameType == null) {
                        continue;
                    }
                    if (altNameType != type) {
                        continue;
                    }
                    final String altName = (String)entry.get(1);
                    if (altName == null) {
                        continue;
                    }
                    result.add(altName);
                }
            }
            return result;
        }
        catch (CertificateParsingException e) {
            return Collections.emptyList();
        }
    }
    
    public boolean verifyHostname(String hostname, String pattern) {
        if (hostname == null || hostname.length() == 0 || hostname.startsWith(".") || hostname.endsWith("..")) {
            return false;
        }
        if (pattern == null || pattern.length() == 0 || pattern.startsWith(".") || pattern.endsWith("..")) {
            return false;
        }
        if (!hostname.endsWith(".")) {
            hostname += '.';
        }
        if (!pattern.endsWith(".")) {
            pattern += '.';
        }
        pattern = pattern.toLowerCase(Locale.US);
        if (!pattern.contains("*")) {
            return hostname.equals(pattern);
        }
        if (!pattern.startsWith("*.") || pattern.indexOf(42, 1) != -1) {
            return false;
        }
        if (hostname.length() < pattern.length()) {
            return false;
        }
        if ("*.".equals(pattern)) {
            return false;
        }
        final String suffix = pattern.substring(1);
        if (!hostname.endsWith(suffix)) {
            return false;
        }
        final int suffixStartIndexInHostname = hostname.length() - suffix.length();
        return suffixStartIndexInHostname <= 0 || hostname.lastIndexOf(46, suffixStartIndexInHostname - 1) == -1;
    }
    
    static {
        INSTANCE = new OkHostnameVerifier();
    }
}
