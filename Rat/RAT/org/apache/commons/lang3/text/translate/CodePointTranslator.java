//Raddon On Top!

package org.apache.commons.lang3.text.translate;

import java.io.*;

@Deprecated
public abstract class CodePointTranslator extends CharSequenceTranslator
{
    public final int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        final int codepoint = Character.codePointAt(input, index);
        final boolean consumed = this.translate(codepoint, out);
        return consumed ? 1 : 0;
    }
    
    public abstract boolean translate(final int p0, final Writer p1) throws IOException;
}
