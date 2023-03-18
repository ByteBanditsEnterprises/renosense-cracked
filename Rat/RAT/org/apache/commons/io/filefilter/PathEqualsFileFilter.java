//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.util.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class PathEqualsFileFilter extends AbstractFileFilter
{
    private final Path path;
    
    public PathEqualsFileFilter(final Path file) {
        this.path = file;
    }
    
    public boolean accept(final File file) {
        return Objects.equals(this.path, file.toPath());
    }
    
    public FileVisitResult accept(final Path path, final BasicFileAttributes attributes) {
        return toFileVisitResult(Objects.equals(this.path, path), path);
    }
}
