//Raddon On Top!

package okio;

import java.nio.channels.*;
import java.nio.charset.*;
import java.io.*;

public interface BufferedSink extends Sink, WritableByteChannel
{
    Buffer buffer();
    
    BufferedSink write(final ByteString p0) throws IOException;
    
    BufferedSink write(final byte[] p0) throws IOException;
    
    BufferedSink write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    long writeAll(final Source p0) throws IOException;
    
    BufferedSink write(final Source p0, final long p1) throws IOException;
    
    BufferedSink writeUtf8(final String p0) throws IOException;
    
    BufferedSink writeUtf8(final String p0, final int p1, final int p2) throws IOException;
    
    BufferedSink writeUtf8CodePoint(final int p0) throws IOException;
    
    BufferedSink writeString(final String p0, final Charset p1) throws IOException;
    
    BufferedSink writeString(final String p0, final int p1, final int p2, final Charset p3) throws IOException;
    
    BufferedSink writeByte(final int p0) throws IOException;
    
    BufferedSink writeShort(final int p0) throws IOException;
    
    BufferedSink writeShortLe(final int p0) throws IOException;
    
    BufferedSink writeInt(final int p0) throws IOException;
    
    BufferedSink writeIntLe(final int p0) throws IOException;
    
    BufferedSink writeLong(final long p0) throws IOException;
    
    BufferedSink writeLongLe(final long p0) throws IOException;
    
    BufferedSink writeDecimalLong(final long p0) throws IOException;
    
    BufferedSink writeHexadecimalUnsignedLong(final long p0) throws IOException;
    
    void flush() throws IOException;
    
    BufferedSink emit() throws IOException;
    
    BufferedSink emitCompleteSegments() throws IOException;
    
    OutputStream outputStream();
}
