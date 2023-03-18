//Raddon On Top!

package org.apache.commons.io.input.buffer;

import java.util.*;
import java.io.*;

public class PeekableInputStream extends CircularBufferInputStream
{
    public PeekableInputStream(final InputStream inputStream, final int bufferSize) {
        super(inputStream, bufferSize);
    }
    
    public PeekableInputStream(final InputStream inputStream) {
        super(inputStream);
    }
    
    public boolean peek(final byte[] sourceBuffer) throws IOException {
        Objects.requireNonNull(sourceBuffer, "sourceBuffer");
        return this.peek(sourceBuffer, 0, sourceBuffer.length);
    }
    
    public boolean peek(final byte[] sourceBuffer, final int offset, final int length) throws IOException {
        Objects.requireNonNull(sourceBuffer, "sourceBuffer");
        if (sourceBuffer.length > this.bufferSize) {
            throw new IllegalArgumentException("Peek request size of " + sourceBuffer.length + " bytes exceeds buffer size of " + this.bufferSize + " bytes");
        }
        if (this.buffer.getCurrentNumberOfBytes() < sourceBuffer.length) {
            this.fillBuffer();
        }
        return this.buffer.peek(sourceBuffer, offset, length);
    }
}
