//Raddon On Top!

package okhttp3.internal.http2;

import okio.*;
import okhttp3.internal.*;
import java.io.*;

public final class Http2
{
    static final ByteString CONNECTION_PREFACE;
    static final int INITIAL_MAX_FRAME_SIZE = 16384;
    static final byte TYPE_DATA = 0;
    static final byte TYPE_HEADERS = 1;
    static final byte TYPE_PRIORITY = 2;
    static final byte TYPE_RST_STREAM = 3;
    static final byte TYPE_SETTINGS = 4;
    static final byte TYPE_PUSH_PROMISE = 5;
    static final byte TYPE_PING = 6;
    static final byte TYPE_GOAWAY = 7;
    static final byte TYPE_WINDOW_UPDATE = 8;
    static final byte TYPE_CONTINUATION = 9;
    static final byte FLAG_NONE = 0;
    static final byte FLAG_ACK = 1;
    static final byte FLAG_END_STREAM = 1;
    static final byte FLAG_END_HEADERS = 4;
    static final byte FLAG_END_PUSH_PROMISE = 4;
    static final byte FLAG_PADDED = 8;
    static final byte FLAG_PRIORITY = 32;
    static final byte FLAG_COMPRESSED = 32;
    private static final String[] FRAME_NAMES;
    static final String[] FLAGS;
    static final String[] BINARY;
    
    private Http2() {
    }
    
    static IllegalArgumentException illegalArgument(final String message, final Object... args) {
        throw new IllegalArgumentException(Util.format(message, args));
    }
    
    static IOException ioException(final String message, final Object... args) throws IOException {
        throw new IOException(Util.format(message, args));
    }
    
    static String frameLog(final boolean inbound, final int streamId, final int length, final byte type, final byte flags) {
        final String formattedType = (type < Http2.FRAME_NAMES.length) ? Http2.FRAME_NAMES[type] : Util.format("0x%02x", type);
        final String formattedFlags = formatFlags(type, flags);
        return Util.format("%s 0x%08x %5d %-13s %s", inbound ? "<<" : ">>", streamId, length, formattedType, formattedFlags);
    }
    
    static String formatFlags(final byte type, final byte flags) {
        if (flags == 0) {
            return "";
        }
        switch (type) {
            case 4:
            case 6: {
                return (flags == 1) ? "ACK" : Http2.BINARY[flags];
            }
            case 2:
            case 3:
            case 7:
            case 8: {
                return Http2.BINARY[flags];
            }
            default: {
                final String result = (flags < Http2.FLAGS.length) ? Http2.FLAGS[flags] : Http2.BINARY[flags];
                if (type == 5 && (flags & 0x4) != 0x0) {
                    return result.replace("HEADERS", "PUSH_PROMISE");
                }
                if (type == 0 && (flags & 0x20) != 0x0) {
                    return result.replace("PRIORITY", "COMPRESSED");
                }
                return result;
            }
        }
    }
    
    static {
        CONNECTION_PREFACE = ByteString.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");
        FRAME_NAMES = new String[] { "DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION" };
        FLAGS = new String[64];
        BINARY = new String[256];
        for (int i = 0; i < Http2.BINARY.length; ++i) {
            Http2.BINARY[i] = Util.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
        }
        Http2.FLAGS[0] = "";
        Http2.FLAGS[1] = "END_STREAM";
        final int[] prefixFlags = { 1 };
        Http2.FLAGS[8] = "PADDED";
        for (final int prefixFlag : prefixFlags) {
            Http2.FLAGS[prefixFlag | 0x8] = Http2.FLAGS[prefixFlag] + "|PADDED";
        }
        Http2.FLAGS[4] = "END_HEADERS";
        Http2.FLAGS[32] = "PRIORITY";
        Http2.FLAGS[36] = "END_HEADERS|PRIORITY";
        final int[] array2;
        final int[] frameFlags = array2 = new int[] { 4, 32, 36 };
        for (final int frameFlag : array2) {
            for (final int prefixFlag2 : prefixFlags) {
                Http2.FLAGS[prefixFlag2 | frameFlag] = Http2.FLAGS[prefixFlag2] + '|' + Http2.FLAGS[frameFlag];
                Http2.FLAGS[prefixFlag2 | frameFlag | 0x8] = Http2.FLAGS[prefixFlag2] + '|' + Http2.FLAGS[frameFlag] + "|PADDED";
            }
        }
        for (int j = 0; j < Http2.FLAGS.length; ++j) {
            if (Http2.FLAGS[j] == null) {
                Http2.FLAGS[j] = Http2.BINARY[j];
            }
        }
    }
}
