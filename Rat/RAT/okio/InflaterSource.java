//Raddon On Top!

package okio;

import java.io.*;
import java.util.zip.*;

public final class InflaterSource implements Source
{
    private final BufferedSource source;
    private final Inflater inflater;
    private int bufferBytesHeldByInflater;
    private boolean closed;
    
    public InflaterSource(final Source source, final Inflater inflater) {
        this(Okio.buffer(source), inflater);
    }
    
    InflaterSource(final BufferedSource source, final Inflater inflater) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (inflater == null) {
            throw new IllegalArgumentException("inflater == null");
        }
        this.source = source;
        this.inflater = inflater;
    }
    
    @Override
    public long read(final Buffer sink, final long byteCount) throws IOException {
        if (byteCount < 0L) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        }
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (byteCount == 0L) {
            return 0L;
        }
        while (true) {
            final boolean sourceExhausted = this.refill();
            try {
                final Segment tail = sink.writableSegment(1);
                final int toRead = (int)Math.min(byteCount, 8192 - tail.limit);
                final int bytesInflated = this.inflater.inflate(tail.data, tail.limit, toRead);
                if (bytesInflated > 0) {
                    final Segment segment = tail;
                    segment.limit += bytesInflated;
                    sink.size += bytesInflated;
                    return bytesInflated;
                }
                if (this.inflater.finished() || this.inflater.needsDictionary()) {
                    this.releaseInflatedBytes();
                    if (tail.pos == tail.limit) {
                        sink.head = tail.pop();
                        SegmentPool.recycle(tail);
                    }
                    return -1L;
                }
                if (sourceExhausted) {
                    throw new EOFException("source exhausted prematurely");
                }
                continue;
            }
            catch (DataFormatException e) {
                throw new IOException(e);
            }
        }
    }
    
    public final boolean refill() throws IOException {
        if (!this.inflater.needsInput()) {
            return false;
        }
        this.releaseInflatedBytes();
        if (this.inflater.getRemaining() != 0) {
            throw new IllegalStateException("?");
        }
        if (this.source.exhausted()) {
            return true;
        }
        final Segment head = this.source.buffer().head;
        this.bufferBytesHeldByInflater = head.limit - head.pos;
        this.inflater.setInput(head.data, head.pos, this.bufferBytesHeldByInflater);
        return false;
    }
    
    private void releaseInflatedBytes() throws IOException {
        if (this.bufferBytesHeldByInflater == 0) {
            return;
        }
        final int toRelease = this.bufferBytesHeldByInflater - this.inflater.getRemaining();
        this.bufferBytesHeldByInflater -= toRelease;
        this.source.skip((long)toRelease);
    }
    
    @Override
    public Timeout timeout() {
        return this.source.timeout();
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.inflater.end();
        this.closed = true;
        this.source.close();
    }
}
