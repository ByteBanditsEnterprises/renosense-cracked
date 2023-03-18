//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import org.apache.commons.io.*;
import java.util.*;

public class LastModifiedFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = 7372168004395734046L;
    public static final Comparator<File> LASTMODIFIED_COMPARATOR;
    public static final Comparator<File> LASTMODIFIED_REVERSE;
    
    public int compare(final File file1, final File file2) {
        final long result = FileUtils.lastModifiedUnchecked(file1) - FileUtils.lastModifiedUnchecked(file2);
        if (result < 0L) {
            return -1;
        }
        if (result > 0L) {
            return 1;
        }
        return 0;
    }
    
    static {
        LASTMODIFIED_COMPARATOR = (Comparator)new LastModifiedFileComparator();
        LASTMODIFIED_REVERSE = (Comparator)new ReverseFileComparator(LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
    }
}
