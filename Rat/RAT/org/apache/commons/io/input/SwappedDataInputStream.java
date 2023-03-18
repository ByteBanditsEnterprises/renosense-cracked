//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import org.apache.commons.io.*;

public class SwappedDataInputStream extends ProxyInputStream implements DataInput
{
    public SwappedDataInputStream(final InputStream input) {
        super(input);
    }
    
    public boolean readBoolean() throws IOException, EOFException {
        return 0 != this.readByte();
    }
    
    public byte readByte() throws IOException, EOFException {
        return (byte)this.in.read();
    }
    
    public char readChar() throws IOException, EOFException {
        return (char)this.readShort();
    }
    
    public double readDouble() throws IOException, EOFException {
        return EndianUtils.readSwappedDouble(this.in);
    }
    
    public float readFloat() throws IOException, EOFException {
        return EndianUtils.readSwappedFloat(this.in);
    }
    
    public void readFully(final byte[] data) throws IOException, EOFException {
        this.readFully(data, 0, data.length);
    }
    
    public void readFully(final byte[] data, final int offset, final int length) throws IOException, EOFException {
        int count;
        for (int remaining = length; remaining > 0; remaining -= count) {
            final int location = offset + length - remaining;
            count = this.read(data, location, remaining);
            if (-1 == count) {
                throw new EOFException();
            }
        }
    }
    
    public int readInt() throws IOException, EOFException {
        return EndianUtils.readSwappedInteger(this.in);
    }
    
    public String readLine() throws IOException, EOFException {
        throw UnsupportedOperationExceptions.method("readLine");
    }
    
    public long readLong() throws IOException, EOFException {
        return EndianUtils.readSwappedLong(this.in);
    }
    
    public short readShort() throws IOException, EOFException {
        return EndianUtils.readSwappedShort(this.in);
    }
    
    public int readUnsignedByte() throws IOException, EOFException {
        return this.in.read();
    }
    
    public int readUnsignedShort() throws IOException, EOFException {
        return EndianUtils.readSwappedUnsignedShort(this.in);
    }
    
    public String readUTF() throws IOException, EOFException {
        throw UnsupportedOperationExceptions.method("readUTF");
    }
    
    public int skipBytes(final int count) throws IOException, EOFException {
        return (int)this.in.skip(count);
    }
}
