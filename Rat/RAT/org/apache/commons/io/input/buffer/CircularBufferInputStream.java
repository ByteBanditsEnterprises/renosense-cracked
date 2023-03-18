//Raddon On Top!

package org.apache.commons.io.input.buffer;

import java.util.*;
import org.apache.commons.io.*;
import java.io.*;

public class CircularBufferInputStream extends InputStream
{
    protected final InputStream in;
    protected final CircularByteBuffer buffer;
    protected final int bufferSize;
    private boolean eof;
    
    public CircularBufferInputStream(final InputStream inputStream, final int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Invalid bufferSize: " + bufferSize);
        }
        this.in = Objects.requireNonNull(inputStream, "inputStream");
        this.buffer = new CircularByteBuffer(bufferSize);
        this.bufferSize = bufferSize;
        this.eof = false;
    }
    
    public CircularBufferInputStream(final InputStream inputStream) {
        this(inputStream, 8192);
    }
    
    protected void fillBuffer() throws IOException {
        if (this.eof) {
            return;
        }
        int space = this.buffer.getSpace();
        final byte[] buf = IOUtils.byteArray(space);
        while (space > 0) {
            final int res = this.in.read(buf, 0, space);
            if (res == -1) {
                this.eof = true;
                return;
            }
            if (res <= 0) {
                continue;
            }
            this.buffer.add(buf, 0, res);
            space -= res;
        }
    }
    
    protected boolean haveBytes(final int count) throws IOException {
        if (this.buffer.getCurrentNumberOfBytes() < count) {
            this.fillBuffer();
        }
        return this.buffer.hasBytes();
    }
    
    @Override
    public int read() throws IOException {
        if (!this.haveBytes(1)) {
            return -1;
        }
        return this.buffer.read() & 0xFF;
    }
    
    @Override
    public int read(final byte[] buffer) throws IOException {
        return this.read(buffer, 0, buffer.length);
    }
    
    @Override
    public int read(final byte[] targetBuffer, final int offset, final int length) throws IOException {
        Objects.requireNonNull(targetBuffer, "targetBuffer");
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative");
        }
        if (!this.haveBytes(length)) {
            return -1;
        }
        final int result = Math.min(length, this.buffer.getCurrentNumberOfBytes());
        for (int i = 0; i < result; ++i) {
            targetBuffer[offset + i] = this.buffer.read();
        }
        return result;
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
        this.eof = true;
        this.buffer.clear();
    }
}
