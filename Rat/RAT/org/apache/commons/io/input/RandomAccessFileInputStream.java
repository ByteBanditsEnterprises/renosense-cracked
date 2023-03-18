//Raddon On Top!

package org.apache.commons.io.input;

import java.util.*;
import java.io.*;

public class RandomAccessFileInputStream extends InputStream
{
    private final boolean closeOnClose;
    private final RandomAccessFile randomAccessFile;
    
    public RandomAccessFileInputStream(final RandomAccessFile file) {
        this(file, false);
    }
    
    public RandomAccessFileInputStream(final RandomAccessFile file, final boolean closeOnClose) {
        this.randomAccessFile = Objects.requireNonNull(file, "file");
        this.closeOnClose = closeOnClose;
    }
    
    @Override
    public int available() throws IOException {
        final long avail = this.availableLong();
        if (avail > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)avail;
    }
    
    public long availableLong() throws IOException {
        return this.randomAccessFile.length() - this.randomAccessFile.getFilePointer();
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        if (this.closeOnClose) {
            this.randomAccessFile.close();
        }
    }
    
    public RandomAccessFile getRandomAccessFile() {
        return this.randomAccessFile;
    }
    
    public boolean isCloseOnClose() {
        return this.closeOnClose;
    }
    
    @Override
    public int read() throws IOException {
        return this.randomAccessFile.read();
    }
    
    @Override
    public int read(final byte[] bytes) throws IOException {
        return this.randomAccessFile.read(bytes);
    }
    
    @Override
    public int read(final byte[] bytes, final int offset, final int length) throws IOException {
        return this.randomAccessFile.read(bytes, offset, length);
    }
    
    private void seek(final long position) throws IOException {
        this.randomAccessFile.seek(position);
    }
    
    @Override
    public long skip(final long skipCount) throws IOException {
        if (skipCount <= 0L) {
            return 0L;
        }
        final long filePointer = this.randomAccessFile.getFilePointer();
        final long fileLength = this.randomAccessFile.length();
        if (filePointer >= fileLength) {
            return 0L;
        }
        final long targetPos = filePointer + skipCount;
        final long newPos = (targetPos > fileLength) ? (fileLength - 1L) : targetPos;
        if (newPos > 0L) {
            this.seek(newPos);
        }
        return this.randomAccessFile.getFilePointer() - filePointer;
    }
}
