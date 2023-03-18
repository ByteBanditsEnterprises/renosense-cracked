//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import org.apache.commons.io.*;
import java.util.*;

public class NameFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = 8397947749814525798L;
    public static final Comparator<File> NAME_COMPARATOR;
    public static final Comparator<File> NAME_REVERSE;
    public static final Comparator<File> NAME_INSENSITIVE_COMPARATOR;
    public static final Comparator<File> NAME_INSENSITIVE_REVERSE;
    public static final Comparator<File> NAME_SYSTEM_COMPARATOR;
    public static final Comparator<File> NAME_SYSTEM_REVERSE;
    private final IOCase caseSensitivity;
    
    public NameFileComparator() {
        this.caseSensitivity = IOCase.SENSITIVE;
    }
    
    public NameFileComparator(final IOCase caseSensitivity) {
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public int compare(final File file1, final File file2) {
        return this.caseSensitivity.checkCompareTo(file1.getName(), file2.getName());
    }
    
    public String toString() {
        return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
    }
    
    static {
        NAME_COMPARATOR = (Comparator)new NameFileComparator();
        NAME_REVERSE = (Comparator)new ReverseFileComparator(NameFileComparator.NAME_COMPARATOR);
        NAME_INSENSITIVE_COMPARATOR = (Comparator)new NameFileComparator(IOCase.INSENSITIVE);
        NAME_INSENSITIVE_REVERSE = (Comparator)new ReverseFileComparator(NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
        NAME_SYSTEM_COMPARATOR = (Comparator)new NameFileComparator(IOCase.SYSTEM);
        NAME_SYSTEM_REVERSE = (Comparator)new ReverseFileComparator(NameFileComparator.NAME_SYSTEM_COMPARATOR);
    }
}
