//Raddon On Top!

package okhttp3.internal.ws;

import java.io.*;
import java.util.concurrent.*;
import java.net.*;
import okio.*;

final class WebSocketReader
{
    final boolean isClient;
    final BufferedSource source;
    final FrameCallback frameCallback;
    boolean closed;
    int opcode;
    long frameLength;
    boolean isFinalFrame;
    boolean isControlFrame;
    private final Buffer controlFrameBuffer;
    private final Buffer messageFrameBuffer;
    private final byte[] maskKey;
    private final Buffer.UnsafeCursor maskCursor;
    
    WebSocketReader(final boolean isClient, final BufferedSource source, final FrameCallback frameCallback) {
        this.controlFrameBuffer = new Buffer();
        this.messageFrameBuffer = new Buffer();
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (frameCallback == null) {
            throw new NullPointerException("frameCallback == null");
        }
        this.isClient = isClient;
        this.source = source;
        this.frameCallback = frameCallback;
        this.maskKey = (byte[])(isClient ? null : new byte[4]);
        this.maskCursor = (isClient ? null : new Buffer.UnsafeCursor());
    }
    
    void processNextFrame() throws IOException {
        this.readHeader();
        if (this.isControlFrame) {
            this.readControlFrame();
        }
        else {
            this.readMessageFrame();
        }
    }
    
    private void readHeader() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        final long timeoutBefore = this.source.timeout().timeoutNanos();
        this.source.timeout().clearTimeout();
        int b0;
        try {
            b0 = (this.source.readByte() & 0xFF);
        }
        finally {
            this.source.timeout().timeout(timeoutBefore, TimeUnit.NANOSECONDS);
        }
        this.opcode = (b0 & 0xF);
        this.isFinalFrame = ((b0 & 0x80) != 0x0);
        this.isControlFrame = ((b0 & 0x8) != 0x0);
        if (this.isControlFrame && !this.isFinalFrame) {
            throw new ProtocolException("Control frames must be final.");
        }
        final boolean reservedFlag1 = (b0 & 0x40) != 0x0;
        final boolean reservedFlag2 = (b0 & 0x20) != 0x0;
        final boolean reservedFlag3 = (b0 & 0x10) != 0x0;
        if (reservedFlag1 || reservedFlag2 || reservedFlag3) {
            throw new ProtocolException("Reserved flags are unsupported.");
        }
        final int b2 = this.source.readByte() & 0xFF;
        final boolean isMasked = (b2 & 0x80) != 0x0;
        if (isMasked == this.isClient) {
            throw new ProtocolException(this.isClient ? "Server-sent frames must not be masked." : "Client-sent frames must be masked.");
        }
        this.frameLength = (b2 & 0x7F);
        if (this.frameLength == 126L) {
            this.frameLength = ((long)this.source.readShort() & 0xFFFFL);
        }
        else if (this.frameLength == 127L) {
            this.frameLength = this.source.readLong();
            if (this.frameLength < 0L) {
                throw new ProtocolException("Frame length 0x" + Long.toHexString(this.frameLength) + " > 0x7FFFFFFFFFFFFFFF");
            }
        }
        if (this.isControlFrame && this.frameLength > 125L) {
            throw new ProtocolException("Control frame must be less than 125B.");
        }
        if (isMasked) {
            this.source.readFully(this.maskKey);
        }
    }
    
    private void readControlFrame() throws IOException {
        if (this.frameLength > 0L) {
            this.source.readFully(this.controlFrameBuffer, this.frameLength);
            if (!this.isClient) {
                this.controlFrameBuffer.readAndWriteUnsafe(this.maskCursor);
                this.maskCursor.seek(0L);
                WebSocketProtocol.toggleMask(this.maskCursor, this.maskKey);
                this.maskCursor.close();
            }
        }
        switch (this.opcode) {
            case 9: {
                this.frameCallback.onReadPing(this.controlFrameBuffer.readByteString());
                break;
            }
            case 10: {
                this.frameCallback.onReadPong(this.controlFrameBuffer.readByteString());
                break;
            }
            case 8: {
                int code = 1005;
                String reason = "";
                final long bufferSize = this.controlFrameBuffer.size();
                if (bufferSize == 1L) {
                    throw new ProtocolException("Malformed close payload length of 1.");
                }
                if (bufferSize != 0L) {
                    code = this.controlFrameBuffer.readShort();
                    reason = this.controlFrameBuffer.readUtf8();
                    final String codeExceptionMessage = WebSocketProtocol.closeCodeExceptionMessage(code);
                    if (codeExceptionMessage != null) {
                        throw new ProtocolException(codeExceptionMessage);
                    }
                }
                this.frameCallback.onReadClose(code, reason);
                this.closed = true;
                break;
            }
            default: {
                throw new ProtocolException("Unknown control opcode: " + Integer.toHexString(this.opcode));
            }
        }
    }
    
    private void readMessageFrame() throws IOException {
        final int opcode = this.opcode;
        if (opcode != 1 && opcode != 2) {
            throw new ProtocolException("Unknown opcode: " + Integer.toHexString(opcode));
        }
        this.readMessage();
        if (opcode == 1) {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readUtf8());
        }
        else {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readByteString());
        }
    }
    
    private void readUntilNonControlFrame() throws IOException {
        while (!this.closed) {
            this.readHeader();
            if (!this.isControlFrame) {
                break;
            }
            this.readControlFrame();
        }
    }
    
    private void readMessage() throws IOException {
        while (!this.closed) {
            if (this.frameLength > 0L) {
                this.source.readFully(this.messageFrameBuffer, this.frameLength);
                if (!this.isClient) {
                    this.messageFrameBuffer.readAndWriteUnsafe(this.maskCursor);
                    this.maskCursor.seek(this.messageFrameBuffer.size() - this.frameLength);
                    WebSocketProtocol.toggleMask(this.maskCursor, this.maskKey);
                    this.maskCursor.close();
                }
            }
            if (this.isFinalFrame) {
                return;
            }
            this.readUntilNonControlFrame();
            if (this.opcode != 0) {
                throw new ProtocolException("Expected continuation opcode. Got: " + Integer.toHexString(this.opcode));
            }
        }
        throw new IOException("closed");
    }
    
    public interface FrameCallback
    {
        void onReadMessage(final String p0) throws IOException;
        
        void onReadMessage(final ByteString p0) throws IOException;
        
        void onReadPing(final ByteString p0);
        
        void onReadPong(final ByteString p0);
        
        void onReadClose(final int p0, final String p1);
    }
}
