//Raddon On Top!

package okhttp3.internal.ws;

import okio.*;

public final class WebSocketProtocol
{
    static final String ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    static final int B0_FLAG_FIN = 128;
    static final int B0_FLAG_RSV1 = 64;
    static final int B0_FLAG_RSV2 = 32;
    static final int B0_FLAG_RSV3 = 16;
    static final int B0_MASK_OPCODE = 15;
    static final int OPCODE_FLAG_CONTROL = 8;
    static final int B1_FLAG_MASK = 128;
    static final int B1_MASK_LENGTH = 127;
    static final int OPCODE_CONTINUATION = 0;
    static final int OPCODE_TEXT = 1;
    static final int OPCODE_BINARY = 2;
    static final int OPCODE_CONTROL_CLOSE = 8;
    static final int OPCODE_CONTROL_PING = 9;
    static final int OPCODE_CONTROL_PONG = 10;
    static final long PAYLOAD_BYTE_MAX = 125L;
    static final long CLOSE_MESSAGE_MAX = 123L;
    static final int PAYLOAD_SHORT = 126;
    static final long PAYLOAD_SHORT_MAX = 65535L;
    static final int PAYLOAD_LONG = 127;
    static final int CLOSE_CLIENT_GOING_AWAY = 1001;
    static final int CLOSE_NO_STATUS_CODE = 1005;
    
    static void toggleMask(final Buffer.UnsafeCursor cursor, final byte[] key) {
        int keyIndex = 0;
        final int keyLength = key.length;
        do {
            final byte[] buffer = cursor.data;
            for (int i = cursor.start, end = cursor.end; i < end; ++i, ++keyIndex) {
                keyIndex %= keyLength;
                buffer[i] ^= key[keyIndex];
            }
        } while (cursor.next() != -1);
    }
    
    static String closeCodeExceptionMessage(final int code) {
        if (code < 1000 || code >= 5000) {
            return "Code must be in range [1000,5000): " + code;
        }
        if ((code >= 1004 && code <= 1006) || (code >= 1012 && code <= 2999)) {
            return "Code " + code + " is reserved and may not be used.";
        }
        return null;
    }
    
    static void validateCloseCode(final int code) {
        final String message = closeCodeExceptionMessage(code);
        if (message != null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static String acceptHeader(final String key) {
        return ByteString.encodeUtf8(key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
    }
    
    private WebSocketProtocol() {
        throw new AssertionError((Object)"No instances.");
    }
}
