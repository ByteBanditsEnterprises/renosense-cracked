//Raddon On Top!

package org.apache.commons.io.input;

import java.nio.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class ReadAheadInputStream extends InputStream
{
    private static final ThreadLocal<byte[]> oneByte;
    private final ReentrantLock stateChangeLock;
    private ByteBuffer activeBuffer;
    private ByteBuffer readAheadBuffer;
    private boolean endOfStream;
    private boolean readInProgress;
    private boolean readAborted;
    private Throwable readException;
    private boolean isClosed;
    private boolean isUnderlyingInputStreamBeingClosed;
    private boolean isReading;
    private final AtomicBoolean isWaiting;
    private final InputStream underlyingInputStream;
    private final ExecutorService executorService;
    private final boolean shutdownExecutorService;
    private final Condition asyncReadComplete;
    
    private static ExecutorService newExecutorService() {
        return Executors.newSingleThreadExecutor(ReadAheadInputStream::newThread);
    }
    
    private static Thread newThread(final Runnable r) {
        final Thread thread = new Thread(r, "commons-io-read-ahead");
        thread.setDaemon(true);
        return thread;
    }
    
    public ReadAheadInputStream(final InputStream inputStream, final int bufferSizeInBytes) {
        this(inputStream, bufferSizeInBytes, newExecutorService(), true);
    }
    
    public ReadAheadInputStream(final InputStream inputStream, final int bufferSizeInBytes, final ExecutorService executorService) {
        this(inputStream, bufferSizeInBytes, executorService, false);
    }
    
    private ReadAheadInputStream(final InputStream inputStream, final int bufferSizeInBytes, final ExecutorService executorService, final boolean shutdownExecutorService) {
        this.stateChangeLock = new ReentrantLock();
        this.isWaiting = new AtomicBoolean(false);
        this.asyncReadComplete = this.stateChangeLock.newCondition();
        if (bufferSizeInBytes <= 0) {
            throw new IllegalArgumentException("bufferSizeInBytes should be greater than 0, but the value is " + bufferSizeInBytes);
        }
        this.executorService = Objects.requireNonNull(executorService, "executorService");
        this.underlyingInputStream = Objects.requireNonNull(inputStream, "inputStream");
        this.shutdownExecutorService = shutdownExecutorService;
        this.activeBuffer = ByteBuffer.allocate(bufferSizeInBytes);
        this.readAheadBuffer = ByteBuffer.allocate(bufferSizeInBytes);
        this.activeBuffer.flip();
        this.readAheadBuffer.flip();
    }
    
    @Override
    public int available() throws IOException {
        this.stateChangeLock.lock();
        try {
            return (int)Math.min(2147483647L, this.activeBuffer.remaining() + (long)this.readAheadBuffer.remaining());
        }
        finally {
            this.stateChangeLock.unlock();
        }
    }
    
    private void checkReadException() throws IOException {
        if (!this.readAborted) {
            return;
        }
        if (this.readException instanceof IOException) {
            throw (IOException)this.readException;
        }
        throw new IOException(this.readException);
    }
    
    @Override
    public void close() throws IOException {
        boolean isSafeToCloseUnderlyingInputStream = false;
        this.stateChangeLock.lock();
        try {
            if (this.isClosed) {
                return;
            }
            this.isClosed = true;
            if (!this.isReading) {
                isSafeToCloseUnderlyingInputStream = true;
                this.isUnderlyingInputStreamBeingClosed = true;
            }
        }
        finally {
            this.stateChangeLock.unlock();
        }
        if (this.shutdownExecutorService) {
            try {
                this.executorService.shutdownNow();
                this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                final InterruptedIOException iio = new InterruptedIOException(e.getMessage());
                iio.initCause(e);
                throw iio;
            }
            finally {
                if (isSafeToCloseUnderlyingInputStream) {
                    this.underlyingInputStream.close();
                }
            }
        }
    }
    
    private void closeUnderlyingInputStreamIfNecessary() {
        boolean needToCloseUnderlyingInputStream = false;
        this.stateChangeLock.lock();
        try {
            this.isReading = false;
            if (this.isClosed && !this.isUnderlyingInputStreamBeingClosed) {
                needToCloseUnderlyingInputStream = true;
            }
        }
        finally {
            this.stateChangeLock.unlock();
        }
        if (needToCloseUnderlyingInputStream) {
            try {
                this.underlyingInputStream.close();
            }
            catch (IOException ex) {}
        }
    }
    
    private boolean isEndOfStream() {
        return !this.activeBuffer.hasRemaining() && !this.readAheadBuffer.hasRemaining() && this.endOfStream;
    }
    
    @Override
    public int read() throws IOException {
        if (this.activeBuffer.hasRemaining()) {
            return this.activeBuffer.get() & 0xFF;
        }
        final byte[] oneByteArray = ReadAheadInputStream.oneByte.get();
        return (this.read(oneByteArray, 0, 1) == -1) ? -1 : (oneByteArray[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] b, final int offset, int len) throws IOException {
        if (offset < 0 || len < 0 || len > b.length - offset) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (!this.activeBuffer.hasRemaining()) {
            this.stateChangeLock.lock();
            try {
                this.waitForAsyncReadComplete();
                if (!this.readAheadBuffer.hasRemaining()) {
                    this.readAsync();
                    this.waitForAsyncReadComplete();
                    if (this.isEndOfStream()) {
                        return -1;
                    }
                }
                this.swapBuffers();
                this.readAsync();
            }
            finally {
                this.stateChangeLock.unlock();
            }
        }
        len = Math.min(len, this.activeBuffer.remaining());
        this.activeBuffer.get(b, offset, len);
        return len;
    }
    
    private void readAsync() throws IOException {
        this.stateChangeLock.lock();
        try {
            final byte[] arr = this.readAheadBuffer.array();
            if (this.endOfStream || this.readInProgress) {
                return;
            }
            this.checkReadException();
            this.readAheadBuffer.position(0);
            this.readAheadBuffer.flip();
            this.readInProgress = true;
        }
        finally {
            this.stateChangeLock.unlock();
        }
        int read;
        int off;
        final byte[] array;
        int len;
        Throwable exception;
        this.executorService.execute(() -> {
            this.stateChangeLock.lock();
            try {
                if (this.isClosed) {
                    this.readInProgress = false;
                    return;
                }
                else {
                    this.isReading = true;
                }
            }
            finally {
                this.stateChangeLock.unlock();
            }
            read = 0;
            off = 0;
            len = array.length;
            exception = null;
            try {
                do {
                    read = this.underlyingInputStream.read(array, off, len);
                    if (read <= 0) {
                        break;
                    }
                    else {
                        off += read;
                        len -= read;
                    }
                } while (len > 0 && !this.isWaiting.get());
            }
            catch (Throwable ex) {
                exception = ex;
                if (ex instanceof Error) {
                    throw (Error)ex;
                }
            }
            finally {
                this.stateChangeLock.lock();
                try {
                    this.readAheadBuffer.limit(off);
                    if (read < 0 || exception instanceof EOFException) {
                        this.endOfStream = true;
                    }
                    else if (exception != null) {
                        this.readAborted = true;
                        this.readException = exception;
                    }
                    this.readInProgress = false;
                    this.signalAsyncReadComplete();
                }
                finally {
                    this.stateChangeLock.unlock();
                }
                this.closeUnderlyingInputStreamIfNecessary();
            }
        });
    }
    
    private void signalAsyncReadComplete() {
        this.stateChangeLock.lock();
        try {
            this.asyncReadComplete.signalAll();
        }
        finally {
            this.stateChangeLock.unlock();
        }
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        if (n <= this.activeBuffer.remaining()) {
            this.activeBuffer.position((int)n + this.activeBuffer.position());
            return n;
        }
        this.stateChangeLock.lock();
        long skipped;
        try {
            skipped = this.skipInternal(n);
        }
        finally {
            this.stateChangeLock.unlock();
        }
        return skipped;
    }
    
    private long skipInternal(final long n) throws IOException {
        assert this.stateChangeLock.isLocked();
        this.waitForAsyncReadComplete();
        if (this.isEndOfStream()) {
            return 0L;
        }
        if (this.available() < n) {
            final int skippedBytes = this.available();
            final long toSkip = n - skippedBytes;
            this.activeBuffer.position(0);
            this.activeBuffer.flip();
            this.readAheadBuffer.position(0);
            this.readAheadBuffer.flip();
            final long skippedFromInputStream = this.underlyingInputStream.skip(toSkip);
            this.readAsync();
            return skippedBytes + skippedFromInputStream;
        }
        int toSkip2 = (int)n;
        toSkip2 -= this.activeBuffer.remaining();
        assert toSkip2 > 0;
        this.activeBuffer.position(0);
        this.activeBuffer.flip();
        this.readAheadBuffer.position(toSkip2 + this.readAheadBuffer.position());
        this.swapBuffers();
        this.readAsync();
        return n;
    }
    
    private void swapBuffers() {
        final ByteBuffer temp = this.activeBuffer;
        this.activeBuffer = this.readAheadBuffer;
        this.readAheadBuffer = temp;
    }
    
    private void waitForAsyncReadComplete() throws IOException {
        this.stateChangeLock.lock();
        try {
            this.isWaiting.set(true);
            while (this.readInProgress) {
                this.asyncReadComplete.await();
            }
        }
        catch (InterruptedException e) {
            final InterruptedIOException iio = new InterruptedIOException(e.getMessage());
            iio.initCause(e);
            throw iio;
        }
        finally {
            this.isWaiting.set(false);
            this.stateChangeLock.unlock();
        }
        this.checkReadException();
    }
    
    static {
        oneByte = ThreadLocal.withInitial(() -> new byte[1]);
    }
}
