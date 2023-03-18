//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class FileFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter INSTANCE;
    @Deprecated
    public static final IOFileFilter FILE;
    private static final long serialVersionUID = 5345244090827540862L;
    
    protected FileFileFilter() {
    }
    
    public boolean accept(final File file) {
        return file.isFile();
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isRegularFile(file, new LinkOption[0]), file);
    }
    
    static {
        INSTANCE = (IOFileFilter)new FileFileFilter();
        FILE = FileFileFilter.INSTANCE;
    }
}
