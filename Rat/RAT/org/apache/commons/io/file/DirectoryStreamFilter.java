//Raddon On Top!

package org.apache.commons.io.file;

import java.util.*;
import java.nio.file.*;
import java.io.*;

public class DirectoryStreamFilter implements DirectoryStream.Filter<Path>
{
    private final PathFilter pathFilter;
    
    public DirectoryStreamFilter(final PathFilter pathFilter) {
        this.pathFilter = Objects.requireNonNull(pathFilter, "pathFilter");
    }
    
    @Override
    public boolean accept(final Path path) throws IOException {
        return this.pathFilter.accept(path, PathUtils.readBasicFileAttributes(path)) == FileVisitResult.CONTINUE;
    }
    
    public PathFilter getPathFilter() {
        return this.pathFilter;
    }
}
