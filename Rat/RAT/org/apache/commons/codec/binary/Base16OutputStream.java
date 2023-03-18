//Raddon On Top!

package org.apache.commons.codec.binary;

import java.io.*;
import org.apache.commons.codec.*;

public class Base16OutputStream extends BaseNCodecOutputStream
{
    public Base16OutputStream(final OutputStream out) {
        this(out, true);
    }
    
    public Base16OutputStream(final OutputStream out, final boolean doEncode) {
        this(out, doEncode, false);
    }
    
    public Base16OutputStream(final OutputStream out, final boolean doEncode, final boolean lowerCase) {
        this(out, doEncode, lowerCase, CodecPolicy.LENIENT);
    }
    
    public Base16OutputStream(final OutputStream out, final boolean doEncode, final boolean lowerCase, final CodecPolicy decodingPolicy) {
        super(out, (BaseNCodec)new Base16(lowerCase, decodingPolicy), doEncode);
    }
}
