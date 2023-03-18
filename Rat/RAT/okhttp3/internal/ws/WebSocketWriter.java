//Raddon On Top!

package okhttp3.internal.ws;

import java.util.*;
import java.io.*;
import okio.*;

final class WebSocketWriter
{
    final boolean isClient;
    final Random random;
    final BufferedSink sink;
    final Buffer sinkBuffer;
    boolean writerClosed;
    final Buffer buffer;
    final FrameSink frameSink;
    boolean activeWriter;
    private final byte[] maskKey;
    private final Buffer.UnsafeCursor maskCursor;
    
    WebSocketWriter(final boolean isClient, final BufferedSink sink, final Random random) {
        this.buffer = new Buffer();
        this.frameSink = new FrameSink();
        if (sink == null) {
            throw new NullPointerException("sink == null");
        }
        if (random == null) {
            throw new NullPointerException("random == null");
        }
        this.isClient = isClient;
        this.sink = sink;
        this.sinkBuffer = sink.buffer();
        this.random = random;
        this.maskKey = (byte[])(isClient ? new byte[4] : null);
        this.maskCursor = (isClient ? new Buffer.UnsafeCursor() : null);
    }
    
    void writePing(final ByteString payload) throws IOException {
        this.writeControlFrame(9, payload);
    }
    
    void writePong(final ByteString payload) throws IOException {
        this.writeControlFrame(10, payload);
    }
    
    void writeClose(final int code, final ByteString reason) throws IOException {
        ByteString payload = ByteString.EMPTY;
        if (code != 0 || reason != null) {
            if (code != 0) {
                WebSocketProtocol.validateCloseCode(code);
            }
            final Buffer buffer = new Buffer();
            buffer.writeShort(code);
            if (reason != null) {
                buffer.write(reason);
            }
            payload = buffer.readByteString();
        }
        try {
            this.writeControlFrame(8, payload);
        }
        finally {
            this.writerClosed = true;
        }
    }
    
    private void writeControlFrame(final int opcode, final ByteString payload) throws IOException {
        if (this.writerClosed) {
            throw new IOException("closed");
        }
        final int length = payload.size();
        if (length > 125L) {
            throw new IllegalArgumentException("Payload size must be less than or equal to 125");
        }
        final int b0 = 0x80 | opcode;
        this.sinkBuffer.writeByte(b0);
        int b2 = length;
        if (this.isClient) {
            b2 |= 0x80;
            this.sinkBuffer.writeByte(b2);
            this.random.nextBytes(this.maskKey);
            this.sinkBuffer.write(this.maskKey);
            if (length > 0) {
                final long payloadStart = this.sinkBuffer.size();
                this.sinkBuffer.write(payload);
                this.sinkBuffer.readAndWriteUnsafe(this.maskCursor);
                this.maskCursor.seek(payloadStart);
                WebSocketProtocol.toggleMask(this.maskCursor, this.maskKey);
                this.maskCursor.close();
            }
        }
        else {
            this.sinkBuffer.writeByte(b2);
            this.sinkBuffer.write(payload);
        }
        this.sink.flush();
    }
    
    Sink newMessageSink(final int formatOpcode, final long contentLength) {
        if (this.activeWriter) {
            throw new IllegalStateException("Another message writer is active. Did you call close()?");
        }
        this.activeWriter = true;
        this.frameSink.formatOpcode = formatOpcode;
        this.frameSink.contentLength = contentLength;
        this.frameSink.isFirstFrame = true;
        this.frameSink.closed = false;
        return (Sink)this.frameSink;
    }
    
    void writeMessageFrame(final int formatOpcode, final long byteCount, final boolean isFirstFrame, final boolean isFinal) throws IOException {
        if (this.writerClosed) {
            throw new IOException("closed");
        }
        int b0 = isFirstFrame ? formatOpcode : 0;
        if (isFinal) {
            b0 |= 0x80;
        }
        this.sinkBuffer.writeByte(b0);
        int b2 = 0;
        if (this.isClient) {
            b2 |= 0x80;
        }
        if (byteCount <= 125L) {
            b2 |= (int)byteCount;
            this.sinkBuffer.writeByte(b2);
        }
        else if (byteCount <= 65535L) {
            b2 |= 0x7E;
            this.sinkBuffer.writeByte(b2);
            this.sinkBuffer.writeShort((int)byteCount);
        }
        else {
            b2 |= 0x7F;
            this.sinkBuffer.writeByte(b2);
            this.sinkBuffer.writeLong(byteCount);
        }
        if (this.isClient) {
            this.random.nextBytes(this.maskKey);
            this.sinkBuffer.write(this.maskKey);
            if (byteCount > 0L) {
                final long bufferStart = this.sinkBuffer.size();
                this.sinkBuffer.write(this.buffer, byteCount);
                this.sinkBuffer.readAndWriteUnsafe(this.maskCursor);
                this.maskCursor.seek(bufferStart);
                WebSocketProtocol.toggleMask(this.maskCursor, this.maskKey);
                this.maskCursor.close();
            }
        }
        else {
            this.sinkBuffer.write(this.buffer, byteCount);
        }
        this.sink.emit();
    }
    
    final class FrameSink implements Sink
    {
        int formatOpcode;
        long contentLength;
        boolean isFirstFrame;
        boolean closed;
        
        public void write(final Buffer source, final long byteCount) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            WebSocketWriter.this.buffer.write(source, byteCount);
            final boolean deferWrite = this.isFirstFrame && this.contentLength != -1L && WebSocketWriter.this.buffer.size() > this.contentLength - 8192L;
            final long emitCount = WebSocketWriter.this.buffer.completeSegmentByteCount();
            if (emitCount > 0L && !deferWrite) {
                WebSocketWriter.this.writeMessageFrame(this.formatOpcode, emitCount, this.isFirstFrame, false);
                this.isFirstFrame = false;
            }
        }
        
        public void flush() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            WebSocketWriter.this.writeMessageFrame(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, false);
            this.isFirstFrame = false;
        }
        
        public Timeout timeout() {
            return WebSocketWriter.this.sink.timeout();
        }
        
        public void close() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            WebSocketWriter.this.writeMessageFrame(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, true);
            this.closed = true;
            WebSocketWriter.this.activeWriter = false;
        }
    }
}
