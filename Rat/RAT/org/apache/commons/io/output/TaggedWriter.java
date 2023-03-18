//Raddon On Top!

package org.apache.commons.io.output;

import java.util.*;
import org.apache.commons.io.*;
import java.io.*;

public class TaggedWriter extends ProxyWriter
{
    private final Serializable tag;
    
    public TaggedWriter(final Writer proxy) {
        super(proxy);
        this.tag = UUID.randomUUID();
    }
    
    public boolean isCauseOf(final Exception exception) {
        return TaggedIOException.isTaggedWith(exception, this.tag);
    }
    
    public void throwIfCauseOf(final Exception exception) throws IOException {
        TaggedIOException.throwCauseIfTaggedWith(exception, this.tag);
    }
    
    protected void handleIOException(final IOException e) throws IOException {
        throw new TaggedIOException(e, this.tag);
    }
}
