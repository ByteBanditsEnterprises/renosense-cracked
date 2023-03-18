//Raddon On Top!

package org.apache.commons.io.file;

import java.util.stream.*;
import java.io.*;
import java.util.function.*;
import org.apache.commons.io.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.nio.file.*;
import java.net.*;

public final class PathUtils
{
    public static final CopyOption[] EMPTY_COPY_OPTIONS;
    public static final DeleteOption[] EMPTY_DELETE_OPTION_ARRAY;
    public static final FileVisitOption[] EMPTY_FILE_VISIT_OPTION_ARRAY;
    public static final LinkOption[] EMPTY_LINK_OPTION_ARRAY;
    public static final LinkOption[] NOFOLLOW_LINK_OPTION_ARRAY;
    public static final OpenOption[] EMPTY_OPEN_OPTION_ARRAY;
    public static final Path[] EMPTY_PATH_ARRAY;
    
    private static AccumulatorPathVisitor accumulate(final Path directory, final int maxDepth, final FileVisitOption[] fileVisitOptions) throws IOException {
        return visitFileTree(AccumulatorPathVisitor.withLongCounters(), directory, toFileVisitOptionSet(fileVisitOptions), maxDepth);
    }
    
    public static Counters.PathCounters cleanDirectory(final Path directory) throws IOException {
        return cleanDirectory(directory, PathUtils.EMPTY_DELETE_OPTION_ARRAY);
    }
    
    public static Counters.PathCounters cleanDirectory(final Path directory, final DeleteOption... deleteOptions) throws IOException {
        return visitFileTree(new CleaningPathVisitor(Counters.longPathCounters(), deleteOptions, new String[0]), directory).getPathCounters();
    }
    
    public static Counters.PathCounters copyDirectory(final Path sourceDirectory, final Path targetDirectory, final CopyOption... copyOptions) throws IOException {
        final Path absoluteSource = sourceDirectory.toAbsolutePath();
        return visitFileTree(new CopyDirectoryVisitor(Counters.longPathCounters(), absoluteSource, targetDirectory, copyOptions), absoluteSource).getPathCounters();
    }
    
    public static Path copyFile(final URL sourceFile, final Path targetFile, final CopyOption... copyOptions) throws IOException {
        try (final InputStream inputStream = sourceFile.openStream()) {
            Files.copy(inputStream, targetFile, copyOptions);
            return targetFile;
        }
    }
    
    public static Path copyFileToDirectory(final Path sourceFile, final Path targetDirectory, final CopyOption... copyOptions) throws IOException {
        return Files.copy(sourceFile, targetDirectory.resolve(sourceFile.getFileName()), copyOptions);
    }
    
    public static Path copyFileToDirectory(final URL sourceFile, final Path targetDirectory, final CopyOption... copyOptions) throws IOException {
        try (final InputStream inputStream = sourceFile.openStream()) {
            Files.copy(inputStream, targetDirectory.resolve(sourceFile.getFile()), copyOptions);
            return targetDirectory;
        }
    }
    
    public static Counters.PathCounters countDirectory(final Path directory) throws IOException {
        return visitFileTree(new CountingPathVisitor(Counters.longPathCounters()), directory).getPathCounters();
    }
    
    public static Path createParentDirectories(final Path path, final FileAttribute<?>... attrs) throws IOException {
        final Path parent = path.getParent();
        if (parent == null) {
            return null;
        }
        return Files.createDirectories(parent, attrs);
    }
    
    public static Path current() {
        return Paths.get("", new String[0]);
    }
    
    public static Counters.PathCounters delete(final Path path) throws IOException {
        return delete(path, PathUtils.EMPTY_DELETE_OPTION_ARRAY);
    }
    
