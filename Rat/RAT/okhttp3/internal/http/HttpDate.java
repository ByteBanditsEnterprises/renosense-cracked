//Raddon On Top!

package okhttp3.internal.http;

import java.util.*;
import java.text.*;
import okhttp3.internal.*;

public final class HttpDate
{
    public static final long MAX_DATE = 253402300799999L;
    private static final ThreadLocal<DateFormat> STANDARD_DATE_FORMAT;
    private static final String[] BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;
    private static final DateFormat[] BROWSER_COMPATIBLE_DATE_FORMATS;
    
    public static Date parse(final String value) {
        if (value.length() == 0) {
            return null;
        }
        final ParsePosition position = new ParsePosition(0);
        Date result = HttpDate.STANDARD_DATE_FORMAT.get().parse(value, position);
        if (position.getIndex() == value.length()) {
            return result;
        }
        synchronized (HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS) {
            for (int i = 0, count = HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length; i < count; ++i) {
                DateFormat format = HttpDate.BROWSER_COMPATIBLE_DATE_FORMATS[i];
                if (format == null) {
                    format = new SimpleDateFormat(HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS[i], Locale.US);
                    format.setTimeZone(Util.UTC);
                    HttpDate.BROWSER_COMPATIBLE_DATE_FORMATS[i] = format;
                }
                position.setIndex(0);
                result = format.parse(value, position);
                if (position.getIndex() != 0) {
                    return result;
                }
            }
        }
        return null;
    }
    
    public static String format(final Date value) {
        return HttpDate.STANDARD_DATE_FORMAT.get().format(value);
    }
    
    private HttpDate() {
    }
    
    static {
        STANDARD_DATE_FORMAT = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                final DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                rfc1123.setLenient(false);
                rfc1123.setTimeZone(Util.UTC);
                return rfc1123;
            }
        };
        BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z", "EEE MMM d yyyy HH:mm:ss z" };
        BROWSER_COMPATIBLE_DATE_FORMATS = new DateFormat[HttpDate.BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length];
    }
}
