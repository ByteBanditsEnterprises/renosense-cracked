//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.effect.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;

public class KillEffects extends Module
{
    public Setting<Boolean> thunder;
    public Setting<Integer> numbersThunder;
    public Setting<Boolean> sound;
    public Setting<Integer> numberSound;
    ArrayList<EntityPlayer> playersDead;
    
    public KillEffects() {
        super("KillEffects", "Effects when you kill.", Module.Category.RENDER, true, false, false);
        this.thunder = (Setting<Boolean>)this.register(new Setting("Thunder", (T)true));
        this.numbersThunder = (Setting<Integer>)this.register(new Setting("Number Thunder", (T)1, (T)1, (T)10));
        this.sound = (Setting<Boolean>)this.register(new Setting("Sound", (T)true));
        this.numberSound = (Setting<Integer>)this.register(new Setting("Number Sound", (T)1, (T)1, (T)10));
        this.playersDead = new ArrayList<EntityPlayer>();
    }
    
    public void onEnable() {
        this.playersDead.clear();
    }
    
    public void onUpdate() {
        if (KillEffects.mc.world == null) {
            this.playersDead.clear();
            return;
        }
        int i;
        int j;
        KillEffects.mc.world.playerEntities.forEach(entity -> {
            if (this.playersDead.contains(entity)) {
                if (entity.getHealth() > 0.0f) {
                    this.playersDead.remove(entity);
                }
            }
            else if (entity.getHealth() == 0.0f) {
                if (this.thunder.getValue()) {
                    for (i = 0; i < this.numbersThunder.getValue(); ++i) {
                        KillEffects.mc.world.spawnEntity((Entity)new EntityLightningBolt((World)KillEffects.mc.world, entity.posX, entity.posY, entity.posZ, true));
                    }
                }
                if (this.sound.getValue()) {
                    for (j = 0; j < this.numberSound.getValue(); ++j) {
                        KillEffects.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 0.5f, 1.0f);
                    }
                }
                this.playersDead.add(entity);
            }
        });
    }
}
