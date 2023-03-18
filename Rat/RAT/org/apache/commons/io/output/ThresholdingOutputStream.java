//Raddon On Top!

package org.apache.commons.io.output;

import org.apache.commons.io.function.*;
import java.io.*;

public class ThresholdingOutputStream extends OutputStream
{
    private static final IOFunction<ThresholdingOutputStream, OutputStream> NOOP_OS_GETTER;
    private final int threshold;
    private final IOConsumer<ThresholdingOutputStream> thresholdConsumer;
    private final IOFunction<ThresholdingOutputStream, OutputStream> outputStreamGetter;
    private long written;
    private boolean thresholdExceeded;
    
    public ThresholdingOutputStream(final int threshold) {
        this(threshold, (IOConsumer<ThresholdingOutputStream>)IOConsumer.noop(), ThresholdingOutputStream.NOOP_OS_GETTER);
    }
    
    public ThresholdingOutputStream(final int threshold, final IOConsumer<ThresholdingOutputStream> thresholdConsumer, final IOFunction<ThresholdingOutputStream, OutputStream> outputStreamGetter) {
        this.threshold = threshold;
        this.thresholdConsumer = (IOConsumer<ThresholdingOutputStream>)((thresholdConsumer == null) ? IOConsumer.noop() : thresholdConsumer);
        this.outputStreamGetter = ((outputStreamGetter == null) ? ThresholdingOutputStream.NOOP_OS_GETTER : outputStreamGetter);
    }
    
    protected void checkThreshold(final int count) throws IOException {
        if (!this.thresholdExceeded && this.written + count > this.threshold) {
            this.thresholdExceeded = true;
            this.thresholdReached();
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.flush();
        }
        catch (IOException ex) {}
        this.getStream().close();
    }
    
    @Override
    public void flush() throws IOException {
        this.getStream().flush();
    }
    
    public long getByteCount() {
        return this.written;
    }
    
    protected OutputStream getStream() throws IOException {
        return (OutputStream)this.outputStreamGetter.apply((Object)this);
    }
    
    public int getThreshold() {
        return this.threshold;
    }
    
    public boolean isThresholdExceeded() {
        return this.written > this.threshold;
    }
    
    protected void resetByteCount() {
        this.thresholdExceeded = false;
        this.written = 0L;
    }
    
    protected void setByteCount(final long count) {
        this.written = count;
    }
    
    protected void thresholdReached() throws IOException {
        this.thresholdConsumer.accept((Object)this);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.checkThreshold(b.length);
        this.getStream().write(b);
        this.written += b.length;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.checkThreshold(len);
        this.getStream().write(b, off, len);
        this.written += len;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.checkThreshold(1);
        this.getStream().write(b);
        ++this.written;
    }
    
    static {
        NOOP_OS_GETTER = (os -> NullOutputStream.NULL_OUTPUT_STREAM);
    }
}
