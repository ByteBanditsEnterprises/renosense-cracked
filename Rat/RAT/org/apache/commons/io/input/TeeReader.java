//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import java.nio.*;

public class TeeReader extends ProxyReader
{
    private final Writer branch;
    private final boolean closeBranch;
    
    public TeeReader(final Reader input, final Writer branch) {
        this(input, branch, false);
    }
    
    public TeeReader(final Reader input, final Writer branch, final boolean closeBranch) {
        super(input);
        this.branch = branch;
        this.closeBranch = closeBranch;
    }
    
    public void close() throws IOException {
        try {
            super.close();
        }
        finally {
            if (this.closeBranch) {
                this.branch.close();
            }
        }
    }
    
    public int read() throws IOException {
        final int ch = super.read();
        if (ch != -1) {
            this.branch.write(ch);
        }
        return ch;
    }
    
    public int read(final char[] chr) throws IOException {
        final int n = super.read(chr);
        if (n != -1) {
            this.branch.write(chr, 0, n);
        }
        return n;
    }
    
    public int read(final char[] chr, final int st, final int end) throws IOException {
        final int n = super.read(chr, st, end);
        if (n != -1) {
            this.branch.write(chr, st, n);
        }
        return n;
    }
    
    public int read(final CharBuffer target) throws IOException {
        final int originalPosition = target.position();
        final int n = super.read(target);
        if (n != -1) {
            final int newPosition = target.position();
            final int newLimit = target.limit();
            try {
                target.position(originalPosition).limit(newPosition);
                this.branch.append((CharSequence)target);
            }
            finally {
                target.position(newPosition).limit(newLimit);
            }
        }
        return n;
    }
}