    public static Counters.PathCounters delete(final Path path, final DeleteOption... deleteOptions) throws IOException {
        return Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) ? deleteDirectory(path, deleteOptions) : deleteFile(path, deleteOptions);
    }
    
    public static Counters.PathCounters delete(final Path path, final LinkOption[] linkOptions, final DeleteOption... deleteOptions) throws IOException {
        return Files.isDirectory(path, linkOptions) ? deleteDirectory(path, linkOptions, deleteOptions) : deleteFile(path, linkOptions, deleteOptions);
    }
    
    public static Counters.PathCounters deleteDirectory(final Path directory) throws IOException {
        return deleteDirectory(directory, PathUtils.EMPTY_DELETE_OPTION_ARRAY);
    }
    
    public static Counters.PathCounters deleteDirectory(final Path directory, final DeleteOption... deleteOptions) throws IOException {
        return visitFileTree(new DeletingPathVisitor(Counters.longPathCounters(), PathUtils.NOFOLLOW_LINK_OPTION_ARRAY, deleteOptions, new String[0]), directory).getPathCounters();
    }
    
    public static Counters.PathCounters deleteDirectory(final Path directory, final LinkOption[] linkOptions, final DeleteOption... deleteOptions) throws IOException {
        return visitFileTree(new DeletingPathVisitor(Counters.longPathCounters(), linkOptions, deleteOptions, new String[0]), directory).getPathCounters();
    }
    
    public static Counters.PathCounters deleteFile(final Path file) throws IOException {
        return deleteFile(file, PathUtils.EMPTY_DELETE_OPTION_ARRAY);
    }
    
    public static Counters.PathCounters deleteFile(final Path file, final DeleteOption... deleteOptions) throws IOException {
        return deleteFile(file, PathUtils.NOFOLLOW_LINK_OPTION_ARRAY, deleteOptions);
    }
    
    public static Counters.PathCounters deleteFile(final Path file, final LinkOption[] linkOptions, final DeleteOption... deleteOptions) throws NoSuchFileException, IOException {
        if (Files.isDirectory(file, linkOptions)) {
            throw new NoSuchFileException(file.toString());
        }
        final Counters.PathCounters pathCounts = Counters.longPathCounters();
        final boolean exists = Files.exists(file, linkOptions);
        final long size = (exists && !Files.isSymbolicLink(file)) ? Files.size(file) : 0L;
        if (overrideReadOnly(deleteOptions) && exists) {
            setReadOnly(file, false, linkOptions);
        }
        if (Files.deleteIfExists(file)) {
            pathCounts.getFileCounter().increment();
            pathCounts.getByteCounter().add(size);
        }
        return pathCounts;
    }
    
    public static boolean directoryAndFileContentEquals(final Path path1, final Path path2) throws IOException {
        return directoryAndFileContentEquals(path1, path2, PathUtils.EMPTY_LINK_OPTION_ARRAY, PathUtils.EMPTY_OPEN_OPTION_ARRAY, PathUtils.EMPTY_FILE_VISIT_OPTION_ARRAY);
    }
    
    public static boolean directoryAndFileContentEquals(final Path path1, final Path path2, final LinkOption[] linkOptions, final OpenOption[] openOptions, final FileVisitOption[] fileVisitOption) throws IOException {
        if (path1 == null && path2 == null) {
            return true;
        }
        if (path1 == null || path2 == null) {
            return false;
        }
        if (Files.notExists(path1, new LinkOption[0]) && Files.notExists(path2, new LinkOption[0])) {
            return true;
        }
        final RelativeSortedPaths relativeSortedPaths = new RelativeSortedPaths(path1, path2, Integer.MAX_VALUE, linkOptions, fileVisitOption);
        if (!relativeSortedPaths.equals) {
            return false;
        }
        final List<Path> fileList1 = relativeSortedPaths.relativeFileList1;
        final List<Path> fileList2 = relativeSortedPaths.relativeFileList2;
        for (final Path path3 : fileList1) {
            final int binarySearch = Collections.binarySearch(fileList2, path3);
            if (binarySearch <= -1) {
                throw new IllegalStateException("Unexpected mismatch.");
            }
            if (!fileContentEquals(path1.resolve(path3), path2.resolve(path3), linkOptions, openOptions)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean directoryContentEquals(final Path path1, final Path path2) throws IOException {
        return directoryContentEquals(path1, path2, Integer.MAX_VALUE, PathUtils.EMPTY_LINK_OPTION_ARRAY, PathUtils.EMPTY_FILE_VISIT_OPTION_ARRAY);
    }
    
    public static boolean directoryContentEquals(final Path path1, final Path path2, final int maxDepth, final LinkOption[] linkOptions, final FileVisitOption[] fileVisitOptions) throws IOException {
        return new RelativeSortedPaths(path1, path2, maxDepth, linkOptions, fileVisitOptions).equals;
    }
    
    public static boolean fileContentEquals(final Path path1, final Path path2) throws IOException {
        return fileContentEquals(path1, path2, PathUtils.EMPTY_LINK_OPTION_ARRAY, PathUtils.EMPTY_OPEN_OPTION_ARRAY);
    }
    
    public static boolean fileContentEquals(final Path path1, final Path path2, final LinkOption[] linkOptions, final OpenOption[] openOptions) throws IOException {
        if (path1 == null && path2 == null) {
            return true;
        }
        if (path1 == null || path2 == null) {
            return false;
        }
        final Path nPath1 = path1.normalize();
        final Path nPath2 = path2.normalize();
        final boolean path1Exists = Files.exists(nPath1, linkOptions);
        if (path1Exists != Files.exists(nPath2, linkOptions)) {
            return false;
        }
        if (!path1Exists) {
            return true;
        }
        if (Files.isDirectory(nPath1, linkOptions)) {
            throw new IOException("Can't compare directories, only files: " + nPath1);
        }
        if (Files.isDirectory(nPath2, linkOptions)) {
            throw new IOException("Can't compare directories, only files: " + nPath2);
        }
        if (Files.size(nPath1) != Files.size(nPath2)) {
            return false;
        }
        if (path1.equals(path2)) {
            return true;
        }
        try (final InputStream inputStream1 = Files.newInputStream(nPath1, openOptions);
             final InputStream inputStream2 = Files.newInputStream(nPath2, openOptions)) {
            return IOUtils.contentEquals(inputStream1, inputStream2);
        }
    }
    
    public static Path[] filter(final PathFilter filter, final Path... paths) {
        Objects.requireNonNull(filter, "filter");
        if (paths == null) {
            return PathUtils.EMPTY_PATH_ARRAY;
        }
        return filterPaths(filter, Stream.of(paths), Collectors.toList()).toArray(PathUtils.EMPTY_PATH_ARRAY);
    }
    
    private static <R, A> R filterPaths(final PathFilter filter, final Stream<Path> stream, final Collector<? super Path, A, R> collector) {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(collector, "collector");
        if (stream == null) {
            return Stream.empty().collect((Collector<? super Object, A, R>)collector);
        }
        return stream.filter(p -> {
            try {
                return p != null && filter.accept(p, readBasicFileAttributes(p)) == FileVisitResult.CONTINUE;
            }
            catch (IOException e) {
                return false;
            }
        }).collect(collector);
    }
    
    public static List<AclEntry> getAclEntryList(final Path sourcePath) throws IOException {
        final AclFileAttributeView fileAttributeView = Files.getFileAttributeView(sourcePath, AclFileAttributeView.class, new LinkOption[0]);
        return (fileAttributeView == null) ? null : fileAttributeView.getAcl();
    }
    
    public static boolean isDirectory(final Path path, final LinkOption... options) {
        return path != null && Files.isDirectory(path, options);
    }
    
    public static boolean isEmpty(final Path path) throws IOException {
        return Files.isDirectory(path, new LinkOption[0]) ? isEmptyDirectory(path) : isEmptyFile(path);
    }
    
    public static boolean isEmptyDirectory(final Path directory) throws IOException {
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            return !directoryStream.iterator().hasNext();
        }
    }
    
    public static boolean isEmptyFile(final Path file) throws IOException {
        return Files.size(file) <= 0L;
    }
    
    public static boolean isNewer(final Path file, final long timeMillis, final LinkOption... options) throws IOException {
        Objects.requireNonNull(file, "file");
        return !Files.notExists(file, new LinkOption[0]) && Files.getLastModifiedTime(file, options).toMillis() > timeMillis;
    }
    
    public static boolean isRegularFile(final Path path, final LinkOption... options) {
        return path != null && Files.isRegularFile(path, options);
    }
    
    public static DirectoryStream<Path> newDirectoryStream(final Path dir, final PathFilter pathFilter) throws IOException {
        return Files.newDirectoryStream(dir, (DirectoryStream.Filter<? super Path>)new DirectoryStreamFilter(pathFilter));
    }
    
    private static boolean overrideReadOnly(final DeleteOption... deleteOptions) {
        return deleteOptions != null && Stream.of(deleteOptions).anyMatch(e -> e == StandardDeleteOption.OVERRIDE_READ_ONLY);
    }
    
    public static BasicFileAttributes readBasicFileAttributes(final Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class, new LinkOption[0]);
    }
    
    public static BasicFileAttributes readBasicFileAttributesUnchecked(final Path path) {
        try {
            return readBasicFileAttributes(path);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    static List<Path> relativize(final Collection<Path> collection, final Path parent, final boolean sort, final Comparator<? super Path> comparator) {
        Stream<Path> stream = collection.stream().map((Function<? super Path, ? extends Path>)parent::relativize);
        if (sort) {
            stream = ((comparator == null) ? stream.sorted() : stream.sorted(comparator));
        }
        return stream.collect((Collector<? super Path, ?, List<Path>>)Collectors.toList());
    }
    
    public static Path setReadOnly(final Path path, final boolean readOnly, final LinkOption... linkOptions) throws IOException {
        final List<Exception> causeList = new ArrayList<Exception>(2);
        final DosFileAttributeView fileAttributeView = Files.getFileAttributeView(path, DosFileAttributeView.class, linkOptions);
        if (fileAttributeView != null) {
            try {
                fileAttributeView.setReadOnly(readOnly);
                return path;
            }
            catch (IOException e) {
                causeList.add(e);
            }
        }
        final PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class, linkOptions);
        if (posixFileAttributeView != null) {
            final PosixFileAttributes readAttributes = posixFileAttributeView.readAttributes();
            final Set<PosixFilePermission> permissions = readAttributes.permissions();
            permissions.remove(PosixFilePermission.OWNER_WRITE);
            permissions.remove(PosixFilePermission.GROUP_WRITE);
            permissions.remove(PosixFilePermission.OTHERS_WRITE);
            try {
                return Files.setPosixFilePermissions(path, permissions);
            }
            catch (IOException e2) {
                causeList.add(e2);
            }
        }
        if (!causeList.isEmpty()) {
            throw new IOExceptionList(path.toString(), causeList);
        }
        throw new IOException(String.format("No DosFileAttributeView or PosixFileAttributeView for '%s' (linkOptions=%s)", path, Arrays.toString(linkOptions)));
    }
    
    static Set<FileVisitOption> toFileVisitOptionSet(final FileVisitOption... fileVisitOptions) {
        return (Set<FileVisitOption>)((fileVisitOptions == null) ? EnumSet.noneOf(FileVisitOption.class) : Stream.of(fileVisitOptions).collect((Collector<? super FileVisitOption, ?, Set<? super FileVisitOption>>)Collectors.toSet()));
    }
    
    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final Path directory) throws IOException {
        Files.walkFileTree(directory, visitor);
        return visitor;
    }
    
    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final Path start, final Set<FileVisitOption> options, final int maxDepth) throws IOException {
        Files.walkFileTree(start, options, maxDepth, visitor);
        return visitor;
    }
    
    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final String first, final String... more) throws IOException {
        return visitFileTree(visitor, Paths.get(first, more));
    }
    
    public static <T extends FileVisitor<? super Path>> T visitFileTree(final T visitor, final URI uri) throws IOException {
        return visitFileTree(visitor, Paths.get(uri));
    }
    
    public static Stream<Path> walk(final Path start, final PathFilter pathFilter, final int maxDepth, final boolean readAttributes, final FileVisitOption... options) throws IOException {
        return Files.walk(start, maxDepth, options).filter(path -> pathFilter.accept(path, readAttributes ? readBasicFileAttributesUnchecked(path) : null) == FileVisitResult.CONTINUE);
    }
    
    private PathUtils() {
    }
    
    static {
        EMPTY_COPY_OPTIONS = new CopyOption[0];
        EMPTY_DELETE_OPTION_ARRAY = new DeleteOption[0];
        EMPTY_FILE_VISIT_OPTION_ARRAY = new FileVisitOption[0];
        EMPTY_LINK_OPTION_ARRAY = new LinkOption[0];
        NOFOLLOW_LINK_OPTION_ARRAY = new LinkOption[] { LinkOption.NOFOLLOW_LINKS };
        EMPTY_OPEN_OPTION_ARRAY = new OpenOption[0];
        EMPTY_PATH_ARRAY = new Path[0];
    }
    
    private static class RelativeSortedPaths
    {
        final boolean equals;
        final List<Path> relativeFileList1;
        final List<Path> relativeFileList2;
        
        private RelativeSortedPaths(final Path dir1, final Path dir2, final int maxDepth, final LinkOption[] linkOptions, final FileVisitOption[] fileVisitOptions) throws IOException {
            List<Path> tmpRelativeFileList1 = null;
            List<Path> tmpRelativeFileList2 = null;
            if (dir1 == null && dir2 == null) {
                this.equals = true;
            }
            else if (dir1 == null ^ dir2 == null) {
                this.equals = false;
            }
            else {
                final boolean parentDirNotExists1 = Files.notExists(dir1, linkOptions);
                final boolean parentDirNotExists2 = Files.notExists(dir2, linkOptions);
                if (parentDirNotExists1 || parentDirNotExists2) {
                    this.equals = (parentDirNotExists1 && parentDirNotExists2);
                }
                else {
                    final AccumulatorPathVisitor visitor1 = accumulate(dir1, maxDepth, fileVisitOptions);
                    final AccumulatorPathVisitor visitor2 = accumulate(dir2, maxDepth, fileVisitOptions);
                    if (visitor1.getDirList().size() != visitor2.getDirList().size() || visitor1.getFileList().size() != visitor2.getFileList().size()) {
                        this.equals = false;
                    }
                    else {
                        final List<Path> tmpRelativeDirList1 = (List<Path>)visitor1.relativizeDirectories(dir1, true, (Comparator)null);
                        final List<Path> tmpRelativeDirList2 = (List<Path>)visitor2.relativizeDirectories(dir2, true, (Comparator)null);
                        if (!tmpRelativeDirList1.equals(tmpRelativeDirList2)) {
                            this.equals = false;
                        }
                        else {
                            tmpRelativeFileList1 = (List<Path>)visitor1.relativizeFiles(dir1, true, (Comparator)null);
                            tmpRelativeFileList2 = (List<Path>)visitor2.relativizeFiles(dir2, true, (Comparator)null);
                            this.equals = tmpRelativeFileList1.equals(tmpRelativeFileList2);
                        }
                    }
                }
            }
            this.relativeFileList1 = tmpRelativeFileList1;
            this.relativeFileList2 = tmpRelativeFileList2;
        }
    }
}
