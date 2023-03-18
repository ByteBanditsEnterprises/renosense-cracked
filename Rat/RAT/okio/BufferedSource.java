//Raddon On Top!

package okio;

import java.nio.channels.*;
import javax.annotation.*;
import java.nio.charset.*;
import java.io.*;

public interface BufferedSource extends Source, ReadableByteChannel
{
    @Deprecated
    Buffer buffer();
    
    Buffer getBuffer();
    
    boolean exhausted() throws IOException;
    
    void require(final long p0) throws IOException;
    
    boolean request(final long p0) throws IOException;
    
    byte readByte() throws IOException;
    
    short readShort() throws IOException;
    
    short readShortLe() throws IOException;
    
    int readInt() throws IOException;
    
    int readIntLe() throws IOException;
    
    long readLong() throws IOException;
    
    long readLongLe() throws IOException;
    
    long readDecimalLong() throws IOException;
    
    long readHexadecimalUnsignedLong() throws IOException;
    
    void skip(final long p0) throws IOException;
    
    ByteString readByteString() throws IOException;
    
    ByteString readByteString(final long p0) throws IOException;
    
    int select(final Options p0) throws IOException;
    
    byte[] readByteArray() throws IOException;
    
    byte[] readByteArray(final long p0) throws IOException;
    
    int read(final byte[] p0) throws IOException;
    
    void readFully(final byte[] p0) throws IOException;
    
    int read(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void readFully(final Buffer p0, final long p1) throws IOException;
    
    long readAll(final Sink p0) throws IOException;
    
    String readUtf8() throws IOException;
    
    String readUtf8(final long p0) throws IOException;
    
    @Nullable
    String readUtf8Line() throws IOException;
    
    String readUtf8LineStrict() throws IOException;
    
    String readUtf8LineStrict(final long p0) throws IOException;
    
    int readUtf8CodePoint() throws IOException;
    
    String readString(final Charset p0) throws IOException;
    
    String readString(final long p0, final Charset p1) throws IOException;
    
    long indexOf(final byte p0) throws IOException;
    
    long indexOf(final byte p0, final long p1) throws IOException;
    
    long indexOf(final byte p0, final long p1, final long p2) throws IOException;
    
    long indexOf(final ByteString p0) throws IOException;
    
    long indexOf(final ByteString p0, final long p1) throws IOException;
    
    long indexOfElement(final ByteString p0) throws IOException;
    
    long indexOfElement(final ByteString p0, final long p1) throws IOException;
    
    boolean rangeEquals(final long p0, final ByteString p1) throws IOException;
    
    boolean rangeEquals(final long p0, final ByteString p1, final int p2, final int p3) throws IOException;
    
    BufferedSource peek();
    
    InputStream inputStream();
}
