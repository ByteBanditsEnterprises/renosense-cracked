//Raddon On Top!

package org.apache.commons.lang3.compare;

import java.util.*;
import java.io.*;

public final class ObjectToStringComparator implements Comparator<Object>, Serializable
{
    public static final ObjectToStringComparator INSTANCE;
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(final Object o1, final Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        final String string1 = o1.toString();
        final String string2 = o2.toString();
        if (string1 == null && string2 == null) {
            return 0;
        }
        if (string1 == null) {
            return 1;
        }
        if (string2 == null) {
            return -1;
        }
        return string1.compareTo(string2);
    }
    
    static {
        INSTANCE = new ObjectToStringComparator();
    }
}
