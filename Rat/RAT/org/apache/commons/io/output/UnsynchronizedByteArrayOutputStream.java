//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;
import org.apache.commons.io.input.*;

public final class UnsynchronizedByteArrayOutputStream extends AbstractByteArrayOutputStream
{
    public UnsynchronizedByteArrayOutputStream() {
        this(1024);
    }
    
    public UnsynchronizedByteArrayOutputStream(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        this.needNewBuffer(size);
    }
    
    public void write(final byte[] b, final int off, final int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException(String.format("offset=%,d, length=%,d", off, len));
        }
        if (len == 0) {
            return;
        }
        this.writeImpl(b, off, len);
    }
    
    public void write(final int b) {
        this.writeImpl(b);
    }
    
    public int write(final InputStream in) throws IOException {
        return this.writeImpl(in);
    }
    
    public int size() {
        return this.count;
    }
    
    public void reset() {
        this.resetImpl();
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        this.writeToImpl(out);
    }
    
    public static InputStream toBufferedInputStream(final InputStream input) throws IOException {
        return toBufferedInputStream(input, 1024);
    }
    
    public static InputStream toBufferedInputStream(final InputStream input, final int size) throws IOException {
        try (final UnsynchronizedByteArrayOutputStream output = new UnsynchronizedByteArrayOutputStream(size)) {
            output.write(input);
            return output.toInputStream();
        }
    }
    
    public InputStream toInputStream() {
        return this.toInputStream(UnsynchronizedByteArrayInputStream::new);
    }
    
    public byte[] toByteArray() {
        return this.toByteArrayImpl();
    }
}
