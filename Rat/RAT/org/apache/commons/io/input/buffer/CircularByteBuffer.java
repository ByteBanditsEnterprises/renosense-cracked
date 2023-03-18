//Raddon On Top!

package org.apache.commons.io.input.buffer;

import org.apache.commons.io.*;
import java.util.*;

public class CircularByteBuffer
{
    private final byte[] buffer;
    private int startOffset;
    private int endOffset;
    private int currentNumberOfBytes;
    
    public CircularByteBuffer(final int size) {
        this.buffer = IOUtils.byteArray(size);
        this.startOffset = 0;
        this.endOffset = 0;
        this.currentNumberOfBytes = 0;
    }
    
    public CircularByteBuffer() {
        this(8192);
    }
    
    public byte read() {
        if (this.currentNumberOfBytes <= 0) {
            throw new IllegalStateException("No bytes available.");
        }
        final byte b = this.buffer[this.startOffset];
        --this.currentNumberOfBytes;
        if (++this.startOffset == this.buffer.length) {
            this.startOffset = 0;
        }
        return b;
    }
    
    public void read(final byte[] targetBuffer, final int targetOffset, final int length) {
        Objects.requireNonNull(targetBuffer, "targetBuffer");
        if (targetOffset < 0 || targetOffset >= targetBuffer.length) {
            throw new IllegalArgumentException("Invalid offset: " + targetOffset);
        }
        if (length < 0 || length > this.buffer.length) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }
        if (targetOffset + length > targetBuffer.length) {
            throw new IllegalArgumentException("The supplied byte array contains only " + targetBuffer.length + " bytes, but offset, and length would require " + (targetOffset + length - 1));
        }
        if (this.currentNumberOfBytes < length) {
            throw new IllegalStateException("Currently, there are only " + this.currentNumberOfBytes + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        for (int i = 0; i < length; ++i) {
            targetBuffer[offset++] = this.buffer[this.startOffset];
            --this.currentNumberOfBytes;
            if (++this.startOffset == this.buffer.length) {
                this.startOffset = 0;
            }
        }
    }
    
    public void add(final byte value) {
        if (this.currentNumberOfBytes >= this.buffer.length) {
            throw new IllegalStateException("No space available");
        }
        this.buffer[this.endOffset] = value;
        ++this.currentNumberOfBytes;
        if (++this.endOffset == this.buffer.length) {
            this.endOffset = 0;
        }
    }
    
    public boolean peek(final byte[] sourceBuffer, final int offset, final int length) {
        Objects.requireNonNull(sourceBuffer, "Buffer");
        if (offset < 0 || offset >= sourceBuffer.length) {
            throw new IllegalArgumentException("Invalid offset: " + offset);
        }
        if (length < 0 || length > this.buffer.length) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }
        if (length < this.currentNumberOfBytes) {
            return false;
        }
        int localOffset = this.startOffset;
        for (int i = 0; i < length; ++i) {
            if (this.buffer[localOffset] != sourceBuffer[i + offset]) {
                return false;
            }
            if (++localOffset == this.buffer.length) {
                localOffset = 0;
            }
        }
        return true;
    }
    
    public void add(final byte[] targetBuffer, final int offset, final int length) {
        Objects.requireNonNull(targetBuffer, "Buffer");
        if (offset < 0 || offset >= targetBuffer.length) {
            throw new IllegalArgumentException("Invalid offset: " + offset);
        }
        if (length < 0) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }
        if (this.currentNumberOfBytes + length > this.buffer.length) {
            throw new IllegalStateException("No space available");
        }
        for (int i = 0; i < length; ++i) {
            this.buffer[this.endOffset] = targetBuffer[offset + i];
            if (++this.endOffset == this.buffer.length) {
                this.endOffset = 0;
            }
        }
        this.currentNumberOfBytes += length;
    }
    
    public boolean hasSpace() {
        return this.currentNumberOfBytes < this.buffer.length;
    }
    
    public boolean hasSpace(final int count) {
        return this.currentNumberOfBytes + count <= this.buffer.length;
    }
    
    public boolean hasBytes() {
        return this.currentNumberOfBytes > 0;
    }
    
    public int getSpace() {
        return this.buffer.length - this.currentNumberOfBytes;
    }
    
    public int getCurrentNumberOfBytes() {
        return this.currentNumberOfBytes;
    }
    
    public void clear() {
        this.startOffset = 0;
        this.endOffset = 0;
        this.currentNumberOfBytes = 0;
    }
}
