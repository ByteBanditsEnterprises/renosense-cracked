//Raddon On Top!

package okhttp3.internal.cache;

import okio.*;
import java.io.*;

class FaultHidingSink extends ForwardingSink
{
    private boolean hasErrors;
    
    FaultHidingSink(final Sink delegate) {
        super(delegate);
    }
    
    public void write(final Buffer source, final long byteCount) throws IOException {
        if (this.hasErrors) {
            source.skip(byteCount);
            return;
        }
        try {
            super.write(source, byteCount);
        }
        catch (IOException e) {
            this.hasErrors = true;
            this.onException(e);
        }
    }
    
    public void flush() throws IOException {
        if (this.hasErrors) {
            return;
        }
        try {
            super.flush();
        }
        catch (IOException e) {
            this.hasErrors = true;
            this.onException(e);
        }
    }
    
    public void close() throws IOException {
        if (this.hasErrors) {
            return;
        }
        try {
            super.close();
        }
        catch (IOException e) {
            this.hasErrors = true;
            this.onException(e);
        }
    }
    
    protected void onException(final IOException e) {
    }
}
