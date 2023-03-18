//Raddon On Top!

package org.apache.commons.io.output;

import java.nio.file.*;
import java.io.*;
import org.apache.commons.io.*;

public class DeferredFileOutputStream extends ThresholdingOutputStream
{
    private ByteArrayOutputStream memoryOutputStream;
    private OutputStream currentOutputStream;
    private File outputFile;
    private final String prefix;
    private final String suffix;
    private final File directory;
    private boolean closed;
    
    public DeferredFileOutputStream(final int threshold, final File outputFile) {
        this(threshold, outputFile, null, null, null, 1024);
    }
    
    private DeferredFileOutputStream(final int threshold, final File outputFile, final String prefix, final String suffix, final File directory, final int initialBufferSize) {
        super(threshold);
        this.outputFile = outputFile;
        this.prefix = prefix;
        this.suffix = suffix;
        this.directory = directory;
        this.memoryOutputStream = new ByteArrayOutputStream(initialBufferSize);
        this.currentOutputStream = (OutputStream)this.memoryOutputStream;
    }
    
    public DeferredFileOutputStream(final int threshold, final int initialBufferSize, final File outputFile) {
        this(threshold, outputFile, null, null, null, initialBufferSize);
        if (initialBufferSize < 0) {
            throw new IllegalArgumentException("Initial buffer size must be atleast 0.");
        }
    }
    
    public DeferredFileOutputStream(final int threshold, final int initialBufferSize, final String prefix, final String suffix, final File directory) {
        this(threshold, null, prefix, suffix, directory, initialBufferSize);
        if (prefix == null) {
            throw new IllegalArgumentException("Temporary file prefix is missing");
        }
        if (initialBufferSize < 0) {
            throw new IllegalArgumentException("Initial buffer size must be atleast 0.");
        }
    }
    
    public DeferredFileOutputStream(final int threshold, final String prefix, final String suffix, final File directory) {
        this(threshold, null, prefix, suffix, directory, 1024);
        if (prefix == null) {
            throw new IllegalArgumentException("Temporary file prefix is missing");
        }
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        this.closed = true;
    }
    
    public byte[] getData() {
        return (byte[])((this.memoryOutputStream != null) ? this.memoryOutputStream.toByteArray() : null);
    }
    
    public File getFile() {
        return this.outputFile;
    }
    
    @Override
    protected OutputStream getStream() throws IOException {
        return this.currentOutputStream;
    }
    
    public boolean isInMemory() {
        return !this.isThresholdExceeded();
    }
    
    @Override
    protected void thresholdReached() throws IOException {
        if (this.prefix != null) {
            this.outputFile = File.createTempFile(this.prefix, this.suffix, this.directory);
        }
        FileUtils.forceMkdirParent(this.outputFile);
        final OutputStream fos = Files.newOutputStream(this.outputFile.toPath(), new OpenOption[0]);
        try {
            this.memoryOutputStream.writeTo(fos);
        }
        catch (IOException e) {
            fos.close();
            throw e;
        }
        this.currentOutputStream = fos;
        this.memoryOutputStream = null;
    }
    
    public InputStream toInputStream() throws IOException {
        if (!this.closed) {
            throw new IOException("Stream not closed");
        }
        if (this.isInMemory()) {
            return this.memoryOutputStream.toInputStream();
        }
        return Files.newInputStream(this.outputFile.toPath(), new OpenOption[0]);
    }
    
    public void writeTo(final OutputStream outputStream) throws IOException {
        if (!this.closed) {
            throw new IOException("Stream not closed");
        }
        if (this.isInMemory()) {
            this.memoryOutputStream.writeTo(outputStream);
        }
        else {
            try (final InputStream fis = Files.newInputStream(this.outputFile.toPath(), new OpenOption[0])) {
                IOUtils.copy(fis, outputStream);
            }
        }
    }
}
