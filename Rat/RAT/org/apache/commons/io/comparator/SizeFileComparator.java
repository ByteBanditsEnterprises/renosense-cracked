//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import org.apache.commons.io.*;
import java.util.*;

public class SizeFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = -1201561106411416190L;
    public static final Comparator<File> SIZE_COMPARATOR;
    public static final Comparator<File> SIZE_REVERSE;
    public static final Comparator<File> SIZE_SUMDIR_COMPARATOR;
    public static final Comparator<File> SIZE_SUMDIR_REVERSE;
    private final boolean sumDirectoryContents;
    
    public SizeFileComparator() {
        this.sumDirectoryContents = false;
    }
    
    public SizeFileComparator(final boolean sumDirectoryContents) {
        this.sumDirectoryContents = sumDirectoryContents;
    }
    
    public int compare(final File file1, final File file2) {
        long size1;
        if (file1.isDirectory()) {
            size1 = ((this.sumDirectoryContents && file1.exists()) ? FileUtils.sizeOfDirectory(file1) : 0L);
        }
        else {
            size1 = file1.length();
        }
        long size2;
        if (file2.isDirectory()) {
            size2 = ((this.sumDirectoryContents && file2.exists()) ? FileUtils.sizeOfDirectory(file2) : 0L);
        }
        else {
            size2 = file2.length();
        }
        final long result = size1 - size2;
        if (result < 0L) {
            return -1;
        }
        if (result > 0L) {
            return 1;
        }
        return 0;
    }
    
    public String toString() {
        return super.toString() + "[sumDirectoryContents=" + this.sumDirectoryContents + "]";
    }
    
    static {
        SIZE_COMPARATOR = (Comparator)new SizeFileComparator();
        SIZE_REVERSE = (Comparator)new ReverseFileComparator((Comparator)SizeFileComparator.SIZE_COMPARATOR);
        SIZE_SUMDIR_COMPARATOR = (Comparator)new SizeFileComparator(true);
        SIZE_SUMDIR_REVERSE = (Comparator)new ReverseFileComparator((Comparator)SizeFileComparator.SIZE_SUMDIR_COMPARATOR);
    }
}
