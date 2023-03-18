//Raddon On Top!

package org.apache.commons.io.filefilter;

import org.apache.commons.io.file.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.nio.file.attribute.*;

public abstract class AbstractFileFilter implements IOFileFilter, PathVisitor
{
    static FileVisitResult toFileVisitResult(final boolean accept, final Path path) {
        return accept ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
    }
    
    @Override
    public boolean accept(final File file) {
        Objects.requireNonNull(file, "file");
        return this.accept(file.getParentFile(), file.getName());
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        Objects.requireNonNull(name, "name");
        return this.accept(new File(dir, name));
    }
    
    protected FileVisitResult handle(final Throwable t) {
        return FileVisitResult.TERMINATE;
    }
    
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
    
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attributes) throws IOException {
        return this.accept(dir, attributes);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) throws IOException {
        return this.accept(file, attributes);
    }
    
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
