//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class NotFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 6131563330944994230L;
    private final IOFileFilter filter;
    
    public NotFileFilter(final IOFileFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("The filter must not be null");
        }
        this.filter = filter;
    }
    
    public boolean accept(final File file) {
        return !this.filter.accept(file);
    }
    
    public boolean accept(final File file, final String name) {
        return !this.filter.accept(file, name);
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return this.not(this.filter.accept(file, attributes));
    }
    
    private FileVisitResult not(final FileVisitResult accept) {
        return (accept == FileVisitResult.CONTINUE) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
    }
    
    public String toString() {
        return "NOT (" + this.filter.toString() + ")";
    }
}
