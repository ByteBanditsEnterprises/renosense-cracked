//Raddon On Top!

package org.apache.commons.io;

import java.util.*;
import java.nio.charset.*;

public enum StandardLineSeparator
{
    CR("\r"), 
    CRLF("\r\n"), 
    LF("\n");
    
    private final String lineSeparator;
    
    private StandardLineSeparator(final String lineSeparator) {
        this.lineSeparator = Objects.requireNonNull(lineSeparator, "lineSeparator");
    }
    
    public byte[] getBytes(final Charset charset) {
        return this.lineSeparator.getBytes(charset);
    }
    
    public String getString() {
        return this.lineSeparator;
    }
}
