//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util;

import me.sjnez.renosense.features.modules.client.*;
import java.text.*;
import java.util.*;

public class MessageUtil
{
    public static String dmer(final String player) {
        if (player != null) {
            final StringBuilder s = new StringBuilder();
            int l = 0;
            for (int i = 0; i < player.length() && (!String.valueOf(player.charAt(i)).equals(":") || i <= 6); ++i) {
                s.append(player.charAt(i));
                ++l;
            }
            if (String.valueOf(player.charAt(l - 11)).equals("§")) {
                s.delete(l - 13, l);
            }
            else {
                s.delete(l - 9, l);
            }
            if (HUD.getInstance().timestamp.getValue()) {
                final String date = new SimpleDateFormat("h:mm").format(new Date());
                s.delete(0, 9 + (String.valueOf(date.charAt(1)).equals(":") ? 4 : 5));
            }
            else {
                s.delete(0, 2);
            }
            return s.toString();
        }
        return null;
    }
}
