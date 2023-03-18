//Raddon On Top!

package org.sqlite.date;

import java.util.*;
import java.text.*;

public class FastDateFormat extends Format implements DateParser, DatePrinter
{
    private static final long serialVersionUID = 2L;
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final FormatCache<FastDateFormat> cache;
    private final FastDatePrinter printer;
    private final FastDateParser parser;
    
    public static FastDateFormat getInstance() {
        return FastDateFormat.cache.getInstance();
    }
    
    public static FastDateFormat getInstance(final String pattern) {
        return FastDateFormat.cache.getInstance(pattern, null, null);
    }
    
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone) {
        return FastDateFormat.cache.getInstance(pattern, timeZone, null);
    }
    
    public static FastDateFormat getInstance(final String pattern, final Locale locale) {
        return FastDateFormat.cache.getInstance(pattern, null, locale);
    }
    
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
        return FastDateFormat.cache.getInstance(pattern, timeZone, locale);
    }
    
    public static FastDateFormat getDateInstance(final int style) {
        return FastDateFormat.cache.getDateInstance(style, null, null);
    }
    
    public static FastDateFormat getDateInstance(final int style, final Locale locale) {
        return FastDateFormat.cache.getDateInstance(style, null, locale);
    }
    
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone) {
        return FastDateFormat.cache.getDateInstance(style, timeZone, null);
    }
    
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone, final Locale locale) {
        return FastDateFormat.cache.getDateInstance(style, timeZone, locale);
    }
    
    public static FastDateFormat getTimeInstance(final int style) {
        return FastDateFormat.cache.getTimeInstance(style, null, null);
    }
    
    public static FastDateFormat getTimeInstance(final int style, final Locale locale) {
        return FastDateFormat.cache.getTimeInstance(style, null, locale);
    }
    
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone) {
        return FastDateFormat.cache.getTimeInstance(style, timeZone, null);
    }
    
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone, final Locale locale) {
        return FastDateFormat.cache.getTimeInstance(style, timeZone, locale);
    }
    
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle) {
        return FastDateFormat.cache.getDateTimeInstance(dateStyle, timeStyle, null, null);
    }
    
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final Locale locale) {
        return FastDateFormat.cache.getDateTimeInstance(dateStyle, timeStyle, null, locale);
    }
    
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone) {
        return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
    }
    
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return FastDateFormat.cache.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
    }
    
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale) {
        this(pattern, timeZone, locale, null);
    }
    
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        this.printer = new FastDatePrinter(pattern, timeZone, locale);
        this.parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }
    
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        return this.printer.format(obj, toAppendTo, pos);
    }
    
    public String format(final long millis) {
        return this.printer.format(millis);
    }
    
    public String format(final Date date) {
        return this.printer.format(date);
    }
    
    public String format(final Calendar calendar) {
        return this.printer.format(calendar);
    }
    
    public StringBuffer format(final long millis, final StringBuffer buf) {
        return this.printer.format(millis, buf);
    }
    
    public StringBuffer format(final Date date, final StringBuffer buf) {
        return this.printer.format(date, buf);
    }
    
    public StringBuffer format(final Calendar calendar, final StringBuffer buf) {
        return this.printer.format(calendar, buf);
    }
    
    public Date parse(final String source) throws ParseException {
        return this.parser.parse(source);
    }
    
    public Date parse(final String source, final ParsePosition pos) {
        return this.parser.parse(source, pos);
    }
    
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        return this.parser.parseObject(source, pos);
    }
    
    public String getPattern() {
        return this.printer.getPattern();
    }
    
    public TimeZone getTimeZone() {
        return this.printer.getTimeZone();
    }
    
    public Locale getLocale() {
        return this.printer.getLocale();
    }
    
    public int getMaxLengthEstimate() {
        return this.printer.getMaxLengthEstimate();
    }
    
    public boolean equals(final Object obj) {
        if (!(obj instanceof FastDateFormat)) {
            return false;
        }
        final FastDateFormat other = (FastDateFormat)obj;
        return this.printer.equals(other.printer);
    }
    
    public int hashCode() {
        return this.printer.hashCode();
    }
    
    public String toString() {
        return "FastDateFormat[" + this.printer.getPattern() + "," + this.printer.getLocale() + "," + this.printer.getTimeZone().getID() + "]";
    }
    
    protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
        return this.printer.applyRules(calendar, buf);
    }
    
    static {
        cache = new FormatCache<FastDateFormat>() {
            @Override
            protected FastDateFormat createInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
                return new FastDateFormat(pattern, timeZone, locale);
            }
        };
    }
}
