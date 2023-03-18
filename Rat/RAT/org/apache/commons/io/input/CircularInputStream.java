//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import java.util.*;

public class CircularInputStream extends InputStream
{
    private long byteCount;
    private int position;
    private final byte[] repeatedContent;
    private final long targetByteCount;
    
    private static byte[] validate(final byte[] repeatContent) {
        Objects.requireNonNull(repeatContent, "repeatContent");
        for (final byte b : repeatContent) {
            if (b == -1) {
                throw new IllegalArgumentException("repeatContent contains the end-of-stream marker -1");
            }
        }
        return repeatContent;
    }
    
    public CircularInputStream(final byte[] repeatContent, final long targetByteCount) {
        this.position = -1;
        this.repeatedContent = validate(repeatContent);
        if (repeatContent.length == 0) {
            throw new IllegalArgumentException("repeatContent is empty.");
        }
        this.targetByteCount = targetByteCount;
    }
    
    @Override
    public int read() {
        if (this.targetByteCount >= 0L) {
            if (this.byteCount == this.targetByteCount) {
                return -1;
            }
            ++this.byteCount;
        }
        this.position = (this.position + 1) % this.repeatedContent.length;
        return this.repeatedContent[this.position] & 0xFF;
    }
}
