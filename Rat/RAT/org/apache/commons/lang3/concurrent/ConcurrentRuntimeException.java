//Raddon On Top!

package org.apache.commons.lang3.concurrent;

public class ConcurrentRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -6582182735562919670L;
    
    protected ConcurrentRuntimeException() {
    }
    
    public ConcurrentRuntimeException(final Throwable cause) {
        super(ConcurrentUtils.checkedException(cause));
    }
    
    public ConcurrentRuntimeException(final String msg, final Throwable cause) {
        super(msg, ConcurrentUtils.checkedException(cause));
    }
}
