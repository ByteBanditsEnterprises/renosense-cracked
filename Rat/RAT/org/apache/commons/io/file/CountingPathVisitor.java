//Raddon On Top!

package org.apache.commons.io.file;

import org.apache.commons.io.filefilter.*;
import java.util.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class CountingPathVisitor extends SimplePathVisitor
{
    static final String[] EMPTY_STRING_ARRAY;
    private final Counters.PathCounters pathCounters;
    private final PathFilter fileFilter;
    private final PathFilter dirFilter;
    
    public static CountingPathVisitor withBigIntegerCounters() {
        return new CountingPathVisitor(Counters.bigIntegerPathCounters());
    }
    
    public static CountingPathVisitor withLongCounters() {
        return new CountingPathVisitor(Counters.longPathCounters());
    }
    
    public CountingPathVisitor(final Counters.PathCounters pathCounter) {
        this(pathCounter, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }
    
    public CountingPathVisitor(final Counters.PathCounters pathCounter, final PathFilter fileFilter, final PathFilter dirFilter) {
        this.pathCounters = Objects.requireNonNull(pathCounter, "pathCounter");
        this.fileFilter = Objects.requireNonNull(fileFilter, "fileFilter");
        this.dirFilter = Objects.requireNonNull(dirFilter, "dirFilter");
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CountingPathVisitor)) {
            return false;
        }
        final CountingPathVisitor other = (CountingPathVisitor)obj;
        return Objects.equals(this.pathCounters, other.pathCounters);
    }
    
    public Counters.PathCounters getPathCounters() {
        return this.pathCounters;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.pathCounters);
    }
    
    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        this.updateDirCounter(dir, exc);
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attributes) throws IOException {
        final FileVisitResult accept = this.dirFilter.accept(dir, attributes);
        return (accept != FileVisitResult.CONTINUE) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
    }
    
    @Override
    public String toString() {
        return this.pathCounters.toString();
    }
    
    protected void updateDirCounter(final Path dir, final IOException exc) {
        this.pathCounters.getDirectoryCounter().increment();
    }
    
    protected void updateFileCounters(final Path file, final BasicFileAttributes attributes) {
        this.pathCounters.getFileCounter().increment();
        this.pathCounters.getByteCounter().add(attributes.size());
    }
    
    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) throws IOException {
        if (Files.exists(file, new LinkOption[0]) && this.fileFilter.accept(file, attributes) == FileVisitResult.CONTINUE) {
            this.updateFileCounters(file, attributes);
        }
        return FileVisitResult.CONTINUE;
    }
    
    static {
        EMPTY_STRING_ARRAY = new String[0];
    }
}
