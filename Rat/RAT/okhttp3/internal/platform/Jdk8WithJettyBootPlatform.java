//Raddon On Top!

package okhttp3.internal.platform;

import javax.net.ssl.*;
import java.util.*;
import okhttp3.*;
import java.lang.reflect.*;
import javax.annotation.*;
import okhttp3.internal.*;

class Jdk8WithJettyBootPlatform extends Platform
{
    private final Method putMethod;
    private final Method getMethod;
    private final Method removeMethod;
    private final Class<?> clientProviderClass;
    private final Class<?> serverProviderClass;
    
    Jdk8WithJettyBootPlatform(final Method putMethod, final Method getMethod, final Method removeMethod, final Class<?> clientProviderClass, final Class<?> serverProviderClass) {
        this.putMethod = putMethod;
        this.getMethod = getMethod;
        this.removeMethod = removeMethod;
        this.clientProviderClass = clientProviderClass;
        this.serverProviderClass = serverProviderClass;
    }
    
    @Override
    public void configureTlsExtensions(final SSLSocket sslSocket, final String hostname, final List<Protocol> protocols) {
        final List<String> names = Platform.alpnProtocolNames(protocols);
        try {
            final Object alpnProvider = Proxy.newProxyInstance(Platform.class.getClassLoader(), new Class[] { this.clientProviderClass, this.serverProviderClass }, new AlpnProvider(names));
            this.putMethod.invoke(null, sslSocket, alpnProvider);
        }
        catch (InvocationTargetException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new AssertionError("failed to set ALPN", e);
        }
    }
    
    @Override
    public void afterHandshake(final SSLSocket sslSocket) {
        try {
            this.removeMethod.invoke(null, sslSocket);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new AssertionError("failed to remove ALPN", e);
        }
    }
    
    @Nullable
    @Override
    public String getSelectedProtocol(final SSLSocket socket) {
        try {
            final AlpnProvider provider = (AlpnProvider)Proxy.getInvocationHandler(this.getMethod.invoke(null, socket));
            if (!provider.unsupported && provider.selected == null) {
                Platform.get().log(4, "ALPN callback dropped: HTTP/2 is disabled. Is alpn-boot on the boot class path?", null);
                return null;
            }
            return provider.unsupported ? null : provider.selected;
        }
        catch (InvocationTargetException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new AssertionError("failed to get ALPN selected protocol", e);
        }
    }
    
    public static Platform buildIfSupported() {
        try {
            final String alpnClassName = "org.eclipse.jetty.alpn.ALPN";
            final Class<?> alpnClass = Class.forName(alpnClassName, true, null);
            final Class<?> providerClass = Class.forName(alpnClassName + "$Provider", true, null);
            final Class<?> clientProviderClass = Class.forName(alpnClassName + "$ClientProvider", true, null);
            final Class<?> serverProviderClass = Class.forName(alpnClassName + "$ServerProvider", true, null);
            final Method putMethod = alpnClass.getMethod("put", SSLSocket.class, providerClass);
            final Method getMethod = alpnClass.getMethod("get", SSLSocket.class);
            final Method removeMethod = alpnClass.getMethod("remove", SSLSocket.class);
            return new Jdk8WithJettyBootPlatform(putMethod, getMethod, removeMethod, clientProviderClass, serverProviderClass);
        }
        catch (ClassNotFoundException | NoSuchMethodException ex) {
            return null;
        }
    }
    
    private static class AlpnProvider implements InvocationHandler
    {
        private final List<String> protocols;
        boolean unsupported;
        String selected;
        
        AlpnProvider(final List<String> protocols) {
            this.protocols = protocols;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, Object[] args) throws Throwable {
            final String methodName = method.getName();
            final Class<?> returnType = method.getReturnType();
            if (args == null) {
                args = Util.EMPTY_STRING_ARRAY;
            }
            if (methodName.equals("supports") && Boolean.TYPE == returnType) {
                return true;
            }
            if (methodName.equals("unsupported") && Void.TYPE == returnType) {
                this.unsupported = true;
                return null;
            }
            if (methodName.equals("protocols") && args.length == 0) {
                return this.protocols;
            }
            if ((methodName.equals("selectProtocol") || methodName.equals("select")) && String.class == returnType && args.length == 1 && args[0] instanceof List) {
                final List<?> peerProtocols = (List<?>)args[0];
                for (int i = 0, size = peerProtocols.size(); i < size; ++i) {
                    final String protocol = (String)peerProtocols.get(i);
                    if (this.protocols.contains(protocol)) {
                        return this.selected = protocol;
                    }
                }
                return this.selected = this.protocols.get(0);
            }
            if ((methodName.equals("protocolSelected") || methodName.equals("selected")) && args.length == 1) {
                this.selected = (String)args[0];
                return null;
            }
            return method.invoke(this, args);
        }
    }
}
