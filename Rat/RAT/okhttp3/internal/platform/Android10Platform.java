//Raddon On Top!

package okhttp3.internal.platform;

import android.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import okhttp3.*;
import java.io.*;
import javax.net.ssl.*;
import org.codehaus.mojo.animal_sniffer.*;
import android.net.ssl.*;
import javax.annotation.*;

@SuppressLint({ "NewApi" })
class Android10Platform extends AndroidPlatform
{
    Android10Platform(final Class<?> sslParametersClass) {
        super(sslParametersClass, null, null, null, null, null);
    }
    
    @SuppressLint({ "NewApi" })
    @IgnoreJRERequirement
    @Override
    public void configureTlsExtensions(final SSLSocket sslSocket, final String hostname, final List<Protocol> protocols) throws IOException {
        try {
            this.enableSessionTickets(sslSocket);
            final SSLParameters sslParameters = sslSocket.getSSLParameters();
            final String[] protocolsArray = Platform.alpnProtocolNames(protocols).toArray(new String[0]);
            sslParameters.setApplicationProtocols(protocolsArray);
            sslSocket.setSSLParameters(sslParameters);
        }
        catch (IllegalArgumentException iae) {
            throw new IOException("Android internal error", iae);
        }
    }
    
    private void enableSessionTickets(final SSLSocket sslSocket) {
        if (SSLSockets.isSupportedSocket(sslSocket)) {
            SSLSockets.setUseSessionTickets(sslSocket, true);
        }
    }
    
    @Nullable
    @IgnoreJRERequirement
    @Override
    public String getSelectedProtocol(final SSLSocket socket) {
        final String alpnResult = socket.getApplicationProtocol();
        if (alpnResult == null || alpnResult.isEmpty()) {
            return null;
        }
        return alpnResult;
    }
    
    @Nullable
    public static Platform buildIfSupported() {
        if (!Platform.isAndroid()) {
            return null;
        }
        try {
            if (getSdkInt() >= 29) {
                final Class<?> sslParametersClass = Class.forName("com.android.org.conscrypt.SSLParametersImpl");
                return new Android10Platform(sslParametersClass);
            }
        }
        catch (ReflectiveOperationException ex) {}
        return null;
    }
}
