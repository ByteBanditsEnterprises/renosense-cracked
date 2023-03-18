//Raddon On Top!

package org.json;

import java.io.*;
import java.util.*;
import java.net.*;

public class JSONPointer
{
    private static final String ENCODING = "utf-8";
    private final List<String> refTokens;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public JSONPointer(final String pointer) {
        if (pointer == null) {
            throw new NullPointerException("pointer cannot be null");
        }
        if (pointer.isEmpty() || pointer.equals("#")) {
            this.refTokens = Collections.emptyList();
            return;
        }
        String refs = null;
        Label_0105: {
            if (pointer.startsWith("#/")) {
                refs = pointer.substring(2);
                try {
                    refs = URLDecoder.decode(refs, "utf-8");
                    break Label_0105;
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!pointer.startsWith("/")) {
                throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'");
            }
            refs = pointer.substring(1);
        }
        this.refTokens = new ArrayList<String>();
        int slashIdx = -1;
        int prevSlashIdx = 0;
        do {
            prevSlashIdx = slashIdx + 1;
            slashIdx = refs.indexOf(47, prevSlashIdx);
            if (prevSlashIdx == slashIdx || prevSlashIdx == refs.length()) {
                this.refTokens.add("");
            }
            else if (slashIdx >= 0) {
                final String token = refs.substring(prevSlashIdx, slashIdx);
                this.refTokens.add(unescape(token));
            }
            else {
                final String token = refs.substring(prevSlashIdx);
                this.refTokens.add(unescape(token));
            }
        } while (slashIdx >= 0);
    }
    
    public JSONPointer(final List<String> refTokens) {
        this.refTokens = new ArrayList<String>(refTokens);
    }
    
    private static String unescape(final String token) {
        return token.replace("~1", "/").replace("~0", "~");
    }
    
    public Object queryFrom(final Object document) throws JSONPointerException {
        if (this.refTokens.isEmpty()) {
            return document;
        }
        Object current = document;
        for (final String token : this.refTokens) {
            if (current instanceof JSONObject) {
                current = ((JSONObject)current).opt(unescape(token));
            }
            else {
                if (!(current instanceof JSONArray)) {
                    throw new JSONPointerException(String.format("value [%s] is not an array or object therefore its key %s cannot be resolved", current, token));
                }
                current = readByIndexToken(current, token);
            }
        }
        return current;
    }
    
    private static Object readByIndexToken(final Object current, final String indexToken) throws JSONPointerException {
        try {
            final int index = Integer.parseInt(indexToken);
            final JSONArray currentArr = (JSONArray)current;
            if (index >= currentArr.length()) {
                throw new JSONPointerException(String.format("index %s is out of bounds - the array has %d elements", indexToken, currentArr.length()));
            }
            try {
                return currentArr.get(index);
            }
            catch (JSONException e) {
                throw new JSONPointerException("Error reading value at index position " + index, (Throwable)e);
            }
        }
        catch (NumberFormatException e2) {
            throw new JSONPointerException(String.format("%s is not an array index", indexToken), e2);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder rval = new StringBuilder("");
        for (final String token : this.refTokens) {
            rval.append('/').append(escape(token));
        }
        return rval.toString();
    }
    
    private static String escape(final String token) {
        return token.replace("~", "~0").replace("/", "~1");
    }
    
    public String toURIFragment() {
        try {
            final StringBuilder rval = new StringBuilder("#");
            for (final String token : this.refTokens) {
                rval.append('/').append(URLEncoder.encode(token, "utf-8"));
            }
            return rval.toString();
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static class Builder
    {
        private final List<String> refTokens;
        
        public Builder() {
            this.refTokens = new ArrayList<String>();
        }
        
        public JSONPointer build() {
            return new JSONPointer(this.refTokens);
        }
        
        public Builder append(final String token) {
            if (token == null) {
                throw new NullPointerException("token cannot be null");
            }
            this.refTokens.add(token);
            return this;
        }
        
        public Builder append(final int arrayIndex) {
            this.refTokens.add(String.valueOf(arrayIndex));
            return this;
        }
    }
}
