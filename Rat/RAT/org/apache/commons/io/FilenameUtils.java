//Raddon On Top!

package org.apache.commons.io;

import java.util.regex.*;
import java.util.*;
import java.io.*;

public class FilenameUtils
{
    private static final String[] EMPTY_STRING_ARRAY;
    private static final String EMPTY_STRING = "";
    private static final int NOT_FOUND = -1;
    public static final char EXTENSION_SEPARATOR = '.';
    public static final String EXTENSION_SEPARATOR_STR;
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    private static final char SYSTEM_SEPARATOR;
    private static final char OTHER_SEPARATOR;
    private static final Pattern IPV4_PATTERN;
    private static final int IPV4_MAX_OCTET_VALUE = 255;
    private static final int IPV6_MAX_HEX_GROUPS = 8;
    private static final int IPV6_MAX_HEX_DIGITS_PER_GROUP = 4;
    private static final int MAX_UNSIGNED_SHORT = 65535;
    private static final int BASE_16 = 16;
    private static final Pattern REG_NAME_PART_PATTERN;
    
    static boolean isSystemWindows() {
        return FilenameUtils.SYSTEM_SEPARATOR == '\\';
    }
    
    private static boolean isSeparator(final char ch) {
        return ch == '/' || ch == '\\';
    }
    
    public static String normalize(final String fileName) {
        return doNormalize(fileName, FilenameUtils.SYSTEM_SEPARATOR, true);
    }
    
    public static String normalize(final String fileName, final boolean unixSeparator) {
        final char separator = unixSeparator ? '/' : '\\';
        return doNormalize(fileName, separator, true);
    }
    
    public static String normalizeNoEndSeparator(final String fileName) {
        return doNormalize(fileName, FilenameUtils.SYSTEM_SEPARATOR, false);
    }
    
    public static String normalizeNoEndSeparator(final String fileName, final boolean unixSeparator) {
        final char separator = unixSeparator ? '/' : '\\';
        return doNormalize(fileName, separator, false);
    }
    
