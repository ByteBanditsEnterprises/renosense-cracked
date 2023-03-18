//Raddon On Top!

package org.apache.commons.lang3.text;

import org.apache.commons.lang3.*;
import java.util.*;

@Deprecated
public abstract class StrMatcher
{
    private static final StrMatcher COMMA_MATCHER;
    private static final StrMatcher TAB_MATCHER;
    private static final StrMatcher SPACE_MATCHER;
    private static final StrMatcher SPLIT_MATCHER;
    private static final StrMatcher TRIM_MATCHER;
    private static final StrMatcher SINGLE_QUOTE_MATCHER;
    private static final StrMatcher DOUBLE_QUOTE_MATCHER;
    private static final StrMatcher QUOTE_MATCHER;
    private static final StrMatcher NONE_MATCHER;
    
    public static StrMatcher commaMatcher() {
        return StrMatcher.COMMA_MATCHER;
    }
    
    public static StrMatcher tabMatcher() {
        return StrMatcher.TAB_MATCHER;
    }
    
    public static StrMatcher spaceMatcher() {
        return StrMatcher.SPACE_MATCHER;
    }
    
    public static StrMatcher splitMatcher() {
        return StrMatcher.SPLIT_MATCHER;
    }
    
    public static StrMatcher trimMatcher() {
        return StrMatcher.TRIM_MATCHER;
    }
    
    public static StrMatcher singleQuoteMatcher() {
        return StrMatcher.SINGLE_QUOTE_MATCHER;
    }
    
    public static StrMatcher doubleQuoteMatcher() {
        return StrMatcher.DOUBLE_QUOTE_MATCHER;
    }
    
    public static StrMatcher quoteMatcher() {
        return StrMatcher.QUOTE_MATCHER;
    }
    
    public static StrMatcher noneMatcher() {
        return StrMatcher.NONE_MATCHER;
    }
    
    public static StrMatcher charMatcher(final char ch) {
        return new CharMatcher(ch);
    }
    
    public static StrMatcher charSetMatcher(final char... chars) {
        if (chars == null || chars.length == 0) {
            return StrMatcher.NONE_MATCHER;
        }
        if (chars.length == 1) {
            return new CharMatcher(chars[0]);
        }
        return new CharSetMatcher(chars);
    }
    
    public static StrMatcher charSetMatcher(final String chars) {
        if (StringUtils.isEmpty((CharSequence)chars)) {
            return StrMatcher.NONE_MATCHER;
        }
        if (chars.length() == 1) {
            return new CharMatcher(chars.charAt(0));
        }
        return new CharSetMatcher(chars.toCharArray());
    }
    
    public static StrMatcher stringMatcher(final String str) {
        if (StringUtils.isEmpty((CharSequence)str)) {
            return StrMatcher.NONE_MATCHER;
        }
        return new StringMatcher(str);
    }
    
    protected StrMatcher() {
    }
    
    public abstract int isMatch(final char[] p0, final int p1, final int p2, final int p3);
    
    public int isMatch(final char[] buffer, final int pos) {
        return this.isMatch(buffer, pos, 0, buffer.length);
    }
    
    static {
        COMMA_MATCHER = new CharMatcher(',');
        TAB_MATCHER = new CharMatcher('\t');
        SPACE_MATCHER = new CharMatcher(' ');
        SPLIT_MATCHER = new CharSetMatcher(" \t\n\r\f".toCharArray());
        TRIM_MATCHER = new TrimMatcher();
        SINGLE_QUOTE_MATCHER = new CharMatcher('\'');
        DOUBLE_QUOTE_MATCHER = new CharMatcher('\"');
        QUOTE_MATCHER = new CharSetMatcher("'\"".toCharArray());
        NONE_MATCHER = new NoMatcher();
    }
    
    static final class CharSetMatcher extends StrMatcher
    {
        private final char[] chars;
        
        CharSetMatcher(final char[] chars) {
            this.chars = ArraySorter.sort((char[])chars.clone());
        }
        
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return (Arrays.binarySearch(this.chars, buffer[pos]) >= 0) ? 1 : 0;
        }
    }
    
    static final class CharMatcher extends StrMatcher
    {
        private final char ch;
        
        CharMatcher(final char ch) {
            this.ch = ch;
        }
        
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return (this.ch == buffer[pos]) ? 1 : 0;
        }
    }
    
    static final class StringMatcher extends StrMatcher
    {
        private final char[] chars;
        
        StringMatcher(final String str) {
            this.chars = str.toCharArray();
        }
        
        @Override
        public int isMatch(final char[] buffer, int pos, final int bufferStart, final int bufferEnd) {
            final int len = this.chars.length;
            if (pos + len > bufferEnd) {
                return 0;
            }
            for (int i = 0; i < this.chars.length; ++i, ++pos) {
                if (this.chars[i] != buffer[pos]) {
                    return 0;
                }
            }
            return len;
        }
        
        @Override
        public String toString() {
            return super.toString() + ' ' + Arrays.toString(this.chars);
        }
    }
    
    static final class NoMatcher extends StrMatcher
    {
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return 0;
        }
    }
    
    static final class TrimMatcher extends StrMatcher
    {
        @Override
        public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
            return (buffer[pos] <= ' ') ? 1 : 0;
        }
    }
}
