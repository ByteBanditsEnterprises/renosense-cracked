//Raddon On Top!

package org.apache.commons.io.monitor;

import java.io.*;
import java.util.concurrent.*;
import org.apache.commons.io.comparator.*;
import org.apache.commons.io.*;
import java.util.*;

public class FileAlterationObserver implements Serializable
{
    private static final long serialVersionUID = 1185122225658782848L;
    private final List<FileAlterationListener> listeners;
    private final FileEntry rootEntry;
    private final FileFilter fileFilter;
    private final Comparator<File> comparator;
    
    public FileAlterationObserver(final String directoryName) {
        this(new File(directoryName));
    }
    
    public FileAlterationObserver(final String directoryName, final FileFilter fileFilter) {
        this(new File(directoryName), fileFilter);
    }
    
    public FileAlterationObserver(final String directoryName, final FileFilter fileFilter, final IOCase caseSensitivity) {
        this(new File(directoryName), fileFilter, caseSensitivity);
    }
    
    public FileAlterationObserver(final File directory) {
        this(directory, null);
    }
    
    public FileAlterationObserver(final File directory, final FileFilter fileFilter) {
        this(directory, fileFilter, null);
    }
    
    public FileAlterationObserver(final File directory, final FileFilter fileFilter, final IOCase caseSensitivity) {
        this(new FileEntry(directory), fileFilter, caseSensitivity);
    }
    
    protected FileAlterationObserver(final FileEntry rootEntry, final FileFilter fileFilter, final IOCase caseSensitivity) {
        this.listeners = new CopyOnWriteArrayList<FileAlterationListener>();
        if (rootEntry == null) {
            throw new IllegalArgumentException("Root entry is missing");
        }
        if (rootEntry.getFile() == null) {
            throw new IllegalArgumentException("Root directory is missing");
        }
        this.rootEntry = rootEntry;
        this.fileFilter = fileFilter;
        if (caseSensitivity == null || caseSensitivity.equals((Object)IOCase.SYSTEM)) {
            this.comparator = (Comparator<File>)NameFileComparator.NAME_SYSTEM_COMPARATOR;
        }
        else if (caseSensitivity.equals((Object)IOCase.INSENSITIVE)) {
            this.comparator = (Comparator<File>)NameFileComparator.NAME_INSENSITIVE_COMPARATOR;
        }
        else {
            this.comparator = (Comparator<File>)NameFileComparator.NAME_COMPARATOR;
        }
    }
    
    public File getDirectory() {
        return this.rootEntry.getFile();
    }
    
    public FileFilter getFileFilter() {
        return this.fileFilter;
    }
    
    public void addListener(final FileAlterationListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }
    
    public void removeListener(final FileAlterationListener listener) {
        if (listener != null) {
            while (this.listeners.remove(listener)) {}
        }
    }
    
    public Iterable<FileAlterationListener> getListeners() {
        return this.listeners;
    }
    
    public void initialize() throws Exception {
        this.rootEntry.refresh(this.rootEntry.getFile());
        final FileEntry[] children = this.doListFiles(this.rootEntry.getFile(), this.rootEntry);
        this.rootEntry.setChildren(children);
    }
    
    public void destroy() throws Exception {
    }
    
    public void checkAndNotify() {
        for (final FileAlterationListener listener : this.listeners) {
            listener.onStart(this);
        }
        final File rootFile = this.rootEntry.getFile();
        if (rootFile.exists()) {
            this.checkAndNotify(this.rootEntry, this.rootEntry.getChildren(), this.listFiles(rootFile));
        }
        else if (this.rootEntry.isExists()) {
            this.checkAndNotify(this.rootEntry, this.rootEntry.getChildren(), FileUtils.EMPTY_FILE_ARRAY);
        }
        for (final FileAlterationListener listener2 : this.listeners) {
            listener2.onStop(this);
        }
    }
    
    private void checkAndNotify(final FileEntry parent, final FileEntry[] previous, final File[] files) {
        int c = 0;
        final FileEntry[] current = (files.length > 0) ? new FileEntry[files.length] : FileEntry.EMPTY_FILE_ENTRY_ARRAY;
        for (final FileEntry entry : previous) {
            while (c < files.length && this.comparator.compare(entry.getFile(), files[c]) > 0) {
                this.doCreate(current[c] = this.createFileEntry(parent, files[c]));
                ++c;
            }
            if (c < files.length && this.comparator.compare(entry.getFile(), files[c]) == 0) {
                this.doMatch(entry, files[c]);
                this.checkAndNotify(entry, entry.getChildren(), this.listFiles(files[c]));
                current[c] = entry;
                ++c;
            }
            else {
                this.checkAndNotify(entry, entry.getChildren(), FileUtils.EMPTY_FILE_ARRAY);
                this.doDelete(entry);
            }
        }
        while (c < files.length) {
            this.doCreate(current[c] = this.createFileEntry(parent, files[c]));
            ++c;
        }
        parent.setChildren(current);
    }
    
    private FileEntry createFileEntry(final FileEntry parent, final File file) {
        final FileEntry entry = parent.newChildInstance(file);
        entry.refresh(file);
        final FileEntry[] children = this.doListFiles(file, entry);
        entry.setChildren(children);
        return entry;
    }
    
    private FileEntry[] doListFiles(final File file, final FileEntry entry) {
        final File[] files = this.listFiles(file);
        final FileEntry[] children = (files.length > 0) ? new FileEntry[files.length] : FileEntry.EMPTY_FILE_ENTRY_ARRAY;
        for (int i = 0; i < files.length; ++i) {
            children[i] = this.createFileEntry(entry, files[i]);
        }
        return children;
    }
    
    private void doCreate(final FileEntry entry) {
        for (final FileAlterationListener listener : this.listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryCreate(entry.getFile());
            }
            else {
                listener.onFileCreate(entry.getFile());
            }
        }
        final FileEntry[] children2;
        final FileEntry[] children = children2 = entry.getChildren();
        for (final FileEntry aChildren : children2) {
            this.doCreate(aChildren);
        }
    }
    
    private void doMatch(final FileEntry entry, final File file) {
        if (entry.refresh(file)) {
            for (final FileAlterationListener listener : this.listeners) {
                if (entry.isDirectory()) {
                    listener.onDirectoryChange(file);
                }
                else {
                    listener.onFileChange(file);
                }
            }
        }
    }
    
    private void doDelete(final FileEntry entry) {
        for (final FileAlterationListener listener : this.listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryDelete(entry.getFile());
            }
            else {
                listener.onFileDelete(entry.getFile());
            }
        }
    }
    
    private File[] listFiles(final File file) {
        File[] children = null;
        if (file.isDirectory()) {
            children = ((this.fileFilter == null) ? file.listFiles() : file.listFiles(this.fileFilter));
        }
        if (children == null) {
            children = FileUtils.EMPTY_FILE_ARRAY;
        }
        if (this.comparator != null && children.length > 1) {
            Arrays.sort(children, this.comparator);
        }
        return children;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("[file='");
        builder.append(this.getDirectory().getPath());
        builder.append('\'');
        if (this.fileFilter != null) {
            builder.append(", ");
            builder.append(this.fileFilter.toString());
        }
        builder.append(", listeners=");
        builder.append(this.listeners.size());
        builder.append("]");
        return builder.toString();
    }
}
