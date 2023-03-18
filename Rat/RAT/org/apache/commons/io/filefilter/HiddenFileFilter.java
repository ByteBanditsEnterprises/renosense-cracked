//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.nio.file.attribute.*;
import java.nio.file.*;
import java.io.*;

public class HiddenFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter HIDDEN;
    private static final long serialVersionUID = 8930842316112759062L;
    public static final IOFileFilter VISIBLE;
    
    protected HiddenFileFilter() {
    }
    
    public boolean accept(final File file) {
        return file.isHidden();
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        try {
            return toFileVisitResult(Files.isHidden(file), file);
        }
        catch (IOException e) {
            return this.handle((Throwable)e);
        }
    }
    
    static {
        HIDDEN = (IOFileFilter)new HiddenFileFilter();
        VISIBLE = HiddenFileFilter.HIDDEN.negate();
    }
}
