//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import java.util.*;

public class DefaultFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = 3260141861365313518L;
    public static final Comparator<File> DEFAULT_COMPARATOR;
    public static final Comparator<File> DEFAULT_REVERSE;
    
    public int compare(final File file1, final File file2) {
        return file1.compareTo(file2);
    }
    
    static {
        DEFAULT_COMPARATOR = (Comparator)new DefaultFileComparator();
        DEFAULT_REVERSE = (Comparator)new ReverseFileComparator(DefaultFileComparator.DEFAULT_COMPARATOR);
    }
}
