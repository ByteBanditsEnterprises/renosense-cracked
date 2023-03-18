//Raddon On Top!

package org.apache.commons.io.filefilter;

import org.apache.commons.io.file.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public interface IOFileFilter extends FileFilter, FilenameFilter, PathFilter
{
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    boolean accept(final File p0);
    
    boolean accept(final File p0, final String p1);
    
    default FileVisitResult accept(final Path path, final BasicFileAttributes attributes) {
        return AbstractFileFilter.toFileVisitResult(this.accept(path.toFile()), path);
    }
    
    default IOFileFilter and(final IOFileFilter fileFilter) {
        return (IOFileFilter)new AndFileFilter(this, fileFilter);
    }
    
    default IOFileFilter negate() {
        return (IOFileFilter)new NotFileFilter(this);
    }
    
    default IOFileFilter or(final IOFileFilter fileFilter) {
        return (IOFileFilter)new OrFileFilter(this, fileFilter);
    }
}
