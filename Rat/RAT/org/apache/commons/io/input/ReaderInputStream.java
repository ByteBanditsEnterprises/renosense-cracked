//Raddon On Top!

package org.apache.commons.io.input;

import java.nio.*;
import java.nio.charset.*;
import java.io.*;
import java.util.*;

public class ReaderInputStream extends InputStream
{
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final Reader reader;
    private final CharsetEncoder encoder;
    private final CharBuffer encoderIn;
    private final ByteBuffer encoderOut;
    private CoderResult lastCoderResult;
    private boolean endOfInput;
    
    public ReaderInputStream(final Reader reader, final CharsetEncoder encoder) {
        this(reader, encoder, 1024);
    }
    
    public ReaderInputStream(final Reader reader, final CharsetEncoder encoder, final int bufferSize) {
        this.reader = reader;
        this.encoder = encoder;
        (this.encoderIn = CharBuffer.allocate(bufferSize)).flip();
        (this.encoderOut = ByteBuffer.allocate(128)).flip();
    }
    
    public ReaderInputStream(final Reader reader, final Charset charset, final int bufferSize) {
        this(reader, charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE), bufferSize);
    }
    
    public ReaderInputStream(final Reader reader, final Charset charset) {
        this(reader, charset, 1024);
    }
    
    public ReaderInputStream(final Reader reader, final String charsetName, final int bufferSize) {
        this(reader, Charset.forName(charsetName), bufferSize);
    }
    
    public ReaderInputStream(final Reader reader, final String charsetName) {
        this(reader, charsetName, 1024);
    }
    
    @Deprecated
    public ReaderInputStream(final Reader reader) {
        this(reader, Charset.defaultCharset());
    }
    
    private void fillBuffer() throws IOException {
        if (!this.endOfInput && (this.lastCoderResult == null || this.lastCoderResult.isUnderflow())) {
            this.encoderIn.compact();
            final int position = this.encoderIn.position();
            final int c = this.reader.read(this.encoderIn.array(), position, this.encoderIn.remaining());
            if (c == -1) {
                this.endOfInput = true;
            }
            else {
                this.encoderIn.position(position + c);
            }
            this.encoderIn.flip();
        }
        this.encoderOut.compact();
        this.lastCoderResult = this.encoder.encode(this.encoderIn, this.encoderOut, this.endOfInput);
        this.encoderOut.flip();
    }
    
    @Override
    public int read(final byte[] array, int off, int len) throws IOException {
        Objects.requireNonNull(array, "array");
        if (len < 0 || off < 0 || off + len > array.length) {
            throw new IndexOutOfBoundsException("Array Size=" + array.length + ", offset=" + off + ", length=" + len);
        }
        int read = 0;
        if (len == 0) {
            return 0;
        }
        while (len > 0) {
            if (this.encoderOut.hasRemaining()) {
                final int c = Math.min(this.encoderOut.remaining(), len);
                this.encoderOut.get(array, off, c);
                off += c;
                len -= c;
                read += c;
            }
            else {
                this.fillBuffer();
                if (this.endOfInput && !this.encoderOut.hasRemaining()) {
                    break;
                }
                continue;
            }
        }
        return (read == 0 && this.endOfInput) ? -1 : read;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read() throws IOException {
        while (!this.encoderOut.hasRemaining()) {
            this.fillBuffer();
            if (this.endOfInput && !this.encoderOut.hasRemaining()) {
                return -1;
            }
        }
        return this.encoderOut.get() & 0xFF;
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
