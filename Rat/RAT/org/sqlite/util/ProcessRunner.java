//Raddon On Top!

package org.sqlite.util;

import java.util.concurrent.*;
import java.io.*;

public class ProcessRunner
{
    String runAndWaitFor(final String command) throws IOException, InterruptedException {
        final Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        return getProcessOutput(p);
    }
    
    String runAndWaitFor(final String command, final long timeout, final TimeUnit unit) throws IOException, InterruptedException {
        final Process p = Runtime.getRuntime().exec(command);
        p.waitFor(timeout, unit);
        return getProcessOutput(p);
    }
    
    static String getProcessOutput(final Process process) throws IOException {
        final InputStream in = process.getInputStream();
        try {
            final ByteArrayOutputStream b = new ByteArrayOutputStream();
            final byte[] buf = new byte[32];
            int readLen;
            while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
                b.write(buf, 0, readLen);
            }
            final String string = b.toString();
            if (in != null) {
                in.close();
            }
            return string;
        }
        catch (Throwable t) {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Throwable t2) {
                    t.addSuppressed(t2);
                }
            }
            throw t;
        }
    }
}
