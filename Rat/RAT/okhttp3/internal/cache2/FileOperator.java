//Raddon On Top!

package okhttp3.internal.cache2;

import okio.*;
import java.io.*;
import java.nio.channels.*;

final class FileOperator
{
    private final FileChannel fileChannel;
    
    FileOperator(final FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }
    
    public void write(long pos, final Buffer source, long byteCount) throws IOException {
        if (byteCount < 0L || byteCount > source.size()) {
            throw new IndexOutOfBoundsException();
        }
        while (byteCount > 0L) {
            final long bytesWritten = this.fileChannel.transferFrom((ReadableByteChannel)source, pos, byteCount);
            pos += bytesWritten;
            byteCount -= bytesWritten;
        }
    }
    
    public void read(long pos, final Buffer sink, long byteCount) throws IOException {
        if (byteCount < 0L) {
            throw new IndexOutOfBoundsException();
        }
        while (byteCount > 0L) {
            final long bytesRead = this.fileChannel.transferTo(pos, byteCount, (WritableByteChannel)sink);
            pos += bytesRead;
            byteCount -= bytesRead;
        }
    }
}
