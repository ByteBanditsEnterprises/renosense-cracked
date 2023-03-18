//Raddon On Top!

package org.apache.commons.codec.binary;

import java.io.*;
import org.apache.commons.codec.*;

public class Base16InputStream extends BaseNCodecInputStream
{
    public Base16InputStream(final InputStream in) {
        this(in, false);
    }
    
    public Base16InputStream(final InputStream in, final boolean doEncode) {
        this(in, doEncode, false);
    }
    
    public Base16InputStream(final InputStream in, final boolean doEncode, final boolean lowerCase) {
        this(in, doEncode, lowerCase, CodecPolicy.LENIENT);
    }
    
    public Base16InputStream(final InputStream in, final boolean doEncode, final boolean lowerCase, final CodecPolicy decodingPolicy) {
        super(in, (BaseNCodec)new Base16(lowerCase, decodingPolicy), doEncode);
    }
}
