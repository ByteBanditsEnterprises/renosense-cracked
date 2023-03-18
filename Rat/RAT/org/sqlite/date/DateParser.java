//Raddon On Top!

package org.sqlite.date;

import java.text.*;
import java.util.*;

public interface DateParser
{
    Date parse(final String p0) throws ParseException;
    
    Date parse(final String p0, final ParsePosition p1);
    
    String getPattern();
    
    TimeZone getTimeZone();
    
    Locale getLocale();
    
    Object parseObject(final String p0) throws ParseException;
    
    Object parseObject(final String p0, final ParsePosition p1);
}
