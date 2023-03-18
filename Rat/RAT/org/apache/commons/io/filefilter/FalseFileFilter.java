//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class FalseFileFilter implements IOFileFilter, Serializable
{
    private static final String TO_STRING;
    public static final IOFileFilter FALSE;
    public static final IOFileFilter INSTANCE;
    private static final long serialVersionUID = 6210271677940926200L;
    
    protected FalseFileFilter() {
    }
    
    @Override
    public boolean accept(final File file) {
        return false;
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        return false;
    }
    
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return FileVisitResult.TERMINATE;
    }
    
    @Override
    public IOFileFilter negate() {
        return TrueFileFilter.INSTANCE;
    }
    
    @Override
    public String toString() {
        return FalseFileFilter.TO_STRING;
    }
    
    @Override
    public IOFileFilter and(final IOFileFilter fileFilter) {
        return FalseFileFilter.INSTANCE;
    }
    
    @Override
    public IOFileFilter or(final IOFileFilter fileFilter) {
        return fileFilter;
    }
    
    static {
        TO_STRING = Boolean.FALSE.toString();
        FALSE = new FalseFileFilter();
        INSTANCE = FalseFileFilter.FALSE;
    }
}
