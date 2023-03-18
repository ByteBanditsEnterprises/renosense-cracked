//Raddon On Top!

package org.apache.commons.io.input;

import org.apache.commons.io.*;
import java.io.*;

public class DemuxInputStream extends InputStream
{
    private final InheritableThreadLocal<InputStream> inputStreamLocal;
    
    public DemuxInputStream() {
        this.inputStreamLocal = new InheritableThreadLocal<InputStream>();
    }
    
    public InputStream bindStream(final InputStream input) {
        final InputStream oldValue = this.inputStreamLocal.get();
        this.inputStreamLocal.set(input);
        return oldValue;
    }
    
    @Override
    public void close() throws IOException {
        IOUtils.close(this.inputStreamLocal.get());
    }
    
    @Override
    public int read() throws IOException {
        final InputStream inputStream = this.inputStreamLocal.get();
        if (null != inputStream) {
            return inputStream.read();
        }
        return -1;
    }
}
