//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.manager;

import me.sjnez.renosense.features.*;
import net.minecraft.entity.player.*;
import me.sjnez.renosense.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.features.modules.misc.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import me.sjnez.renosense.features.command.commands.*;
import me.sjnez.renosense.features.setting.*;
import java.util.function.*;
import net.minecraft.client.network.*;
import me.sjnez.renosense.util.*;
import java.util.*;

public class FriendManager extends Feature
{
    private List<Friend> friends;
    
    public FriendManager() {
        super("Friends");
        this.friends = new ArrayList<Friend>();
    }
    
    public boolean isFriend(final String name) {
        this.cleanFriends();
        for (final Friend friend : this.friends) {
            if (friend.username.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isFriend(final EntityPlayer player) {
        return this.isFriend(player.getName());
    }
    
    public void addFriend(final String name) {
        final Friend friend = this.getFriendByName(name);
        if (friend == null) {
            return;
        }
        if (!fullNullCheck()) {
            if (RenoSense.friendManager.isFriend(name)) {
                Command.sendMessage(ChatFormatting.RED + name + " is already a friend.");
                return;
            }
            if (RenoSense.enemyManager.isEnemy(name)) {
                Command.sendMessage(ChatFormatting.RED + name + ChatFormatting.RED + " is an enemy!");
                return;
            }
            Command.sendMessage(ChatFormatting.GREEN + name + ChatFormatting.GREEN + " has been friended.");
            if ((boolean)FriendSettings.getInstance().notify.getValue() && FriendSettings.getInstance().isOn()) {
                FriendManager.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("/w " + name + " I just added you to my friends list on RenoSense!"));
            }
        }
        this.friends.add(friend);
        this.cleanFriends();
    }
    
    public void removeFriend(final String name) {
        this.cleanFriends();
        if (!fullNullCheck() && !RenoSense.friendManager.isFriend(name)) {
            Command.sendMessage(ChatFormatting.RED + name + " is not a friend.");
            return;
        }
        for (final Friend friend : this.friends) {
            if (!friend.getUsername().equalsIgnoreCase(name)) {
                continue;
            }
            if (!fullNullCheck()) {
                if ((boolean)FriendSettings.getInstance().notify.getValue() && FriendSettings.getInstance().isOn()) {
                    FriendManager.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("/w " + name + " I just removed you from my friends list on RenoSense!"));
                }
                FriendCommand.sendMessage(ChatFormatting.RED + name + " has been unfriended");
            }
            this.friends.remove(friend);
            break;
        }
    }
    
    public void onLoad() {
        this.friends = new ArrayList<Friend>();
        this.clearSettings();
    }
    
    public void saveFriends() {
        this.clearSettings();
        this.cleanFriends();
        for (final Friend friend : this.friends) {
            this.register(new Setting(friend.getUuid().toString(), (Object)friend.getUsername()));
        }
    }
    
    public void cleanFriends() {
        this.friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
    }
    
    public List<Friend> getFriends() {
        this.cleanFriends();
        return this.friends;
    }
    
    public boolean areFriendsOnline() {
        for (final NetworkPlayerInfo networkPlayerInfo : Objects.requireNonNull(FriendManager.mc.getConnection()).getPlayerInfoMap()) {
            if (this.isFriend(Objects.requireNonNull(networkPlayerInfo.getGameProfile().getName()))) {
                return true;
            }
        }
        return false;
    }
    
    public Friend getFriendByName(final String input) {
        final UUID uuid = PlayerUtil.getUUIDFromName(input);
        if (uuid != null) {
            final Friend friend = new Friend(input, uuid);
            return friend;
        }
        return null;
    }
    
    public void addFriend(final Friend friend) {
        this.friends.add(friend);
    }
    
    public static class Friend
    {
        private final String username;
        private final UUID uuid;
        
        public Friend(final String username, final UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }
        
        public String getUsername() {
            return this.username;
        }
        
        public UUID getUuid() {
            return this.uuid;
        }
    }
}
