//Raddon On Top!

package org.apache.commons.lang3.time;

import java.util.concurrent.*;
import org.apache.commons.lang3.*;
import java.text.*;
import java.util.*;

abstract class FormatCache<F extends Format>
{
    static final int NONE = -1;
    private final ConcurrentMap<ArrayKey, F> cInstanceCache;
    private static final ConcurrentMap<ArrayKey, String> cDateTimeInstanceCache;
    
    FormatCache() {
        this.cInstanceCache = new ConcurrentHashMap<ArrayKey, F>(7);
    }
    
    public F getInstance() {
        return this.getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
    }
    
    public F getInstance(final String pattern, TimeZone timeZone, Locale locale) {
        Validate.notNull(pattern, "pattern", new Object[0]);
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        locale = LocaleUtils.toLocale(locale);
        final ArrayKey key = new ArrayKey(new Object[] { pattern, timeZone, locale });
        F format = this.cInstanceCache.get(key);
        if (format == null) {
            format = this.createInstance(pattern, timeZone, locale);
            final F previousValue = this.cInstanceCache.putIfAbsent(key, format);
            if (previousValue != null) {
                format = previousValue;
            }
        }
        return format;
    }
    
    protected abstract F createInstance(final String p0, final TimeZone p1, final Locale p2);
    
    private F getDateTimeInstance(final Integer dateStyle, final Integer timeStyle, final TimeZone timeZone, Locale locale) {
        locale = LocaleUtils.toLocale(locale);
        final String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
        return this.getInstance(pattern, timeZone, locale);
    }
    
    F getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return this.getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
    }
    
    F getDateInstance(final int dateStyle, final TimeZone timeZone, final Locale locale) {
        return this.getDateTimeInstance(dateStyle, null, timeZone, locale);
    }
    
    F getTimeInstance(final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return this.getDateTimeInstance(null, timeStyle, timeZone, locale);
    }
    
    static String getPatternForStyle(final Integer dateStyle, final Integer timeStyle, final Locale locale) {
        final Locale safeLocale = LocaleUtils.toLocale(locale);
        final ArrayKey key = new ArrayKey(new Object[] { dateStyle, timeStyle, safeLocale });
        String pattern = FormatCache.cDateTimeInstanceCache.get(key);
        if (pattern == null) {
            try {
                DateFormat formatter;
                if (dateStyle == null) {
                    formatter = DateFormat.getTimeInstance(timeStyle, safeLocale);
                }
                else if (timeStyle == null) {
                    formatter = DateFormat.getDateInstance(dateStyle, safeLocale);
                }
                else {
                    formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, safeLocale);
                }
                pattern = ((SimpleDateFormat)formatter).toPattern();
                final String previous = FormatCache.cDateTimeInstanceCache.putIfAbsent(key, pattern);
                if (previous != null) {
                    pattern = previous;
                }
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException("No date time pattern for locale: " + safeLocale);
            }
        }
        return pattern;
    }
    
    static {
        cDateTimeInstanceCache = new ConcurrentHashMap<ArrayKey, String>(7);
    }
    
    private static final class ArrayKey
    {
        private final Object[] keys;
        private final int hashCode;
        
        private static int computeHashCode(final Object[] keys) {
            final int prime = 31;
            int result = 1;
            result = 31 * result + Arrays.hashCode(keys);
            return result;
        }
        
        ArrayKey(final Object... keys) {
            this.keys = keys;
            this.hashCode = computeHashCode(keys);
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ArrayKey other = (ArrayKey)obj;
            return Arrays.deepEquals(this.keys, other.keys);
        }
    }
}
