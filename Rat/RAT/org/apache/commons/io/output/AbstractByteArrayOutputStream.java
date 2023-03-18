//Raddon On Top!

package org.apache.commons.io.output;

import org.apache.commons.io.*;
import org.apache.commons.io.input.*;
import java.util.*;
import java.nio.charset.*;
import java.io.*;

public abstract class AbstractByteArrayOutputStream extends OutputStream
{
    static final int DEFAULT_SIZE = 1024;
    private final List<byte[]> buffers;
    private int currentBufferIndex;
    private int filledBufferSum;
    private byte[] currentBuffer;
    protected int count;
    private boolean reuseBuffers;
    
    public AbstractByteArrayOutputStream() {
        this.buffers = new ArrayList<byte[]>();
        this.reuseBuffers = true;
    }
    
    protected void needNewBuffer(final int newcount) {
        if (this.currentBufferIndex < this.buffers.size() - 1) {
            this.filledBufferSum += this.currentBuffer.length;
            ++this.currentBufferIndex;
            this.currentBuffer = this.buffers.get(this.currentBufferIndex);
        }
        else {
            int newBufferSize;
            if (this.currentBuffer == null) {
                newBufferSize = newcount;
                this.filledBufferSum = 0;
            }
            else {
                newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
                this.filledBufferSum += this.currentBuffer.length;
            }
            ++this.currentBufferIndex;
            this.currentBuffer = IOUtils.byteArray(newBufferSize);
            this.buffers.add(this.currentBuffer);
        }
    }
    
    @Override
    public abstract void write(final byte[] p0, final int p1, final int p2);
    
    protected void writeImpl(final byte[] b, final int off, final int len) {
        final int newcount = this.count + len;
        int remaining = len;
        int inBufferPos = this.count - this.filledBufferSum;
        while (remaining > 0) {
            final int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
            System.arraycopy(b, off + len - remaining, this.currentBuffer, inBufferPos, part);
            remaining -= part;
            if (remaining > 0) {
                this.needNewBuffer(newcount);
                inBufferPos = 0;
            }
        }
        this.count = newcount;
    }
    
    @Override
    public abstract void write(final int p0);
    
    protected void writeImpl(final int b) {
        int inBufferPos = this.count - this.filledBufferSum;
        if (inBufferPos == this.currentBuffer.length) {
            this.needNewBuffer(this.count + 1);
            inBufferPos = 0;
        }
        this.currentBuffer[inBufferPos] = (byte)b;
        ++this.count;
    }
    
    public abstract int write(final InputStream p0) throws IOException;
    
    protected int writeImpl(final InputStream in) throws IOException {
        int readCount = 0;
        for (int inBufferPos = this.count - this.filledBufferSum, n = in.read(this.currentBuffer, inBufferPos, this.currentBuffer.length - inBufferPos); n != -1; n = in.read(this.currentBuffer, inBufferPos, this.currentBuffer.length - inBufferPos)) {
            readCount += n;
            inBufferPos += n;
            this.count += n;
            if (inBufferPos == this.currentBuffer.length) {
                this.needNewBuffer(this.currentBuffer.length);
                inBufferPos = 0;
            }
        }
        return readCount;
    }
    
    public abstract int size();
    
    @Override
    public void close() throws IOException {
    }
    
    public abstract void reset();
    
    protected void resetImpl() {
        this.count = 0;
        this.filledBufferSum = 0;
        this.currentBufferIndex = 0;
        if (this.reuseBuffers) {
            this.currentBuffer = this.buffers.get(this.currentBufferIndex);
        }
        else {
            this.currentBuffer = null;
            final int size = this.buffers.get(0).length;
            this.buffers.clear();
            this.needNewBuffer(size);
            this.reuseBuffers = true;
        }
    }
    
    public abstract void writeTo(final OutputStream p0) throws IOException;
    
    protected void writeToImpl(final OutputStream out) throws IOException {
        int remaining = this.count;
        for (final byte[] buf : this.buffers) {
            final int c = Math.min(buf.length, remaining);
            out.write(buf, 0, c);
            remaining -= c;
            if (remaining == 0) {
                break;
            }
        }
    }
    
    public abstract InputStream toInputStream();
    
    protected <T extends InputStream> InputStream toInputStream(final InputStreamConstructor<T> isConstructor) {
        int remaining = this.count;
        if (remaining == 0) {
            return (InputStream)ClosedInputStream.CLOSED_INPUT_STREAM;
        }
        final List<T> list = new ArrayList<T>(this.buffers.size());
        for (final byte[] buf : this.buffers) {
            final int c = Math.min(buf.length, remaining);
            list.add(isConstructor.construct(buf, 0, c));
            remaining -= c;
            if (remaining == 0) {
                break;
            }
        }
        this.reuseBuffers = false;
        return new SequenceInputStream(Collections.enumeration(list));
    }
    
    public abstract byte[] toByteArray();
    
    protected byte[] toByteArrayImpl() {
        int remaining = this.count;
        if (remaining == 0) {
            return IOUtils.EMPTY_BYTE_ARRAY;
        }
        final byte[] newbuf = IOUtils.byteArray(remaining);
        int pos = 0;
        for (final byte[] buf : this.buffers) {
            final int c = Math.min(buf.length, remaining);
            System.arraycopy(buf, 0, newbuf, pos, c);
            pos += c;
            remaining -= c;
            if (remaining == 0) {
                break;
            }
        }
        return newbuf;
    }
    
    @Deprecated
    @Override
    public String toString() {
        return new String(this.toByteArray(), Charset.defaultCharset());
    }
    
    public String toString(final String enc) throws UnsupportedEncodingException {
        return new String(this.toByteArray(), enc);
    }
    
    public String toString(final Charset charset) {
        return new String(this.toByteArray(), charset);
    }
    
    @FunctionalInterface
    protected interface InputStreamConstructor<T extends InputStream>
    {
        T construct(final byte[] p0, final int p1, final int p2);
    }
}
