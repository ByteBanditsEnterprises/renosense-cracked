//Raddon On Top!

package okio;

import javax.annotation.*;

final class Segment
{
    static final int SIZE = 8192;
    static final int SHARE_MINIMUM = 1024;
    final byte[] data;
    int pos;
    int limit;
    boolean shared;
    boolean owner;
    Segment next;
    Segment prev;
    
    Segment() {
        this.data = new byte[8192];
        this.owner = true;
        this.shared = false;
    }
    
    Segment(final byte[] data, final int pos, final int limit, final boolean shared, final boolean owner) {
        this.data = data;
        this.pos = pos;
        this.limit = limit;
        this.shared = shared;
        this.owner = owner;
    }
    
    final Segment sharedCopy() {
        this.shared = true;
        return new Segment(this.data, this.pos, this.limit, true, false);
    }
    
    final Segment unsharedCopy() {
        return new Segment(this.data.clone(), this.pos, this.limit, false, true);
    }
    
    @Nullable
    public final Segment pop() {
        final Segment result = (this.next != this) ? this.next : null;
        this.prev.next = this.next;
        this.next.prev = this.prev;
        this.next = null;
        this.prev = null;
        return result;
    }
    
    public final Segment push(final Segment segment) {
        segment.prev = this;
        segment.next = this.next;
        this.next.prev = segment;
        return this.next = segment;
    }
    
    public final Segment split(final int byteCount) {
        if (byteCount <= 0 || byteCount > this.limit - this.pos) {
            throw new IllegalArgumentException();
        }
        Segment prefix;
        if (byteCount >= 1024) {
            prefix = this.sharedCopy();
        }
        else {
            prefix = SegmentPool.take();
            System.arraycopy(this.data, this.pos, prefix.data, 0, byteCount);
        }
        prefix.limit = prefix.pos + byteCount;
        this.pos += byteCount;
        this.prev.push(prefix);
        return prefix;
    }
    
    public final void compact() {
        if (this.prev == this) {
            throw new IllegalStateException();
        }
        if (!this.prev.owner) {
            return;
        }
        final int byteCount = this.limit - this.pos;
        final int availableByteCount = 8192 - this.prev.limit + (this.prev.shared ? 0 : this.prev.pos);
        if (byteCount > availableByteCount) {
            return;
        }
        this.writeTo(this.prev, byteCount);
        this.pop();
        SegmentPool.recycle(this);
    }
    
    public final void writeTo(final Segment sink, final int byteCount) {
        if (!sink.owner) {
            throw new IllegalArgumentException();
        }
        if (sink.limit + byteCount > 8192) {
            if (sink.shared) {
                throw new IllegalArgumentException();
            }
            if (sink.limit + byteCount - sink.pos > 8192) {
                throw new IllegalArgumentException();
            }
            System.arraycopy(sink.data, sink.pos, sink.data, 0, sink.limit - sink.pos);
            sink.limit -= sink.pos;
            sink.pos = 0;
        }
        System.arraycopy(this.data, this.pos, sink.data, sink.limit, byteCount);
        sink.limit += byteCount;
        this.pos += byteCount;
    }
}
