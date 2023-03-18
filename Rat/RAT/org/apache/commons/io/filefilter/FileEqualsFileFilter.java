//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.util.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class FileEqualsFileFilter extends AbstractFileFilter
{
    private final File file;
    private final Path path;
    
    public FileEqualsFileFilter(final File file) {
        this.file = Objects.requireNonNull(file, "file");
        this.path = file.toPath();
    }
    
    public boolean accept(final File file) {
        return Objects.equals(this.file, file);
    }
    
    public FileVisitResult accept(final Path path, final BasicFileAttributes attributes) {
        return toFileVisitResult(Objects.equals(this.path, path), path);
    }
}
