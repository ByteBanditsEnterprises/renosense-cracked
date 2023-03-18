//Raddon On Top!

package org.apache.commons.io.output;

import java.io.*;

public class AppendableOutputStream<T extends Appendable> extends OutputStream
{
    private final T appendable;
    
    public AppendableOutputStream(final T appendable) {
        this.appendable = appendable;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.appendable.append((char)b);
    }
    
    public T getAppendable() {
        return this.appendable;
    }
}
