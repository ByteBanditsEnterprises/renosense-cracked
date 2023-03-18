//Raddon On Top!

package org.apache.commons.lang3.exception;

import org.apache.commons.lang3.tuple.*;
import java.util.*;

public class ContextedRuntimeException extends RuntimeException implements ExceptionContext
{
    private static final long serialVersionUID = 20110706L;
    private final ExceptionContext exceptionContext;
    
    public ContextedRuntimeException() {
        this.exceptionContext = new DefaultExceptionContext();
    }
    
    public ContextedRuntimeException(final String message) {
        super(message);
        this.exceptionContext = new DefaultExceptionContext();
    }
    
    public ContextedRuntimeException(final Throwable cause) {
        super(cause);
        this.exceptionContext = new DefaultExceptionContext();
    }
    
    public ContextedRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
        this.exceptionContext = new DefaultExceptionContext();
    }
    
    public ContextedRuntimeException(final String message, final Throwable cause, ExceptionContext context) {
        super(message, cause);
        if (context == null) {
            context = new DefaultExceptionContext();
        }
        this.exceptionContext = context;
    }
    
    @Override
    public ContextedRuntimeException addContextValue(final String label, final Object value) {
        this.exceptionContext.addContextValue(label, value);
        return this;
    }
    
    @Override
    public ContextedRuntimeException setContextValue(final String label, final Object value) {
        this.exceptionContext.setContextValue(label, value);
        return this;
    }
    
    @Override
    public List<Object> getContextValues(final String label) {
        return this.exceptionContext.getContextValues(label);
    }
    
    @Override
    public Object getFirstContextValue(final String label) {
        return this.exceptionContext.getFirstContextValue(label);
    }
    
    @Override
    public List<Pair<String, Object>> getContextEntries() {
        return this.exceptionContext.getContextEntries();
    }
    
    @Override
    public Set<String> getContextLabels() {
        return this.exceptionContext.getContextLabels();
    }
    
    @Override
    public String getMessage() {
        return this.getFormattedExceptionMessage(super.getMessage());
    }
    
    public String getRawMessage() {
        return super.getMessage();
    }
    
    @Override
    public String getFormattedExceptionMessage(final String baseMessage) {
        return this.exceptionContext.getFormattedExceptionMessage(baseMessage);
    }
}
