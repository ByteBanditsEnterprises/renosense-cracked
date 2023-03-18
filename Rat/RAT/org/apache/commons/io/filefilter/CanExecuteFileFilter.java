//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class CanExecuteFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter CAN_EXECUTE;
    public static final IOFileFilter CANNOT_EXECUTE;
    private static final long serialVersionUID = 3179904805251622989L;
    
    protected CanExecuteFileFilter() {
    }
    
    public boolean accept(final File file) {
        return file.canExecute();
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isExecutable(file), file);
    }
    
    static {
        CAN_EXECUTE = (IOFileFilter)new CanExecuteFileFilter();
        CANNOT_EXECUTE = CanExecuteFileFilter.CAN_EXECUTE.negate();
    }
}
