//Raddon On Top!

package org.apache.commons.lang3;

public class CharSequenceUtils
{
    private static final int NOT_FOUND = -1;
    static final int TO_STRING_LIMIT = 16;
    
    public static CharSequence subSequence(final CharSequence cs, final int start) {
        return (cs == null) ? null : cs.subSequence(start, cs.length());
    }
    
    static int indexOf(final CharSequence cs, final int searchChar, int start) {
        if (cs instanceof String) {
            return ((String)cs).indexOf(searchChar, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        if (searchChar < 65536) {
            for (int i = start; i < sz; ++i) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
            return -1;
        }
        if (searchChar <= 1114111) {
            final char[] chars = Character.toChars(searchChar);
            for (int j = start; j < sz - 1; ++j) {
                final char high = cs.charAt(j);
                final char low = cs.charAt(j + 1);
                if (high == chars[0] && low == chars[1]) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        if (cs instanceof String) {
            return ((String)cs).indexOf(searchChar.toString(), start);
        }
        if (cs instanceof StringBuilder) {
            return ((StringBuilder)cs).indexOf(searchChar.toString(), start);
        }
        if (cs instanceof StringBuffer) {
            return ((StringBuffer)cs).indexOf(searchChar.toString(), start);
        }
        return cs.toString().indexOf(searchChar.toString(), start);
    }
    
    static int lastIndexOf(final CharSequence cs, final int searchChar, int start) {
        if (cs instanceof String) {
            return ((String)cs).lastIndexOf(searchChar, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            return -1;
        }
        if (start >= sz) {
            start = sz - 1;
        }
        if (searchChar < 65536) {
            for (int i = start; i >= 0; --i) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
            return -1;
        }
        if (searchChar <= 1114111) {
            final char[] chars = Character.toChars(searchChar);
            if (start == sz - 1) {
                return -1;
            }
            for (int j = start; j >= 0; --j) {
                final char high = cs.charAt(j);
                final char low = cs.charAt(j + 1);
                if (chars[0] == high && chars[1] == low) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    static int lastIndexOf(final CharSequence cs, final CharSequence searchChar, int start) {
        if (searchChar == null || cs == null) {
            return -1;
        }
        if (searchChar instanceof String) {
            if (cs instanceof String) {
                return ((String)cs).lastIndexOf((String)searchChar, start);
            }
            if (cs instanceof StringBuilder) {
                return ((StringBuilder)cs).lastIndexOf((String)searchChar, start);
            }
            if (cs instanceof StringBuffer) {
                return ((StringBuffer)cs).lastIndexOf((String)searchChar, start);
            }
        }
        final int len1 = cs.length();
        final int len2 = searchChar.length();
        if (start > len1) {
            start = len1;
        }
        if (start < 0 || len2 < 0 || len2 > len1) {
            return -1;
        }
        if (len2 == 0) {
            return start;
        }
        if (len2 <= 16) {
            if (cs instanceof String) {
                return ((String)cs).lastIndexOf(searchChar.toString(), start);
            }
            if (cs instanceof StringBuilder) {
                return ((StringBuilder)cs).lastIndexOf(searchChar.toString(), start);
            }
            if (cs instanceof StringBuffer) {
                return ((StringBuffer)cs).lastIndexOf(searchChar.toString(), start);
            }
        }
        if (start + len2 > len1) {
            start = len1 - len2;
        }
        final char char0 = searchChar.charAt(0);
        int i = start;
        while (true) {
            if (cs.charAt(i) != char0) {
                if (--i < 0) {
                    return -1;
                }
                continue;
            }
            else {
                if (checkLaterThan1(cs, searchChar, len2, i)) {
                    return i;
                }
                if (--i < 0) {
                    return -1;
                }
                continue;
            }
        }
    }
    
    private static boolean checkLaterThan1(final CharSequence cs, final CharSequence searchChar, final int len2, final int start1) {
        for (int i = 1, j = len2 - 1; i <= j; ++i, --j) {
            if (cs.charAt(start1 + i) != searchChar.charAt(i) || cs.charAt(start1 + j) != searchChar.charAt(j)) {
                return false;
            }
        }
        return true;
    }
    
    public static char[] toCharArray(final CharSequence source) {
        final int len = StringUtils.length(source);
        if (len == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        if (source instanceof String) {
            return ((String)source).toCharArray();
        }
        final char[] array = new char[len];
        for (int i = 0; i < len; ++i) {
            array[i] = source.charAt(i);
        }
        return array;
    }
    
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String)cs).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }
        if (srcLen < length || otherLen < length) {
            return false;
        }
        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);
            if (c1 == c2) {
                continue;
            }
            if (!ignoreCase) {
                return false;
            }
            final char u1 = Character.toUpperCase(c1);
            final char u2 = Character.toUpperCase(c2);
            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                return false;
            }
        }
        return true;
    }
}
