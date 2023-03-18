//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import java.util.*;

public class DirectoryFileComparator extends AbstractFileComparator implements Serializable
{
    private static final int TYPE_FILE = 2;
    private static final int TYPE_DIRECTORY = 1;
    private static final long serialVersionUID = 296132640160964395L;
    public static final Comparator<File> DIRECTORY_COMPARATOR;
    public static final Comparator<File> DIRECTORY_REVERSE;
    
    public int compare(final File file1, final File file2) {
        return this.getType(file1) - this.getType(file2);
    }
    
    private int getType(final File file) {
        return file.isDirectory() ? 1 : 2;
    }
    
    static {
        DIRECTORY_COMPARATOR = (Comparator)new DirectoryFileComparator();
        DIRECTORY_REVERSE = (Comparator)new ReverseFileComparator(DirectoryFileComparator.DIRECTORY_COMPARATOR);
    }
}
