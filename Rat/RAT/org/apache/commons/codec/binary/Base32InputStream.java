//Raddon On Top!

package org.apache.commons.codec.binary;

import java.io.*;
import org.apache.commons.codec.*;

public class Base32InputStream extends BaseNCodecInputStream
{
    public Base32InputStream(final InputStream in) {
        this(in, false);
    }
    
    public Base32InputStream(final InputStream in, final boolean doEncode) {
        super(in, (BaseNCodec)new Base32(false), doEncode);
    }
    
    public Base32InputStream(final InputStream input, final boolean doEncode, final int lineLength, final byte[] lineSeparator) {
        super(input, (BaseNCodec)new Base32(lineLength, lineSeparator), doEncode);
    }
    
    public Base32InputStream(final InputStream input, final boolean doEncode, final int lineLength, final byte[] lineSeparator, final CodecPolicy decodingPolicy) {
        super(input, (BaseNCodec)new Base32(lineLength, lineSeparator, false, (byte)61, decodingPolicy), doEncode);
    }
}
