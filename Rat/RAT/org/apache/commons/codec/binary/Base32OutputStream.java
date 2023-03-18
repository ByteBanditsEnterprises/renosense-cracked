//Raddon On Top!

package org.apache.commons.codec.binary;

import java.io.*;
import org.apache.commons.codec.*;

public class Base32OutputStream extends BaseNCodecOutputStream
{
    public Base32OutputStream(final OutputStream out) {
        this(out, true);
    }
    
    public Base32OutputStream(final OutputStream out, final boolean doEncode) {
        super(out, (BaseNCodec)new Base32(false), doEncode);
    }
    
    public Base32OutputStream(final OutputStream ouput, final boolean doEncode, final int lineLength, final byte[] lineSeparator) {
        super(ouput, (BaseNCodec)new Base32(lineLength, lineSeparator), doEncode);
    }
    
    public Base32OutputStream(final OutputStream ouput, final boolean doEncode, final int lineLength, final byte[] lineSeparator, final CodecPolicy decodingPolicy) {
        super(ouput, (BaseNCodec)new Base32(lineLength, lineSeparator, false, (byte)61, decodingPolicy), doEncode);
    }
}
