//Raddon On Top!

package org.apache.commons.lang3.time;

import java.util.*;

public class CalendarUtils
{
    public static final CalendarUtils INSTANCE;
    private final Calendar calendar;
    
    public CalendarUtils(final Calendar calendar) {
        this.calendar = Objects.requireNonNull(calendar, "calendar");
    }
    
    public int getDayOfMonth() {
        return this.calendar.get(5);
    }
    
    public int getMonth() {
        return this.calendar.get(2);
    }
    
    public int getYear() {
        return this.calendar.get(1);
    }
    
    static {
        INSTANCE = new CalendarUtils(Calendar.getInstance());
    }
}
