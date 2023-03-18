//Raddon On Top!

package org.apache.commons.io.input;

class UnsupportedOperationExceptions
{
    private static final String MARK_RESET = "mark/reset";
    
    static UnsupportedOperationException mark() {
        return method("mark/reset");
    }
    
    static UnsupportedOperationException method(final String method) {
        return new UnsupportedOperationException(method + " not supported");
    }
    
    static UnsupportedOperationException reset() {
        return method("mark/reset");
    }
}
