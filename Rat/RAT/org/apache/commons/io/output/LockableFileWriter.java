//Raddon On Top!

package org.apache.commons.io.output;

import java.nio.charset.*;
import org.apache.commons.io.*;
import java.io.*;

public class LockableFileWriter extends Writer
{
    private static final String LCK = ".lck";
    private final Writer out;
    private final File lockFile;
    
    public LockableFileWriter(final String fileName) throws IOException {
        this(fileName, false, null);
    }
    
    public LockableFileWriter(final String fileName, final boolean append) throws IOException {
        this(fileName, append, null);
    }
    
    public LockableFileWriter(final String fileName, final boolean append, final String lockDir) throws IOException {
        this(new File(fileName), append, lockDir);
    }
    
    public LockableFileWriter(final File file) throws IOException {
        this(file, false, null);
    }
    
    public LockableFileWriter(final File file, final boolean append) throws IOException {
        this(file, append, null);
    }
    
    @Deprecated
    public LockableFileWriter(final File file, final boolean append, final String lockDir) throws IOException {
        this(file, Charset.defaultCharset(), append, lockDir);
    }
    
    public LockableFileWriter(final File file, final Charset charset) throws IOException {
        this(file, charset, false, null);
    }
    
    public LockableFileWriter(final File file, final String charsetName) throws IOException {
        this(file, charsetName, false, null);
    }
    
    public LockableFileWriter(File file, final Charset charset, final boolean append, String lockDir) throws IOException {
        file = file.getAbsoluteFile();
        if (file.getParentFile() != null) {
            FileUtils.forceMkdir(file.getParentFile());
        }
        if (file.isDirectory()) {
            throw new IOException("File specified is a directory");
        }
        if (lockDir == null) {
            lockDir = System.getProperty("java.io.tmpdir");
        }
        final File lockDirFile = new File(lockDir);
        FileUtils.forceMkdir(lockDirFile);
        this.testLockDir(lockDirFile);
        this.lockFile = new File(lockDirFile, file.getName() + ".lck");
        this.createLock();
        this.out = this.initWriter(file, charset, append);
    }
    
    public LockableFileWriter(final File file, final String charsetName, final boolean append, final String lockDir) throws IOException {
        this(file, Charsets.toCharset(charsetName), append, lockDir);
    }
    
    private void testLockDir(final File lockDir) throws IOException {
        if (!lockDir.exists()) {
            throw new IOException("Could not find lockDir: " + lockDir.getAbsolutePath());
        }
        if (!lockDir.canWrite()) {
            throw new IOException("Could not write to lockDir: " + lockDir.getAbsolutePath());
        }
    }
    
    private void createLock() throws IOException {
        synchronized (LockableFileWriter.class) {
            if (!this.lockFile.createNewFile()) {
                throw new IOException("Can't write file, lock " + this.lockFile.getAbsolutePath() + " exists");
            }
            this.lockFile.deleteOnExit();
        }
    }
    
    private Writer initWriter(final File file, final Charset charset, final boolean append) throws IOException {
        final boolean fileExistedAlready = file.exists();
        try {
            return new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath(), append), Charsets.toCharset(charset));
        }
        catch (IOException | RuntimeException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            FileUtils.deleteQuietly(this.lockFile);
            if (!fileExistedAlready) {
                FileUtils.deleteQuietly(file);
            }
            throw ex;
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.out.close();
        }
        finally {
            FileUtils.delete(this.lockFile);
        }
    }
    
    @Override
    public void write(final int c) throws IOException {
        this.out.write(c);
    }
    
    @Override
    public void write(final char[] cbuf) throws IOException {
        this.out.write(cbuf);
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        this.out.write(cbuf, off, len);
    }
    
    @Override
    public void write(final String str) throws IOException {
        this.out.write(str);
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        this.out.write(str, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
}
