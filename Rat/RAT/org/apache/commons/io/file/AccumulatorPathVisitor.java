//Raddon On Top!

package org.apache.commons.io.file;

import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.nio.file.attribute.*;

public class AccumulatorPathVisitor extends CountingPathVisitor
{
    private final List<Path> dirList;
    private final List<Path> fileList;
    
    public static AccumulatorPathVisitor withBigIntegerCounters() {
        return new AccumulatorPathVisitor(Counters.bigIntegerPathCounters());
    }
    
    public static AccumulatorPathVisitor withBigIntegerCounters(final PathFilter fileFilter, final PathFilter dirFilter) {
        return new AccumulatorPathVisitor(Counters.bigIntegerPathCounters(), fileFilter, dirFilter);
    }
    
    public static AccumulatorPathVisitor withLongCounters() {
        return new AccumulatorPathVisitor(Counters.longPathCounters());
    }
    
    public static AccumulatorPathVisitor withLongCounters(final PathFilter fileFilter, final PathFilter dirFilter) {
        return new AccumulatorPathVisitor(Counters.longPathCounters(), fileFilter, dirFilter);
    }
    
    public AccumulatorPathVisitor() {
        super(Counters.noopPathCounters());
        this.dirList = new ArrayList<Path>();
        this.fileList = new ArrayList<Path>();
    }
    
    public AccumulatorPathVisitor(final Counters.PathCounters pathCounter) {
        super(pathCounter);
        this.dirList = new ArrayList<Path>();
        this.fileList = new ArrayList<Path>();
    }
    
    public AccumulatorPathVisitor(final Counters.PathCounters pathCounter, final PathFilter fileFilter, final PathFilter dirFilter) {
        super(pathCounter, fileFilter, dirFilter);
        this.dirList = new ArrayList<Path>();
        this.fileList = new ArrayList<Path>();
    }
    
    private void add(final List<Path> list, final Path dir) {
        list.add(dir.normalize());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof AccumulatorPathVisitor)) {
            return false;
        }
        final AccumulatorPathVisitor other = (AccumulatorPathVisitor)obj;
        return Objects.equals(this.dirList, other.dirList) && Objects.equals(this.fileList, other.fileList);
    }
    
    public List<Path> getDirList() {
        return this.dirList;
    }
    
    public List<Path> getFileList() {
        return this.fileList;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Objects.hash(this.dirList, this.fileList);
        return result;
    }
    
    public List<Path> relativizeDirectories(final Path parent, final boolean sort, final Comparator<? super Path> comparator) {
        return PathUtils.relativize(this.getDirList(), parent, sort, comparator);
    }
    
    public List<Path> relativizeFiles(final Path parent, final boolean sort, final Comparator<? super Path> comparator) {
        return PathUtils.relativize(this.getFileList(), parent, sort, comparator);
    }
    
    @Override
    protected void updateDirCounter(final Path dir, final IOException exc) {
        super.updateDirCounter(dir, exc);
        this.add(this.dirList, dir);
    }
    
    @Override
    protected void updateFileCounters(final Path file, final BasicFileAttributes attributes) {
        super.updateFileCounters(file, attributes);
        this.add(this.fileList, file);
    }
}
