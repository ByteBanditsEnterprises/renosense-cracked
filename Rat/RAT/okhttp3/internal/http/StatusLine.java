//Raddon On Top!

package okhttp3.internal.http;

import okhttp3.*;
import java.net.*;
import java.io.*;

public final class StatusLine
{
    public static final int HTTP_TEMP_REDIRECT = 307;
    public static final int HTTP_PERM_REDIRECT = 308;
    public static final int HTTP_CONTINUE = 100;
    public final Protocol protocol;
    public final int code;
    public final String message;
    
    public StatusLine(final Protocol protocol, final int code, final String message) {
        this.protocol = protocol;
        this.code = code;
        this.message = message;
    }
    
    public static StatusLine get(final Response response) {
        return new StatusLine(response.protocol(), response.code(), response.message());
    }
    
    public static StatusLine parse(final String statusLine) throws IOException {
        int codeStart;
        Protocol protocol;
        if (statusLine.startsWith("HTTP/1.")) {
            if (statusLine.length() < 9 || statusLine.charAt(8) != ' ') {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
            final int httpMinorVersion = statusLine.charAt(7) - '0';
            codeStart = 9;
            if (httpMinorVersion == 0) {
                protocol = Protocol.HTTP_1_0;
            }
            else {
                if (httpMinorVersion != 1) {
                    throw new ProtocolException("Unexpected status line: " + statusLine);
                }
                protocol = Protocol.HTTP_1_1;
            }
        }
        else {
            if (!statusLine.startsWith("ICY ")) {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
            protocol = Protocol.HTTP_1_0;
            codeStart = 4;
        }
        if (statusLine.length() < codeStart + 3) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }
        int code;
        try {
            code = Integer.parseInt(statusLine.substring(codeStart, codeStart + 3));
        }
        catch (NumberFormatException e) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
        }
        String message = "";
        if (statusLine.length() > codeStart + 3) {
            if (statusLine.charAt(codeStart + 3) != ' ') {
                throw new ProtocolException("Unexpected status line: " + statusLine);
            }
            message = statusLine.substring(codeStart + 4);
        }
        return new StatusLine(protocol, code, message);
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append((this.protocol == Protocol.HTTP_1_0) ? "HTTP/1.0" : "HTTP/1.1");
        result.append(' ').append(this.code);
        if (this.message != null) {
            result.append(' ').append(this.message);
        }
        return result.toString();
    }
}
