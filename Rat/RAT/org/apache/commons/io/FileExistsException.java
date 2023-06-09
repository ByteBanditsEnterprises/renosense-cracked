//Raddon On Top!

package org.apache.commons.io;

import java.io.*;

public class FileExistsException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public FileExistsException() {
    }
    
    public FileExistsException(final String message) {
        super(message);
    }
    
    public FileExistsException(final File file) {
        super("File " + file + " exists");
    }
}
