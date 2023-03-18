//Raddon On Top!

package org.apache.commons.io.input;

import java.nio.channels.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import java.nio.*;
import org.apache.commons.io.*;

public class ReversedLinesFileReader implements Closeable
{
    private static final String EMPTY_STRING = "";
    private static final int DEFAULT_BLOCK_SIZE = 8192;
    private final int blockSize;
    private final Charset charset;
    private final SeekableByteChannel channel;
    private final long totalByteLength;
    private final long totalBlockCount;
    private final byte[][] newLineSequences;
    private final int avoidNewlineSplitBufferSize;
    private final int byteDecrement;
    private FilePart currentFilePart;
    private boolean trailingNewlineOfFileSkipped;
    
    @Deprecated
    public ReversedLinesFileReader(final File file) throws IOException {
        this(file, 8192, Charset.defaultCharset());
    }
    
    public ReversedLinesFileReader(final File file, final Charset charset) throws IOException {
        this(file.toPath(), charset);
    }
    
    public ReversedLinesFileReader(final File file, final int blockSize, final Charset charset) throws IOException {
        this(file.toPath(), blockSize, charset);
    }
    
    public ReversedLinesFileReader(final File file, final int blockSize, final String charsetName) throws IOException {
        this(file.toPath(), blockSize, charsetName);
    }
    
    public ReversedLinesFileReader(final Path file, final Charset charset) throws IOException {
        this(file, 8192, charset);
    }
    
    public ReversedLinesFileReader(final Path file, final int blockSize, final Charset charset) throws IOException {
        this.blockSize = blockSize;
        this.charset = Charsets.toCharset(charset);
        final CharsetEncoder charsetEncoder = this.charset.newEncoder();
        final float maxBytesPerChar = charsetEncoder.maxBytesPerChar();
        if (maxBytesPerChar == 1.0f) {
            this.byteDecrement = 1;
        }
        else if (this.charset == StandardCharsets.UTF_8) {
            this.byteDecrement = 1;
        }
        else if (this.charset == Charset.forName("Shift_JIS") || this.charset == Charset.forName("windows-31j") || this.charset == Charset.forName("x-windows-949") || this.charset == Charset.forName("gbk") || this.charset == Charset.forName("x-windows-950")) {
            this.byteDecrement = 1;
        }
        else if (this.charset == StandardCharsets.UTF_16BE || this.charset == StandardCharsets.UTF_16LE) {
            this.byteDecrement = 2;
        }
        else {
            if (this.charset == StandardCharsets.UTF_16) {
                throw new UnsupportedEncodingException("For UTF-16, you need to specify the byte order (use UTF-16BE or UTF-16LE)");
            }
            throw new UnsupportedEncodingException("Encoding " + charset + " is not supported yet (feel free to submit a patch)");
        }
        this.newLineSequences = new byte[][] { StandardLineSeparator.CRLF.getBytes(this.charset), StandardLineSeparator.LF.getBytes(this.charset), StandardLineSeparator.CR.getBytes(this.charset) };
        this.avoidNewlineSplitBufferSize = this.newLineSequences[0].length;
        this.channel = Files.newByteChannel(file, StandardOpenOption.READ);
        this.totalByteLength = this.channel.size();
        int lastBlockLength = (int)(this.totalByteLength % blockSize);
        if (lastBlockLength > 0) {
            this.totalBlockCount = this.totalByteLength / blockSize + 1L;
        }
        else {
            this.totalBlockCount = this.totalByteLength / blockSize;
            if (this.totalByteLength > 0L) {
                lastBlockLength = blockSize;
            }
        }
        this.currentFilePart = new FilePart(this.totalBlockCount, lastBlockLength, (byte[])null);
    }
    
    public ReversedLinesFileReader(final Path file, final int blockSize, final String charsetName) throws IOException {
        this(file, blockSize, Charsets.toCharset(charsetName));
    }
    
    @Override
    public void close() throws IOException {
        this.channel.close();
    }
    
    public String readLine() throws IOException {
        String line;
        for (line = this.currentFilePart.readLine(); line == null; line = this.currentFilePart.readLine()) {
            this.currentFilePart = this.currentFilePart.rollOver();
            if (this.currentFilePart == null) {
                break;
            }
        }
        if ("".equals(line) && !this.trailingNewlineOfFileSkipped) {
            this.trailingNewlineOfFileSkipped = true;
            line = this.readLine();
        }
        return line;
    }
    
