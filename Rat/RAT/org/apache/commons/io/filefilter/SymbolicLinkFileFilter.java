//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class SymbolicLinkFileFilter extends AbstractFileFilter implements Serializable
{
    public static final SymbolicLinkFileFilter INSTANCE;
    private static final long serialVersionUID = 1L;
    
    protected SymbolicLinkFileFilter() {
    }
    
    public boolean accept(final File file) {
        return file.isFile();
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isSymbolicLink(file), file);
    }
    
    static {
        INSTANCE = new SymbolicLinkFileFilter();
    }
}
