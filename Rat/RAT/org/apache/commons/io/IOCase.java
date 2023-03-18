//Raddon On Top!

package org.apache.commons.io;

import java.util.*;

public enum IOCase
{
    SENSITIVE("Sensitive", true), 
    INSENSITIVE("Insensitive", false), 
    SYSTEM("System", !FilenameUtils.isSystemWindows());
    
    private static final long serialVersionUID = -6343169151696340687L;
    private final String name;
    private final transient boolean sensitive;
    
    public static boolean isCaseSensitive(final IOCase caseSensitivity) {
        return caseSensitivity != null && !caseSensitivity.isCaseSensitive();
    }
    
    public static IOCase forName(final String name) {
        for (final IOCase ioCase : values()) {
            if (ioCase.getName().equals(name)) {
                return ioCase;
            }
        }
        throw new IllegalArgumentException("Invalid IOCase name: " + name);
    }
    
    private IOCase(final String name, final boolean sensitive) {
        this.name = name;
        this.sensitive = sensitive;
    }
    
    private Object readResolve() {
        return forName(this.name);
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isCaseSensitive() {
        return this.sensitive;
    }
    
    public int checkCompareTo(final String str1, final String str2) {
        Objects.requireNonNull(str1, "str1");
        Objects.requireNonNull(str2, "str2");
        return this.sensitive ? str1.compareTo(str2) : str1.compareToIgnoreCase(str2);
    }
    
    public boolean checkEquals(final String str1, final String str2) {
        Objects.requireNonNull(str1, "str1");
        Objects.requireNonNull(str2, "str2");
        return this.sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
    }
    
    public boolean checkStartsWith(final String str, final String start) {
        return str != null && start != null && str.regionMatches(!this.sensitive, 0, start, 0, start.length());
    }
    
    public boolean checkEndsWith(final String str, final String end) {
        if (str == null || end == null) {
            return false;
        }
        final int endLen = end.length();
        return str.regionMatches(!this.sensitive, str.length() - endLen, end, 0, endLen);
    }
    
    public int checkIndexOf(final String str, final int strStartIndex, final String search) {
        final int endIndex = str.length() - search.length();
        if (endIndex >= strStartIndex) {
            for (int i = strStartIndex; i <= endIndex; ++i) {
                if (this.checkRegionMatches(str, i, search)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public boolean checkRegionMatches(final String str, final int strStartIndex, final String search) {
        return str.regionMatches(!this.sensitive, strStartIndex, search, 0, search.length());
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
