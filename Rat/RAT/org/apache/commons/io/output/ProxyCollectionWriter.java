//Raddon On Top!

package org.apache.commons.io.output;

import java.util.*;
import java.io.*;
import org.apache.commons.io.*;

public class ProxyCollectionWriter extends FilterCollectionWriter
{
    public ProxyCollectionWriter(final Collection<Writer> writers) {
        super((Collection)writers);
    }
    
    public ProxyCollectionWriter(final Writer... writers) {
        super(writers);
    }
    
    protected void afterWrite(final int n) throws IOException {
    }
    
    public Writer append(final char c) throws IOException {
        try {
            this.beforeWrite(1);
            super.append(c);
            this.afterWrite(1);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
        return (Writer)this;
    }
    
    public Writer append(final CharSequence csq) throws IOException {
        try {
            final int len = IOUtils.length(csq);
            this.beforeWrite(len);
            super.append(csq);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
        return (Writer)this;
    }
    
    public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
        try {
            this.beforeWrite(end - start);
            super.append(csq, start, end);
            this.afterWrite(end - start);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
        return (Writer)this;
    }
    
    protected void beforeWrite(final int n) throws IOException {
    }
    
    public void close() throws IOException {
        try {
            super.close();
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    public void flush() throws IOException {
        try {
            super.flush();
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    protected void handleIOException(final IOException e) throws IOException {
        throw e;
    }
    
    public void write(final char[] cbuf) throws IOException {
        try {
            final int len = IOUtils.length(cbuf);
            this.beforeWrite(len);
            super.write(cbuf);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        try {
            this.beforeWrite(len);
            super.write(cbuf, off, len);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    public void write(final int c) throws IOException {
        try {
            this.beforeWrite(1);
            super.write(c);
            this.afterWrite(1);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    public void write(final String str) throws IOException {
        try {
            final int len = IOUtils.length((CharSequence)str);
            this.beforeWrite(len);
            super.write(str);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
    
    public void write(final String str, final int off, final int len) throws IOException {
        try {
            this.beforeWrite(len);
            super.write(str, off, len);
            this.afterWrite(len);
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
    }
}
