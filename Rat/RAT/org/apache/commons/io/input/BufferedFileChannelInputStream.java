//Raddon On Top!

package org.apache.commons.io.input;

import java.nio.*;
import java.nio.channels.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.lang.reflect.*;

public final class BufferedFileChannelInputStream extends InputStream
{
    private final ByteBuffer byteBuffer;
    private final FileChannel fileChannel;
    private static final Class<?> DIRECT_BUFFER_CLASS;
    
    private static Class<?> getDirectBufferClass() {
        Class<?> res = null;
        try {
            res = Class.forName("sun.nio.ch.DirectBuffer");
        }
        catch (IllegalAccessError illegalAccessError) {}
        catch (ClassNotFoundException ex) {}
        return res;
    }
    
    private static boolean isDirectBuffer(final Object object) {
        return BufferedFileChannelInputStream.DIRECT_BUFFER_CLASS != null && BufferedFileChannelInputStream.DIRECT_BUFFER_CLASS.isInstance(object);
    }
    
    public BufferedFileChannelInputStream(final File file) throws IOException {
        this(file, 8192);
    }
    
    public BufferedFileChannelInputStream(final File file, final int bufferSizeInBytes) throws IOException {
        this(file.toPath(), bufferSizeInBytes);
    }
    
    public BufferedFileChannelInputStream(final Path path) throws IOException {
        this(path, 8192);
    }
    
    public BufferedFileChannelInputStream(final Path path, final int bufferSizeInBytes) throws IOException {
        Objects.requireNonNull(path, "path");
        this.fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        (this.byteBuffer = ByteBuffer.allocateDirect(bufferSizeInBytes)).flip();
    }
    
    @Override
    public synchronized int available() throws IOException {
        return this.byteBuffer.remaining();
    }
    
    private void clean(final ByteBuffer buffer) {
        if (isDirectBuffer(buffer)) {
            this.cleanDirectBuffer(buffer);
        }
    }
    
    private void cleanDirectBuffer(final ByteBuffer buffer) {
        final String specVer = System.getProperty("java.specification.version");
        if ("1.8".equals(specVer)) {
            try {
                final Class<?> clsCleaner = Class.forName("sun.misc.Cleaner");
                final Method cleanerMethod = BufferedFileChannelInputStream.DIRECT_BUFFER_CLASS.getMethod("cleaner", (Class<?>[])new Class[0]);
                final Object cleaner = cleanerMethod.invoke(buffer, new Object[0]);
                if (cleaner != null) {
                    final Method cleanMethod = clsCleaner.getMethod("clean", (Class<?>[])new Class[0]);
                    cleanMethod.invoke(cleaner, new Object[0]);
                }
                return;
            }
            catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
        try {
            final Class<?> clsUnsafe = Class.forName("sun.misc.Unsafe");
            final Method cleanerMethod = clsUnsafe.getMethod("invokeCleaner", ByteBuffer.class);
            final Field unsafeField = clsUnsafe.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            cleanerMethod.invoke(unsafeField.get(null), buffer);
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        try {
            this.fileChannel.close();
        }
        finally {
            this.clean(this.byteBuffer);
        }
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (!this.refill()) {
            return -1;
        }
        return this.byteBuffer.get() & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] b, final int offset, int len) throws IOException {
        if (offset < 0 || len < 0 || offset + len < 0 || offset + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.refill()) {
            return -1;
        }
        len = Math.min(len, this.byteBuffer.remaining());
        this.byteBuffer.get(b, offset, len);
        return len;
    }
    
    private boolean refill() throws IOException {
        if (!this.byteBuffer.hasRemaining()) {
            this.byteBuffer.clear();
            int nRead;
            for (nRead = 0; nRead == 0; nRead = this.fileChannel.read(this.byteBuffer)) {}
            this.byteBuffer.flip();
            return nRead >= 0;
        }
        return true;
    }
    
    @Override
    public synchronized long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        if (this.byteBuffer.remaining() >= n) {
            this.byteBuffer.position(this.byteBuffer.position() + (int)n);
            return n;
        }
        final long skippedFromBuffer = this.byteBuffer.remaining();
        final long toSkipFromFileChannel = n - skippedFromBuffer;
        this.byteBuffer.position(0);
        this.byteBuffer.flip();
        return skippedFromBuffer + this.skipFromFileChannel(toSkipFromFileChannel);
    }
    
    private long skipFromFileChannel(final long n) throws IOException {
        final long currentFilePosition = this.fileChannel.position();
        final long size = this.fileChannel.size();
        if (n > size - currentFilePosition) {
            this.fileChannel.position(size);
            return size - currentFilePosition;
        }
        this.fileChannel.position(currentFilePosition + n);
        return n;
    }
    
    static {
        DIRECT_BUFFER_CLASS = getDirectBufferClass();
    }
}
