//Raddon On Top!

package org.apache.commons.io.input;

import java.util.*;
import org.apache.commons.io.*;
import java.io.*;

public class TaggedReader extends ProxyReader
{
    private final Serializable tag;
    
    public TaggedReader(final Reader proxy) {
        super(proxy);
        this.tag = UUID.randomUUID();
    }
    
    public boolean isCauseOf(final Throwable exception) {
        return TaggedIOException.isTaggedWith(exception, this.tag);
    }
    
    public void throwIfCauseOf(final Throwable throwable) throws IOException {
        TaggedIOException.throwCauseIfTaggedWith(throwable, this.tag);
    }
    
    protected void handleIOException(final IOException e) throws IOException {
        throw new TaggedIOException(e, this.tag);
    }
}
