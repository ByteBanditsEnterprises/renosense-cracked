//Raddon On Top!

package org.apache.commons.io.filefilter;

import org.apache.commons.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.io.*;
import java.util.stream.*;

public class EmptyFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter EMPTY;
    public static final IOFileFilter NOT_EMPTY;
    private static final long serialVersionUID = 3631422087512832211L;
    
    protected EmptyFileFilter() {
    }
    
    public boolean accept(final File file) {
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            return IOUtils.length(files) == 0;
        }
        return file.length() == 0L;
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        try {
            if (Files.isDirectory(file, new LinkOption[0])) {
                try (final Stream<Path> stream = Files.list(file)) {
                    return toFileVisitResult(!stream.findFirst().isPresent(), file);
                }
            }
            return toFileVisitResult(Files.size(file) == 0L, file);
        }
        catch (IOException e) {
            return this.handle((Throwable)e);
        }
    }
    
    static {
        EMPTY = (IOFileFilter)new EmptyFileFilter();
        NOT_EMPTY = EmptyFileFilter.EMPTY.negate();
    }
}
