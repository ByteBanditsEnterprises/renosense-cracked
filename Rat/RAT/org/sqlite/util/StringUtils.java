//Raddon On Top!

package org.sqlite.util;

import java.util.*;

public class StringUtils
{
    public static String join(final List<String> list, final String separator) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String item : list) {
            if (first) {
                first = false;
            }
            else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
