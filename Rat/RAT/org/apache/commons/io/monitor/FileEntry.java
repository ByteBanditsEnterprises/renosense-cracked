//Raddon On Top!

package org.apache.commons.io.monitor;

import java.nio.file.*;
import org.apache.commons.io.*;
import java.io.*;

public class FileEntry implements Serializable
{
    private static final long serialVersionUID = -2505664948818681153L;
    static final FileEntry[] EMPTY_FILE_ENTRY_ARRAY;
    private final FileEntry parent;
    private FileEntry[] children;
    private final File file;
    private String name;
    private boolean exists;
    private boolean directory;
    private long lastModified;
    private long length;
    
    public FileEntry(final File file) {
        this(null, file);
    }
    
    public FileEntry(final FileEntry parent, final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File is missing");
        }
        this.file = file;
        this.parent = parent;
        this.name = file.getName();
    }
    
    public boolean refresh(final File file) {
        final boolean origExists = this.exists;
        final long origLastModified = this.lastModified;
        final boolean origDirectory = this.directory;
        final long origLength = this.length;
        this.name = file.getName();
        this.exists = Files.exists(file.toPath(), new LinkOption[0]);
        this.directory = (this.exists && file.isDirectory());
        try {
            this.lastModified = (this.exists ? FileUtils.lastModified(file) : 0L);
        }
        catch (IOException e) {
            this.lastModified = 0L;
        }
        this.length = ((this.exists && !this.directory) ? file.length() : 0L);
        return this.exists != origExists || this.lastModified != origLastModified || this.directory != origDirectory || this.length != origLength;
    }
    
    public FileEntry newChildInstance(final File file) {
        return new FileEntry(this, file);
    }
    
    public FileEntry getParent() {
        return this.parent;
    }
    
    public int getLevel() {
        return (this.parent == null) ? 0 : (this.parent.getLevel() + 1);
    }
    
    public FileEntry[] getChildren() {
        return (this.children != null) ? this.children : FileEntry.EMPTY_FILE_ENTRY_ARRAY;
    }
    
    public void setChildren(final FileEntry... children) {
        this.children = children;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public long getLastModified() {
        return this.lastModified;
    }
    
    public void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }
    
    public long getLength() {
        return this.length;
    }
    
    public void setLength(final long length) {
        this.length = length;
    }
    
    public boolean isExists() {
        return this.exists;
    }
    
    public void setExists(final boolean exists) {
        this.exists = exists;
    }
    
    public boolean isDirectory() {
        return this.directory;
    }
    
    public void setDirectory(final boolean directory) {
        this.directory = directory;
    }
    
    static {
        EMPTY_FILE_ENTRY_ARRAY = new FileEntry[0];
    }
}
