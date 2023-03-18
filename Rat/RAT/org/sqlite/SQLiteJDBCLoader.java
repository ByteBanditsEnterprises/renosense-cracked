//Raddon On Top!

package org.sqlite;

import java.util.stream.*;
import java.io.*;
import java.security.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.net.*;
import org.sqlite.util.*;
import java.util.*;

public class SQLiteJDBCLoader
{
    private static final String LOCK_EXT = ".lck";
    private static boolean extracted;
    
    public static synchronized boolean initialize() throws Exception {
        if (!SQLiteJDBCLoader.extracted) {
            cleanup();
        }
        loadSQLiteNativeLibrary();
        return SQLiteJDBCLoader.extracted;
    }
    
    private static File getTempDir() {
        return new File(System.getProperty("org.sqlite.tmpdir", System.getProperty("java.io.tmpdir")));
    }
    
    static void cleanup() {
        final String searchPattern = "sqlite-" + getVersion();
        try {
            final Stream<Path> dirList = Files.list(getTempDir().toPath());
            try {
                final String s;
                final Path lckFile;
                dirList.filter(path -> !path.getFileName().toString().endsWith(".lck") && path.getFileName().toString().startsWith(s)).forEach(nativeLib -> {
                    lckFile = Paths.get(nativeLib + ".lck", new String[0]);
                    if (Files.notExists(lckFile, new LinkOption[0])) {
                        try {
                            Files.delete(nativeLib);
                        }
                        catch (Exception e) {
                            System.err.println("Failed to delete old native lib: " + e.getMessage());
                        }
                    }
                    return;
                });
                if (dirList != null) {
                    dirList.close();
                }
            }
            catch (Throwable t) {
                if (dirList != null) {
                    try {
                        dirList.close();
                    }
                    catch (Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                throw t;
            }
        }
        catch (IOException e2) {
            System.err.println("Failed to open directory: " + e2.getMessage());
        }
    }
    
    @Deprecated
    static boolean getPureJavaFlag() {
        return Boolean.parseBoolean(System.getProperty("sqlite.purejava", "false"));
    }
    
    @Deprecated
    public static boolean isPureJavaMode() {
        return false;
    }
    
    public static boolean isNativeMode() throws Exception {
        initialize();
        return SQLiteJDBCLoader.extracted;
    }
    
    static String md5sum(final InputStream input) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(input);
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final DigestInputStream digestInputStream = new DigestInputStream(in, digest);
            while (digestInputStream.read() >= 0) {}
            final ByteArrayOutputStream md5out = new ByteArrayOutputStream();
            md5out.write(digest.digest());
            return md5out.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available: " + e);
        }
        finally {
            in.close();
        }
    }
    
