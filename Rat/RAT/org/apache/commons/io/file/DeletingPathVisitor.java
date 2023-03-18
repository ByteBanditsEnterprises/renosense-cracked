//Raddon On Top!

package org.apache.commons.io.file;

import java.util.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class DeletingPathVisitor extends CountingPathVisitor
{
    private final String[] skip;
    private final boolean overrideReadOnly;
    private final LinkOption[] linkOptions;
    
    public static DeletingPathVisitor withBigIntegerCounters() {
        return new DeletingPathVisitor(Counters.bigIntegerPathCounters(), new String[0]);
    }
    
    public static DeletingPathVisitor withLongCounters() {
        return new DeletingPathVisitor(Counters.longPathCounters(), new String[0]);
    }
    
    public DeletingPathVisitor(final Counters.PathCounters pathCounter, final DeleteOption[] deleteOption, final String... skip) {
        this(pathCounter, PathUtils.NOFOLLOW_LINK_OPTION_ARRAY, deleteOption, skip);
    }
    
    public DeletingPathVisitor(final Counters.PathCounters pathCounter, final LinkOption[] linkOptions, final DeleteOption[] deleteOption, final String... skip) {
        super(pathCounter);
        final String[] temp = (skip != null) ? skip.clone() : DeletingPathVisitor.EMPTY_STRING_ARRAY;
        Arrays.sort(temp);
        this.skip = temp;
        this.overrideReadOnly = StandardDeleteOption.overrideReadOnly(deleteOption);
        this.linkOptions = ((linkOptions == null) ? PathUtils.NOFOLLOW_LINK_OPTION_ARRAY : linkOptions.clone());
    }
    
    public DeletingPathVisitor(final Counters.PathCounters pathCounter, final String... skip) {
        this(pathCounter, PathUtils.EMPTY_DELETE_OPTION_ARRAY, skip);
    }
    
    private boolean accept(final Path path) {
        return Arrays.binarySearch(this.skip, Objects.toString(path.getFileName(), null)) < 0;
    }
    
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
        final DeletingPathVisitor other = (DeletingPathVisitor)obj;
        return this.overrideReadOnly == other.overrideReadOnly && Arrays.equals(this.skip, other.skip);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.skip);
        result = 31 * result + Objects.hash(this.overrideReadOnly);
        return result;
    }
    
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        if (PathUtils.isEmptyDirectory(dir)) {
            Files.deleteIfExists(dir);
        }
        return super.postVisitDirectory(dir, exc);
    }
    
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        super.preVisitDirectory(dir, attrs);
        return this.accept(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }
    
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        if (this.accept(file)) {
            if (Files.exists(file, this.linkOptions)) {
                if (this.overrideReadOnly) {
                    PathUtils.setReadOnly(file, false, this.linkOptions);
                }
                Files.deleteIfExists(file);
            }
            if (Files.isSymbolicLink(file)) {
                try {
                    Files.delete(file);
                }
                catch (NoSuchFileException ex) {}
            }
        }
        this.updateFileCounters(file, attrs);
        return FileVisitResult.CONTINUE;
    }
}
