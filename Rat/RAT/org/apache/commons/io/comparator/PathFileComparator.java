//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import org.apache.commons.io.*;
import java.util.*;

public class PathFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = 6527501707585768673L;
    public static final Comparator<File> PATH_COMPARATOR;
    public static final Comparator<File> PATH_REVERSE;
    public static final Comparator<File> PATH_INSENSITIVE_COMPARATOR;
    public static final Comparator<File> PATH_INSENSITIVE_REVERSE;
    public static final Comparator<File> PATH_SYSTEM_COMPARATOR;
    public static final Comparator<File> PATH_SYSTEM_REVERSE;
    private final IOCase caseSensitivity;
    
    public PathFileComparator() {
        this.caseSensitivity = IOCase.SENSITIVE;
    }
    
    public PathFileComparator(final IOCase caseSensitivity) {
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public int compare(final File file1, final File file2) {
        return this.caseSensitivity.checkCompareTo(file1.getPath(), file2.getPath());
    }
    
    public String toString() {
        return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
    }
    
    static {
        PATH_COMPARATOR = (Comparator)new PathFileComparator();
        PATH_REVERSE = (Comparator)new ReverseFileComparator(PathFileComparator.PATH_COMPARATOR);
        PATH_INSENSITIVE_COMPARATOR = (Comparator)new PathFileComparator(IOCase.INSENSITIVE);
        PATH_INSENSITIVE_REVERSE = (Comparator)new ReverseFileComparator(PathFileComparator.PATH_INSENSITIVE_COMPARATOR);
        PATH_SYSTEM_COMPARATOR = (Comparator)new PathFileComparator(IOCase.SYSTEM);
        PATH_SYSTEM_REVERSE = (Comparator)new ReverseFileComparator(PathFileComparator.PATH_SYSTEM_COMPARATOR);
    }
}
