//Raddon On Top!

package org.apache.commons.io;

import java.nio.charset.*;
import java.util.*;

public class Charsets
{
    private static final SortedMap<String, Charset> STANDARD_CHARSET_MAP;
    @Deprecated
    public static final Charset ISO_8859_1;
    @Deprecated
    public static final Charset US_ASCII;
    @Deprecated
    public static final Charset UTF_16;
    @Deprecated
    public static final Charset UTF_16BE;
    @Deprecated
    public static final Charset UTF_16LE;
    @Deprecated
    public static final Charset UTF_8;
    
    public static SortedMap<String, Charset> requiredCharsets() {
        return Charsets.STANDARD_CHARSET_MAP;
    }
    
    public static Charset toCharset(final Charset charset) {
        return (charset == null) ? Charset.defaultCharset() : charset;
    }
    
    public static Charset toCharset(final String charsetName) throws UnsupportedCharsetException {
        return (charsetName == null) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }
    
    static {
        final SortedMap<String, Charset> standardCharsetMap = new TreeMap<String, Charset>(String.CASE_INSENSITIVE_ORDER);
        standardCharsetMap.put(StandardCharsets.ISO_8859_1.name(), StandardCharsets.ISO_8859_1);
        standardCharsetMap.put(StandardCharsets.US_ASCII.name(), StandardCharsets.US_ASCII);
        standardCharsetMap.put(StandardCharsets.UTF_16.name(), StandardCharsets.UTF_16);
        standardCharsetMap.put(StandardCharsets.UTF_16BE.name(), StandardCharsets.UTF_16BE);
        standardCharsetMap.put(StandardCharsets.UTF_16LE.name(), StandardCharsets.UTF_16LE);
        standardCharsetMap.put(StandardCharsets.UTF_8.name(), StandardCharsets.UTF_8);
        STANDARD_CHARSET_MAP = Collections.unmodifiableSortedMap((SortedMap<String, ? extends Charset>)standardCharsetMap);
        ISO_8859_1 = StandardCharsets.ISO_8859_1;
        US_ASCII = StandardCharsets.US_ASCII;
        UTF_16 = StandardCharsets.UTF_16;
        UTF_16BE = StandardCharsets.UTF_16BE;
        UTF_16LE = StandardCharsets.UTF_16LE;
        UTF_8 = StandardCharsets.UTF_8;
    }
}
