//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;
import java.util.*;

public class AppendableWriter<T extends Appendable> extends Writer
{
    private final T appendable;
    
    public AppendableWriter(final T appendable) {
        this.appendable = appendable;
    }
    
    @Override
    public Writer append(final char c) throws IOException {
        this.appendable.append(c);
        return this;
    }
    
    @Override
    public Writer append(final CharSequence csq) throws IOException {
        this.appendable.append(csq);
        return this;
    }
    
    @Override
    public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
        this.appendable.append(csq, start, end);
        return this;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    public T getAppendable() {
        return this.appendable;
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        Objects.requireNonNull(cbuf, "Character array is missing");
        if (len < 0 || off + len > cbuf.length) {
            throw new IndexOutOfBoundsException("Array Size=" + cbuf.length + ", offset=" + off + ", length=" + len);
        }
        for (int i = 0; i < len; ++i) {
            this.appendable.append(cbuf[off + i]);
        }
    }
    
    @Override
    public void write(final int c) throws IOException {
        this.appendable.append((char)c);
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        Objects.requireNonNull(str, "String is missing");
        this.appendable.append(str, off, off + len);
    }
}
