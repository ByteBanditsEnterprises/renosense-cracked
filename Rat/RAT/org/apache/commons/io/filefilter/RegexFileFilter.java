//Raddon On Top!

package org.apache.commons.io.filefilter;

import java.util.regex.*;
import java.util.function.*;
import org.apache.commons.io.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class RegexFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = 4269646126155225062L;
    private final Pattern pattern;
    private final Function<Path, String> pathToString;
    
    private static Pattern compile(final String pattern, final int flags) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        return Pattern.compile(pattern, flags);
    }
    
    private static int toFlags(final IOCase caseSensitivity) {
        return IOCase.isCaseSensitive(caseSensitivity) ? 2 : 0;
    }
    
    public RegexFileFilter(final Pattern pattern) {
        this(pattern, p -> p.getFileName().toString());
    }
    
    public RegexFileFilter(final Pattern pattern, final Function<Path, String> pathToString) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        this.pattern = pattern;
        this.pathToString = pathToString;
    }
    
    public RegexFileFilter(final String pattern) {
        this(pattern, 0);
    }
    
    public RegexFileFilter(final String pattern, final int flags) {
        this(compile(pattern, flags));
    }
    
    public RegexFileFilter(final String pattern, final IOCase caseSensitivity) {
        this(compile(pattern, toFlags(caseSensitivity)));
    }
    
    public boolean accept(final File dir, final String name) {
        return this.pattern.matcher(name).matches();
    }
    
    public FileVisitResult accept(final Path path, final BasicFileAttributes attributes) {
        return toFileVisitResult(this.pattern.matcher(this.pathToString.apply(path)).matches(), path);
    }
    
    public String toString() {
        return "RegexFileFilter [pattern=" + this.pattern + "]";
    }
}
