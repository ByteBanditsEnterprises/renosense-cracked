//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.util.function.*;
import java.util.stream.*;
import org.apache.commons.io.*;
import java.util.*;

public class FileFilterUtils
{
    private static final IOFileFilter cvsFilter;
    private static final IOFileFilter svnFilter;
    
    public static IOFileFilter ageFileFilter(final Date cutoffDate) {
        return (IOFileFilter)new AgeFileFilter(cutoffDate);
    }
    
    public static IOFileFilter ageFileFilter(final Date cutoffDate, final boolean acceptOlder) {
        return (IOFileFilter)new AgeFileFilter(cutoffDate, acceptOlder);
    }
    
    public static IOFileFilter ageFileFilter(final File cutoffReference) {
        return (IOFileFilter)new AgeFileFilter(cutoffReference);
    }
    
    public static IOFileFilter ageFileFilter(final File cutoffReference, final boolean acceptOlder) {
        return (IOFileFilter)new AgeFileFilter(cutoffReference, acceptOlder);
    }
    
    public static IOFileFilter ageFileFilter(final long cutoff) {
        return (IOFileFilter)new AgeFileFilter(cutoff);
    }
    
    public static IOFileFilter ageFileFilter(final long cutoff, final boolean acceptOlder) {
        return (IOFileFilter)new AgeFileFilter(cutoff, acceptOlder);
    }
    
    public static IOFileFilter and(final IOFileFilter... filters) {
        return (IOFileFilter)new AndFileFilter((List)toList(filters));
    }
    
    @Deprecated
    public static IOFileFilter andFileFilter(final IOFileFilter filter1, final IOFileFilter filter2) {
        return (IOFileFilter)new AndFileFilter(filter1, filter2);
    }
    
    public static IOFileFilter asFileFilter(final FileFilter filter) {
        return (IOFileFilter)new DelegateFileFilter(filter);
    }
    
    public static IOFileFilter asFileFilter(final FilenameFilter filter) {
        return (IOFileFilter)new DelegateFileFilter(filter);
    }
    
    public static IOFileFilter directoryFileFilter() {
        return DirectoryFileFilter.DIRECTORY;
    }
    
    public static IOFileFilter falseFileFilter() {
        return FalseFileFilter.FALSE;
    }
    
    public static IOFileFilter fileFileFilter() {
        return FileFileFilter.INSTANCE;
    }
    
    public static File[] filter(final IOFileFilter filter, final File... files) {
        if (filter == null) {
            throw new IllegalArgumentException("file filter is null");
        }
        if (files == null) {
            return FileUtils.EMPTY_FILE_ARRAY;
        }
        return filterFiles(filter, Stream.of(files), Collectors.toList()).toArray(FileUtils.EMPTY_FILE_ARRAY);
    }
    
    private static <R, A> R filterFiles(final IOFileFilter filter, final Stream<File> stream, final Collector<? super File, A, R> collector) {
        Objects.requireNonNull(collector, "collector");
        if (filter == null) {
            throw new IllegalArgumentException("file filter is null");
        }
        if (stream == null) {
            return Stream.empty().collect((Collector<? super Object, A, R>)collector);
        }
        return stream.filter(filter::accept).collect(collector);
    }
    
    public static File[] filter(final IOFileFilter filter, final Iterable<File> files) {
        return filterList(filter, files).toArray(FileUtils.EMPTY_FILE_ARRAY);
    }
    
    public static List<File> filterList(final IOFileFilter filter, final File... files) {
        return Arrays.asList(filter(filter, files));
    }
    
    public static List<File> filterList(final IOFileFilter filter, final Iterable<File> files) {
        if (files == null) {
            return Collections.emptyList();
        }
        return filterFiles(filter, StreamSupport.stream(files.spliterator(), false), (Collector<? super File, ?, List<File>>)Collectors.toList());
    }
    
    public static Set<File> filterSet(final IOFileFilter filter, final File... files) {
        return new HashSet<File>(Arrays.asList(filter(filter, files)));
    }
    
    public static Set<File> filterSet(final IOFileFilter filter, final Iterable<File> files) {
        if (files == null) {
            return Collections.emptySet();
        }
        return filterFiles(filter, StreamSupport.stream(files.spliterator(), false), (Collector<? super File, ?, Set<File>>)Collectors.toSet());
    }
    
    public static IOFileFilter magicNumberFileFilter(final byte[] magicNumber) {
        return (IOFileFilter)new MagicNumberFileFilter(magicNumber);
    }
    
