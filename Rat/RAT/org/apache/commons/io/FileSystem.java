//Raddon On Top!

package org.apache.commons.io;

import java.util.*;

public enum FileSystem
{
    GENERIC(false, false, Integer.MAX_VALUE, Integer.MAX_VALUE, new char[] { '\0' }, new String[0], false), 
    LINUX(true, true, 255, 4096, new char[] { '\0', '/' }, new String[0], false), 
    MAC_OSX(true, true, 255, 1024, new char[] { '\0', '/', ':' }, new String[0], false), 
    WINDOWS(false, true, 255, 32000, new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '\"', '*', '/', ':', '<', '>', '?', '\\', '|' }, new String[] { "AUX", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "CON", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", "NUL", "PRN" }, true);
    
    private static final boolean IS_OS_LINUX;
    private static final boolean IS_OS_MAC;
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    private static final boolean IS_OS_WINDOWS;
    private final boolean casePreserving;
    private final boolean caseSensitive;
    private final char[] illegalFileNameChars;
    private final int maxFileNameLength;
    private final int maxPathLength;
    private final String[] reservedFileNames;
    private final boolean supportsDriveLetter;
    
    public static FileSystem getCurrent() {
        if (FileSystem.IS_OS_LINUX) {
            return FileSystem.LINUX;
        }
        if (FileSystem.IS_OS_MAC) {
            return FileSystem.MAC_OSX;
        }
        if (FileSystem.IS_OS_WINDOWS) {
            return FileSystem.WINDOWS;
        }
        return FileSystem.GENERIC;
    }
    
    private static boolean getOsMatchesName(final String osNamePrefix) {
        return isOsNameMatch(getSystemProperty("os.name"), osNamePrefix);
    }
    
    private static String getSystemProperty(final String property) {
        try {
            return System.getProperty(property);
        }
        catch (SecurityException ex) {
            System.err.println("Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }
    
    private static boolean isOsNameMatch(final String osName, final String osNamePrefix) {
        return osName != null && osName.toUpperCase(Locale.ROOT).startsWith(osNamePrefix.toUpperCase(Locale.ROOT));
    }
    
    private FileSystem(final boolean caseSensitive, final boolean casePreserving, final int maxFileLength, final int maxPathLength, final char[] illegalFileNameChars, final String[] reservedFileNames, final boolean supportsDriveLetter) {
        this.maxFileNameLength = maxFileLength;
        this.maxPathLength = maxPathLength;
        this.illegalFileNameChars = Objects.requireNonNull(illegalFileNameChars, "illegalFileNameChars");
        this.reservedFileNames = Objects.requireNonNull(reservedFileNames, "reservedFileNames");
        this.caseSensitive = caseSensitive;
        this.casePreserving = casePreserving;
        this.supportsDriveLetter = supportsDriveLetter;
    }
    
    public char[] getIllegalFileNameChars() {
        return this.illegalFileNameChars.clone();
    }
    
    public int getMaxFileNameLength() {
        return this.maxFileNameLength;
    }
    
    public int getMaxPathLength() {
        return this.maxPathLength;
    }
    
    public String[] getReservedFileNames() {
        return this.reservedFileNames.clone();
    }
    
    public boolean isCasePreserving() {
        return this.casePreserving;
    }
    
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }
    
    private boolean isIllegalFileNameChar(final char c) {
        return Arrays.binarySearch(this.illegalFileNameChars, c) >= 0;
    }
    
    public boolean isLegalFileName(final CharSequence candidate) {
        if (candidate == null || candidate.length() == 0 || candidate.length() > this.maxFileNameLength) {
            return false;
        }
        if (this.isReservedFileName(candidate)) {
            return false;
        }
        for (int i = 0; i < candidate.length(); ++i) {
            if (this.isIllegalFileNameChar(candidate.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isReservedFileName(final CharSequence candidate) {
        return Arrays.binarySearch(this.reservedFileNames, candidate) >= 0;
    }
    
    public boolean supportsDriveLetter() {
        return this.supportsDriveLetter;
    }
    
    public String toLegalFileName(final String candidate, final char replacement) {
        if (this.isIllegalFileNameChar(replacement)) {
            throw new IllegalArgumentException(String.format("The replacement character '%s' cannot be one of the %s illegal characters: %s", (replacement == '\0') ? "\\0" : Character.valueOf(replacement), this.name(), Arrays.toString(this.illegalFileNameChars)));
        }
        final String truncated = (candidate.length() > this.maxFileNameLength) ? candidate.substring(0, this.maxFileNameLength) : candidate;
        boolean changed = false;
        final char[] charArray = truncated.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            if (this.isIllegalFileNameChar(charArray[i])) {
                charArray[i] = replacement;
                changed = true;
            }
        }
        return changed ? String.valueOf(charArray) : truncated;
    }
    
    static {
        IS_OS_LINUX = getOsMatchesName("Linux");
        IS_OS_MAC = getOsMatchesName("Mac");
        IS_OS_WINDOWS = getOsMatchesName("Windows");
    }
}
