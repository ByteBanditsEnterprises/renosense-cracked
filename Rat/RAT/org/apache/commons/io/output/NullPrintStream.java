//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class NullPrintStream extends PrintStream
{
    public static final NullPrintStream NULL_PRINT_STREAM;
    
    public NullPrintStream() {
        super((OutputStream)NullOutputStream.NULL_OUTPUT_STREAM);
    }
    
    static {
        NULL_PRINT_STREAM = new NullPrintStream();
    }
}
