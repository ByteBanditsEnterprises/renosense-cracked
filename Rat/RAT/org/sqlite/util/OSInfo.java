//Raddon On Top!

package org.sqlite.util;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;
import java.util.*;

public class OSInfo
{
    protected static ProcessRunner processRunner;
    private static final HashMap<String, String> archMapping;
    public static final String X86 = "x86";
    public static final String X86_64 = "x86_64";
    public static final String IA64_32 = "ia64_32";
    public static final String IA64 = "ia64";
    public static final String PPC = "ppc";
    public static final String PPC64 = "ppc64";
    
    public static void main(final String[] args) {
        if (args.length >= 1) {
            if ("--os".equals(args[0])) {
                System.out.print(getOSName());
                return;
            }
            if ("--arch".equals(args[0])) {
                System.out.print(getArchName());
                return;
            }
        }
        System.out.print(getNativeLibFolderPathForCurrentOS());
    }
    
    public static String getNativeLibFolderPathForCurrentOS() {
        return getOSName() + "/" + getArchName();
    }
    
    public static String getOSName() {
        return translateOSNameToFolderName(System.getProperty("os.name"));
    }
    
    public static boolean isAndroid() {
        return isAndroidRuntime() || isAndroidTermux();
    }
    
    public static boolean isAndroidRuntime() {
        return System.getProperty("java.runtime.name", "").toLowerCase().contains("android");
    }
    
    public static boolean isAndroidTermux() {
        try {
            return OSInfo.processRunner.runAndWaitFor("uname -o").toLowerCase().contains("android");
        }
        catch (Exception ignored) {
            return false;
        }
    }
    
    public static boolean isMusl() {
        final Path mapFilesDir = Paths.get("/proc/self/map_files", new String[0]);
        try {
            final Stream<Path> dirStream = Files.list(mapFilesDir);
            try {
                final boolean anyMatch = dirStream.map(path -> {
                    try {
                        return path.toRealPath(new LinkOption[0]).toString();
                    }
                    catch (IOException e) {
                        return "";
                    }
                }).anyMatch(s -> s.toLowerCase().contains("musl"));
                if (dirStream != null) {
                    dirStream.close();
                }
                return anyMatch;
            }
            catch (Throwable t) {
                if (dirStream != null) {
                    try {
                        dirStream.close();
                    }
                    catch (Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                throw t;
            }
        }
        catch (Exception ignored) {
            return isAlpineLinux();
        }
    }
    
    private static boolean isAlpineLinux() {
        try {
            final Stream<String> osLines = Files.lines(Paths.get("/etc/os-release", new String[0]));
            try {
                final boolean anyMatch = osLines.anyMatch(l -> l.startsWith("ID") && l.contains("alpine"));
                if (osLines != null) {
                    osLines.close();
                }
                return anyMatch;
            }
            catch (Throwable t) {
                if (osLines != null) {
                    try {
                        osLines.close();
                    }
                    catch (Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                throw t;
            }
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    static String getHardwareName() {
        try {
            return OSInfo.processRunner.runAndWaitFor("uname -m");
        }
        catch (Throwable e) {
            System.err.println("Error while running uname -m: " + e.getMessage());
            return "unknown";
        }
    }
    
    static String resolveArmArchType() {
        if (System.getProperty("os.name").contains("Linux")) {
            final String armType = getHardwareName();
            if (isAndroid()) {
                if (armType.startsWith("aarch64")) {
                    return "aarch64";
                }
                return "arm";
            }
            else {
                if (armType.startsWith("armv6")) {
                    return "armv6";
                }
                if (armType.startsWith("armv7")) {
                    return "armv7";
                }
                if (armType.startsWith("armv5")) {
                    return "arm";
                }
                if (armType.startsWith("aarch64")) {
                    return "aarch64";
                }
                final String abi = System.getProperty("sun.arch.abi");
                if (abi != null && abi.startsWith("gnueabihf")) {
                    return "armv7";
                }
                final String javaHome = System.getProperty("java.home");
                try {
                    int exitCode = Runtime.getRuntime().exec("which readelf").waitFor();
                    if (exitCode == 0) {
                        final String[] cmdarray = { "/bin/sh", "-c", "find '" + javaHome + "' -name 'libjvm.so' | head -1 | xargs readelf -A | grep 'Tag_ABI_VFP_args: VFP registers'" };
                        exitCode = Runtime.getRuntime().exec(cmdarray).waitFor();
                        if (exitCode == 0) {
                            return "armv7";
                        }
                    }
                    else {
                        System.err.println("WARNING! readelf not found. Cannot check if running on an armhf system, armel architecture will be presumed.");
                    }
                }
                catch (IOException ex) {}
                catch (InterruptedException ex2) {}
            }
        }
        return "arm";
    }
    
    public static String getArchName() {
        final String override = System.getProperty("org.sqlite.osinfo.architecture");
        if (override != null) {
            return override;
        }
        String osArch = System.getProperty("os.arch");
        if (osArch.startsWith("arm")) {
            osArch = resolveArmArchType();
        }
        else {
            final String lc = osArch.toLowerCase(Locale.US);
            if (OSInfo.archMapping.containsKey(lc)) {
                return OSInfo.archMapping.get(lc);
            }
        }
        return translateArchNameToFolderName(osArch);
    }
    
    static String translateOSNameToFolderName(final String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        }
        if (osName.contains("Mac") || osName.contains("Darwin")) {
            return "Mac";
        }
        if (osName.contains("AIX")) {
            return "AIX";
        }
        if (isMusl()) {
            return "Linux-Musl";
        }
        if (isAndroid()) {
            return "Linux-Android";
        }
        if (osName.contains("Linux")) {
            return "Linux";
        }
        return osName.replaceAll("\\W", "");
    }
    
    static String translateArchNameToFolderName(final String archName) {
        return archName.replaceAll("\\W", "");
    }
    
    static {
        OSInfo.processRunner = new ProcessRunner();
        (archMapping = new HashMap<String, String>()).put("x86", "x86");
        OSInfo.archMapping.put("i386", "x86");
        OSInfo.archMapping.put("i486", "x86");
        OSInfo.archMapping.put("i586", "x86");
        OSInfo.archMapping.put("i686", "x86");
        OSInfo.archMapping.put("pentium", "x86");
        OSInfo.archMapping.put("x86_64", "x86_64");
        OSInfo.archMapping.put("amd64", "x86_64");
        OSInfo.archMapping.put("em64t", "x86_64");
        OSInfo.archMapping.put("universal", "x86_64");
        OSInfo.archMapping.put("ia64", "ia64");
        OSInfo.archMapping.put("ia64w", "ia64");
        OSInfo.archMapping.put("ia64_32", "ia64_32");
        OSInfo.archMapping.put("ia64n", "ia64_32");
        OSInfo.archMapping.put("ppc", "ppc");
        OSInfo.archMapping.put("power", "ppc");
        OSInfo.archMapping.put("powerpc", "ppc");
        OSInfo.archMapping.put("power_pc", "ppc");
        OSInfo.archMapping.put("power_rs", "ppc");
        OSInfo.archMapping.put("ppc64", "ppc64");
        OSInfo.archMapping.put("power64", "ppc64");
        OSInfo.archMapping.put("powerpc64", "ppc64");
        OSInfo.archMapping.put("power_pc64", "ppc64");
        OSInfo.archMapping.put("power_rs64", "ppc64");
        OSInfo.archMapping.put("ppc64el", "ppc64");
        OSInfo.archMapping.put("ppc64le", "ppc64");
    }
}