    private static String doNormalize(final String fileName, final char separator, final boolean keepSeparator) {
        if (fileName == null) {
            return null;
        }
        requireNonNullChars(fileName);
        int size = fileName.length();
        if (size == 0) {
            return fileName;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }
        final char[] array = new char[size + 2];
        fileName.getChars(0, fileName.length(), array, 0);
        final char otherSeparator = (separator == FilenameUtils.SYSTEM_SEPARATOR) ? FilenameUtils.OTHER_SEPARATOR : FilenameUtils.SYSTEM_SEPARATOR;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == otherSeparator) {
                array[i] = separator;
            }
        }
        boolean lastIsDirectory = true;
        if (array[size - 1] != separator) {
            array[size++] = separator;
            lastIsDirectory = false;
        }
        for (int j = (prefix != 0) ? prefix : 1; j < size; ++j) {
            if (array[j] == separator && array[j - 1] == separator) {
                System.arraycopy(array, j, array, j - 1, size - j);
                --size;
                --j;
            }
        }
        for (int j = prefix + 1; j < size; ++j) {
            if (array[j] == separator && array[j - 1] == '.' && (j == prefix + 1 || array[j - 2] == separator)) {
                if (j == size - 1) {
                    lastIsDirectory = true;
                }
                System.arraycopy(array, j + 1, array, j - 1, size - j);
                size -= 2;
                --j;
            }
        }
    Label_0475:
        for (int j = prefix + 2; j < size; ++j) {
            if (array[j] == separator && array[j - 1] == '.' && array[j - 2] == '.' && (j == prefix + 2 || array[j - 3] == separator)) {
                if (j == prefix + 2) {
                    return null;
                }
                if (j == size - 1) {
                    lastIsDirectory = true;
                }
                for (int k = j - 4; k >= prefix; --k) {
                    if (array[k] == separator) {
                        System.arraycopy(array, j + 1, array, k + 1, size - j);
                        size -= j - k;
                        j = k + 1;
                        continue Label_0475;
                    }
                }
                System.arraycopy(array, j + 1, array, prefix, size - j);
                size -= j + 1 - prefix;
                j = prefix + 1;
            }
        }
        if (size <= 0) {
            return "";
        }
        if (size <= prefix) {
            return new String(array, 0, size);
        }
        if (lastIsDirectory && keepSeparator) {
            return new String(array, 0, size);
        }
        return new String(array, 0, size - 1);
    }
    
    public static String concat(final String basePath, final String fullFileNameToAdd) {
        final int prefix = getPrefixLength(fullFileNameToAdd);
        if (prefix < 0) {
            return null;
        }
        if (prefix > 0) {
            return normalize(fullFileNameToAdd);
        }
        if (basePath == null) {
            return null;
        }
        final int len = basePath.length();
        if (len == 0) {
            return normalize(fullFileNameToAdd);
        }
        final char ch = basePath.charAt(len - 1);
        if (isSeparator(ch)) {
            return normalize(basePath + fullFileNameToAdd);
        }
        return normalize(basePath + '/' + fullFileNameToAdd);
    }
    
    public static boolean directoryContains(final String canonicalParent, final String canonicalChild) {
        Objects.requireNonNull(canonicalParent, "canonicalParent");
        return canonicalChild != null && !IOCase.SYSTEM.checkEquals(canonicalParent, canonicalChild) && IOCase.SYSTEM.checkStartsWith(canonicalChild, canonicalParent);
    }
    
    public static String separatorsToUnix(final String path) {
        if (path == null || path.indexOf(92) == -1) {
            return path;
        }
        return path.replace('\\', '/');
    }
    
    public static String separatorsToWindows(final String path) {
        if (path == null || path.indexOf(47) == -1) {
            return path;
        }
        return path.replace('/', '\\');
    }
    
    public static String separatorsToSystem(final String path) {
        if (path == null) {
            return null;
        }
        return isSystemWindows() ? separatorsToWindows(path) : separatorsToUnix(path);
    }
    
    public static int getPrefixLength(final String fileName) {
        if (fileName == null) {
            return -1;
        }
        final int len = fileName.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = fileName.charAt(0);
        if (ch0 == ':') {
            return -1;
        }
        if (len == 1) {
            if (ch0 == '~') {
                return 2;
            }
            return isSeparator(ch0) ? 1 : 0;
        }
        else if (ch0 == '~') {
            int posUnix = fileName.indexOf(47, 1);
            int posWin = fileName.indexOf(92, 1);
            if (posUnix == -1 && posWin == -1) {
                return len + 1;
            }
            posUnix = ((posUnix == -1) ? posWin : posUnix);
            posWin = ((posWin == -1) ? posUnix : posWin);
            return Math.min(posUnix, posWin) + 1;
        }
        else {
            final char ch2 = fileName.charAt(1);
            if (ch2 == ':') {
                ch0 = Character.toUpperCase(ch0);
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    if (len == 2 && !FileSystem.getCurrent().supportsDriveLetter()) {
                        return 0;
                    }
                    if (len == 2 || !isSeparator(fileName.charAt(2))) {
                        return 2;
                    }
                    return 3;
                }
                else {
                    if (ch0 == '/') {
                        return 1;
                    }
                    return -1;
                }
            }
            else {
                if (!isSeparator(ch0) || !isSeparator(ch2)) {
                    return isSeparator(ch0) ? 1 : 0;
                }
                int posUnix2 = fileName.indexOf(47, 2);
                int posWin2 = fileName.indexOf(92, 2);
                if ((posUnix2 == -1 && posWin2 == -1) || posUnix2 == 2 || posWin2 == 2) {
                    return -1;
                }
                posUnix2 = ((posUnix2 == -1) ? posWin2 : posUnix2);
                posWin2 = ((posWin2 == -1) ? posUnix2 : posWin2);
                final int pos = Math.min(posUnix2, posWin2) + 1;
                final String hostnamePart = fileName.substring(2, pos - 1);
                return isValidHostName(hostnamePart) ? pos : -1;
            }
        }
    }
    
    public static int indexOfLastSeparator(final String fileName) {
        if (fileName == null) {
            return -1;
        }
        final int lastUnixPos = fileName.lastIndexOf(47);
        final int lastWindowsPos = fileName.lastIndexOf(92);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
    
    public static int indexOfExtension(final String fileName) throws IllegalArgumentException {
        if (fileName == null) {
            return -1;
        }
        if (isSystemWindows()) {
            final int offset = fileName.indexOf(58, getAdsCriticalOffset(fileName));
            if (offset != -1) {
                throw new IllegalArgumentException("NTFS ADS separator (':') in file name is forbidden.");
            }
        }
        final int extensionPos = fileName.lastIndexOf(46);
        final int lastSeparator = indexOfLastSeparator(fileName);
        return (lastSeparator > extensionPos) ? -1 : extensionPos;
    }
    
    public static String getPrefix(final String fileName) {
        if (fileName == null) {
            return null;
        }
        final int len = getPrefixLength(fileName);
        if (len < 0) {
            return null;
        }
        if (len > fileName.length()) {
            requireNonNullChars(fileName + '/');
            return fileName + '/';
        }
        final String path = fileName.substring(0, len);
        requireNonNullChars(path);
        return path;
    }
    
    public static String getPath(final String fileName) {
        return doGetPath(fileName, 1);
    }
    
    public static String getPathNoEndSeparator(final String fileName) {
        return doGetPath(fileName, 0);
    }
    
    private static String doGetPath(final String fileName, final int separatorAdd) {
        if (fileName == null) {
            return null;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }
        final int index = indexOfLastSeparator(fileName);
        final int endIndex = index + separatorAdd;
        if (prefix >= fileName.length() || index < 0 || prefix >= endIndex) {
            return "";
        }
        final String path = fileName.substring(prefix, endIndex);
        requireNonNullChars(path);
        return path;
    }
    
    public static String getFullPath(final String fileName) {
        return doGetFullPath(fileName, true);
    }
    
    public static String getFullPathNoEndSeparator(final String fileName) {
        return doGetFullPath(fileName, false);
    }
    
    private static String doGetFullPath(final String fileName, final boolean includeSeparator) {
        if (fileName == null) {
            return null;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }
        if (prefix >= fileName.length()) {
            if (includeSeparator) {
                return getPrefix(fileName);
            }
            return fileName;
        }
        else {
            final int index = indexOfLastSeparator(fileName);
            if (index < 0) {
                return fileName.substring(0, prefix);
            }
            int end = index + (includeSeparator ? 1 : 0);
            if (end == 0) {
                ++end;
            }
            return fileName.substring(0, end);
        }
    }
    
    public static String getName(final String fileName) {
        if (fileName == null) {
            return null;
        }
        requireNonNullChars(fileName);
        final int index = indexOfLastSeparator(fileName);
        return fileName.substring(index + 1);
    }
    
    private static void requireNonNullChars(final String path) {
        if (path.indexOf(0) >= 0) {
            throw new IllegalArgumentException("Null byte present in file/path name. There are no known legitimate use cases for such data, but several injection attacks may use it");
        }
    }
    
    public static String getBaseName(final String fileName) {
        return removeExtension(getName(fileName));
    }
    
    public static String getExtension(final String fileName) throws IllegalArgumentException {
        if (fileName == null) {
            return null;
        }
        final int index = indexOfExtension(fileName);
        if (index == -1) {
            return "";
        }
        return fileName.substring(index + 1);
    }
    
    private static int getAdsCriticalOffset(final String fileName) {
        final int offset1 = fileName.lastIndexOf(FilenameUtils.SYSTEM_SEPARATOR);
        final int offset2 = fileName.lastIndexOf(FilenameUtils.OTHER_SEPARATOR);
        if (offset1 == -1) {
            if (offset2 == -1) {
                return 0;
            }
            return offset2 + 1;
        }
        else {
            if (offset2 == -1) {
                return offset1 + 1;
            }
            return Math.max(offset1, offset2) + 1;
        }
    }
    
    public static String removeExtension(final String fileName) {
        if (fileName == null) {
            return null;
        }
        requireNonNullChars(fileName);
        final int index = indexOfExtension(fileName);
        if (index == -1) {
            return fileName;
        }
        return fileName.substring(0, index);
    }
    
    public static boolean equals(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, false, IOCase.SENSITIVE);
    }
    
    public static boolean equalsOnSystem(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, false, IOCase.SYSTEM);
    }
    
    public static boolean equalsNormalized(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, true, IOCase.SENSITIVE);
    }
    
    public static boolean equalsNormalizedOnSystem(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, true, IOCase.SYSTEM);
    }
    
    public static boolean equals(String fileName1, String fileName2, final boolean normalized, IOCase caseSensitivity) {
        if (fileName1 == null || fileName2 == null) {
            return fileName1 == null && fileName2 == null;
        }
        if (normalized) {
            fileName1 = normalize(fileName1);
            if (fileName1 == null) {
                return false;
            }
            fileName2 = normalize(fileName2);
            if (fileName2 == null) {
                return false;
            }
        }
        if (caseSensitivity == null) {
            caseSensitivity = IOCase.SENSITIVE;
        }
        return caseSensitivity.checkEquals(fileName1, fileName2);
    }
    
    public static boolean isExtension(final String fileName, final String extension) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);
        if (extension == null || extension.isEmpty()) {
            return indexOfExtension(fileName) == -1;
        }
        final String fileExt = getExtension(fileName);
        return fileExt.equals(extension);
    }
    
    public static boolean isExtension(final String fileName, final String... extensions) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);
        if (extensions == null || extensions.length == 0) {
            return indexOfExtension(fileName) == -1;
        }
        final String fileExt = getExtension(fileName);
        for (final String extension : extensions) {
            if (fileExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isExtension(final String fileName, final Collection<String> extensions) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);
        if (extensions == null || extensions.isEmpty()) {
            return indexOfExtension(fileName) == -1;
        }
        final String fileExt = getExtension(fileName);
        for (final String extension : extensions) {
            if (fileExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean wildcardMatch(final String fileName, final String wildcardMatcher) {
        return wildcardMatch(fileName, wildcardMatcher, IOCase.SENSITIVE);
    }
    
    public static boolean wildcardMatchOnSystem(final String fileName, final String wildcardMatcher) {
        return wildcardMatch(fileName, wildcardMatcher, IOCase.SYSTEM);
    }
    
    public static boolean wildcardMatch(final String fileName, final String wildcardMatcher, IOCase caseSensitivity) {
        if (fileName == null && wildcardMatcher == null) {
            return true;
        }
        if (fileName == null || wildcardMatcher == null) {
            return false;
        }
        if (caseSensitivity == null) {
            caseSensitivity = IOCase.SENSITIVE;
        }
        final String[] wcs = splitOnTokens(wildcardMatcher);
        boolean anyChars = false;
        int textIdx = 0;
        int wcsIdx = 0;
        final Deque<int[]> backtrack = new ArrayDeque<int[]>(wcs.length);
        do {
            if (!backtrack.isEmpty()) {
                final int[] array = backtrack.pop();
                wcsIdx = array[0];
                textIdx = array[1];
                anyChars = true;
            }
            while (wcsIdx < wcs.length) {
                if (wcs[wcsIdx].equals("?")) {
                    if (++textIdx > fileName.length()) {
                        break;
                    }
                    anyChars = false;
                }
                else if (wcs[wcsIdx].equals("*")) {
                    anyChars = true;
                    if (wcsIdx == wcs.length - 1) {
                        textIdx = fileName.length();
                    }
                }
                else {
                    if (anyChars) {
                        textIdx = caseSensitivity.checkIndexOf(fileName, textIdx, wcs[wcsIdx]);
                        if (textIdx == -1) {
                            break;
                        }
                        final int repeat = caseSensitivity.checkIndexOf(fileName, textIdx + 1, wcs[wcsIdx]);
                        if (repeat >= 0) {
                            backtrack.push(new int[] { wcsIdx, repeat });
                        }
                    }
                    else if (!caseSensitivity.checkRegionMatches(fileName, textIdx, wcs[wcsIdx])) {
                        break;
                    }
                    textIdx += wcs[wcsIdx].length();
                    anyChars = false;
                }
                ++wcsIdx;
            }
            if (wcsIdx == wcs.length && textIdx == fileName.length()) {
                return true;
            }
        } while (!backtrack.isEmpty());
        return false;
    }
    
    static String[] splitOnTokens(final String text) {
        if (text.indexOf(63) == -1 && text.indexOf(42) == -1) {
            return new String[] { text };
        }
        final char[] array = text.toCharArray();
        final ArrayList<String> list = new ArrayList<String>();
        final StringBuilder buffer = new StringBuilder();
        char prevChar = '\0';
        for (final char ch : array) {
            if (ch == '?' || ch == '*') {
                if (buffer.length() != 0) {
                    list.add(buffer.toString());
                    buffer.setLength(0);
                }
                if (ch == '?') {
                    list.add("?");
                }
                else if (prevChar != '*') {
                    list.add("*");
                }
            }
            else {
                buffer.append(ch);
            }
            prevChar = ch;
        }
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }
        return list.toArray(FilenameUtils.EMPTY_STRING_ARRAY);
    }
    
    private static boolean isValidHostName(final String name) {
        return isIPv6Address(name) || isRFC3986HostName(name);
    }
    
    private static boolean isIPv4Address(final String name) {
        final Matcher m = FilenameUtils.IPV4_PATTERN.matcher(name);
        if (!m.matches() || m.groupCount() != 4) {
            return false;
        }
        for (int i = 1; i <= 4; ++i) {
            final String ipSegment = m.group(i);
            final int iIpSegment = Integer.parseInt(ipSegment);
            if (iIpSegment > 255) {
                return false;
            }
            if (ipSegment.length() > 1 && ipSegment.startsWith("0")) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isIPv6Address(final String inet6Address) {
        final boolean containsCompressedZeroes = inet6Address.contains("::");
        if (containsCompressedZeroes && inet6Address.indexOf("::") != inet6Address.lastIndexOf("::")) {
            return false;
        }
        if ((inet6Address.startsWith(":") && !inet6Address.startsWith("::")) || (inet6Address.endsWith(":") && !inet6Address.endsWith("::"))) {
            return false;
        }
        String[] octets = inet6Address.split(":");
        if (containsCompressedZeroes) {
            final List<String> octetList = new ArrayList<String>(Arrays.asList(octets));
            if (inet6Address.endsWith("::")) {
                octetList.add("");
            }
            else if (inet6Address.startsWith("::") && !octetList.isEmpty()) {
                octetList.remove(0);
            }
            octets = octetList.toArray(FilenameUtils.EMPTY_STRING_ARRAY);
        }
        if (octets.length > 8) {
            return false;
        }
        int validOctets = 0;
        int emptyOctets = 0;
        for (int index = 0; index < octets.length; ++index) {
            final String octet = octets[index];
            if (octet.isEmpty()) {
                if (++emptyOctets > 1) {
                    return false;
                }
            }
            else {
                emptyOctets = 0;
                if (index == octets.length - 1 && octet.contains(".")) {
                    if (!isIPv4Address(octet)) {
                        return false;
                    }
                    validOctets += 2;
                    continue;
                }
                else {
                    if (octet.length() > 4) {
                        return false;
                    }
                    int octetInt;
                    try {
                        octetInt = Integer.parseInt(octet, 16);
                    }
                    catch (NumberFormatException e) {
                        return false;
                    }
                    if (octetInt < 0 || octetInt > 65535) {
                        return false;
                    }
                }
            }
            ++validOctets;
        }
        return validOctets <= 8 && (validOctets >= 8 || containsCompressedZeroes);
    }
    
    private static boolean isRFC3986HostName(final String name) {
        final String[] parts = name.split("\\.", -1);
        for (int i = 0; i < parts.length; ++i) {
            if (parts[i].isEmpty()) {
                return i == parts.length - 1;
            }
            if (!FilenameUtils.REG_NAME_PART_PATTERN.matcher(parts[i]).matches()) {
                return false;
            }
        }
        return true;
    }
    
    static {
        EMPTY_STRING_ARRAY = new String[0];
        EXTENSION_SEPARATOR_STR = Character.toString('.');
        SYSTEM_SEPARATOR = File.separatorChar;
        if (isSystemWindows()) {
            OTHER_SEPARATOR = '/';
        }
        else {
            OTHER_SEPARATOR = '\\';
        }
        IPV4_PATTERN = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
        REG_NAME_PART_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9-]*$");
    }
}
