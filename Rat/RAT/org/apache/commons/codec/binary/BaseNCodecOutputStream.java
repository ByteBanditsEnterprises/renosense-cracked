//Raddon On Top!

package org.apache.commons.codec.binary;

import java.io.*;
import java.util.*;

public class BaseNCodecOutputStream extends FilterOutputStream
{
    private final boolean doEncode;
    private final BaseNCodec baseNCodec;
    private final byte[] singleByte;
    private final BaseNCodec.Context context;
    
    public BaseNCodecOutputStream(final OutputStream output, final BaseNCodec basedCodec, final boolean doEncode) {
        super(output);
        this.singleByte = new byte[1];
        this.context = new BaseNCodec.Context();
        this.baseNCodec = basedCodec;
        this.doEncode = doEncode;
    }
    
    @Override
    public void close() throws IOException {
        this.eof();
        this.flush();
        this.out.close();
    }
    
    public void eof() throws IOException {
        if (this.doEncode) {
            this.baseNCodec.encode(this.singleByte, 0, -1, this.context);
        }
        else {
            this.baseNCodec.decode(this.singleByte, 0, -1, this.context);
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.flush(true);
    }
    
    private void flush(final boolean propagate) throws IOException {
        final int avail = this.baseNCodec.available(this.context);
        if (avail > 0) {
            final byte[] buf = new byte[avail];
            final int c = this.baseNCodec.readResults(buf, 0, avail, this.context);
            if (c > 0) {
                this.out.write(buf, 0, c);
            }
        }
        if (propagate) {
            this.out.flush();
        }
    }
    
    public boolean isStrictDecoding() {
        return this.baseNCodec.isStrictDecoding();
    }
    
    @Override
    public void write(final byte[] array, final int offset, final int len) throws IOException {
        Objects.requireNonNull(array, "array");
        if (offset < 0 || len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > array.length || offset + len > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > 0) {
            if (this.doEncode) {
                this.baseNCodec.encode(array, offset, len, this.context);
            }
            else {
                this.baseNCodec.decode(array, offset, len, this.context);
            }
            this.flush(false);
        }
    }
    
    @Override
    public void write(final int i) throws IOException {
        this.singleByte[0] = (byte)i;
        this.write(this.singleByte, 0, 1);
    }
}
