//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.client;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import java.util.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import net.minecraft.util.*;
import me.sjnez.renosense.event.events.*;
import java.awt.*;
import me.sjnez.renosense.util.*;

public class KillMode extends Module
{
    public static Timer particleTimer;
    private final Random random;
    private final Setting<Integer> rageAmount;
    
    public KillMode() {
        super("KillMode", ChatFormatting.OBFUSCATED + "GO INSANE", Category.CLIENT, true, false, false);
        this.random = new Random();
        this.rageAmount = (Setting<Integer>)this.register(new Setting("RageAmount", (T)10, (T)0, (T)100, "Particles."));
    }
    
    @Override
    public String getDisplayInfo() {
        return this.rageAmount.getValue().toString() + "%";
    }
    
    @Override
    public void onUpdate() {
        for (final EntityPlayer player : KillMode.mc.world.playerEntities) {
            if (player != null) {
                if (player.getHealth() > 0.0f) {
                    continue;
                }
                MinecraftForge.EVENT_BUS.post((Event)new DeathEvent(player));
                this.deathPlay();
            }
        }
    }
    
    public void deathPlay() {
        KillMode.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_GHAST_DEATH, 1.0f));
    }
    
    public void play() {
        KillMode.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_ENDERMEN_STARE, 1.0f));
    }
    
    @Override
    public void onEnable() {
        this.play();
    }
    
    @Override
    public void onDisable() {
        KillMode.mc.getSoundHandler().stopSounds();
        KillMode.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, 1.0f));
    }
    
    @Override
    public void onTick() {
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (KillMode.particleTimer.passedMs(50L)) {
            for (int i = 0; i < this.rageAmount.getValue() / 2; ++i) {
                KillMode.mc.world.spawnParticle(EnumParticleTypes.REDSTONE, KillMode.mc.player.posX - 1.0 + this.random.nextFloat() * 2.0f, KillMode.mc.player.posY + KillMode.mc.player.eyeHeight - 1.0 + this.random.nextFloat() * 2.0f, KillMode.mc.player.posZ - 1.0 + this.random.nextFloat() * 2.0f, 0.0, 0.0, 0.0, new int[0]);
            }
            KillMode.particleTimer.reset();
        }
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        RenderUtil.drawRect(0.0f, 0.0f, (float)event.getScreenWidth(), (float)event.getScreenHeight(), new Color(255, 0, 0, 100).getRGB());
    }
    
    static {
        KillMode.particleTimer = new Timer();
    }
}
