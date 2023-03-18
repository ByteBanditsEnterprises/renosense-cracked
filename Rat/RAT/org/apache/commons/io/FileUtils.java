//Raddon On Top!

package org.apache.commons.io;

import java.math.*;
import java.util.zip.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.time.chrono.*;
import java.time.*;
import org.apache.commons.io.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.apache.commons.io.filefilter.*;
import java.nio.file.*;
import java.io.*;

public class FileUtils
{
    public static final long ONE_KB = 1024L;
    public static final BigInteger ONE_KB_BI;
    public static final long ONE_MB = 1048576L;
    public static final BigInteger ONE_MB_BI;
    public static final long ONE_GB = 1073741824L;
    public static final BigInteger ONE_GB_BI;
    public static final long ONE_TB = 1099511627776L;
    public static final BigInteger ONE_TB_BI;
    public static final long ONE_PB = 1125899906842624L;
    public static final BigInteger ONE_PB_BI;
    public static final long ONE_EB = 1152921504606846976L;
    public static final BigInteger ONE_EB_BI;
    public static final BigInteger ONE_ZB;
    public static final BigInteger ONE_YB;
    public static final File[] EMPTY_FILE_ARRAY;
    
    private static CopyOption[] addCopyAttributes(final CopyOption... copyOptions) {
        final CopyOption[] actual = Arrays.copyOf(copyOptions, copyOptions.length + 1);
        Arrays.sort(actual, 0, copyOptions.length);
        if (Arrays.binarySearch(copyOptions, 0, copyOptions.length, StandardCopyOption.COPY_ATTRIBUTES) >= 0) {
            return copyOptions;
        }
        actual[actual.length - 1] = StandardCopyOption.COPY_ATTRIBUTES;
        return actual;
    }
    
