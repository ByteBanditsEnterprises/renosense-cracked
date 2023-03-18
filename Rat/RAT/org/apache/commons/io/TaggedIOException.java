//Raddon On Top!

package org.apache.commons.io;

import java.io.*;

public class TaggedIOException extends IOExceptionWithCause
{
    private static final long serialVersionUID = -6994123481142850163L;
    private final Serializable tag;
    
    public static boolean isTaggedWith(final Throwable throwable, final Object tag) {
        return tag != null && throwable instanceof TaggedIOException && tag.equals(((TaggedIOException)throwable).tag);
    }
    
    public static void throwCauseIfTaggedWith(final Throwable throwable, final Object tag) throws IOException {
        if (isTaggedWith(throwable, tag)) {
            throw ((TaggedIOException)throwable).getCause();
        }
    }
    
    public TaggedIOException(final IOException original, final Serializable tag) {
        super(original.getMessage(), (Throwable)original);
        this.tag = tag;
    }
    
    public Serializable getTag() {
        return this.tag;
    }
    
    public synchronized IOException getCause() {
        return (IOException)super.getCause();
    }
}
