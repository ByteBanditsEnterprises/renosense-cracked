//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.util.*;
import org.apache.commons.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import org.apache.commons.io.file.*;
import java.io.*;

public class AgeFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -2132740084016138541L;
    private final boolean acceptOlder;
    private final long cutoffMillis;
    
    public AgeFileFilter(final Date cutoffDate) {
        this(cutoffDate, true);
    }
    
    public AgeFileFilter(final Date cutoffDate, final boolean acceptOlder) {
        this(cutoffDate.getTime(), acceptOlder);
    }
    
    public AgeFileFilter(final File cutoffReference) {
        this(cutoffReference, true);
    }
    
    public AgeFileFilter(final File cutoffReference, final boolean acceptOlder) {
        this(FileUtils.lastModifiedUnchecked(cutoffReference), acceptOlder);
    }
    
    public AgeFileFilter(final long cutoffMillis) {
        this(cutoffMillis, true);
    }
    
    public AgeFileFilter(final long cutoffMillis, final boolean acceptOlder) {
        this.acceptOlder = acceptOlder;
        this.cutoffMillis = cutoffMillis;
    }
    
    public boolean accept(final File file) {
        final boolean newer = FileUtils.isFileNewer(file, this.cutoffMillis);
        return this.acceptOlder != newer;
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        boolean newer;
        try {
            newer = PathUtils.isNewer(file, this.cutoffMillis, new LinkOption[0]);
        }
        catch (IOException e) {
            return this.handle((Throwable)e);
        }
        return toFileVisitResult(this.acceptOlder != newer, file);
    }
    
    public String toString() {
        final String condition = this.acceptOlder ? "<=" : ">";
        return super.toString() + "(" + condition + this.cutoffMillis + ")";
    }
}
