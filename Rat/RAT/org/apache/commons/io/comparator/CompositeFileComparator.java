//Raddon On Top!

package org.apache.commons.io.comparator;

import java.io.*;
import java.util.*;

public class CompositeFileComparator extends AbstractFileComparator implements Serializable
{
    private static final Comparator<?>[] EMPTY_COMPARATOR_ARRAY;
    private static final long serialVersionUID = -2224170307287243428L;
    private static final Comparator<?>[] NO_COMPARATORS;
    private final Comparator<File>[] delegates;
    
    public CompositeFileComparator(final Comparator<File>... delegates) {
        if (delegates == null) {
            this.delegates = (Comparator<File>[])CompositeFileComparator.NO_COMPARATORS;
        }
        else {
            System.arraycopy(delegates, 0, this.delegates = (Comparator<File>[])new Comparator[delegates.length], 0, delegates.length);
        }
    }
    
    public CompositeFileComparator(final Iterable<Comparator<File>> delegates) {
        if (delegates == null) {
            this.delegates = (Comparator<File>[])CompositeFileComparator.NO_COMPARATORS;
        }
        else {
            final List<Comparator<File>> list = new ArrayList<Comparator<File>>();
            for (final Comparator<File> comparator : delegates) {
                list.add(comparator);
            }
            this.delegates = list.toArray(CompositeFileComparator.EMPTY_COMPARATOR_ARRAY);
        }
    }
    
    public int compare(final File file1, final File file2) {
        int result = 0;
        for (final Comparator<File> delegate : this.delegates) {
            result = delegate.compare(file1, file2);
            if (result != 0) {
                break;
            }
        }
        return result;
    }
    
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append('{');
        for (int i = 0; i < this.delegates.length; ++i) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(this.delegates[i]);
        }
        builder.append('}');
        return builder.toString();
    }
    
    static {
        EMPTY_COMPARATOR_ARRAY = new Comparator[0];
        NO_COMPARATORS = new Comparator[0];
    }
}
