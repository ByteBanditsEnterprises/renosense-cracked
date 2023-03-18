//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import me.sjnez.renosense.*;
import com.mojang.text2speech.*;
import java.util.*;
import net.minecraft.client.renderer.*;

public class Jarvis extends Module
{
    private final Timer timer;
    
    public Jarvis() {
        super("Jarvis", "IronMans best friend.", Category.CLIENT, true, false, false);
        this.timer = new Timer();
    }
    
    @Override
    public void onUpdate() {
        if (!fullNullCheck()) {
            for (final EntityPlayer player : Jarvis.mc.world.playerEntities) {
                if (this.timer.passedMs(5000L)) {
                    if (player.getGameProfile() == Jarvis.mc.player.getGameProfile()) {
                        continue;
                    }
                    final BlockPos pos = new BlockPos(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
                    if (player.getDistanceSq(pos) >= 50.0 || RenoSense.friendManager.isFriend(player)) {
                        continue;
                    }
                    Narrator.getNarrator().say(player.getName() + " Has been spotted at: " + (int)player.posX + " X, " + (int)player.posY + " Y.");
                    this.timer.reset();
                }
            }
        }
    }
    
    public void renderPlayer(final EntityPlayer player) {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
    }
    
    @Override
    public void onDisable() {
        Narrator.getNarrator().clear();
    }
}
