//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.player;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.client.entity.*;
import me.sjnez.renosense.features.command.*;
import com.mojang.authlib.*;
import net.minecraft.world.*;
import net.minecraft.potion.*;
import net.minecraft.entity.*;
import java.util.*;

public class FakePlayer extends Module
{
    private static FakePlayer instance;
    public final Setting<Boolean> rizzMode;
    public final Setting<String> pName;
    private EntityOtherPlayerMP _fakePlayer;
    
    public FakePlayer() {
        super("FakePlayer", "Spawns a FakePlayer for testing.", Module.Category.PLAYER, true, false, false);
        this.rizzMode = (Setting<Boolean>)this.register(new Setting("RizzMode", (T)true, "Rizz"));
        this.pName = (Setting<String>)this.register(new Setting("Name", (T)"Scott", "FakePlayerName"));
        FakePlayer.instance = this;
    }
    
    public static FakePlayer getInstance() {
        if (FakePlayer.instance == null) {
            FakePlayer.instance = new FakePlayer();
        }
        return FakePlayer.instance;
    }
    
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
            return;
        }
        this._fakePlayer = null;
        if (FakePlayer.mc.player != null) {
            Command.sendMessage(String.format("%s has been spawned.", this.pName.getValue()));
            (this._fakePlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.world, new GameProfile(UUID.fromString(String.valueOf(UUID.randomUUID())), (String)this.pName.getValue()))).copyLocationAndAnglesFrom((Entity)FakePlayer.mc.player);
            this._fakePlayer.inventory = FakePlayer.mc.player.inventory;
            this._fakePlayer.inventoryContainer = FakePlayer.mc.player.inventoryContainer;
            this._fakePlayer.setPositionAndRotation(FakePlayer.mc.player.posX, FakePlayer.mc.player.getEntityBoundingBox().minY, FakePlayer.mc.player.posZ, FakePlayer.mc.player.rotationYaw, FakePlayer.mc.player.rotationPitch);
            this._fakePlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
            this._fakePlayer.onGround = FakePlayer.mc.player.onGround;
            this._fakePlayer.setSneaking(FakePlayer.mc.player.isSneaking());
            this._fakePlayer.setHealth(FakePlayer.mc.player.getHealth());
            this._fakePlayer.setAbsorptionAmount(FakePlayer.mc.player.getAbsorptionAmount());
            for (final PotionEffect effect : FakePlayer.mc.player.getActivePotionEffects()) {
                this._fakePlayer.addPotionEffect(effect);
            }
            FakePlayer.mc.world.addEntityToWorld(-100, (Entity)this._fakePlayer);
            if (FakePlayer.mc.player != null && this._fakePlayer != null) {
                if (this.rizzMode.getValue()) {
                    this._fakePlayer.startRiding((Entity)FakePlayer.mc.player);
                }
                else {
                    this._fakePlayer.dismountRidingEntity();
                }
                this._fakePlayer.move(MoverType.SELF, FakePlayer.mc.player.motionX, FakePlayer.mc.player.motionY, FakePlayer.mc.player.motionZ);
            }
        }
    }
    
    public void onDisable() {
        if (FakePlayer.mc.world != null && this._fakePlayer != null) {
            FakePlayer.mc.world.removeEntity((Entity)this._fakePlayer);
            this._fakePlayer = null;
        }
    }
}
