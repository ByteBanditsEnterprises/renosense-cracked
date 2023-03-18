//Raddon On Top!

package com.github.windpapi4j;

public class HResultException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private final int hResult;
    
    HResultException(final String message, final int hresult) {
        super(String.format("%s HRESULT=%s", message, hresult));
        this.hResult = hresult;
    }
    
    public final int getHResult() {
        return this.hResult;
    }
}
