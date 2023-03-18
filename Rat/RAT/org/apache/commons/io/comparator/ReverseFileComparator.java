//Raddon On Top!

package org.apache.commons.io.comparator;

import java.util.*;
import java.io.*;

class ReverseFileComparator extends AbstractFileComparator implements Serializable
{
    private static final long serialVersionUID = -4808255005272229056L;
    private final Comparator<File> delegate;
    
    public ReverseFileComparator(final Comparator<File> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate comparator is missing");
        }
        this.delegate = delegate;
    }
    
    public int compare(final File file1, final File file2) {
        return this.delegate.compare(file2, file1);
    }
    
    public String toString() {
        return super.toString() + "[" + this.delegate.toString() + "]";
    }
}
