//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import org.apache.commons.io.*;
import java.util.*;

public class ExtensionFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = 1928235200184222815L;
    public static final Comparator<File> EXTENSION_COMPARATOR;
    public static final Comparator<File> EXTENSION_REVERSE;
    public static final Comparator<File> EXTENSION_INSENSITIVE_COMPARATOR;
    public static final Comparator<File> EXTENSION_INSENSITIVE_REVERSE;
    public static final Comparator<File> EXTENSION_SYSTEM_COMPARATOR;
    public static final Comparator<File> EXTENSION_SYSTEM_REVERSE;
    private final IOCase caseSensitivity;
    
    public ExtensionFileComparator() {
        this.caseSensitivity = IOCase.SENSITIVE;
    }
    
    public ExtensionFileComparator(final IOCase caseSensitivity) {
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public int compare(final File file1, final File file2) {
        final String suffix1 = FilenameUtils.getExtension(file1.getName());
        final String suffix2 = FilenameUtils.getExtension(file2.getName());
        return this.caseSensitivity.checkCompareTo(suffix1, suffix2);
    }
    
    public String toString() {
        return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
    }
    
    static {
        EXTENSION_COMPARATOR = (Comparator)new ExtensionFileComparator();
        EXTENSION_REVERSE = (Comparator)new ReverseFileComparator(ExtensionFileComparator.EXTENSION_COMPARATOR);
        EXTENSION_INSENSITIVE_COMPARATOR = (Comparator)new ExtensionFileComparator(IOCase.INSENSITIVE);
        EXTENSION_INSENSITIVE_REVERSE = (Comparator)new ReverseFileComparator(ExtensionFileComparator.EXTENSION_INSENSITIVE_COMPARATOR);
        EXTENSION_SYSTEM_COMPARATOR = (Comparator)new ExtensionFileComparator(IOCase.SYSTEM);
        EXTENSION_SYSTEM_REVERSE = (Comparator)new ReverseFileComparator(ExtensionFileComparator.EXTENSION_SYSTEM_COMPARATOR);
    }
}
