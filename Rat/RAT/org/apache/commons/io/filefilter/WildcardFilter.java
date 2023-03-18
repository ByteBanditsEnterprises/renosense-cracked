//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.io.*;
import org.apache.commons.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.util.*;

@Deprecated
public class WildcardFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -5037645902506953517L;
    private final String[] wildcards;
    
    public WildcardFilter(final List<String> wildcards) {
        if (wildcards == null) {
            throw new IllegalArgumentException("The wildcard list must not be null");
        }
        this.wildcards = wildcards.toArray(WildcardFilter.EMPTY_STRING_ARRAY);
    }
    
    public WildcardFilter(final String wildcard) {
        if (wildcard == null) {
            throw new IllegalArgumentException("The wildcard must not be null");
        }
        this.wildcards = new String[] { wildcard };
    }
    
    public WildcardFilter(final String... wildcards) {
        if (wildcards == null) {
            throw new IllegalArgumentException("The wildcard array must not be null");
        }
        System.arraycopy(wildcards, 0, this.wildcards = new String[wildcards.length], 0, wildcards.length);
    }
    
    public boolean accept(final File file) {
        if (file.isDirectory()) {
            return false;
        }
        for (final String wildcard : this.wildcards) {
            if (FilenameUtils.wildcardMatch(file.getName(), wildcard)) {
                return true;
            }
        }
        return false;
    }
    
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        if (Files.isDirectory(file, new LinkOption[0])) {
            return FileVisitResult.TERMINATE;
        }
        for (final String wildcard : this.wildcards) {
            if (FilenameUtils.wildcardMatch(Objects.toString(file.getFileName(), null), wildcard)) {
                return FileVisitResult.CONTINUE;
            }
        }
        return FileVisitResult.TERMINATE;
    }
    
    public boolean accept(final File dir, final String name) {
        if (dir != null && new File(dir, name).isDirectory()) {
            return false;
        }
        for (final String wildcard : this.wildcards) {
            if (FilenameUtils.wildcardMatch(name, wildcard)) {
                return true;
            }
        }
        return false;
    }
}
