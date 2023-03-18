//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.player;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.features.*;
import org.lwjgl.input.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import me.sjnez.renosense.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import me.sjnez.renosense.util.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class MiddleClick extends Module
{
    private boolean clicked;
    private boolean clickedbutton;
    private final Setting<FriendorEnemy> f;
    private final Setting<Boolean> pearl;
    private final Setting<Mode> mode;
    
    public MiddleClick() {
        super("MiddleClick", "Stuff for middle clicking.", Module.Category.PLAYER, true, false, false);
        this.clicked = false;
        this.clickedbutton = false;
        this.f = (Setting<FriendorEnemy>)this.register(new Setting("PlayerMode", (T)FriendorEnemy.NONE, "Friend, or enemy?"));
        this.pearl = (Setting<Boolean>)this.register(new Setting("Pearl", (T)false));
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", (T)Mode.MiddleClick, v -> this.pearl.getValue()));
    }
    
    public void onEnable() {
        if (this.pearl.getValue() && !Feature.fullNullCheck() && this.mode.getValue() == Mode.Toggle) {
            this.throwPearl();
            this.disable();
        }
    }
    
    public void onUpdate() {
        if (!this.f.getValue().equals(FriendorEnemy.NONE)) {
            if (Mouse.isButtonDown(2)) {
                if (!this.clicked && MiddleClick.mc.currentScreen == null) {
                    this.onClick();
                }
                this.clicked = true;
            }
            else {
                this.clicked = false;
            }
        }
    }
    
    public void onTick() {
        if (this.pearl.getValue() && this.mode.getValue() == Mode.MiddleClick) {
            if (Mouse.isButtonDown(2)) {
                if (!this.clickedbutton) {
                    this.throwPearl();
                }
                this.clickedbutton = true;
            }
            else {
                this.clickedbutton = false;
            }
        }
    }
    
    private void onClick() {
        switch (this.f.getValue()) {
            case FRIEND: {
                final RayTraceResult result = MiddleClick.mc.objectMouseOver;
                final Entity entity;
                if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
                    if (RenoSense.friendManager.isFriend(entity.getName())) {
                        RenoSense.friendManager.removeFriend(entity.getName());
                    }
                    else {
                        RenoSense.friendManager.addFriend(entity.getName());
                    }
                }
                this.clicked = true;
                break;
            }
            case ENEMY: {
                final RayTraceResult result2 = MiddleClick.mc.objectMouseOver;
                final Entity entity2;
                if (result2 != null && result2.typeOfHit == RayTraceResult.Type.ENTITY && (entity2 = result2.entityHit) instanceof EntityPlayer) {
                    if (RenoSense.enemyManager.isSuperEnemy(entity2.getName())) {
                        RenoSense.enemyManager.removeEnemy(entity2.getName());
                    }
                    else if (RenoSense.enemyManager.isEnemy(entity2.getName()) && !RenoSense.enemyManager.isSuperEnemy(entity2.getName())) {
                        RenoSense.enemyManager.editEnemy(entity2.getName(), 2);
                    }
                    else {
                        RenoSense.enemyManager.addEnemy(entity2.getName(), 1);
                    }
                }
                this.clicked = true;
                break;
            }
        }
    }
    
    private void throwPearl() {
        if (this.pearl.getValue()) {
            final int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
            final RayTraceResult result;
            final Entity entity;
            if ((result = MiddleClick.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
                return;
            }
            final boolean bl;
            final boolean offhand = bl = (MiddleClick.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL);
            if (pearlSlot != -1 || offhand) {
                final int oldslot = MiddleClick.mc.player.inventory.currentItem;
                if (!offhand) {
                    InventoryUtil.switchToHotbarSlot(pearlSlot, false);
                }
                MiddleClick.mc.playerController.processRightClick((EntityPlayer)MiddleClick.mc.player, (World)MiddleClick.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                if (!offhand) {
                    InventoryUtil.switchToHotbarSlot(oldslot, false);
                }
            }
        }
    }
    
    public enum Mode
    {
        Toggle, 
        MiddleClick;
    }
    
    public enum FriendorEnemy
    {
        FRIEND, 
        ENEMY, 
        NONE;
    }
}
