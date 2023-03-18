//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.misc;

import me.sjnez.renosense.features.modules.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import me.sjnez.renosense.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.command.*;
import java.util.*;

public class PearlNotify extends Module
{
    private final HashMap<EntityPlayer, UUID> list;
    private Entity enderPearl;
    private boolean flag;
    
    public PearlNotify() {
        super("PearlNotify", "Notify pearl throws.", Category.MISC, true, false, false);
        this.list = new HashMap<EntityPlayer, UUID>();
    }
    
    @Override
    public void onEnable() {
        this.flag = true;
    }
    
    @Override
    public void onUpdate() {
        if (PearlNotify.mc.world == null || PearlNotify.mc.player == null) {
            return;
        }
        this.enderPearl = null;
        for (final Entity e : PearlNotify.mc.world.loadedEntityList) {
            if (e instanceof EntityEnderPearl) {
                this.enderPearl = e;
                break;
            }
        }
        if (this.enderPearl == null) {
            this.flag = true;
            return;
        }
        EntityPlayer closestPlayer = null;
        for (final EntityPlayer entity : PearlNotify.mc.world.playerEntities) {
            if (closestPlayer == null) {
                closestPlayer = entity;
            }
            else {
                if (closestPlayer.getDistance(this.enderPearl) <= entity.getDistance(this.enderPearl)) {
                    continue;
                }
                closestPlayer = entity;
            }
        }
        if (closestPlayer == PearlNotify.mc.player) {
            this.flag = false;
        }
        if (closestPlayer != null && this.flag) {
            String faceing = this.enderPearl.getHorizontalFacing().toString();
            if (faceing.equals("west")) {
                faceing = "east";
            }
            else if (faceing.equals("east")) {
                faceing = "west";
            }
            if (RenoSense.friendManager.isFriend(closestPlayer.getName())) {
                Command.sendMessage(ChatFormatting.AQUA + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing + "!");
            }
            else {
                Command.sendMessage(ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing + "!");
            }
            this.flag = false;
        }
    }
}
