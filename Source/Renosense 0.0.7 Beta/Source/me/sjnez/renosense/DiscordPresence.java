//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense;

import me.sjnez.renosense.util.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import java.util.*;
import club.minnced.discord.rpc.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.features.gui.management.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.multiplayer.*;
import net.minecraftforge.fml.client.*;
import me.sjnez.renosense.features.gui.*;
import net.minecraft.client.gui.*;

public class DiscordPresence implements Util
{
    public static DiscordRichPresence presence;
    private static final DiscordRPC rpc;
    private static Thread thread;
    public static EntityOtherPlayerMP renderer;
    public static EntityOtherPlayerMP old;
    
    public static EntityPlayer findClosestTarget() {
        EntityPlayer lowest = null;
        for (final EntityPlayer entityPlayer : DiscordPresence.mc.world.playerEntities) {
            if (entityPlayer.equals((Object)DiscordPresence.mc.player)) {
                continue;
            }
            if (entityPlayer.equals((Object)DiscordPresence.renderer)) {
                continue;
            }
            if (entityPlayer.equals((Object)DiscordPresence.old)) {
                continue;
            }
            if (entityPlayer.isDead) {
                continue;
            }
            if (entityPlayer.getHealth() <= 0.0f) {
                continue;
            }
            if (RenoSense.friendManager.isFriend(entityPlayer)) {
                continue;
            }
            if (lowest != null && DiscordPresence.mc.player.getDistance((Entity)entityPlayer) >= DiscordPresence.mc.player.getDistance((Entity)lowest)) {
                continue;
            }
            lowest = entityPlayer;
        }
        return lowest;
    }
    
