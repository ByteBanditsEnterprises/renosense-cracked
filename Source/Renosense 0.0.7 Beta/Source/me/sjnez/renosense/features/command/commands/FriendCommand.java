//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.command.commands;

import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.manager.*;
import java.util.*;

public class FriendCommand extends Command
{
    public FriendCommand() {
        super("friend", "Lets you add and delete friends, gets friends list.", new String[] { "<add/del/name/clear>", "<name>" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            if (RenoSense.friendManager.getFriends().isEmpty()) {
                sendMessage("Friend list empty.");
            }
            else {
                sendMessage("Friends:");
                for (final FriendManager.Friend friend : RenoSense.friendManager.getFriends()) {
                    try {
                        final String f = friend.getUsername();
                        sendMessage(f);
                    }
                    catch (Exception ex) {}
                }
            }
            return;
        }
        if (commands.length != 2) {
            if (commands.length >= 2) {
                final String s = commands[0];
                switch (s) {
                    case "add": {
                        RenoSense.friendManager.addFriend(commands[1]);
                    }
                    case "del": {
                        RenoSense.friendManager.removeFriend(commands[1]);
                    }
                    default: {
                        sendMessage("Unknown Command, try friend add/del (name)");
                        break;
                    }
                }
            }
            return;
        }
        final String s2 = commands[0];
        switch (s2) {
            case "clear":
            case "reset": {
                RenoSense.friendManager.onLoad();
                sendMessage("Friends got reset.");
            }
            default: {
                sendMessage(commands[0] + (RenoSense.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
            }
        }
    }
}
