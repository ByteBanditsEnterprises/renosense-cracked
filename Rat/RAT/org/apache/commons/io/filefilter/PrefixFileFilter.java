//Raddon On Top!

package org.apache.commons.io.filefilter;

import org.apache.commons.io.*;
import java.util.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class PrefixFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 8533897440809599867L;
    private final String[] prefixes;
    private final IOCase caseSensitivity;
    
    public PrefixFileFilter(final List<String> prefixes) {
        this(prefixes, IOCase.SENSITIVE);
    }
    
    public PrefixFileFilter(final List<String> prefixes, final IOCase caseSensitivity) {
        if (prefixes == null) {
            throw new IllegalArgumentException("The list of prefixes must not be null");
        }
        this.prefixes = prefixes.toArray(PrefixFileFilter.EMPTY_STRING_ARRAY);
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public PrefixFileFilter(final String prefix) {
        this(prefix, IOCase.SENSITIVE);
    }
    
    public PrefixFileFilter(final String... prefixes) {
        this(prefixes, IOCase.SENSITIVE);
    }
    
    public PrefixFileFilter(final String prefix, final IOCase caseSensitivity) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        this.prefixes = new String[] { prefix };
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public PrefixFileFilter(final String[] prefixes, final IOCase caseSensitivity) {
        if (prefixes == null) {
            throw new IllegalArgumentException("The array of prefixes must not be null");
        }
        System.arraycopy(prefixes, 0, this.prefixes = new String[prefixes.length], 0, prefixes.length);
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public boolean accept(final File file) {
        return this.accept((file == null) ? null : file.getName());
    }
    
    public boolean accept(final File file, final String name) {
        return this.accept(name);
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        final Path fileName = file.getFileName();
        return toFileVisitResult(this.accept((fileName == null) ? null : fileName.toFile()), file);
    }
    
    private boolean accept(final String name) {
        for (final String prefix : this.prefixes) {
            if (this.caseSensitivity.checkStartsWith(name, prefix)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        if (this.prefixes != null) {
            for (int i = 0; i < this.prefixes.length; ++i) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(this.prefixes[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
}
