//Raddon On Top!

package org.apache.commons.io.filefilter;

import org.apache.commons.io.*;
import java.nio.charset.*;
import java.util.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.nio.*;

public class MagicNumberFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -547733176983104172L;
    private final byte[] magicNumbers;
    private final long byteOffset;
    
    public MagicNumberFileFilter(final byte[] magicNumber) {
        this(magicNumber, 0L);
    }
    
    public MagicNumberFileFilter(final byte[] magicNumber, final long offset) {
        if (magicNumber == null) {
            throw new IllegalArgumentException("The magic number cannot be null");
        }
        if (magicNumber.length == 0) {
            throw new IllegalArgumentException("The magic number must contain at least one byte");
        }
        if (offset < 0L) {
            throw new IllegalArgumentException("The offset cannot be negative");
        }
        System.arraycopy(magicNumber, 0, this.magicNumbers = IOUtils.byteArray(magicNumber.length), 0, magicNumber.length);
        this.byteOffset = offset;
    }
    
    public MagicNumberFileFilter(final String magicNumber) {
        this(magicNumber, 0L);
    }
    
    public MagicNumberFileFilter(final String magicNumber, final long offset) {
        if (magicNumber == null) {
            throw new IllegalArgumentException("The magic number cannot be null");
        }
        if (magicNumber.isEmpty()) {
            throw new IllegalArgumentException("The magic number must contain at least one byte");
        }
        if (offset < 0L) {
            throw new IllegalArgumentException("The offset cannot be negative");
        }
        this.magicNumbers = magicNumber.getBytes(Charset.defaultCharset());
        this.byteOffset = offset;
    }
    
    public boolean accept(final File file) {
        if (file != null && file.isFile() && file.canRead()) {
            try (final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                final byte[] fileBytes = IOUtils.byteArray(this.magicNumbers.length);
                randomAccessFile.seek(this.byteOffset);
                final int read = randomAccessFile.read(fileBytes);
                if (read != this.magicNumbers.length) {
                    return false;
                }
                return Arrays.equals(this.magicNumbers, fileBytes);
            }
            catch (IOException ex) {}
        }
        return false;
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        if (file != null && Files.isRegularFile(file, new LinkOption[0]) && Files.isReadable(file)) {
            try (final FileChannel fileChannel = FileChannel.open(file, new OpenOption[0])) {
                final ByteBuffer byteBuffer = ByteBuffer.allocate(this.magicNumbers.length);
                final int read = fileChannel.read(byteBuffer);
                if (read != this.magicNumbers.length) {
                    return FileVisitResult.TERMINATE;
                }
                return toFileVisitResult(Arrays.equals(this.magicNumbers, byteBuffer.array()), file);
            }
            catch (IOException ex) {}
        }
        return FileVisitResult.TERMINATE;
    }
    
    public String toString() {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("(");
        builder.append(new String(this.magicNumbers, Charset.defaultCharset()));
        builder.append(",");
        builder.append(this.byteOffset);
        builder.append(")");
        return builder.toString();
    }
}
