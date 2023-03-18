//Raddon On Top!

package org.apache.commons.io.file;

import java.util.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.nio.file.*;

public class CleaningPathVisitor extends CountingPathVisitor
{
    private final String[] skip;
    private final boolean overrideReadOnly;
    
    public static CountingPathVisitor withBigIntegerCounters() {
        return new CleaningPathVisitor(Counters.bigIntegerPathCounters(), new String[0]);
    }
    
    public static CountingPathVisitor withLongCounters() {
        return new CleaningPathVisitor(Counters.longPathCounters(), new String[0]);
    }
    
    public CleaningPathVisitor(final Counters.PathCounters pathCounter, final DeleteOption[] deleteOption, final String... skip) {
        super(pathCounter);
        final String[] temp = (skip != null) ? skip.clone() : CleaningPathVisitor.EMPTY_STRING_ARRAY;
        Arrays.sort(temp);
        this.skip = temp;
        this.overrideReadOnly = StandardDeleteOption.overrideReadOnly(deleteOption);
    }
    
    public CleaningPathVisitor(final Counters.PathCounters pathCounter, final String... skip) {
        this(pathCounter, PathUtils.EMPTY_DELETE_OPTION_ARRAY, skip);
    }
    
    private boolean accept(final Path path) {
        return Arrays.binarySearch(this.skip, Objects.toString(path.getFileName(), null)) < 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CleaningPathVisitor other = (CleaningPathVisitor)obj;
        return this.overrideReadOnly == other.overrideReadOnly && Arrays.equals(this.skip, other.skip);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.skip);
        result = 31 * result + Objects.hash(this.overrideReadOnly);
        return result;
    }
    
    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attributes) throws IOException {
        super.preVisitDirectory(dir, attributes);
        return this.accept(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }
    
    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) throws IOException {
        if (this.accept(file) && Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            if (this.overrideReadOnly) {
                PathUtils.setReadOnly(file, false, LinkOption.NOFOLLOW_LINKS);
            }
            Files.deleteIfExists(file);
        }
        this.updateFileCounters(file, attributes);
        return FileVisitResult.CONTINUE;
    }
}
