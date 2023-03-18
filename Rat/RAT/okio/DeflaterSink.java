//Raddon On Top!

package okio;

import java.util.zip.*;
import java.io.*;
import org.codehaus.mojo.animal_sniffer.*;

public final class DeflaterSink implements Sink
{
    private final BufferedSink sink;
    private final Deflater deflater;
    private boolean closed;
    
    public DeflaterSink(final Sink sink, final Deflater deflater) {
        this(Okio.buffer(sink), deflater);
    }
    
    DeflaterSink(final BufferedSink sink, final Deflater deflater) {
        if (sink == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (deflater == null) {
            throw new IllegalArgumentException("inflater == null");
        }
        this.sink = sink;
        this.deflater = deflater;
    }
    
    @Override
    public void write(final Buffer source, long byteCount) throws IOException {
        Util.checkOffsetAndCount(source.size, 0L, byteCount);
        while (byteCount > 0L) {
            final Segment head = source.head;
            final int toDeflate = (int)Math.min(byteCount, head.limit - head.pos);
            this.deflater.setInput(head.data, head.pos, toDeflate);
            this.deflate(false);
            source.size -= toDeflate;
            final Segment segment = head;
            segment.pos += toDeflate;
            if (head.pos == head.limit) {
                source.head = head.pop();
                SegmentPool.recycle(head);
            }
            byteCount -= toDeflate;
        }
    }
    
    @IgnoreJRERequirement
    private void deflate(final boolean syncFlush) throws IOException {
        final Buffer buffer = this.sink.buffer();
        Segment s;
        while (true) {
            s = buffer.writableSegment(1);
            final int deflated = syncFlush ? this.deflater.deflate(s.data, s.limit, 8192 - s.limit, 2) : this.deflater.deflate(s.data, s.limit, 8192 - s.limit);
            if (deflated > 0) {
                final Segment segment = s;
                segment.limit += deflated;
                final Buffer buffer2 = buffer;
                buffer2.size += deflated;
                this.sink.emitCompleteSegments();
            }
            else {
                if (this.deflater.needsInput()) {
                    break;
                }
                continue;
            }
        }
        if (s.pos == s.limit) {
            buffer.head = s.pop();
            SegmentPool.recycle(s);
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.deflate(true);
        this.sink.flush();
    }
    
    void finishDeflate() throws IOException {
        this.deflater.finish();
        this.deflate(false);
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        Throwable thrown = null;
        try {
            this.finishDeflate();
        }
        catch (Throwable e) {
            thrown = e;
        }
        try {
            this.deflater.end();
        }
        catch (Throwable e) {
            if (thrown == null) {
                thrown = e;
            }
        }
        try {
            this.sink.close();
        }
        catch (Throwable e) {
            if (thrown == null) {
                thrown = e;
            }
        }
        this.closed = true;
        if (thrown != null) {
            Util.sneakyRethrow(thrown);
        }
    }
    
    @Override
    public Timeout timeout() {
        return this.sink.timeout();
    }
    
    @Override
    public String toString() {
        return "DeflaterSink(" + this.sink + ")";
    }
}
