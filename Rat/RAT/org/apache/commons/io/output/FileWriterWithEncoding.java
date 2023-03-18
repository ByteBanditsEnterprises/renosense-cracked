//Raddon On Top!

package org.apache.commons.io.output;

import java.nio.charset.*;
import java.util.*;
import java.nio.file.*;
import org.apache.commons.io.*;
import java.io.*;

public class FileWriterWithEncoding extends Writer
{
    private final Writer out;
    
    public FileWriterWithEncoding(final String fileName, final String charsetName) throws IOException {
        this(new File(fileName), charsetName, false);
    }
    
    public FileWriterWithEncoding(final String fileName, final String charsetName, final boolean append) throws IOException {
        this(new File(fileName), charsetName, append);
    }
    
    public FileWriterWithEncoding(final String fileName, final Charset charset) throws IOException {
        this(new File(fileName), charset, false);
    }
    
    public FileWriterWithEncoding(final String fileName, final Charset charset, final boolean append) throws IOException {
        this(new File(fileName), charset, append);
    }
    
    public FileWriterWithEncoding(final String fileName, final CharsetEncoder encoding) throws IOException {
        this(new File(fileName), encoding, false);
    }
    
    public FileWriterWithEncoding(final String fileName, final CharsetEncoder charsetEncoder, final boolean append) throws IOException {
        this(new File(fileName), charsetEncoder, append);
    }
    
    public FileWriterWithEncoding(final File file, final String charsetName) throws IOException {
        this(file, charsetName, false);
    }
    
    public FileWriterWithEncoding(final File file, final String charsetName, final boolean append) throws IOException {
        this.out = initWriter(file, charsetName, append);
    }
    
    public FileWriterWithEncoding(final File file, final Charset charset) throws IOException {
        this(file, charset, false);
    }
    
    public FileWriterWithEncoding(final File file, final Charset encoding, final boolean append) throws IOException {
        this.out = initWriter(file, encoding, append);
    }
    
    public FileWriterWithEncoding(final File file, final CharsetEncoder charsetEncoder) throws IOException {
        this(file, charsetEncoder, false);
    }
    
    public FileWriterWithEncoding(final File file, final CharsetEncoder charsetEncoder, final boolean append) throws IOException {
        this.out = initWriter(file, charsetEncoder, append);
    }
    
    private static Writer initWriter(final File file, final Object encoding, final boolean append) throws IOException {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(encoding, "encoding");
        OutputStream stream = null;
        final boolean fileExistedAlready = file.exists();
        try {
            stream = Files.newOutputStream(file.toPath(), append ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            if (encoding instanceof Charset) {
                return new OutputStreamWriter(stream, (Charset)encoding);
            }
            if (encoding instanceof CharsetEncoder) {
                return new OutputStreamWriter(stream, (CharsetEncoder)encoding);
            }
            return new OutputStreamWriter(stream, (String)encoding);
        }
        catch (IOException | RuntimeException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            try {
                IOUtils.close((Closeable)stream);
            }
            catch (IOException e) {
                ex.addSuppressed(e);
            }
            if (!fileExistedAlready) {
                FileUtils.deleteQuietly(file);
            }
            throw ex;
        }
    }
    
    @Override
    public void write(final int idx) throws IOException {
        this.out.write(idx);
    }
    
    @Override
    public void write(final char[] chr) throws IOException {
        this.out.write(chr);
    }
    
    @Override
    public void write(final char[] chr, final int st, final int end) throws IOException {
        this.out.write(chr, st, end);
    }
    
    @Override
    public void write(final String str) throws IOException {
        this.out.write(str);
    }
    
    @Override
    public void write(final String str, final int st, final int end) throws IOException {
        this.out.write(str, st, end);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
