//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class CanWriteFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter CAN_WRITE;
    public static final IOFileFilter CANNOT_WRITE;
    private static final long serialVersionUID = 5132005214688990379L;
    
    protected CanWriteFileFilter() {
    }
    
    public boolean accept(final File file) {
        return file.canWrite();
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isWritable(file), file);
    }
    
    static {
        CAN_WRITE = (IOFileFilter)new CanWriteFileFilter();
        CANNOT_WRITE = CanWriteFileFilter.CAN_WRITE.negate();
    }
}