    public List<String> readLines(final int lineCount) throws IOException {
        if (lineCount < 0) {
            throw new IllegalArgumentException("lineCount < 0");
        }
        final ArrayList<String> arrayList = new ArrayList<String>(lineCount);
        for (int i = 0; i < lineCount; ++i) {
            final String line = this.readLine();
            if (line == null) {
                return arrayList;
            }
            arrayList.add(line);
        }
        return arrayList;
    }
    
    public String toString(final int lineCount) throws IOException {
        final List<String> lines = this.readLines(lineCount);
        Collections.reverse(lines);
        return lines.isEmpty() ? "" : (String.join(System.lineSeparator(), lines) + System.lineSeparator());
    }
    
    private class FilePart
    {
        private final long no;
        private final byte[] data;
        private byte[] leftOver;
        private int currentLastBytePos;
        
        private FilePart(final long no, final int length, final byte[] leftOverOfLastFilePart) throws IOException {
            this.no = no;
            final int dataLength = length + ((leftOverOfLastFilePart != null) ? leftOverOfLastFilePart.length : 0);
            this.data = new byte[dataLength];
            final long off = (no - 1L) * ReversedLinesFileReader.this.blockSize;
            if (no > 0L) {
                ReversedLinesFileReader.this.channel.position(off);
                final int countRead = ReversedLinesFileReader.this.channel.read(ByteBuffer.wrap(this.data, 0, length));
                if (countRead != length) {
                    throw new IllegalStateException("Count of requested bytes and actually read bytes don't match");
                }
            }
            if (leftOverOfLastFilePart != null) {
                System.arraycopy(leftOverOfLastFilePart, 0, this.data, length, leftOverOfLastFilePart.length);
            }
            this.currentLastBytePos = this.data.length - 1;
            this.leftOver = null;
        }
        
        private void createLeftOver() {
            final int lineLengthBytes = this.currentLastBytePos + 1;
            if (lineLengthBytes > 0) {
                this.leftOver = IOUtils.byteArray(lineLengthBytes);
                System.arraycopy(this.data, 0, this.leftOver, 0, lineLengthBytes);
            }
            else {
                this.leftOver = null;
            }
            this.currentLastBytePos = -1;
        }
        
        private int getNewLineMatchByteCount(final byte[] data, final int i) {
            for (final byte[] newLineSequence : ReversedLinesFileReader.this.newLineSequences) {
                boolean match = true;
                for (int j = newLineSequence.length - 1; j >= 0; --j) {
                    final int k = i + j - (newLineSequence.length - 1);
                    match &= (k >= 0 && data[k] == newLineSequence[j]);
                }
                if (match) {
                    return newLineSequence.length;
                }
            }
            return 0;
        }
        
        private String readLine() {
            String line = null;
            final boolean isLastFilePart = this.no == 1L;
            int i = this.currentLastBytePos;
            while (i > -1) {
                if (!isLastFilePart && i < ReversedLinesFileReader.this.avoidNewlineSplitBufferSize) {
                    this.createLeftOver();
                    break;
                }
                final int newLineMatchByteCount;
                if ((newLineMatchByteCount = this.getNewLineMatchByteCount(this.data, i)) > 0) {
                    final int lineStart = i + 1;
                    final int lineLengthBytes = this.currentLastBytePos - lineStart + 1;
                    if (lineLengthBytes < 0) {
                        throw new IllegalStateException("Unexpected negative line length=" + lineLengthBytes);
                    }
                    final byte[] lineData = IOUtils.byteArray(lineLengthBytes);
                    System.arraycopy(this.data, lineStart, lineData, 0, lineLengthBytes);
                    line = new String(lineData, ReversedLinesFileReader.this.charset);
                    this.currentLastBytePos = i - newLineMatchByteCount;
                    break;
                }
                else {
                    i -= ReversedLinesFileReader.this.byteDecrement;
                    if (i < 0) {
                        this.createLeftOver();
                        break;
                    }
                    continue;
                }
            }
            if (isLastFilePart && this.leftOver != null) {
                line = new String(this.leftOver, ReversedLinesFileReader.this.charset);
                this.leftOver = null;
            }
            return line;
        }
        
        private FilePart rollOver() throws IOException {
            if (this.currentLastBytePos > -1) {
                throw new IllegalStateException("Current currentLastCharPos unexpectedly positive... last readLine() should have returned something! currentLastCharPos=" + this.currentLastBytePos);
            }
            if (this.no > 1L) {
                return new FilePart(this.no - 1L, ReversedLinesFileReader.this.blockSize, this.leftOver);
            }
            if (this.leftOver != null) {
                throw new IllegalStateException("Unexpected leftover of the last block: leftOverOfThisFilePart=" + new String(this.leftOver, ReversedLinesFileReader.this.charset));
            }
            return null;
        }
    }
}
