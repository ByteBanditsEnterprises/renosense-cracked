//Raddon On Top!

package okio;

import java.util.zip.*;
import java.io.*;

public final class GzipSource implements Source
{
    private static final byte FHCRC = 1;
    private static final byte FEXTRA = 2;
    private static final byte FNAME = 3;
    private static final byte FCOMMENT = 4;
    private static final byte SECTION_HEADER = 0;
    private static final byte SECTION_BODY = 1;
    private static final byte SECTION_TRAILER = 2;
    private static final byte SECTION_DONE = 3;
    private int section;
    private final BufferedSource source;
    private final Inflater inflater;
    private final InflaterSource inflaterSource;
    private final CRC32 crc;
    
    public GzipSource(final Source source) {
        this.section = 0;
        this.crc = new CRC32();
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        this.inflater = new Inflater(true);
        this.source = Okio.buffer(source);
        this.inflaterSource = new InflaterSource(this.source, this.inflater);
    }
    
    @Override
    public long read(final Buffer sink, final long byteCount) throws IOException {
        if (byteCount < 0L) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        }
        if (byteCount == 0L) {
            return 0L;
        }
        if (this.section == 0) {
            this.consumeHeader();
            this.section = 1;
        }
        if (this.section == 1) {
            final long offset = sink.size;
            final long result = this.inflaterSource.read(sink, byteCount);
            if (result != -1L) {
                this.updateCrc(sink, offset, result);
                return result;
            }
            this.section = 2;
        }
        if (this.section == 2) {
            this.consumeTrailer();
            this.section = 3;
            if (!this.source.exhausted()) {
                throw new IOException("gzip finished without exhausting source");
            }
        }
        return -1L;
    }
    
    private void consumeHeader() throws IOException {
        this.source.require(10L);
        final byte flags = this.source.buffer().getByte(3L);
        final boolean fhcrc = (flags >> 1 & 0x1) == 0x1;
        if (fhcrc) {
            this.updateCrc(this.source.buffer(), 0L, 10L);
        }
        final short id1id2 = this.source.readShort();
        this.checkEqual("ID1ID2", 8075, id1id2);
        this.source.skip(8L);
        if ((flags >> 2 & 0x1) == 0x1) {
            this.source.require(2L);
            if (fhcrc) {
                this.updateCrc(this.source.buffer(), 0L, 2L);
            }
            final int xlen = this.source.buffer().readShortLe();
            this.source.require((long)xlen);
            if (fhcrc) {
                this.updateCrc(this.source.buffer(), 0L, xlen);
            }
            this.source.skip((long)xlen);
        }
        if ((flags >> 3 & 0x1) == 0x1) {
            final long index = this.source.indexOf((byte)0);
            if (index == -1L) {
                throw new EOFException();
            }
            if (fhcrc) {
                this.updateCrc(this.source.buffer(), 0L, index + 1L);
            }
            this.source.skip(index + 1L);
        }
        if ((flags >> 4 & 0x1) == 0x1) {
            final long index = this.source.indexOf((byte)0);
            if (index == -1L) {
                throw new EOFException();
            }
            if (fhcrc) {
                this.updateCrc(this.source.buffer(), 0L, index + 1L);
            }
            this.source.skip(index + 1L);
        }
        if (fhcrc) {
            this.checkEqual("FHCRC", this.source.readShortLe(), (short)this.crc.getValue());
            this.crc.reset();
        }
    }
    
    private void consumeTrailer() throws IOException {
        this.checkEqual("CRC", this.source.readIntLe(), (int)this.crc.getValue());
        this.checkEqual("ISIZE", this.source.readIntLe(), (int)this.inflater.getBytesWritten());
    }
    
    @Override
    public Timeout timeout() {
        return this.source.timeout();
    }
    
    @Override
    public void close() throws IOException {
        this.inflaterSource.close();
    }
    
    private void updateCrc(final Buffer buffer, long offset, long byteCount) {
        Segment s;
        for (s = buffer.head; offset >= s.limit - s.pos; offset -= s.limit - s.pos, s = s.next) {}
        while (byteCount > 0L) {
            final int pos = (int)(s.pos + offset);
            final int toUpdate = (int)Math.min(s.limit - pos, byteCount);
            this.crc.update(s.data, pos, toUpdate);
            byteCount -= toUpdate;
            offset = 0L;
            s = s.next;
        }
    }
    
    private void checkEqual(final String name, final int expected, final int actual) throws IOException {
        if (actual != expected) {
            throw new IOException(String.format("%s: actual 0x%08x != expected 0x%08x", name, actual, expected));
        }
    }
}
