//Raddon On Top!

package okio;

import java.io.*;

final class Base64
{
    private static final byte[] MAP;
    private static final byte[] URL_MAP;
    
    private Base64() {
    }
    
    public static byte[] decode(final String in) {
        int limit;
        for (limit = in.length(); limit > 0; --limit) {
            final char c = in.charAt(limit - 1);
            if (c != '=' && c != '\n' && c != '\r' && c != ' ' && c != '\t') {
                break;
            }
        }
        final byte[] out = new byte[(int)(limit * 6L / 8L)];
        int outCount = 0;
        int inCount = 0;
        int word = 0;
        for (int pos = 0; pos < limit; ++pos) {
            final char c2 = in.charAt(pos);
            int bits;
            if (c2 >= 'A' && c2 <= 'Z') {
                bits = c2 - 'A';
            }
            else if (c2 >= 'a' && c2 <= 'z') {
                bits = c2 - 'G';
            }
            else if (c2 >= '0' && c2 <= '9') {
                bits = c2 + '\u0004';
            }
            else if (c2 == '+' || c2 == '-') {
                bits = 62;
            }
            else if (c2 == '/' || c2 == '_') {
                bits = 63;
            }
            else {
                if (c2 == '\n' || c2 == '\r' || c2 == ' ') {
                    continue;
                }
                if (c2 == '\t') {
                    continue;
                }
                return null;
            }
            word = (word << 6 | (byte)bits);
            if (++inCount % 4 == 0) {
                out[outCount++] = (byte)(word >> 16);
                out[outCount++] = (byte)(word >> 8);
                out[outCount++] = (byte)word;
            }
        }
        final int lastWordChars = inCount % 4;
        if (lastWordChars == 1) {
            return null;
        }
        if (lastWordChars == 2) {
            word <<= 12;
            out[outCount++] = (byte)(word >> 16);
        }
        else if (lastWordChars == 3) {
            word <<= 6;
            out[outCount++] = (byte)(word >> 16);
            out[outCount++] = (byte)(word >> 8);
        }
        if (outCount == out.length) {
            return out;
        }
        final byte[] prefix = new byte[outCount];
        System.arraycopy(out, 0, prefix, 0, outCount);
        return prefix;
    }
    
    public static String encode(final byte[] in) {
        return encode(in, Base64.MAP);
    }
    
    public static String encodeUrl(final byte[] in) {
        return encode(in, Base64.URL_MAP);
    }
    
    private static String encode(final byte[] in, final byte[] map) {
        final int length = (in.length + 2) / 3 * 4;
        final byte[] out = new byte[length];
        int index = 0;
        final int end = in.length - in.length % 3;
        for (int i = 0; i < end; i += 3) {
            out[index++] = map[(in[i] & 0xFF) >> 2];
            out[index++] = map[(in[i] & 0x3) << 4 | (in[i + 1] & 0xFF) >> 4];
            out[index++] = map[(in[i + 1] & 0xF) << 2 | (in[i + 2] & 0xFF) >> 6];
            out[index++] = map[in[i + 2] & 0x3F];
        }
        switch (in.length % 3) {
            case 1: {
                out[index++] = map[(in[end] & 0xFF) >> 2];
                out[index++] = map[(in[end] & 0x3) << 4];
                out[index++] = 61;
                out[index++] = 61;
                break;
            }
            case 2: {
                out[index++] = map[(in[end] & 0xFF) >> 2];
                out[index++] = map[(in[end] & 0x3) << 4 | (in[end + 1] & 0xFF) >> 4];
                out[index++] = map[(in[end + 1] & 0xF) << 2];
                out[index++] = 61;
                break;
            }
        }
        try {
            return new String(out, "US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    static {
        MAP = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        URL_MAP = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
    }
}
