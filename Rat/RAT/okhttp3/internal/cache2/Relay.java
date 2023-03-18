//Raddon On Top!

package okhttp3.internal.cache2;

import okhttp3.internal.*;
import java.io.*;
import okio.*;

final class Relay
{
    private static final int SOURCE_UPSTREAM = 1;
    private static final int SOURCE_FILE = 2;
    static final ByteString PREFIX_CLEAN;
    static final ByteString PREFIX_DIRTY;
    private static final long FILE_HEADER_SIZE = 32L;
    RandomAccessFile file;
    Thread upstreamReader;
    Source upstream;
    final Buffer upstreamBuffer;
    long upstreamPos;
    boolean complete;
    private final ByteString metadata;
    final Buffer buffer;
    final long bufferMaxSize;
    int sourceCount;
    
    private Relay(final RandomAccessFile file, final Source upstream, final long upstreamPos, final ByteString metadata, final long bufferMaxSize) {
        this.upstreamBuffer = new Buffer();
        this.buffer = new Buffer();
        this.file = file;
        this.upstream = upstream;
        this.complete = (upstream == null);
        this.upstreamPos = upstreamPos;
        this.metadata = metadata;
        this.bufferMaxSize = bufferMaxSize;
    }
    
    public static Relay edit(final File file, final Source upstream, final ByteString metadata, final long bufferMaxSize) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        final Relay result = new Relay(randomAccessFile, upstream, 0L, metadata, bufferMaxSize);
        randomAccessFile.setLength(0L);
        result.writeHeader(Relay.PREFIX_DIRTY, -1L, -1L);
        return result;
    }
    
    public static Relay read(final File file) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        final FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
        final Buffer header = new Buffer();
        fileOperator.read(0L, header, 32L);
        final ByteString prefix = header.readByteString((long)Relay.PREFIX_CLEAN.size());
        if (!prefix.equals((Object)Relay.PREFIX_CLEAN)) {
            throw new IOException("unreadable cache file");
        }
        final long upstreamSize = header.readLong();
        final long metadataSize = header.readLong();
        final Buffer metadataBuffer = new Buffer();
        fileOperator.read(32L + upstreamSize, metadataBuffer, metadataSize);
        final ByteString metadata = metadataBuffer.readByteString();
        return new Relay(randomAccessFile, null, upstreamSize, metadata, 0L);
    }
    
    private void writeHeader(final ByteString prefix, final long upstreamSize, final long metadataSize) throws IOException {
        final Buffer header = new Buffer();
        header.write(prefix);
        header.writeLong(upstreamSize);
        header.writeLong(metadataSize);
        if (header.size() != 32L) {
            throw new IllegalArgumentException();
        }
        final FileOperator fileOperator = new FileOperator(this.file.getChannel());
        fileOperator.write(0L, header, 32L);
    }
    
    private void writeMetadata(final long upstreamSize) throws IOException {
        final Buffer metadataBuffer = new Buffer();
        metadataBuffer.write(this.metadata);
        final FileOperator fileOperator = new FileOperator(this.file.getChannel());
        fileOperator.write(32L + upstreamSize, metadataBuffer, (long)this.metadata.size());
    }
    
    void commit(final long upstreamSize) throws IOException {
        this.writeMetadata(upstreamSize);
        this.file.getChannel().force(false);
        this.writeHeader(Relay.PREFIX_CLEAN, upstreamSize, this.metadata.size());
        this.file.getChannel().force(false);
        synchronized (this) {
            this.complete = true;
        }
        Util.closeQuietly((Closeable)this.upstream);
        this.upstream = null;
    }
    
    boolean isClosed() {
        return this.file == null;
    }
    
    public ByteString metadata() {
        return this.metadata;
    }
    
    public Source newSource() {
        synchronized (this) {
            if (this.file == null) {
                return null;
            }
            ++this.sourceCount;
        }
        return (Source)new RelaySource();
    }
    
    static {
        PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
        PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
    }
    
    class RelaySource implements Source
    {
        private final Timeout timeout;
        private FileOperator fileOperator;
        private long sourcePos;
        
        RelaySource() {
            this.timeout = new Timeout();
            this.fileOperator = new FileOperator(Relay.this.file.getChannel());
        }
        
        public long read(final Buffer sink, final long byteCount) throws IOException {
            if (this.fileOperator == null) {
                throw new IllegalStateException("closed");
            }
            long upstreamPos = 0L;
            int source = 0;
            Label_0196: {
                synchronized (Relay.this) {
                    while (this.sourcePos == (upstreamPos = Relay.this.upstreamPos)) {
                        if (Relay.this.complete) {
                            return -1L;
                        }
                        if (Relay.this.upstreamReader == null) {
                            Relay.this.upstreamReader = Thread.currentThread();
                            source = 1;
                            break Label_0196;
                        }
                        this.timeout.waitUntilNotified((Object)Relay.this);
                    }
                    final long bufferPos = upstreamPos - Relay.this.buffer.size();
                    if (this.sourcePos >= bufferPos) {
                        final long bytesToRead = Math.min(byteCount, upstreamPos - this.sourcePos);
                        Relay.this.buffer.copyTo(sink, this.sourcePos - bufferPos, bytesToRead);
                        this.sourcePos += bytesToRead;
                        return bytesToRead;
                    }
                    source = 2;
                }
            }
            if (source == 2) {
                final long bytesToRead2 = Math.min(byteCount, upstreamPos - this.sourcePos);
                this.fileOperator.read(32L + this.sourcePos, sink, bytesToRead2);
                this.sourcePos += bytesToRead2;
                return bytesToRead2;
            }
            try {
                final long upstreamBytesRead = Relay.this.upstream.read(Relay.this.upstreamBuffer, Relay.this.bufferMaxSize);
                if (upstreamBytesRead == -1L) {
                    Relay.this.commit(upstreamPos);
                    return -1L;
                }
                final long bytesRead = Math.min(upstreamBytesRead, byteCount);
                Relay.this.upstreamBuffer.copyTo(sink, 0L, bytesRead);
                this.sourcePos += bytesRead;
                this.fileOperator.write(32L + upstreamPos, Relay.this.upstreamBuffer.clone(), upstreamBytesRead);
                synchronized (Relay.this) {
                    Relay.this.buffer.write(Relay.this.upstreamBuffer, upstreamBytesRead);
                    if (Relay.this.buffer.size() > Relay.this.bufferMaxSize) {
                        Relay.this.buffer.skip(Relay.this.buffer.size() - Relay.this.bufferMaxSize);
                    }
                    final Relay this$0 = Relay.this;
                    this$0.upstreamPos += upstreamBytesRead;
                }
                return bytesRead;
            }
            finally {
                synchronized (Relay.this) {
                    Relay.this.upstreamReader = null;
                    Relay.this.notifyAll();
                }
            }
        }
        
        public Timeout timeout() {
            return this.timeout;
        }
        
        public void close() throws IOException {
            if (this.fileOperator == null) {
                return;
            }
            this.fileOperator = null;
            RandomAccessFile fileToClose = null;
            synchronized (Relay.this) {
                final Relay this$0 = Relay.this;
                --this$0.sourceCount;
                if (Relay.this.sourceCount == 0) {
                    fileToClose = Relay.this.file;
                    Relay.this.file = null;
                }
            }
            if (fileToClose != null) {
                Util.closeQuietly(fileToClose);
            }
        }
    }
}
