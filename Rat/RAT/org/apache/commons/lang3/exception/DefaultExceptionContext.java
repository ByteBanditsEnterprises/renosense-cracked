//Raddon On Top!

package org.apache.commons.lang3.exception;

import java.io.*;
import org.apache.commons.lang3.tuple.*;
import org.apache.commons.lang3.*;
import java.util.*;

public class DefaultExceptionContext implements ExceptionContext, Serializable
{
    private static final long serialVersionUID = 20110706L;
    private final List<Pair<String, Object>> contextValues;
    
    public DefaultExceptionContext() {
        this.contextValues = new ArrayList<Pair<String, Object>>();
    }
    
    @Override
    public DefaultExceptionContext addContextValue(final String label, final Object value) {
        this.contextValues.add(new ImmutablePair<String, Object>(label, value));
        return this;
    }
    
    @Override
    public DefaultExceptionContext setContextValue(final String label, final Object value) {
        this.contextValues.removeIf(p -> StringUtils.equals(label, p.getKey()));
        this.addContextValue(label, value);
        return this;
    }
    
    @Override
    public List<Object> getContextValues(final String label) {
        final List<Object> values = new ArrayList<Object>();
        for (final Pair<String, Object> pair : this.contextValues) {
            if (StringUtils.equals(label, pair.getKey())) {
                values.add(pair.getValue());
            }
        }
        return values;
    }
    
    @Override
    public Object getFirstContextValue(final String label) {
        for (final Pair<String, Object> pair : this.contextValues) {
            if (StringUtils.equals(label, pair.getKey())) {
                return pair.getValue();
            }
        }
        return null;
    }
    
    @Override
    public Set<String> getContextLabels() {
        final Set<String> labels = new HashSet<String>();
        for (final Pair<String, Object> pair : this.contextValues) {
            labels.add(pair.getKey());
        }
        return labels;
    }
    
    @Override
    public List<Pair<String, Object>> getContextEntries() {
        return this.contextValues;
    }
    
    @Override
    public String getFormattedExceptionMessage(final String baseMessage) {
        final StringBuilder buffer = new StringBuilder(256);
        if (baseMessage != null) {
            buffer.append(baseMessage);
        }
        if (!this.contextValues.isEmpty()) {
            if (buffer.length() > 0) {
                buffer.append('\n');
            }
            buffer.append("Exception Context:\n");
            int i = 0;
            for (final Pair<String, Object> pair : this.contextValues) {
                buffer.append("\t[");
                buffer.append(++i);
                buffer.append(':');
                buffer.append(pair.getKey());
                buffer.append("=");
                final Object value = pair.getValue();
                if (value == null) {
                    buffer.append("null");
                }
                else {
                    String valueStr;
                    try {
                        valueStr = value.toString();
                    }
                    catch (Exception e) {
                        valueStr = "Exception thrown on toString(): " + ExceptionUtils.getStackTrace(e);
                    }
                    buffer.append(valueStr);
                }
                buffer.append("]\n");
            }
            buffer.append("---------------------------------");
        }
        return buffer.toString();
    }
}
