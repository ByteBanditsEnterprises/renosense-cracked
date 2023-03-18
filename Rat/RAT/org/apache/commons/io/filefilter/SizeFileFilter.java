//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.nio.file.attribute.*;
import java.nio.file.*;
import java.io.*;

public class SizeFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 7388077430788600069L;
    private final boolean acceptLarger;
    private final long size;
    
    public SizeFileFilter(final long size) {
        this(size, true);
    }
    
    public SizeFileFilter(final long size, final boolean acceptLarger) {
        if (size < 0L) {
            throw new IllegalArgumentException("The size must be non-negative");
        }
        this.size = size;
        this.acceptLarger = acceptLarger;
    }
    
    public boolean accept(final File file) {
        return this.accept(file.length());
    }
    
    private boolean accept(final long length) {
        return this.acceptLarger != length < this.size;
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        try {
            return toFileVisitResult(this.accept(Files.size(file)), file);
        }
        catch (IOException e) {
            return this.handle((Throwable)e);
        }
    }
    
    public String toString() {
        final String condition = this.acceptLarger ? ">=" : "<";
        return super.toString() + "(" + condition + this.size + ")";
    }
    
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        return toFileVisitResult(this.accept(Files.size(file)), file);
    }
}