    public static String byteCountToDisplaySize(final BigInteger size) {
        Objects.requireNonNull(size, "size");
        String displaySize;
        if (size.divide(FileUtils.ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(FileUtils.ONE_EB_BI) + " EB";
        }
        else if (size.divide(FileUtils.ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(FileUtils.ONE_PB_BI) + " PB";
        }
        else if (size.divide(FileUtils.ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(FileUtils.ONE_TB_BI) + " TB";
        }
        else if (size.divide(FileUtils.ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(FileUtils.ONE_GB_BI) + " GB";
        }
        else if (size.divide(FileUtils.ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(FileUtils.ONE_MB_BI) + " MB";
        }
        else if (size.divide(FileUtils.ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(FileUtils.ONE_KB_BI) + " KB";
        }
        else {
            displaySize = size + " bytes";
        }
        return displaySize;
    }
    
    public static String byteCountToDisplaySize(final long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }
    
    public static Checksum checksum(final File file, final Checksum checksum) throws IOException {
        requireExistsChecked(file, "file");
        requireFile(file, "file");
        Objects.requireNonNull(checksum, "checksum");
        try (final InputStream inputStream = new CheckedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]), checksum)) {
            IOUtils.consume(inputStream);
        }
        return checksum;
    }
    
    public static long checksumCRC32(final File file) throws IOException {
        return checksum(file, new CRC32()).getValue();
    }
    
    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = listFiles(directory, null);
        final List<Exception> causeList = new ArrayList<Exception>();
        for (final File file : files) {
            try {
                forceDelete(file);
            }
            catch (IOException ioe) {
                causeList.add(ioe);
            }
        }
        if (!causeList.isEmpty()) {
            throw new IOExceptionList(directory.toString(), causeList);
        }
    }
    
    private static void cleanDirectoryOnExit(final File directory) throws IOException {
        final File[] files = listFiles(directory, null);
        final List<Exception> causeList = new ArrayList<Exception>();
        for (final File file : files) {
            try {
                forceDeleteOnExit(file);
            }
            catch (IOException ioe) {
                causeList.add(ioe);
            }
        }
        if (!causeList.isEmpty()) {
            throw new IOExceptionList(causeList);
        }
    }
    
    public static boolean contentEquals(final File file1, final File file2) throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        }
        if (file1 == null || file2 == null) {
            return false;
        }
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        requireFile(file1, "file1");
        requireFile(file2, "file2");
        if (file1.length() != file2.length()) {
            return false;
        }
        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        }
        try (final InputStream input1 = Files.newInputStream(file1.toPath(), new OpenOption[0]);
             final InputStream input2 = Files.newInputStream(file2.toPath(), new OpenOption[0])) {
            return IOUtils.contentEquals(input1, input2);
        }
    }
    
    public static boolean contentEqualsIgnoreEOL(final File file1, final File file2, final String charsetName) throws IOException {
        if (file1 == null && file2 == null) {
            return true;
        }
        if (file1 == null || file2 == null) {
            return false;
        }
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        requireFile(file1, "file1");
        requireFile(file2, "file2");
        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        }
        final Charset charset = Charsets.toCharset(charsetName);
        try (final Reader input1 = new InputStreamReader(Files.newInputStream(file1.toPath(), new OpenOption[0]), charset);
             final Reader input2 = new InputStreamReader(Files.newInputStream(file2.toPath(), new OpenOption[0]), charset)) {
            return IOUtils.contentEqualsIgnoreEOL(input1, input2);
        }
    }
    
    public static File[] convertFileCollectionToFileArray(final Collection<File> files) {
        return files.toArray(FileUtils.EMPTY_FILE_ARRAY);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir, final boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter) throws IOException {
        copyDirectory(srcDir, destDir, filter, true);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter filter, final boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, filter, preserveFileDate, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static void copyDirectory(final File srcDir, final File destDir, final FileFilter fileFilter, final boolean preserveFileDate, final CopyOption... copyOptions) throws IOException {
        requireFileCopy(srcDir, destDir);
        requireDirectory(srcDir, "srcDir");
        requireCanonicalPathsNotEquals(srcDir, destDir);
        List<String> exclusionList = null;
        final String srcDirCanonicalPath = srcDir.getCanonicalPath();
        final String destDirCanonicalPath = destDir.getCanonicalPath();
        if (destDirCanonicalPath.startsWith(srcDirCanonicalPath)) {
            final File[] srcFiles = listFiles(srcDir, fileFilter);
            if (srcFiles.length > 0) {
                exclusionList = new ArrayList<String>(srcFiles.length);
                for (final File srcFile : srcFiles) {
                    final File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, fileFilter, exclusionList, preserveFileDate, preserveFileDate ? addCopyAttributes(copyOptions) : copyOptions);
    }
    
    public static void copyDirectoryToDirectory(final File sourceDir, final File destinationDir) throws IOException {
        requireDirectoryIfExists(sourceDir, "sourceDir");
        requireDirectoryIfExists(destinationDir, "destinationDir");
        copyDirectory(sourceDir, new File(destinationDir, sourceDir.getName()), true);
    }
    
    public static void copyFile(final File srcFile, final File destFile) throws IOException {
        copyFile(srcFile, destFile, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
        copyFile(srcFile, destFile, preserveFileDate ? new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING } : new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
    }
    
    public static void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate, final CopyOption... copyOptions) throws IOException {
        copyFile(srcFile, destFile, preserveFileDate ? addCopyAttributes(copyOptions) : copyOptions);
    }
    
    public static void copyFile(final File srcFile, final File destFile, final CopyOption... copyOptions) throws IOException {
        requireFileCopy(srcFile, destFile);
        requireFile(srcFile, "srcFile");
        requireCanonicalPathsNotEquals(srcFile, destFile);
        createParentDirectories(destFile);
        requireFileIfExists(destFile, "destFile");
        if (destFile.exists()) {
            requireCanWrite(destFile, "destFile");
        }
        Files.copy(srcFile.toPath(), destFile.toPath(), copyOptions);
        requireEqualSizes(srcFile, destFile, srcFile.length(), destFile.length());
    }
    
    public static long copyFile(final File input, final OutputStream output) throws IOException {
        try (final InputStream fis = Files.newInputStream(input.toPath(), new OpenOption[0])) {
            return IOUtils.copyLarge(fis, output);
        }
    }
    
    public static void copyFileToDirectory(final File srcFile, final File destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, true);
    }
    
    public static void copyFileToDirectory(final File sourceFile, final File destinationDir, final boolean preserveFileDate) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        requireDirectoryIfExists(destinationDir, "destinationDir");
        copyFile(sourceFile, new File(destinationDir, sourceFile.getName()), preserveFileDate);
    }
    
    public static void copyInputStreamToFile(final InputStream source, final File destination) throws IOException {
        try (final InputStream inputStream = source) {
            copyToFile(inputStream, destination);
        }
    }
    
    public static void copyToDirectory(final File sourceFile, final File destinationDir) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        if (sourceFile.isFile()) {
            copyFileToDirectory(sourceFile, destinationDir);
        }
        else {
            if (!sourceFile.isDirectory()) {
                throw new FileNotFoundException("The source " + sourceFile + " does not exist");
            }
            copyDirectoryToDirectory(sourceFile, destinationDir);
        }
    }
    
    public static void copyToDirectory(final Iterable<File> sourceIterable, final File destinationDir) throws IOException {
        Objects.requireNonNull(sourceIterable, "sourceIterable");
        for (final File src : sourceIterable) {
            copyFileToDirectory(src, destinationDir);
        }
    }
    
    public static void copyToFile(final InputStream inputStream, final File file) throws IOException {
        try (final OutputStream out = openOutputStream(file)) {
            IOUtils.copy(inputStream, out);
        }
    }
    
    public static void copyURLToFile(final URL source, final File destination) throws IOException {
        try (final InputStream stream = source.openStream()) {
            copyInputStreamToFile(stream, destination);
        }
    }
    
    public static void copyURLToFile(final URL source, final File destination, final int connectionTimeoutMillis, final int readTimeoutMillis) throws IOException {
        final URLConnection connection = source.openConnection();
        connection.setConnectTimeout(connectionTimeoutMillis);
        connection.setReadTimeout(readTimeoutMillis);
        try (final InputStream stream = connection.getInputStream()) {
            copyInputStreamToFile(stream, destination);
        }
    }
    
    public static File createParentDirectories(final File file) throws IOException {
        return mkdirs(getParentFile(file));
    }
    
    static String decodeUrl(final String url) {
        String decoded = url;
        if (url != null && url.indexOf(37) >= 0) {
            final int n = url.length();
            final StringBuilder buffer = new StringBuilder();
            final ByteBuffer bytes = ByteBuffer.allocate(n);
            int i = 0;
            while (i < n) {
                if (url.charAt(i) == '%') {
                    try {
                        do {
                            final byte octet = (byte)Integer.parseInt(url.substring(i + 1, i + 3), 16);
                            bytes.put(octet);
                            i += 3;
                        } while (i < n && url.charAt(i) == '%');
                        continue;
                    }
                    catch (RuntimeException ex) {}
                    finally {
                        if (bytes.position() > 0) {
                            bytes.flip();
                            buffer.append(StandardCharsets.UTF_8.decode(bytes).toString());
                            bytes.clear();
                        }
                    }
                }
                buffer.append(url.charAt(i++));
            }
            decoded = buffer.toString();
        }
        return decoded;
    }
    
    public static File delete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        Files.delete(file.toPath());
        return file;
    }
    
    public static void deleteDirectory(final File directory) throws IOException {
        Objects.requireNonNull(directory, "directory");
        if (!directory.exists()) {
            return;
        }
        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }
        delete(directory);
    }
    
    private static void deleteDirectoryOnExit(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        directory.deleteOnExit();
        if (!isSymlink(directory)) {
            cleanDirectoryOnExit(directory);
        }
    }
    
    public static boolean deleteQuietly(final File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        }
        catch (Exception ex) {}
        try {
            return file.delete();
        }
        catch (Exception ignored) {
            return false;
        }
    }
    
    public static boolean directoryContains(final File directory, final File child) throws IOException {
        requireDirectoryExists(directory, "directory");
        return child != null && directory.exists() && child.exists() && FilenameUtils.directoryContains(directory.getCanonicalPath(), child.getCanonicalPath());
    }
    
    private static void doCopyDirectory(final File srcDir, final File destDir, final FileFilter fileFilter, final List<String> exclusionList, final boolean preserveDirDate, final CopyOption... copyOptions) throws IOException {
        final File[] srcFiles = listFiles(srcDir, fileFilter);
        requireDirectoryIfExists(destDir, "destDir");
        mkdirs(destDir);
        requireCanWrite(destDir, "destDir");
        for (final File srcFile : srcFiles) {
            final File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, fileFilter, exclusionList, preserveDirDate, copyOptions);
                }
                else {
                    copyFile(srcFile, dstFile, copyOptions);
                }
            }
        }
        if (preserveDirDate) {
            setLastModified(srcDir, destDir);
        }
    }
    
    public static void forceDelete(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        Counters.PathCounters deleteCounters;
        try {
            deleteCounters = PathUtils.delete(file.toPath(), PathUtils.EMPTY_LINK_OPTION_ARRAY, new DeleteOption[] { (DeleteOption)StandardDeleteOption.OVERRIDE_READ_ONLY });
        }
        catch (IOException e) {
            throw new IOException("Cannot delete file: " + file, e);
        }
        if (deleteCounters.getFileCounter().get() < 1L && deleteCounters.getDirectoryCounter().get() < 1L) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
    }
    
    public static void forceDeleteOnExit(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        }
        else {
            file.deleteOnExit();
        }
    }
    
    public static void forceMkdir(final File directory) throws IOException {
        mkdirs(directory);
    }
    
    public static void forceMkdirParent(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        final File parent = getParentFile(file);
        if (parent == null) {
            return;
        }
        forceMkdir(parent);
    }
    
    public static File getFile(final File directory, final String... names) {
        Objects.requireNonNull(directory, "directory");
        Objects.requireNonNull(names, "names");
        File file = directory;
        for (final String name : names) {
            file = new File(file, name);
        }
        return file;
    }
    
    public static File getFile(final String... names) {
        Objects.requireNonNull(names, "names");
        File file = null;
        for (final String name : names) {
            if (file == null) {
                file = new File(name);
            }
            else {
                file = new File(file, name);
            }
        }
        return file;
    }
    
    private static File getParentFile(final File file) {
        return (file == null) ? null : file.getParentFile();
    }
    
    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }
    
    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }
    
    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }
    
    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }
    
    public static boolean isDirectory(final File file, final LinkOption... options) {
        return file != null && Files.isDirectory(file.toPath(), options);
    }
    
    public static boolean isEmptyDirectory(final File directory) throws IOException {
        return PathUtils.isEmptyDirectory(directory.toPath());
    }
    
    public static boolean isFileNewer(final File file, final ChronoLocalDate chronoLocalDate) {
        return isFileNewer(file, chronoLocalDate, LocalTime.now());
    }
    
    public static boolean isFileNewer(final File file, final ChronoLocalDate chronoLocalDate, final LocalTime localTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(localTime, "localTime");
        return isFileNewer(file, chronoLocalDate.atTime(localTime));
    }
    
    public static boolean isFileNewer(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime) {
        return isFileNewer(file, chronoLocalDateTime, ZoneId.systemDefault());
    }
    
    public static boolean isFileNewer(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime, final ZoneId zoneId) {
        Objects.requireNonNull(chronoLocalDateTime, "chronoLocalDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return isFileNewer(file, chronoLocalDateTime.atZone(zoneId));
    }
    
    public static boolean isFileNewer(final File file, final ChronoZonedDateTime<?> chronoZonedDateTime) {
        Objects.requireNonNull(chronoZonedDateTime, "chronoZonedDateTime");
        return isFileNewer(file, chronoZonedDateTime.toInstant());
    }
    
    public static boolean isFileNewer(final File file, final Date date) {
        Objects.requireNonNull(date, "date");
        return isFileNewer(file, date.getTime());
    }
    
    public static boolean isFileNewer(final File file, final File reference) {
        requireExists(reference, "reference");
        return isFileNewer(file, lastModifiedUnchecked(reference));
    }
    
    public static boolean isFileNewer(final File file, final Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return isFileNewer(file, instant.toEpochMilli());
    }
    
    public static boolean isFileNewer(final File file, final long timeMillis) {
        Objects.requireNonNull(file, "file");
        return file.exists() && lastModifiedUnchecked(file) > timeMillis;
    }
    
    public static boolean isFileOlder(final File file, final ChronoLocalDate chronoLocalDate) {
        return isFileOlder(file, chronoLocalDate, LocalTime.now());
    }
    
    public static boolean isFileOlder(final File file, final ChronoLocalDate chronoLocalDate, final LocalTime localTime) {
        Objects.requireNonNull(chronoLocalDate, "chronoLocalDate");
        Objects.requireNonNull(localTime, "localTime");
        return isFileOlder(file, chronoLocalDate.atTime(localTime));
    }
    
    public static boolean isFileOlder(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime) {
        return isFileOlder(file, chronoLocalDateTime, ZoneId.systemDefault());
    }
    
    public static boolean isFileOlder(final File file, final ChronoLocalDateTime<?> chronoLocalDateTime, final ZoneId zoneId) {
        Objects.requireNonNull(chronoLocalDateTime, "chronoLocalDateTime");
        Objects.requireNonNull(zoneId, "zoneId");
        return isFileOlder(file, chronoLocalDateTime.atZone(zoneId));
    }
    
    public static boolean isFileOlder(final File file, final ChronoZonedDateTime<?> chronoZonedDateTime) {
        Objects.requireNonNull(chronoZonedDateTime, "chronoZonedDateTime");
        return isFileOlder(file, chronoZonedDateTime.toInstant());
    }
    
    public static boolean isFileOlder(final File file, final Date date) {
        Objects.requireNonNull(date, "date");
        return isFileOlder(file, date.getTime());
    }
    
    public static boolean isFileOlder(final File file, final File reference) {
        requireExists(reference, "reference");
        return isFileOlder(file, lastModifiedUnchecked(reference));
    }
    
    public static boolean isFileOlder(final File file, final Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return isFileOlder(file, instant.toEpochMilli());
    }
    
    public static boolean isFileOlder(final File file, final long timeMillis) {
        Objects.requireNonNull(file, "file");
        return file.exists() && lastModifiedUnchecked(file) < timeMillis;
    }
    
    public static boolean isRegularFile(final File file, final LinkOption... options) {
        return file != null && Files.isRegularFile(file.toPath(), options);
    }
    
    public static boolean isSymlink(final File file) {
        return file != null && Files.isSymbolicLink(file.toPath());
    }
    
    public static Iterator<File> iterateFiles(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        return listFiles(directory, fileFilter, dirFilter).iterator();
    }
    
    public static Iterator<File> iterateFiles(final File directory, final String[] extensions, final boolean recursive) {
        try {
            return StreamIterator.iterator(streamFiles(directory, recursive, extensions));
        }
        catch (IOException e) {
            throw new UncheckedIOException(directory.toString(), e);
        }
    }
    
    public static Iterator<File> iterateFilesAndDirs(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        return listFilesAndDirs(directory, fileFilter, dirFilter).iterator();
    }
    
    public static long lastModified(final File file) throws IOException {
        return Files.getLastModifiedTime(Objects.requireNonNull(file.toPath(), "file"), new LinkOption[0]).toMillis();
    }
    
    public static long lastModifiedUnchecked(final File file) {
        try {
            return lastModified(file);
        }
        catch (IOException e) {
            throw new UncheckedIOException(file.toString(), e);
        }
    }
    
    public static LineIterator lineIterator(final File file) throws IOException {
        return lineIterator(file, null);
    }
    
    public static LineIterator lineIterator(final File file, final String charsetName) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = openInputStream(file);
            return IOUtils.lineIterator(inputStream, charsetName);
        }
        catch (IOException | RuntimeException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            IOUtils.closeQuietly(inputStream, ex::addSuppressed);
            throw ex;
        }
    }
    
    private static AccumulatorPathVisitor listAccumulate(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) throws IOException {
        final boolean isDirFilterSet = dirFilter != null;
        final FileEqualsFileFilter rootDirFilter = new FileEqualsFileFilter(directory);
        final PathFilter dirPathFilter = (PathFilter)(isDirFilterSet ? rootDirFilter.or(dirFilter) : rootDirFilter);
        final AccumulatorPathVisitor visitor = new AccumulatorPathVisitor(Counters.noopPathCounters(), (PathFilter)fileFilter, dirPathFilter);
        Files.walkFileTree(directory.toPath(), Collections.emptySet(), toMaxDepth(isDirFilterSet), (FileVisitor<? super Path>)visitor);
        return visitor;
    }
    
    private static File[] listFiles(final File directory, final FileFilter fileFilter) throws IOException {
        requireDirectoryExists(directory, "directory");
        final File[] files = (fileFilter == null) ? directory.listFiles() : directory.listFiles(fileFilter);
        if (files == null) {
            throw new IOException("Unknown I/O error listing contents of directory: " + directory);
        }
        return files;
    }
    
    public static Collection<File> listFiles(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        try {
            final AccumulatorPathVisitor visitor = listAccumulate(directory, fileFilter, dirFilter);
            return (Collection<File>)visitor.getFileList().stream().map(Path::toFile).collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new UncheckedIOException(directory.toString(), e);
        }
    }
    
    public static Collection<File> listFiles(final File directory, final String[] extensions, final boolean recursive) {
        try {
            return toList(streamFiles(directory, recursive, extensions));
        }
        catch (IOException e) {
            throw new UncheckedIOException(directory.toString(), e);
        }
    }
    
    public static Collection<File> listFilesAndDirs(final File directory, final IOFileFilter fileFilter, final IOFileFilter dirFilter) {
        try {
            final AccumulatorPathVisitor visitor = listAccumulate(directory, fileFilter, dirFilter);
            final List<Path> list = (List<Path>)visitor.getFileList();
            list.addAll(visitor.getDirList());
            return list.stream().map((Function<? super Object, ?>)Path::toFile).collect((Collector<? super Object, ?, Collection<File>>)Collectors.toList());
        }
        catch (IOException e) {
            throw new UncheckedIOException(directory.toString(), e);
        }
    }
    
    private static File mkdirs(final File directory) throws IOException {
        if (directory != null && !directory.mkdirs() && !directory.isDirectory()) {
            throw new IOException("Cannot create directory '" + directory + "'.");
        }
        return directory;
    }
    
    public static void moveDirectory(final File srcDir, final File destDir) throws IOException {
        validateMoveParameters(srcDir, destDir);
        requireDirectory(srcDir, "srcDir");
        requireAbsent(destDir, "destDir");
        if (!srcDir.renameTo(destDir)) {
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath() + File.separator)) {
                throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
            }
            copyDirectory(srcDir, destDir);
            deleteDirectory(srcDir);
            if (srcDir.exists()) {
                throw new IOException("Failed to delete original directory '" + srcDir + "' after copy to '" + destDir + "'");
            }
        }
    }
    
    public static void moveDirectoryToDirectory(final File src, final File destDir, final boolean createDestDir) throws IOException {
        validateMoveParameters(src, destDir);
        if (!destDir.isDirectory()) {
            if (destDir.exists()) {
                throw new IOException("Destination '" + destDir + "' is not a directory");
            }
            if (!createDestDir) {
                throw new FileNotFoundException("Destination directory '" + destDir + "' does not exist [createDestDir=" + false + "]");
            }
            mkdirs(destDir);
        }
        moveDirectory(src, new File(destDir, src.getName()));
    }
    
    public static void moveFile(final File srcFile, final File destFile) throws IOException {
        moveFile(srcFile, destFile, StandardCopyOption.COPY_ATTRIBUTES);
    }
    
    public static void moveFile(final File srcFile, final File destFile, final CopyOption... copyOptions) throws IOException {
        validateMoveParameters(srcFile, destFile);
        requireFile(srcFile, "srcFile");
        requireAbsent(destFile, null);
        final boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile, destFile, copyOptions);
            if (!srcFile.delete()) {
                deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
            }
        }
    }
    
    public static void moveFileToDirectory(final File srcFile, final File destDir, final boolean createDestDir) throws IOException {
        validateMoveParameters(srcFile, destDir);
        if (!destDir.exists() && createDestDir) {
            mkdirs(destDir);
        }
        requireExistsChecked(destDir, "destDir");
        requireDirectory(destDir, "destDir");
        moveFile(srcFile, new File(destDir, srcFile.getName()));
    }
    
    public static void moveToDirectory(final File src, final File destDir, final boolean createDestDir) throws IOException {
        validateMoveParameters(src, destDir);
        if (src.isDirectory()) {
            moveDirectoryToDirectory(src, destDir, createDestDir);
        }
        else {
            moveFileToDirectory(src, destDir, createDestDir);
        }
    }
    
    public static FileInputStream openInputStream(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        return new FileInputStream(file);
    }
    
    public static FileOutputStream openOutputStream(final File file) throws IOException {
        return openOutputStream(file, false);
    }
    
    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.exists()) {
            requireFile(file, "file");
            requireCanWrite(file, "file");
        }
        else {
            createParentDirectories(file);
        }
        return new FileOutputStream(file, append);
    }
    
    public static byte[] readFileToByteArray(final File file) throws IOException {
        try (final InputStream inputStream = openInputStream(file)) {
            final long fileLength = file.length();
            return (fileLength > 0L) ? IOUtils.toByteArray(inputStream, fileLength) : IOUtils.toByteArray(inputStream);
        }
    }
    
    @Deprecated
    public static String readFileToString(final File file) throws IOException {
        return readFileToString(file, Charset.defaultCharset());
    }
    
    public static String readFileToString(final File file, final Charset charsetName) throws IOException {
        try (final InputStream inputStream = openInputStream(file)) {
            return IOUtils.toString(inputStream, Charsets.toCharset(charsetName));
        }
    }
    
    public static String readFileToString(final File file, final String charsetName) throws IOException {
        return readFileToString(file, Charsets.toCharset(charsetName));
    }
    
    @Deprecated
    public static List<String> readLines(final File file) throws IOException {
        return readLines(file, Charset.defaultCharset());
    }
    
    public static List<String> readLines(final File file, final Charset charset) throws IOException {
        try (final InputStream inputStream = openInputStream(file)) {
            return IOUtils.readLines(inputStream, Charsets.toCharset(charset));
        }
    }
    
    public static List<String> readLines(final File file, final String charsetName) throws IOException {
        return readLines(file, Charsets.toCharset(charsetName));
    }
    
    private static void requireAbsent(final File file, final String name) throws FileExistsException {
        if (file.exists()) {
            throw new FileExistsException(String.format("File element in parameter '%s' already exists: '%s'", name, file));
        }
    }
    
    private static void requireCanonicalPathsNotEquals(final File file1, final File file2) throws IOException {
        final String canonicalPath = file1.getCanonicalPath();
        if (canonicalPath.equals(file2.getCanonicalPath())) {
            throw new IllegalArgumentException(String.format("File canonical paths are equal: '%s' (file1='%s', file2='%s')", canonicalPath, file1, file2));
        }
    }
    
    private static void requireCanWrite(final File file, final String name) {
        Objects.requireNonNull(file, "file");
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File parameter '" + name + " is not writable: '" + file + "'");
        }
    }
    
    private static File requireDirectory(final File directory, final String name) {
        Objects.requireNonNull(directory, name);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a directory: '" + directory + "'");
        }
        return directory;
    }
    
    private static File requireDirectoryExists(final File directory, final String name) {
        requireExists(directory, name);
        requireDirectory(directory, name);
        return directory;
    }
    
    private static File requireDirectoryIfExists(final File directory, final String name) {
        Objects.requireNonNull(directory, name);
        if (directory.exists()) {
            requireDirectory(directory, name);
        }
        return directory;
    }
    
    private static void requireEqualSizes(final File srcFile, final File destFile, final long srcLen, final long dstLen) throws IOException {
        if (srcLen != dstLen) {
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
        }
    }
    
    private static File requireExists(final File file, final String fileParamName) {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new IllegalArgumentException("File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
        return file;
    }
    
    private static File requireExistsChecked(final File file, final String fileParamName) throws FileNotFoundException {
        Objects.requireNonNull(file, fileParamName);
        if (!file.exists()) {
            throw new FileNotFoundException("File system element for parameter '" + fileParamName + "' does not exist: '" + file + "'");
        }
        return file;
    }
    
    private static File requireFile(final File file, final String name) {
        Objects.requireNonNull(file, name);
        if (!file.isFile()) {
            throw new IllegalArgumentException("Parameter '" + name + "' is not a file: " + file);
        }
        return file;
    }
    
    private static void requireFileCopy(final File source, final File destination) throws FileNotFoundException {
        requireExistsChecked(source, "source");
        Objects.requireNonNull(destination, "destination");
    }
    
    private static File requireFileIfExists(final File file, final String name) {
        Objects.requireNonNull(file, name);
        return file.exists() ? requireFile(file, name) : file;
    }
    
    private static void setLastModified(final File sourceFile, final File targetFile) throws IOException {
        Objects.requireNonNull(sourceFile, "sourceFile");
        setLastModified(targetFile, lastModified(sourceFile));
    }
    
    private static void setLastModified(final File file, final long timeMillis) throws IOException {
        Objects.requireNonNull(file, "file");
        if (!file.setLastModified(timeMillis)) {
            throw new IOException(String.format("Failed setLastModified(%s) on '%s'", timeMillis, file));
        }
    }
    
    public static long sizeOf(final File file) {
        requireExists(file, "file");
        return file.isDirectory() ? sizeOfDirectory0(file) : file.length();
    }
    
    private static long sizeOf0(final File file) {
        Objects.requireNonNull(file, "file");
        if (file.isDirectory()) {
            return sizeOfDirectory0(file);
        }
        return file.length();
    }
    
    public static BigInteger sizeOfAsBigInteger(final File file) {
        requireExists(file, "file");
        return file.isDirectory() ? sizeOfDirectoryBig0(file) : BigInteger.valueOf(file.length());
    }
    
    private static BigInteger sizeOfBig0(final File file) {
        Objects.requireNonNull(file, "fileOrDir");
        return file.isDirectory() ? sizeOfDirectoryBig0(file) : BigInteger.valueOf(file.length());
    }
    
    public static long sizeOfDirectory(final File directory) {
        return sizeOfDirectory0(requireDirectoryExists(directory, "directory"));
    }
    
    private static long sizeOfDirectory0(final File directory) {
        Objects.requireNonNull(directory, "directory");
        final File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }
        long size = 0L;
        for (final File file : files) {
            if (!isSymlink(file)) {
                size += sizeOf0(file);
                if (size < 0L) {
                    break;
                }
            }
        }
        return size;
    }
    
    public static BigInteger sizeOfDirectoryAsBigInteger(final File directory) {
        return sizeOfDirectoryBig0(requireDirectoryExists(directory, "directory"));
    }
    
    private static BigInteger sizeOfDirectoryBig0(final File directory) {
        Objects.requireNonNull(directory, "directory");
        final File[] files = directory.listFiles();
        if (files == null) {
            return BigInteger.ZERO;
        }
        BigInteger size = BigInteger.ZERO;
        for (final File file : files) {
            if (!isSymlink(file)) {
                size = size.add(sizeOfBig0(file));
            }
        }
        return size;
    }
    
    public static Stream<File> streamFiles(final File directory, final boolean recursive, final String... extensions) throws IOException {
        final IOFileFilter filter = (extensions == null) ? FileFileFilter.INSTANCE : FileFileFilter.INSTANCE.and((IOFileFilter)new SuffixFileFilter(toSuffixes(extensions)));
        return PathUtils.walk(directory.toPath(), (PathFilter)filter, toMaxDepth(recursive), false, new FileVisitOption[] { FileVisitOption.FOLLOW_LINKS }).map(Path::toFile);
    }
    
    public static File toFile(final URL url) {
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return null;
        }
        final String filename = url.getFile().replace('/', File.separatorChar);
        return new File(decodeUrl(filename));
    }
    
    public static File[] toFiles(final URL... urls) {
        if (IOUtils.length(urls) == 0) {
            return FileUtils.EMPTY_FILE_ARRAY;
        }
        final File[] files = new File[urls.length];
        for (int i = 0; i < urls.length; ++i) {
            final URL url = urls[i];
            if (url != null) {
                if (!"file".equalsIgnoreCase(url.getProtocol())) {
                    throw new IllegalArgumentException("Can only convert file URL to a File: " + url);
                }
                files[i] = toFile(url);
            }
        }
        return files;
    }
    
    private static List<File> toList(final Stream<File> stream) {
        return stream.collect((Collector<? super File, ?, List<File>>)Collectors.toList());
    }
    
    private static int toMaxDepth(final boolean recursive) {
        return recursive ? Integer.MAX_VALUE : 1;
    }
    
    private static String[] toSuffixes(final String... extensions) {
        Objects.requireNonNull(extensions, "extensions");
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; ++i) {
            suffixes[i] = "." + extensions[i];
        }
        return suffixes;
    }
    
    public static void touch(final File file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (!file.exists()) {
            openOutputStream(file).close();
        }
        setLastModified(file, System.currentTimeMillis());
    }
    
    public static URL[] toURLs(final File... files) throws IOException {
        Objects.requireNonNull(files, "files");
        final URL[] urls = new URL[files.length];
        for (int i = 0; i < urls.length; ++i) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }
    
    private static void validateMoveParameters(final File source, final File destination) throws FileNotFoundException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        if (!source.exists()) {
            throw new FileNotFoundException("Source '" + source + "' does not exist");
        }
    }
    
    public static boolean waitFor(final File file, final int seconds) {
        Objects.requireNonNull(file, "file");
        final long finishAtMillis = System.currentTimeMillis() + seconds * 1000L;
        boolean wasInterrupted = false;
        try {
            while (!file.exists()) {
                final long remainingMillis = finishAtMillis - System.currentTimeMillis();
                if (remainingMillis < 0L) {
                    return false;
                }
                try {
                    Thread.sleep(Math.min(100L, remainingMillis));
                }
                catch (InterruptedException ignore) {
                    wasInterrupted = true;
                }
                catch (Exception ex) {
                    break;
                }
            }
        }
        finally {
            if (wasInterrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return true;
    }
    
    @Deprecated
    public static void write(final File file, final CharSequence data) throws IOException {
        write(file, data, Charset.defaultCharset(), false);
    }
    
    @Deprecated
    public static void write(final File file, final CharSequence data, final boolean append) throws IOException {
        write(file, data, Charset.defaultCharset(), append);
    }
    
    public static void write(final File file, final CharSequence data, final Charset charset) throws IOException {
        write(file, data, charset, false);
    }
    
    public static void write(final File file, final CharSequence data, final Charset charset, final boolean append) throws IOException {
        writeStringToFile(file, Objects.toString(data, null), charset, append);
    }
    
    public static void write(final File file, final CharSequence data, final String charsetName) throws IOException {
        write(file, data, charsetName, false);
    }
    
    public static void write(final File file, final CharSequence data, final String charsetName, final boolean append) throws IOException {
        write(file, data, Charsets.toCharset(charsetName), append);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data, final boolean append) throws IOException {
        writeByteArrayToFile(file, data, 0, data.length, append);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data, final int off, final int len) throws IOException {
        writeByteArrayToFile(file, data, off, len, false);
    }
    
    public static void writeByteArrayToFile(final File file, final byte[] data, final int off, final int len, final boolean append) throws IOException {
        try (final OutputStream out = openOutputStream(file, append)) {
            out.write(data, off, len);
        }
    }
    
    public static void writeLines(final File file, final Collection<?> lines) throws IOException {
        writeLines(file, null, lines, null, false);
    }
    
    public static void writeLines(final File file, final Collection<?> lines, final boolean append) throws IOException {
        writeLines(file, null, lines, null, append);
    }
    
    public static void writeLines(final File file, final Collection<?> lines, final String lineEnding) throws IOException {
        writeLines(file, null, lines, lineEnding, false);
    }
    
    public static void writeLines(final File file, final Collection<?> lines, final String lineEnding, final boolean append) throws IOException {
        writeLines(file, null, lines, lineEnding, append);
    }
    
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines) throws IOException {
        writeLines(file, charsetName, lines, null, false);
    }
    
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines, final boolean append) throws IOException {
        writeLines(file, charsetName, lines, null, append);
    }
    
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines, final String lineEnding) throws IOException {
        writeLines(file, charsetName, lines, lineEnding, false);
    }
    
    public static void writeLines(final File file, final String charsetName, final Collection<?> lines, final String lineEnding, final boolean append) throws IOException {
        try (final OutputStream out = new BufferedOutputStream(openOutputStream(file, append))) {
            IOUtils.writeLines(lines, lineEnding, out, charsetName);
        }
    }
    
    @Deprecated
    public static void writeStringToFile(final File file, final String data) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), false);
    }
    
    @Deprecated
    public static void writeStringToFile(final File file, final String data, final boolean append) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), append);
    }
    
    public static void writeStringToFile(final File file, final String data, final Charset charset) throws IOException {
        writeStringToFile(file, data, charset, false);
    }
    
    public static void writeStringToFile(final File file, final String data, final Charset charset, final boolean append) throws IOException {
        try (final OutputStream out = openOutputStream(file, append)) {
            IOUtils.write(data, out, charset);
        }
    }
    
    public static void writeStringToFile(final File file, final String data, final String charsetName) throws IOException {
        writeStringToFile(file, data, charsetName, false);
    }
    
    public static void writeStringToFile(final File file, final String data, final String charsetName, final boolean append) throws IOException {
        writeStringToFile(file, data, Charsets.toCharset(charsetName), append);
    }
    
    @Deprecated
    public FileUtils() {
    }
    
    static {
        ONE_KB_BI = BigInteger.valueOf(1024L);
        ONE_MB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_KB_BI);
        ONE_GB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_MB_BI);
        ONE_TB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_GB_BI);
        ONE_PB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_TB_BI);
        ONE_EB_BI = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_PB_BI);
        ONE_ZB = BigInteger.valueOf(1024L).multiply(BigInteger.valueOf(1152921504606846976L));
        ONE_YB = FileUtils.ONE_KB_BI.multiply(FileUtils.ONE_ZB);
        EMPTY_FILE_ARRAY = new File[0];
    }
}