    public static IOFileFilter magicNumberFileFilter(final byte[] magicNumber, final long offset) {
        return (IOFileFilter)new MagicNumberFileFilter(magicNumber, offset);
    }
    
    public static IOFileFilter magicNumberFileFilter(final String magicNumber) {
        return (IOFileFilter)new MagicNumberFileFilter(magicNumber);
    }
    
    public static IOFileFilter magicNumberFileFilter(final String magicNumber, final long offset) {
        return (IOFileFilter)new MagicNumberFileFilter(magicNumber, offset);
    }
    
    public static IOFileFilter makeCVSAware(final IOFileFilter filter) {
        return (filter == null) ? FileFilterUtils.cvsFilter : and(filter, FileFilterUtils.cvsFilter);
    }
    
    public static IOFileFilter makeDirectoryOnly(final IOFileFilter filter) {
        if (filter == null) {
            return DirectoryFileFilter.DIRECTORY;
        }
        return DirectoryFileFilter.DIRECTORY.and(filter);
    }
    
    public static IOFileFilter makeFileOnly(final IOFileFilter filter) {
        if (filter == null) {
            return FileFileFilter.INSTANCE;
        }
        return FileFileFilter.INSTANCE.and(filter);
    }
    
    public static IOFileFilter makeSVNAware(final IOFileFilter filter) {
        return (filter == null) ? FileFilterUtils.svnFilter : and(filter, FileFilterUtils.svnFilter);
    }
    
    public static IOFileFilter nameFileFilter(final String name) {
        return (IOFileFilter)new NameFileFilter(name);
    }
    
    public static IOFileFilter nameFileFilter(final String name, final IOCase caseSensitivity) {
        return (IOFileFilter)new NameFileFilter(name, caseSensitivity);
    }
    
    public static IOFileFilter notFileFilter(final IOFileFilter filter) {
        return filter.negate();
    }
    
    public static IOFileFilter or(final IOFileFilter... filters) {
        return (IOFileFilter)new OrFileFilter(toList(filters));
    }
    
    @Deprecated
    public static IOFileFilter orFileFilter(final IOFileFilter filter1, final IOFileFilter filter2) {
        return (IOFileFilter)new OrFileFilter(filter1, filter2);
    }
    
    public static IOFileFilter prefixFileFilter(final String prefix) {
        return (IOFileFilter)new PrefixFileFilter(prefix);
    }
    
    public static IOFileFilter prefixFileFilter(final String prefix, final IOCase caseSensitivity) {
        return (IOFileFilter)new PrefixFileFilter(prefix, caseSensitivity);
    }
    
    public static IOFileFilter sizeFileFilter(final long threshold) {
        return (IOFileFilter)new SizeFileFilter(threshold);
    }
    
    public static IOFileFilter sizeFileFilter(final long threshold, final boolean acceptLarger) {
        return (IOFileFilter)new SizeFileFilter(threshold, acceptLarger);
    }
    
    public static IOFileFilter sizeRangeFileFilter(final long minSizeInclusive, final long maxSizeInclusive) {
        final IOFileFilter minimumFilter = (IOFileFilter)new SizeFileFilter(minSizeInclusive, true);
        final IOFileFilter maximumFilter = (IOFileFilter)new SizeFileFilter(maxSizeInclusive + 1L, false);
        return minimumFilter.and(maximumFilter);
    }
    
    public static IOFileFilter suffixFileFilter(final String suffix) {
        return (IOFileFilter)new SuffixFileFilter(suffix);
    }
    
    public static IOFileFilter suffixFileFilter(final String suffix, final IOCase caseSensitivity) {
        return (IOFileFilter)new SuffixFileFilter(suffix, caseSensitivity);
    }
    
    public static List<IOFileFilter> toList(final IOFileFilter... filters) {
        if (filters == null) {
            throw new IllegalArgumentException("The filters must not be null");
        }
        final List<IOFileFilter> list = new ArrayList<IOFileFilter>(filters.length);
        for (int i = 0; i < filters.length; ++i) {
            if (filters[i] == null) {
                throw new IllegalArgumentException("The filter[" + i + "] is null");
            }
            list.add(filters[i]);
        }
        return list;
    }
    
    public static IOFileFilter trueFileFilter() {
        return TrueFileFilter.TRUE;
    }
    
    static {
        cvsFilter = notFileFilter(and(directoryFileFilter(), nameFileFilter("CVS")));
        svnFilter = notFileFilter(and(directoryFileFilter(), nameFileFilter(".svn")));
    }
}
