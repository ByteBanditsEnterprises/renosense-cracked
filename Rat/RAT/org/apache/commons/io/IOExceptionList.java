//Raddon On Top!

package org.apache.commons.io;

import java.io.*;
import java.util.*;

public class IOExceptionList extends IOException
{
    private static final long serialVersionUID = 1L;
    private final List<? extends Throwable> causeList;
    
    public IOExceptionList(final List<? extends Throwable> causeList) {
        this(String.format("%,d exceptions: %s", (causeList == null) ? 0 : causeList.size(), causeList), causeList);
    }
    
    public IOExceptionList(final String message, final List<? extends Throwable> causeList) {
        super(message, (causeList == null || causeList.isEmpty()) ? null : ((Throwable)causeList.get(0)));
        this.causeList = ((causeList == null) ? Collections.emptyList() : causeList);
    }
    
    public <T extends Throwable> T getCause(final int index) {
        return (T)this.causeList.get(index);
    }
    
    public <T extends Throwable> T getCause(final int index, final Class<T> clazz) {
        return clazz.cast(this.causeList.get(index));
    }
    
    public <T extends Throwable> List<T> getCauseList() {
        return (List<T>)this.causeList;
    }
    
    public <T extends Throwable> List<T> getCauseList(final Class<T> clazz) {
        return (List<T>)this.causeList;
    }
}
