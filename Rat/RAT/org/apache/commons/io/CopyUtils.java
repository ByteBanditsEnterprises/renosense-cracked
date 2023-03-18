//Raddon On Top!

package org.apache.commons.io;

import java.nio.charset.*;
import java.io.*;

@Deprecated
public class CopyUtils
{
    public static void copy(final byte[] input, final OutputStream output) throws IOException {
        output.write(input);
    }
    
    @Deprecated
    public static void copy(final byte[] input, final Writer output) throws IOException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        copy(inputStream, output);
    }
    
    public static void copy(final byte[] input, final Writer output, final String encoding) throws IOException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        copy(inputStream, output, encoding);
    }
    
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = IOUtils.byteArray();
        int count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static int copy(final Reader input, final Writer output) throws IOException {
        final char[] buffer = IOUtils.getCharArray();
        int count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    @Deprecated
    public static void copy(final InputStream input, final Writer output) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, Charset.defaultCharset());
        copy(in, output);
    }
    
    public static void copy(final InputStream input, final Writer output, final String encoding) throws IOException {
        final InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output);
    }
    
    @Deprecated
    public static void copy(final Reader input, final OutputStream output) throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output, Charset.defaultCharset());
        copy(input, out);
        out.flush();
    }
    
    public static void copy(final Reader input, final OutputStream output, final String encoding) throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output, encoding);
        copy(input, out);
        out.flush();
    }
    
    @Deprecated
    public static void copy(final String input, final OutputStream output) throws IOException {
        final StringReader in = new StringReader(input);
        final OutputStreamWriter out = new OutputStreamWriter(output, Charset.defaultCharset());
        copy(in, out);
        out.flush();
    }
    
    public static void copy(final String input, final OutputStream output, final String encoding) throws IOException {
        final StringReader in = new StringReader(input);
        final OutputStreamWriter out = new OutputStreamWriter(output, encoding);
        copy(in, out);
        out.flush();
    }
    
    public static void copy(final String input, final Writer output) throws IOException {
        output.write(input);
    }
}
