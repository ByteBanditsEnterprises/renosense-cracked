//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.util.*;
import org.apache.commons.io.*;

public class WildcardFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -7426486598995782105L;
    private final String[] wildcards;
    private final IOCase caseSensitivity;
    
    public WildcardFileFilter(final List<String> wildcards) {
        this(wildcards, IOCase.SENSITIVE);
    }
    
    public WildcardFileFilter(final List<String> wildcards, final IOCase caseSensitivity) {
        if (wildcards == null) {
            throw new IllegalArgumentException("The wildcard list must not be null");
        }
        this.wildcards = wildcards.toArray(WildcardFileFilter.EMPTY_STRING_ARRAY);
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public WildcardFileFilter(final String wildcard) {
        this(wildcard, IOCase.SENSITIVE);
    }
    
    public WildcardFileFilter(final String... wildcards) {
        this(wildcards, IOCase.SENSITIVE);
    }
    
    public WildcardFileFilter(final String wildcard, final IOCase caseSensitivity) {
        if (wildcard == null) {
            throw new IllegalArgumentException("The wildcard must not be null");
        }
        this.wildcards = new String[] { wildcard };
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public WildcardFileFilter(final String[] wildcards, final IOCase caseSensitivity) {
        if (wildcards == null) {
            throw new IllegalArgumentException("The wildcard array must not be null");
        }
        System.arraycopy(wildcards, 0, this.wildcards = new String[wildcards.length], 0, wildcards.length);
        this.caseSensitivity = ((caseSensitivity == null) ? IOCase.SENSITIVE : caseSensitivity);
    }
    
    public boolean accept(final File file) {
        return this.accept(file.getName());
    }
    
    public boolean accept(final File dir, final String name) {
        return this.accept(name);
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(this.accept(Objects.toString(file.getFileName(), null)), file);
    }
    
    private boolean accept(final String name) {
        for (final String wildcard : this.wildcards) {
            if (FilenameUtils.wildcardMatch(name, wildcard, this.caseSensitivity)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        for (int i = 0; i < this.wildcards.length; ++i) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append(this.wildcards[i]);
        }
        buffer.append(")");
        return buffer.toString();
    }
}
