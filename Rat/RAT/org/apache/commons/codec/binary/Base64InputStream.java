//Raddon On Top!

package org.apache.commons.codec.binary;

import java.io.*;
import org.apache.commons.codec.*;

public class Base64InputStream extends BaseNCodecInputStream
{
    public Base64InputStream(final InputStream in) {
        this(in, false);
    }
    
    public Base64InputStream(final InputStream in, final boolean doEncode) {
        super(in, (BaseNCodec)new Base64(false), doEncode);
    }
    
    public Base64InputStream(final InputStream in, final boolean doEncode, final int lineLength, final byte[] lineSeparator) {
        super(in, (BaseNCodec)new Base64(lineLength, lineSeparator), doEncode);
    }
    
    public Base64InputStream(final InputStream in, final boolean doEncode, final int lineLength, final byte[] lineSeparator, final CodecPolicy decodingPolicy) {
        super(in, (BaseNCodec)new Base64(lineLength, lineSeparator, false, decodingPolicy), doEncode);
    }
}
