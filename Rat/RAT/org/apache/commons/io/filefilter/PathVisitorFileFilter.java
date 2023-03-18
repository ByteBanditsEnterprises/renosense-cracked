//Raddon On Top!

package org.apache.commons.io.filefilter;

import org.apache.commons.io.file.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class PathVisitorFileFilter extends AbstractFileFilter
{
    private final PathVisitor pathVisitor;
    
    public PathVisitorFileFilter(final PathVisitor pathVisitor) {
        this.pathVisitor = (PathVisitor)((pathVisitor == null) ? NoopPathVisitor.INSTANCE : pathVisitor);
    }
    
    public boolean accept(final File file) {
        try {
            final Path path = file.toPath();
            return this.visitFile(path, file.exists() ? PathUtils.readBasicFileAttributes(path) : null) == FileVisitResult.CONTINUE;
        }
        catch (IOException e) {
            return this.handle((Throwable)e) == FileVisitResult.CONTINUE;
        }
    }
    
    public boolean accept(final File dir, final String name) {
        try {
            final Path path = dir.toPath().resolve(name);
            return this.accept(path, PathUtils.readBasicFileAttributes(path)) == FileVisitResult.CONTINUE;
        }
        catch (IOException e) {
            return this.handle((Throwable)e) == FileVisitResult.CONTINUE;
        }
    }
    
    public FileVisitResult accept(final Path path, final BasicFileAttributes attributes) {
        try {
            return Files.isDirectory(path, new LinkOption[0]) ? this.pathVisitor.postVisitDirectory((Object)path, (IOException)null) : this.visitFile(path, attributes);
        }
        catch (IOException e) {
            return this.handle((Throwable)e);
        }
    }
    
    public FileVisitResult visitFile(final Path path, final BasicFileAttributes attributes) throws IOException {
        return this.pathVisitor.visitFile((Object)path, attributes);
    }
}
