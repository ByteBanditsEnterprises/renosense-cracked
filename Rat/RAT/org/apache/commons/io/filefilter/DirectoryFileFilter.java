//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class DirectoryFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter DIRECTORY;
    public static final IOFileFilter INSTANCE;
    private static final long serialVersionUID = -5148237843784525732L;
    
    protected DirectoryFileFilter() {
    }
    
    public boolean accept(final File file) {
        return file.isDirectory();
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isDirectory(file, new LinkOption[0]), file);
    }
    
    static {
        DIRECTORY = (IOFileFilter)new DirectoryFileFilter();
        INSTANCE = DirectoryFileFilter.DIRECTORY;
    }
}
