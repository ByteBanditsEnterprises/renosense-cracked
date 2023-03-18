//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class CanReadFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter CAN_READ;
    public static final IOFileFilter CANNOT_READ;
    public static final IOFileFilter READ_ONLY;
    private static final long serialVersionUID = 3179904805251622989L;
    
    protected CanReadFileFilter() {
    }
    
    public boolean accept(final File file) {
        return file.canRead();
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isReadable(file), file);
    }
    
    static {
        CAN_READ = (IOFileFilter)new CanReadFileFilter();
        CANNOT_READ = CanReadFileFilter.CAN_READ.negate();
        READ_ONLY = CanReadFileFilter.CAN_READ.and(CanWriteFileFilter.CANNOT_WRITE);
    }
}
