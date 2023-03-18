//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class TrueFileFilter implements IOFileFilter, Serializable
{
    private static final String TO_STRING;
    private static final long serialVersionUID = 8782512160909720199L;
    public static final IOFileFilter TRUE;
    public static final IOFileFilter INSTANCE;
    
    protected TrueFileFilter() {
    }
    
    public boolean accept(final File file) {
        return true;
    }
    
    public boolean accept(final File dir, final String name) {
        return true;
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return FileVisitResult.CONTINUE;
    }
    
    public IOFileFilter negate() {
        return FalseFileFilter.INSTANCE;
    }
    
    public IOFileFilter or(final IOFileFilter fileFilter) {
        return TrueFileFilter.INSTANCE;
    }
    
    public IOFileFilter and(final IOFileFilter fileFilter) {
        return fileFilter;
    }
    
    @Override
    public String toString() {
        return TrueFileFilter.TO_STRING;
    }
    
    static {
        TO_STRING = Boolean.TRUE.toString();
        TRUE = (IOFileFilter)new TrueFileFilter();
        INSTANCE = TrueFileFilter.TRUE;
    }
}
