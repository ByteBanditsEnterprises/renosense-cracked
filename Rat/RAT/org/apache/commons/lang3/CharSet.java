//Raddon On Top!

package org.apache.commons.lang3;

import java.io.*;
import java.util.*;

public class CharSet implements Serializable
{
    private static final long serialVersionUID = 5947847346149275958L;
    public static final CharSet EMPTY;
    public static final CharSet ASCII_ALPHA;
    public static final CharSet ASCII_ALPHA_LOWER;
    public static final CharSet ASCII_ALPHA_UPPER;
    public static final CharSet ASCII_NUMERIC;
    protected static final Map<String, CharSet> COMMON;
    private final Set<CharRange> set;
    
    public static CharSet getInstance(final String... setStrs) {
        if (setStrs == null) {
            return null;
        }
        if (setStrs.length == 1) {
            final CharSet common = CharSet.COMMON.get(setStrs[0]);
            if (common != null) {
                return common;
            }
        }
        return new CharSet(setStrs);
    }
    
    protected CharSet(final String... set) {
        this.set = Collections.synchronizedSet(new HashSet<CharRange>());
        for (final String s : set) {
            this.add(s);
        }
    }
    
    protected void add(final String str) {
        if (str == null) {
            return;
        }
        final int len = str.length();
        int pos = 0;
        while (pos < len) {
            final int remainder = len - pos;
            if (remainder >= 4 && str.charAt(pos) == '^' && str.charAt(pos + 2) == '-') {
                this.set.add(CharRange.isNotIn(str.charAt(pos + 1), str.charAt(pos + 3)));
                pos += 4;
            }
            else if (remainder >= 3 && str.charAt(pos + 1) == '-') {
                this.set.add(CharRange.isIn(str.charAt(pos), str.charAt(pos + 2)));
                pos += 3;
            }
            else if (remainder >= 2 && str.charAt(pos) == '^') {
                this.set.add(CharRange.isNot(str.charAt(pos + 1)));
                pos += 2;
            }
            else {
                this.set.add(CharRange.is(str.charAt(pos)));
                ++pos;
            }
        }
    }
    
    CharRange[] getCharRanges() {
        return this.set.toArray(CharRange.EMPTY_ARRAY);
    }
    
    public boolean contains(final char ch) {
        synchronized (this.set) {
            for (final CharRange range : this.set) {
                if (range.contains(ch)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CharSet)) {
            return false;
        }
        final CharSet other = (CharSet)obj;
        return this.set.equals(other.set);
    }
    
    @Override
    public int hashCode() {
        return 89 + this.set.hashCode();
    }
    
    @Override
    public String toString() {
        return this.set.toString();
    }
    
    static {
        EMPTY = new CharSet(new String[] { null });
        ASCII_ALPHA = new CharSet(new String[] { "a-zA-Z" });
        ASCII_ALPHA_LOWER = new CharSet(new String[] { "a-z" });
        ASCII_ALPHA_UPPER = new CharSet(new String[] { "A-Z" });
        ASCII_NUMERIC = new CharSet(new String[] { "0-9" });
        (COMMON = Collections.synchronizedMap(new HashMap<String, CharSet>())).put(null, CharSet.EMPTY);
        CharSet.COMMON.put("", CharSet.EMPTY);
        CharSet.COMMON.put("a-zA-Z", CharSet.ASCII_ALPHA);
        CharSet.COMMON.put("A-Za-z", CharSet.ASCII_ALPHA);
        CharSet.COMMON.put("a-z", CharSet.ASCII_ALPHA_LOWER);
        CharSet.COMMON.put("A-Z", CharSet.ASCII_ALPHA_UPPER);
        CharSet.COMMON.put("0-9", CharSet.ASCII_NUMERIC);
    }
}
