//Raddon On Top!

package org.apache.commons.io.filefilter;

import org.apache.commons.io.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.util.*;

public class SuffixFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -3389157631240246157L;
    private final String[] suffixes;
    private final IOCase caseSensitivity;
    
    public SuffixFileFilter(final List<String> suffixes) {
        this(suffixes, IOCase.SENSITIVE);
    }
    
    public SuffixFileFilter(final List<String> suffixes, final IOCase caseSensitivity) {
        if (suffixes == null) {
            throw new IllegalArgumentException("The list of suffixes must not be null");
        }
        this.suffixes = suffixes.toArray(SuffixFileFilter.EMPTY_STRING_ARRAY);
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public SuffixFileFilter(final String suffix) {
        this(suffix, IOCase.SENSITIVE);
    }
    
    public SuffixFileFilter(final String... suffixes) {
        this(suffixes, IOCase.SENSITIVE);
    }
    
    public SuffixFileFilter(final String suffix, final IOCase caseSensitivity) {
        if (suffix == null) {
            throw new IllegalArgumentException("The suffix must not be null");
        }
        this.suffixes = new String[] { suffix };
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public SuffixFileFilter(final String[] suffixes, final IOCase caseSensitivity) {
        if (suffixes == null) {
            throw new IllegalArgumentException("The array of suffixes must not be null");
        }
        System.arraycopy(suffixes, 0, this.suffixes = new String[suffixes.length], 0, suffixes.length);
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public boolean accept(final File file) {
        return this.accept(file.getName());
    }
    
    public boolean accept(final File file, final String name) {
        return this.accept(name);
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(this.accept(Objects.toString(file.getFileName(), null)), file);
    }
    
    private boolean accept(final String name) {
        for (final String suffix : this.suffixes) {
            if (this.caseSensitivity.checkEndsWith(name, suffix)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        if (this.suffixes != null) {
            for (int i = 0; i < this.suffixes.length; ++i) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(this.suffixes[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
}
