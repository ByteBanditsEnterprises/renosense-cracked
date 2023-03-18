//Raddon On Top!

package org.apache.commons.io.input;

import java.util.*;
import java.io.*;

public class SequenceReader extends Reader
{
    private Reader reader;
    private Iterator<? extends Reader> readers;
    
    public SequenceReader(final Iterable<? extends Reader> readers) {
        this.readers = Objects.requireNonNull(readers, "readers").iterator();
        this.reader = this.nextReader();
    }
    
    public SequenceReader(final Reader... readers) {
        this(Arrays.asList(readers));
    }
    
    @Override
    public void close() throws IOException {
        this.readers = null;
        this.reader = null;
    }
    
    private Reader nextReader() {
        return this.readers.hasNext() ? ((Reader)this.readers.next()) : null;
    }
    
    @Override
    public int read() throws IOException {
        int c = -1;
        while (this.reader != null) {
            c = this.reader.read();
            if (c != -1) {
                break;
            }
            this.reader = this.nextReader();
        }
        return c;
    }
    
    @Override
    public int read(final char[] cbuf, int off, int len) throws IOException {
        Objects.requireNonNull(cbuf, "cbuf");
        if (len < 0 || off < 0 || off + len > cbuf.length) {
            throw new IndexOutOfBoundsException("Array Size=" + cbuf.length + ", offset=" + off + ", length=" + len);
        }
        int count = 0;
        while (this.reader != null) {
            final int readLen = this.reader.read(cbuf, off, len);
            if (readLen == -1) {
                this.reader = this.nextReader();
            }
            else {
                count += readLen;
                off += readLen;
                len -= readLen;
                if (len <= 0) {
                    break;
                }
                continue;
            }
        }
        if (count > 0) {
            return count;
        }
        return -1;
    }
}
