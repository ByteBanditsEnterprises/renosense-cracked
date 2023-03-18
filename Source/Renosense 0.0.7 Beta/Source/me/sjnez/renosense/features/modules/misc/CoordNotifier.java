//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.misc;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.network.play.server.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.command.*;
import me.sjnez.renosense.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.client.network.*;
import me.sjnez.renosense.util.*;
import net.minecraft.util.text.*;
import java.util.*;

public class CoordNotifier extends Module
{
    public static CoordNotifier instance;
    private Setting<String> coordKey;
    private Setting<Boolean> friendOnly;
    private Setting<Boolean> safety;
    private Setting<Boolean> pvpMode;
    private Setting<Bind> bind;
    private Setting<Integer> delay;
    public Timer timer;
    public String coords;
    public List<String> wait;
    
    public CoordNotifier() {
        super("CoordNotifier", "Send your coordinates to friends.", Category.MISC, true, false, false);
        this.coordKey = (Setting<String>)this.register(new Setting("CoordKey", (T)"8vjakxcv8", "Give this to your friends, and if they whisper it to you, you send them your coordinates automatically!"));
        this.friendOnly = (Setting<Boolean>)this.register(new Setting("FriendOnly", (T)true, "For coord key, will work with friends or no."));
        this.safety = (Setting<Boolean>)this.register(new Setting("Safety", (T)false));
        this.pvpMode = (Setting<Boolean>)this.register(new Setting("PvPMode", (T)true));
        this.bind = (Setting<Bind>)this.register(new Setting("Activate", (T)new Bind(-1), "Press to show list of friends to send your coords"));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)0, (T)1, (T)7));
        this.timer = new Timer();
        this.wait = new ArrayList<String>();
        CoordNotifier.instance = this;
    }
    
    public static CoordNotifier getInstance() {
        if (CoordNotifier.instance == null) {
            CoordNotifier.instance = new CoordNotifier();
        }
        return CoordNotifier.instance;
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            final String text = ((SPacketChat)event.getPacket()).chatComponent.getFormattedText();
            if (text.contains("whispers:") && text.contains(this.coordKey.getValue())) {
                final String player = MessageUtil.dmer(text);
                if (this.safety.getValue() && !this.isSafe()) {
                    Command.sendMessage(ChatFormatting.RED + "You are not at spawn and have Safety enabled.");
                    return;
                }
                if ((RenoSense.friendManager.isFriend(player) && this.friendOnly.getValue()) || (!RenoSense.friendManager.isFriend(player) && !this.friendOnly.getValue()) || (RenoSense.friendManager.isFriend(player) && !this.friendOnly.getValue())) {
                    final String d = "/w " + player + " " + this.coords + " Here are my coordinates!";
                    if (this.timer.passedS(this.delay.getValue())) {
                        Command.sendDebugMessage("sent", (Module)this);
                        CoordNotifier.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(d));
                        this.timer.reset();
                        System.out.println("reset timer");
                    }
                    else {
                        this.wait.add(player);
                        Command.sendDebugMessage("added " + this.wait.get(0), (Module)this);
                    }
                }
                else {
                    Command.sendDebugMessage(player + " is not a friend, or this was not a message. will not send to them!", (Module)this);
                }
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (this.wait.size() == 0) {
            return;
        }
        if (this.timer.passedS(this.delay.getValue())) {
            System.out.println(this.timer.getPassedTimeMs());
            Command.sendDebugMessage(String.valueOf(this.timer.getPassedTimeMs()), (Module)this);
            this.updateCoordinates();
            final String mout = this.wait.get(0);
            final String d = "/w " + mout + " " + this.coords + " Here are my coordinates!";
            CoordNotifier.mc.player.connection.sendPacket((Packet)new CPacketChatMessage(d));
            this.wait.remove(0);
            this.timer.reset();
        }
    }
    
    public void onKeyInput(final int key) {
        this.updateCoordinates();
        if (this.isOff()) {
            return;
        }
        if (this.bind.getValue().getKey() == key) {
            if (CoordNotifier.mc.isSingleplayer()) {
                return;
            }
            final Collection<NetworkPlayerInfo> InfoMap = (Collection<NetworkPlayerInfo>)CoordNotifier.mc.player.connection.getPlayerInfoMap();
            if (this.safety.getValue() && !this.isSafe()) {
                Command.sendMessage(ChatFormatting.RED + "You are not at spawn and have Safety enabled.");
                return;
            }
            if (!RenoSense.friendManager.areFriendsOnline()) {
                Command.sendMessage(ChatFormatting.RED + "No friends are online. Disabling.");
                return;
            }
            Command.sendMessage(ChatFormatting.RED + "Send your coordinates to which friends?");
            for (final NetworkPlayerInfo p : InfoMap) {
                final String getUrl = "/w " + p.getGameProfile().getName() + " " + this.coords + " Here are my coordinates!";
                final ChatHelper ch = new ChatHelper();
                if (RenoSense.friendManager.isFriend(p.getGameProfile().getName()) && (!this.safety.getValue() || this.isSafe())) {
                    ch.sendMessage((ITextComponent)new ChatComponent(p.getGameProfile().getName()).green().bold().setUrl(getUrl, "Click on this to send them your coordinates!").italic());
                }
            }
        }
    }
    
    public boolean isSafe() {
        return this.safety.getValue() && CoordNotifier.mc.player.getPosition().getX() < 5000 && CoordNotifier.mc.player.getPosition().getY() < 5000 && CoordNotifier.mc.player.getPosition().getX() > -5000 && CoordNotifier.mc.player.getPosition().getY() > -5000;
    }
    
    public void updateCoordinates() {
        final boolean inHell = CoordNotifier.mc.world.getBiome(CoordNotifier.mc.player.getPosition()).getBiomeName().equals("Hell");
        final int posX = (int)CoordNotifier.mc.player.posX;
        final int posY = (int)CoordNotifier.mc.player.posY;
        final int posZ = (int)CoordNotifier.mc.player.posZ;
        final float nether = inHell ? 8.0f : 0.125f;
        final int hposX = (int)(CoordNotifier.mc.player.posX * nether);
        final int hposZ = (int)(CoordNotifier.mc.player.posZ * nether);
        if (this.pvpMode.getValue()) {
            this.coords = posX + ", " + posZ;
        }
        else {
            this.coords = (inHell ? (posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]") : (posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]"));
        }
    }
}
