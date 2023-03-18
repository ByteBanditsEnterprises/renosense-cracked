//Raddon On Top!

package org.apache.commons.codec.digest;

import java.security.*;
import java.util.*;

class B64
{
    static final String B64T_STRING = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final char[] B64T_ARRAY;
    
    static void b64from24bit(final byte b2, final byte b1, final byte b0, final int outLen, final StringBuilder buffer) {
        int w = (b2 << 16 & 0xFFFFFF) | (b1 << 8 & 0xFFFF) | (b0 & 0xFF);
        int n = outLen;
        while (n-- > 0) {
            buffer.append(B64.B64T_ARRAY[w & 0x3F]);
            w >>= 6;
        }
    }
    
    static String getRandomSalt(final int num) {
        return getRandomSalt(num, new SecureRandom());
    }
    
    static String getRandomSalt(final int num, final Random random) {
        final StringBuilder saltString = new StringBuilder(num);
        for (int i = 1; i <= num; ++i) {
            saltString.append("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(random.nextInt("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length())));
        }
        return saltString.toString();
    }
    
    static {
        B64T_ARRAY = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    }
}
