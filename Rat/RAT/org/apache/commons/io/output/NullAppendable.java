//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class NullAppendable implements Appendable
{
    public static final NullAppendable INSTANCE;
    
    private NullAppendable() {
    }
    
    @Override
    public Appendable append(final char c) throws IOException {
        return this;
    }
    
    @Override
    public Appendable append(final CharSequence csq) throws IOException {
        return this;
    }
    
    @Override
    public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
        return this;
    }
    
    static {
        INSTANCE = new NullAppendable();
    }
}
