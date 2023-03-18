//Raddon On Top!

package okhttp3;

import java.io.*;

public enum Protocol
{
    HTTP_1_0("http/1.0"), 
    HTTP_1_1("http/1.1"), 
    @Deprecated
    SPDY_3("spdy/3.1"), 
    HTTP_2("h2"), 
    H2_PRIOR_KNOWLEDGE("h2_prior_knowledge"), 
    QUIC("quic");
    
    private final String protocol;
    
    private Protocol(final String protocol) {
        this.protocol = protocol;
    }
    
    public static Protocol get(final String protocol) throws IOException {
        if (protocol.equals(Protocol.HTTP_1_0.protocol)) {
            return Protocol.HTTP_1_0;
        }
        if (protocol.equals(Protocol.HTTP_1_1.protocol)) {
            return Protocol.HTTP_1_1;
        }
        if (protocol.equals(Protocol.H2_PRIOR_KNOWLEDGE.protocol)) {
            return Protocol.H2_PRIOR_KNOWLEDGE;
        }
        if (protocol.equals(Protocol.HTTP_2.protocol)) {
            return Protocol.HTTP_2;
        }
        if (protocol.equals(Protocol.SPDY_3.protocol)) {
            return Protocol.SPDY_3;
        }
        if (protocol.equals(Protocol.QUIC.protocol)) {
            return Protocol.QUIC;
        }
        throw new IOException("Unexpected protocol: " + protocol);
    }
    
    @Override
    public String toString() {
        return this.protocol;
    }
}
