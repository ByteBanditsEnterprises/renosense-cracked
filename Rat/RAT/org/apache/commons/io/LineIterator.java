//Raddon On Top!

package org.apache.commons.io;

import java.util.function.*;
import java.io.*;
import java.util.*;

public class LineIterator implements Iterator<String>, Closeable
{
    private final BufferedReader bufferedReader;
    private String cachedLine;
    private boolean finished;
    
    public LineIterator(final Reader reader) throws IllegalArgumentException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (reader instanceof BufferedReader) {
            this.bufferedReader = (BufferedReader)reader;
        }
        else {
            this.bufferedReader = new BufferedReader(reader);
        }
    }
    
    @Override
    public boolean hasNext() {
        if (this.cachedLine != null) {
            return true;
        }
        if (this.finished) {
            return false;
        }
        Label_0018: {
            break Label_0018;
            try {
                while (true) {
                    final String line = this.bufferedReader.readLine();
                    if (line == null) {
                        this.finished = true;
                        return false;
                    }
                    if (this.isValidLine(line)) {
                        this.cachedLine = line;
                        return true;
                    }
                }
            }
            catch (IOException ioe) {
                IOUtils.closeQuietly((Closeable)this, (Consumer)ioe::addSuppressed);
                throw new IllegalStateException(ioe);
            }
        }
    }
    
    protected boolean isValidLine(final String line) {
        return true;
    }
    
    @Override
    public String next() {
        return this.nextLine();
    }
    
    public String nextLine() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No more lines");
        }
        final String currentLine = this.cachedLine;
        this.cachedLine = null;
        return currentLine;
    }
    
    @Override
    public void close() throws IOException {
        this.finished = true;
        this.cachedLine = null;
        IOUtils.close((Closeable)this.bufferedReader);
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove not supported");
    }
    
    @Deprecated
    public static void closeQuietly(final LineIterator iterator) {
        IOUtils.closeQuietly((Closeable)iterator);
    }
}