    private static boolean contentsEquals(InputStream in1, InputStream in2) throws IOException {
        if (!(in1 instanceof BufferedInputStream)) {
            in1 = new BufferedInputStream(in1);
        }
        if (!(in2 instanceof BufferedInputStream)) {
            in2 = new BufferedInputStream(in2);
        }
        for (int ch = in1.read(); ch != -1; ch = in1.read()) {
            final int ch2 = in2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = in2.read();
        return ch2 == -1;
    }
    
    private static boolean extractAndLoadLibraryFile(final String libFolderForCurrentOS, final String libraryFileName, final String targetFolder) {
        final String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;
        final String uuid = UUID.randomUUID().toString();
        final String extractedLibFileName = String.format("sqlite-%s-%s-%s", getVersion(), uuid, libraryFileName);
        final String extractedLckFileName = extractedLibFileName + ".lck";
        final Path extractedLibFile = Paths.get(targetFolder, extractedLibFileName);
        final Path extractedLckFile = Paths.get(targetFolder, extractedLckFileName);
        try {
            try {
                final InputStream reader = getResourceAsStream(nativeLibraryFilePath);
                try {
                    if (Files.notExists(extractedLckFile, new LinkOption[0])) {
                        Files.createFile(extractedLckFile, (FileAttribute<?>[])new FileAttribute[0]);
                    }
                    Files.copy(reader, extractedLibFile, StandardCopyOption.REPLACE_EXISTING);
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (Throwable t) {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                    }
                    throw t;
                }
            }
            finally {
                extractedLibFile.toFile().deleteOnExit();
                extractedLckFile.toFile().deleteOnExit();
            }
            extractedLibFile.toFile().setReadable(true);
            extractedLibFile.toFile().setWritable(true, true);
            extractedLibFile.toFile().setExecutable(true);
            final InputStream nativeIn = getResourceAsStream(nativeLibraryFilePath);
            try {
                final InputStream extractedLibIn = Files.newInputStream(extractedLibFile, new OpenOption[0]);
                try {
                    if (!contentsEquals(nativeIn, extractedLibIn)) {
                        throw new RuntimeException(String.format("Failed to write a native library file at %s", extractedLibFile));
                    }
                    if (extractedLibIn != null) {
                        extractedLibIn.close();
                    }
                }
                catch (Throwable t3) {
                    if (extractedLibIn != null) {
                        try {
                            extractedLibIn.close();
                        }
                        catch (Throwable t4) {
                            t3.addSuppressed(t4);
                        }
                    }
                    throw t3;
                }
                if (nativeIn != null) {
                    nativeIn.close();
                }
            }
            catch (Throwable t5) {
                if (nativeIn != null) {
                    try {
                        nativeIn.close();
                    }
                    catch (Throwable t6) {
                        t5.addSuppressed(t6);
                    }
                }
                throw t5;
            }
            return loadNativeLibrary(targetFolder, extractedLibFileName);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static InputStream getResourceAsStream(final String name) {
        final String resolvedName = name.substring(1);
        final ClassLoader cl = SQLiteJDBCLoader.class.getClassLoader();
        final URL url = cl.getResource(resolvedName);
        if (url == null) {
            return null;
        }
        try {
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static boolean loadNativeLibrary(final String path, final String name) {
        final File libPath = new File(path, name);
        if (libPath.exists()) {
            try {
                System.load(new File(path, name).getAbsolutePath());
                return true;
            }
            catch (UnsatisfiedLinkError e) {
                System.err.println("Failed to load native library:" + name + ". osinfo: " + OSInfo.getNativeLibFolderPathForCurrentOS());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    private static void loadSQLiteNativeLibrary() throws Exception {
        if (SQLiteJDBCLoader.extracted) {
            return;
        }
        final List<String> triedPaths = new LinkedList<String>();
        String sqliteNativeLibraryPath = System.getProperty("org.sqlite.lib.path");
        String sqliteNativeLibraryName = System.getProperty("org.sqlite.lib.name");
        if (sqliteNativeLibraryName == null) {
            sqliteNativeLibraryName = System.mapLibraryName("sqlitejdbc");
            if (sqliteNativeLibraryName != null && sqliteNativeLibraryName.endsWith(".dylib")) {
                sqliteNativeLibraryName = sqliteNativeLibraryName.replace(".dylib", ".jnilib");
            }
        }
        if (sqliteNativeLibraryPath != null) {
            if (loadNativeLibrary(sqliteNativeLibraryPath, sqliteNativeLibraryName)) {
                SQLiteJDBCLoader.extracted = true;
                return;
            }
            triedPaths.add(sqliteNativeLibraryPath);
        }
        final String packagePath = SQLiteJDBCLoader.class.getPackage().getName().replaceAll("\\.", "/");
        sqliteNativeLibraryPath = String.format("/%s/native/%s", packagePath, OSInfo.getNativeLibFolderPathForCurrentOS());
        boolean hasNativeLib = hasResource(sqliteNativeLibraryPath + "/" + sqliteNativeLibraryName);
        if (!hasNativeLib && OSInfo.getOSName().equals("Mac")) {
            final String altName = "libsqlitejdbc.jnilib";
            if (hasResource(sqliteNativeLibraryPath + "/" + altName)) {
                sqliteNativeLibraryName = altName;
                hasNativeLib = true;
            }
        }
        if (hasNativeLib) {
            final String tempFolder = getTempDir().getAbsolutePath();
            if (extractAndLoadLibraryFile(sqliteNativeLibraryPath, sqliteNativeLibraryName, tempFolder)) {
                SQLiteJDBCLoader.extracted = true;
                return;
            }
            triedPaths.add(sqliteNativeLibraryPath);
        }
        final String javaLibraryPath = System.getProperty("java.library.path", "");
        for (final String ldPath : javaLibraryPath.split(File.pathSeparator)) {
            if (!ldPath.isEmpty()) {
                if (loadNativeLibrary(ldPath, sqliteNativeLibraryName)) {
                    SQLiteJDBCLoader.extracted = true;
                    return;
                }
                triedPaths.add(ldPath);
            }
        }
        SQLiteJDBCLoader.extracted = false;
        throw new Exception(String.format("No native library found for os.name=%s, os.arch=%s, paths=[%s]", OSInfo.getOSName(), OSInfo.getArchName(), StringUtils.join(triedPaths, File.pathSeparator)));
    }
    
    private static boolean hasResource(final String path) {
        return SQLiteJDBCLoader.class.getResource(path) != null;
    }
    
    private static void getNativeLibraryFolderForTheCurrentOS() {
        final String osName = OSInfo.getOSName();
        final String archName = OSInfo.getArchName();
    }
    
    public static int getMajorVersion() {
        final String[] c = getVersion().split("\\.");
        return (c.length > 0) ? Integer.parseInt(c[0]) : 1;
    }
    
    public static int getMinorVersion() {
        final String[] c = getVersion().split("\\.");
        return (c.length > 1) ? Integer.parseInt(c[1]) : 0;
    }
    
    public static String getVersion() {
        URL versionFile = SQLiteJDBCLoader.class.getResource("/META-INF/maven/org.xerial/sqlite-jdbc/pom.properties");
        if (versionFile == null) {
            versionFile = SQLiteJDBCLoader.class.getResource("/META-INF/maven/org.xerial/sqlite-jdbc/VERSION");
        }
        String version = "unknown";
        try {
            if (versionFile != null) {
                final Properties versionData = new Properties();
                versionData.load(versionFile.openStream());
                version = versionData.getProperty("version", version);
                version = version.trim().replaceAll("[^0-9\\.]", "");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
    
    static {
        SQLiteJDBCLoader.extracted = false;
    }
}
