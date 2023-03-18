//Raddon On Top!

package com.github.windpapi4j;

public class WinAPICallFailedException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    WinAPICallFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