    public static void start() {
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordPresence.rpc.Discord_Initialize((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? "1060009632011137034" : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.SKYLANDERS) ? "1063706341501325342" : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.PLAGUEINC) ? "1063717861245337650" : "1063718855672877066")), handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordPresence.presence.state = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? RPC.INSTANCE.state.getValue() : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.SKYLANDERS) ? "" : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.PLAGUEINC) ? ("Spreading a virus to " + DiscordPresence.mc.joinPlayerCounter + " civillians.") : (RPC.INSTANCE.driving.getValue() ? "r" : RPC.INSTANCE.uberText.getValue()))));
        DiscordPresence.presence.largeImageText = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? RPC.INSTANCE.largeImageText.getValue() : "");
        DiscordPresence.presence.smallImageKey = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? RPC.INSTANCE.smallImage.getValue().toString() : "");
        DiscordPresence.presence.largeImageKey = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? RPC.INSTANCE.largeImage.getValue().toString() : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.SKYLANDERS) ? "giants" : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.PLAGUEINC) ? "plague" : "uber")));
        DiscordPresence.presence.smallImageText = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? RPC.INSTANCE.smallImageText.getValue() : "");
        DiscordPresence.presence.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        DiscordPresence.presence.partyMax = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? 50 : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.SKYLANDERS) ? 0 : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.PLAGUEINC) ? 0 : 5)));
        DiscordPresence.presence.partySize = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? 1 : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.SKYLANDERS) ? 0 : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.PLAGUEINC) ? 0 : RPC.INSTANCE.passengers.getValue())));
        DiscordPresence.presence.joinSecret = "joinDisTest";
        DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
        String r;
        GuiScreen current;
        String s;
        String serv;
        String string;
        DiscordRichPresence presence;
        String string2;
        (DiscordPresence.thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                DiscordPresence.rpc.Discord_RunCallbacks();
                r = "Driving my car into " + findClosestTarget().getGameProfile().getName();
                if (findClosestTarget() == null || DiscordPresence.mc.world == null || DiscordPresence.mc.player == null) {
                    r = "UBER!";
                }
                current = DiscordPresence.mc.currentScreen;
                s = "";
                if (current instanceof ConfigManagerGui) {
                    s = "In RenoSense Config Manager";
                }
                if (current instanceof PlayerManagerGui) {
                    s = "In RenoSense Player Manager";
                }
                if (current instanceof RemoveAds) {
                    s = "Trying to Remove Ads";
                }
                if (current instanceof RSManagerGui) {
                    s = "In RenoSense Manager";
                }
                if (current instanceof ServerManagerGui) {
                    s = "In RenoSense Server Manager";
                }
                if (current instanceof GuiMainMenu) {
                    s = "In the Main Menu";
                }
                if (DiscordPresence.mc.isSingleplayer()) {
                    s = "SinglePlayer";
                }
                if (DiscordPresence.mc.currentServerData != null) {
                    serv = DiscordPresence.mc.currentServerData.serverIP;
                    if (RPC.INSTANCE.showIP.getValue()) {
                        string = "On " + serv.substring(0, 1).toUpperCase() + serv.substring(1) + ".";
                    }
                    else {
                        string = " Multiplayer.";
                    }
                    s = string;
                }
                if (current instanceof GuiInventory) {
                    s = "Looking in the Inventory";
                }
                if (current instanceof GuiIngameMenu) {
                    s = "Looking at the In Game Menu";
                }
                if (current instanceof GuiChat) {
                    s = "Typing In Chat";
                }
                if (current instanceof GuiEditSign) {
                    s = "Editing a Sign";
                }
                if (current instanceof GuiChest) {
                    s = "Looking Through a Chest";
                }
                if (current instanceof GuiControls) {
                    s = "Changing the Controls";
                }
                if (current instanceof GuiDownloadTerrain || current instanceof GuiConnecting) {
                    s = "Loading Into a Server";
                }
                if (current instanceof GuiOptions) {
                    s = "Looking Through Options";
                }
                if (current instanceof GuiLanguage) {
                    s = "Changing the Language";
                }
                if (current instanceof GuiScreenResourcePacks) {
                    s = "Changing the texture pack";
                }
                if (current instanceof GuiScreenOptionsSounds) {
                    s = "Changing the Sound Levels";
                }
                if (current instanceof GuiVideoSettings) {
                    s = "Looking Through Video Options";
                }
                if (current instanceof GuiSnooper) {
                    s = "Looking through Snooper Settings";
                }
                if (current instanceof GuiModList) {
                    s = "Looking at their mods";
                }
                if (current instanceof GuiCustomizeSkin) {
                    s = "Customizing Skin";
                }
                if (current instanceof GuiGameOver) {
                    s = "Looking at Death Screen";
                }
                if (current instanceof GuiDisconnected) {
                    s = "Disconnected";
                }
                if (current instanceof GuiScreenServerList || current instanceof GuiScreenAddServer || current instanceof GuiMultiplayer) {
                    s = "Picking a Server to Play";
                }
                if (current instanceof RenoSenseGui) {
                    s = "In the RenoSense Gui";
                }
                DiscordPresence.presence.details = ((RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) ? s : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.SKYLANDERS) ? "" : ((RPC.INSTANCE.mode.getValue() == RPC.Mode.PLAGUEINC) ? "Plague Inc." : (RPC.INSTANCE.driving.getValue() ? r : RPC.INSTANCE.uberText.getValue()))));
                presence = DiscordPresence.presence;
                if (RPC.INSTANCE.mode.getValue() == RPC.Mode.RENOSENSE) {
                    string2 = RPC.INSTANCE.state.getValue();
                }
                else if (RPC.INSTANCE.mode.getValue() == RPC.Mode.SKYLANDERS) {
                    string2 = "";
                }
                else if (RPC.INSTANCE.mode.getValue() == RPC.Mode.PLAGUEINC) {
                    string2 = "Spreading a virus to " + playerCount() + " civillians.";
                }
                else {
                    string2 = "Passengers in car:";
                }
                presence.state = string2;
                DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException ex) {}
            }
        }, "RPC-Callback-Handler")).start();
    }
    
    public static int playerCount() {
        return DiscordPresence.mc.player.connection.getPlayerInfoMap().size() - 1;
    }
    
    public static void stop() {
        if (DiscordPresence.thread != null && !DiscordPresence.thread.isInterrupted()) {
            DiscordPresence.thread.interrupt();
        }
        DiscordPresence.rpc.Discord_Shutdown();
    }
    
    static {
        rpc = DiscordRPC.INSTANCE;
        DiscordPresence.presence = new DiscordRichPresence();
    }
}
