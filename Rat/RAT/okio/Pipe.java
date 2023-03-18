//Raddon On Top!

package okio;

import javax.annotation.*;
import java.io.*;

public final class Pipe
{
    final long maxBufferSize;
    final Buffer buffer;
    boolean sinkClosed;
    boolean sourceClosed;
    private final Sink sink;
    private final Source source;
    @Nullable
    private Sink foldedSink;
    
    public Pipe(final long maxBufferSize) {
        this.buffer = new Buffer();
        this.sink = new PipeSink();
        this.source = new PipeSource();
        if (maxBufferSize < 1L) {
            throw new IllegalArgumentException("maxBufferSize < 1: " + maxBufferSize);
        }
        this.maxBufferSize = maxBufferSize;
    }
    
    public final Source source() {
        return this.source;
    }
    
    public final Sink sink() {
        return this.sink;
    }
    
    public void fold(final Sink sink) throws IOException {
        while (true) {
            final Buffer sinkBuffer;
            synchronized (this.buffer) {
                if (this.foldedSink != null) {
                    throw new IllegalStateException("sink already folded");
                }
                if (this.buffer.exhausted()) {
                    this.sourceClosed = true;
                    this.foldedSink = sink;
                    return;
                }
                sinkBuffer = new Buffer();
                sinkBuffer.write(this.buffer, this.buffer.size);
                this.buffer.notifyAll();
            }
            boolean success = false;
            try {
                sink.write(sinkBuffer, sinkBuffer.size);
                sink.flush();
                success = true;
            }
            finally {
                if (!success) {
                    synchronized (this.buffer) {
                        this.sourceClosed = true;
                        this.buffer.notifyAll();
                    }
                }
            }
        }
    }
    
    final class PipeSink implements Sink
    {
        final PushableTimeout timeout;
        
        PipeSink() {
            this.timeout = new PushableTimeout();
        }
        
        @Override
        public void write(final Buffer source, long byteCount) throws IOException {
            Sink delegate = null;
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sinkClosed) {
                    throw new IllegalStateException("closed");
                }
                while (byteCount > 0L) {
                    if (Pipe.this.foldedSink != null) {
                        delegate = Pipe.this.foldedSink;
                        break;
                    }
                    if (Pipe.this.sourceClosed) {
                        throw new IOException("source is closed");
                    }
                    final long bufferSpaceAvailable = Pipe.this.maxBufferSize - Pipe.this.buffer.size();
                    if (bufferSpaceAvailable == 0L) {
                        this.timeout.waitUntilNotified(Pipe.this.buffer);
                    }
                    else {
                        final long bytesToWrite = Math.min(bufferSpaceAvailable, byteCount);
                        Pipe.this.buffer.write(source, bytesToWrite);
                        byteCount -= bytesToWrite;
                        Pipe.this.buffer.notifyAll();
                    }
                }
            }
            if (delegate != null) {
                this.timeout.push(delegate.timeout());
                try {
                    delegate.write(source, byteCount);
                }
                finally {
                    this.timeout.pop();
                }
            }
        }
        
        @Override
        public void flush() throws IOException {
            Sink delegate = null;
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sinkClosed) {
                    throw new IllegalStateException("closed");
                }
                if (Pipe.this.foldedSink != null) {
                    delegate = Pipe.this.foldedSink;
                }
                else if (Pipe.this.sourceClosed && Pipe.this.buffer.size() > 0L) {
                    throw new IOException("source is closed");
                }
            }
            if (delegate != null) {
                this.timeout.push(delegate.timeout());
                try {
                    delegate.flush();
                }
                finally {
                    this.timeout.pop();
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            Sink delegate = null;
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sinkClosed) {
                    return;
                }
                if (Pipe.this.foldedSink != null) {
                    delegate = Pipe.this.foldedSink;
                }
                else {
                    if (Pipe.this.sourceClosed && Pipe.this.buffer.size() > 0L) {
                        throw new IOException("source is closed");
                    }
                    Pipe.this.sinkClosed = true;
                    Pipe.this.buffer.notifyAll();
                }
            }
            if (delegate != null) {
                this.timeout.push(delegate.timeout());
                try {
                    delegate.close();
                }
                finally {
                    this.timeout.pop();
                }
            }
        }
        
        @Override
        public Timeout timeout() {
            return this.timeout;
        }
    }
    
    final class PipeSource implements Source
    {
        final Timeout timeout;
        
        PipeSource() {
            this.timeout = new Timeout();
        }
        
        @Override
        public long read(final Buffer sink, final long byteCount) throws IOException {
            synchronized (Pipe.this.buffer) {
                if (Pipe.this.sourceClosed) {
                    throw new IllegalStateException("closed");
                }
                while (Pipe.this.buffer.size() == 0L) {
                    if (Pipe.this.sinkClosed) {
                        return -1L;
                    }
                    this.timeout.waitUntilNotified(Pipe.this.buffer);
                }
                final long result = Pipe.this.buffer.read(sink, byteCount);
                Pipe.this.buffer.notifyAll();
                return result;
            }
        }
        
        @Override
        public void close() throws IOException {
            synchronized (Pipe.this.buffer) {
                Pipe.this.sourceClosed = true;
                Pipe.this.buffer.notifyAll();
            }
        }
        
        @Override
        public Timeout timeout() {
            return this.timeout;
        }
    }
}
