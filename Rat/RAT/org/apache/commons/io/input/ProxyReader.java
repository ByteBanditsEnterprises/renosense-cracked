//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import org.apache.commons.io.*;
import java.nio.*;

public abstract class ProxyReader extends FilterReader
{
    public ProxyReader(final Reader proxy) {
        super(proxy);
    }
    
    @Override
    public int read() throws IOException {
        try {
            this.beforeRead(1);
            final int c = this.in.read();
            this.afterRead((c != -1) ? 1 : -1);
            return c;
        }
        catch (IOException e) {
            this.handleIOException(e);
            return -1;
        }
    }
    
    @Override
    public int read(final char[] chr) throws IOException {
        try {
            this.beforeRead(IOUtils.length(chr));
            final int n = this.in.read(chr);
            this.afterRead(n);
            return n;
        }
        catch (IOException e) {
            this.handleIOException(e);
            return -1;
        }
    }
    
    @Override
    public int read(final char[] chr, final int st, final int len) throws IOException {
        try {
            this.beforeRead(len);
            final int n = this.in.read(chr, st, len);
            this.afterRead(n);
            return n;
        }
        catch (IOException e) {
            this.handleIOException(e);
            return -1;
        }
    }
    
    @Override
    public int read(final CharBuffer target) throws IOException {
        try {
            this.beforeRead(IOUtils.length(target));
            final int n = this.in.read(target);
            this.afterRead(n);
            return n;
        }
        catch (IOException e) {
            this.handleIOException(e);
            return -1;
        }
    }
    
    @Override
    public long skip(final long ln) throws IOException {
        try {
            return this.in.skip(ln);
        }
        catch (IOException e) {
            this.handleIOException(e);
            return 0L;
        }
    }
    
    @Override
    public boolean ready() throws IOException {
        try {
            return this.in.ready();
        }
        catch (IOException e) {
            this.handleIOException(e);
            return false;
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.in.close();
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public synchronized void mark(final int idx) throws IOException {
        try {
            this.in.mark(idx);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public synchronized void reset() throws IOException {
        try {
            this.in.reset();
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }
    
    protected void beforeRead(final int n) throws IOException {
    }
    
    protected void afterRead(final int n) throws IOException {
    }
    
    protected void handleIOException(final IOException e) throws IOException {
        throw e;
    }
}
