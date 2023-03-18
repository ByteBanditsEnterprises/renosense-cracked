//Raddon On Top!

package org.apache.commons.io.output;

import org.apache.commons.io.*;
import java.io.*;

public class ProxyWriter extends FilterWriter
{
    public ProxyWriter(final Writer proxy) {
        super(proxy);
    }
    
    @Override
    public Writer append(final char c) throws IOException {
        try {
            this.beforeWrite(1);
            this.out.append(c);
            this.afterWrite(1);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
        return this;
    }
    
    @Override
    public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
        try {
            this.beforeWrite(end - start);
            this.out.append(csq, start, end);
            this.afterWrite(end - start);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
        return this;
    }
    
    @Override
    public Writer append(final CharSequence csq) throws IOException {
        try {
            final int len = IOUtils.length(csq);
            this.beforeWrite(len);
            this.out.append(csq);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
        return this;
    }
    
    @Override
    public void write(final int c) throws IOException {
        try {
            this.beforeWrite(1);
            this.out.write(c);
            this.afterWrite(1);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public void write(final char[] cbuf) throws IOException {
        try {
            final int len = IOUtils.length(cbuf);
            this.beforeWrite(len);
            this.out.write(cbuf);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        try {
            this.beforeWrite(len);
            this.out.write(cbuf, off, len);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public void write(final String str) throws IOException {
        try {
            final int len = IOUtils.length((CharSequence)str);
            this.beforeWrite(len);
            this.out.write(str);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        try {
            this.beforeWrite(len);
            this.out.write(str, off, len);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public void flush() throws IOException {
        try {
            this.out.flush();
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    @Override
    public void close() throws IOException {
        IOUtils.close((Closeable)this.out, this::handleIOException);
    }
    
    protected void beforeWrite(final int n) throws IOException {
    }
    
    protected void afterWrite(final int n) throws IOException {
    }
    
    protected void handleIOException(final IOException e) throws IOException {
        throw e;
    }
}
