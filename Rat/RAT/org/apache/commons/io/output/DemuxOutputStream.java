//Raddon On Top!

package org.apache.commons.io.output;

import org.apache.commons.io.*;
import java.io.*;

public class DemuxOutputStream extends OutputStream
{
    private final InheritableThreadLocal<OutputStream> outputStreamThreadLocal;
    
    public DemuxOutputStream() {
        this.outputStreamThreadLocal = new InheritableThreadLocal<OutputStream>();
    }
    
    public OutputStream bindStream(final OutputStream output) {
        final OutputStream stream = this.outputStreamThreadLocal.get();
        this.outputStreamThreadLocal.set(output);
        return stream;
    }
    
    @Override
    public void close() throws IOException {
        IOUtils.close((Closeable)this.outputStreamThreadLocal.get());
    }
    
    @Override
    public void flush() throws IOException {
        final OutputStream output = this.outputStreamThreadLocal.get();
        if (null != output) {
            output.flush();
        }
    }
    
    @Override
    public void write(final int ch) throws IOException {
        final OutputStream output = this.outputStreamThreadLocal.get();
        if (null != output) {
            output.write(ch);
        }
    }
}
