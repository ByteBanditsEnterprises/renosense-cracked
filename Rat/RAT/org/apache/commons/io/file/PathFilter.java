//Raddon On Top!

package org.apache.commons.io.file;

import java.nio.file.attribute.*;
import java.nio.file.*;

@FunctionalInterface
public interface PathFilter
{
    FileVisitResult accept(final Path p0, final BasicFileAttributes p1);
}
