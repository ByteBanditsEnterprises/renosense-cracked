//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.movement;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.client.event.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import net.minecraft.client.settings.*;
import net.minecraft.util.*;

public class NoSlow extends Module
{
    public Setting<Boolean> noSlow;
    public Setting<Boolean> explosions;
    public Setting<Float> horizontal;
    public Setting<Float> vertical;
    public Setting<Boolean> inventoryMove;
    public Setting<Boolean> imNoChat;
    private static NoSlow INSTANCE;
    
    public NoSlow() {
        super("NoSlow", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
        this.noSlow = (Setting<Boolean>)this.register(new Setting("NoSlow", (T)true));
        this.explosions = (Setting<Boolean>)this.register(new Setting("Explosions", (T)false));
        this.horizontal = (Setting<Float>)this.register(new Setting("Horizontal", (T)0.0f, (T)0.0f, (T)100.0f, v -> this.explosions.getValue()));
        this.vertical = (Setting<Float>)this.register(new Setting("Vertical", (T)0.0f, (T)0.0f, (T)100.0f, v -> this.explosions.getValue()));
        this.inventoryMove = (Setting<Boolean>)this.register(new Setting("Inventory Move", (T)true));
        this.imNoChat = (Setting<Boolean>)this.register(new Setting("IM NoChat", (T)true, v -> this.inventoryMove.getValue(), "Inventory Move does not work with chat when toggled. This helps because some people want to move in the inventory but not in chat."));
        this.setInstance();
    }
    
    private void setInstance() {
        NoSlow.INSTANCE = this;
    }
    
    public static NoSlow getInstance() {
        if (NoSlow.INSTANCE == null) {
            NoSlow.INSTANCE = new NoSlow();
        }
        return NoSlow.INSTANCE;
    }
    
    @SubscribeEvent
    public void onPacketReceived(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && NoSlow.mc.player != null) {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                final SPacketEntityVelocity velocity = (SPacketEntityVelocity)event.getPacket();
                if (velocity.getEntityID() == NoSlow.mc.player.entityId) {
                    if (this.horizontal.getValue() == 0.0f && this.vertical.getValue() == 0.0f) {
                        event.setCanceled(true);
                        return;
                    }
                    velocity.motionX *= (int)(Object)this.horizontal.getValue();
                    velocity.motionY *= (int)(Object)this.vertical.getValue();
                    velocity.motionZ *= (int)(Object)this.horizontal.getValue();
                }
            }
            if (this.explosions.getValue() && event.getPacket() instanceof SPacketExplosion) {
                final SPacketExplosion sPacketExplosion;
                final SPacketExplosion velocity2 = sPacketExplosion = (SPacketExplosion)event.getPacket();
                sPacketExplosion.motionX *= this.horizontal.getValue();
                final SPacketExplosion sPacketExplosion2 = velocity2;
                sPacketExplosion2.motionY *= this.vertical.getValue();
                final SPacketExplosion sPacketExplosion3 = velocity2;
                sPacketExplosion3.motionZ *= this.horizontal.getValue();
            }
        }
    }
    
    @SubscribeEvent
    public void onInput(final InputUpdateEvent event) {
        if (nullCheck()) {
            return;
        }
        if (this.inventoryMove.getValue() && NoSlow.mc.currentScreen != null) {
            if (this.imNoChat.getValue() && NoSlow.mc.currentScreen instanceof GuiChat) {
                return;
            }
            NoSlow.mc.player.movementInput.moveStrafe = 0.0f;
            NoSlow.mc.player.movementInput.moveForward = 0.0f;
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindForward.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindForward.getKeyCode())) {
                final MovementInput movementInput = NoSlow.mc.player.movementInput;
                ++movementInput.moveForward;
                NoSlow.mc.player.movementInput.forwardKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.forwardKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindBack.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindBack.getKeyCode())) {
                final MovementInput movementInput2 = NoSlow.mc.player.movementInput;
                --movementInput2.moveForward;
                NoSlow.mc.player.movementInput.backKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.backKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindLeft.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindLeft.getKeyCode())) {
                final MovementInput movementInput3 = NoSlow.mc.player.movementInput;
                ++movementInput3.moveStrafe;
                NoSlow.mc.player.movementInput.leftKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.leftKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindRight.getKeyCode()));
            if (Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindRight.getKeyCode())) {
                final MovementInput movementInput4 = NoSlow.mc.player.movementInput;
                --movementInput4.moveStrafe;
                NoSlow.mc.player.movementInput.rightKeyDown = true;
            }
            else {
                NoSlow.mc.player.movementInput.rightKeyDown = false;
            }
            KeyBinding.setKeyBindState(NoSlow.mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindJump.getKeyCode()));
            NoSlow.mc.player.movementInput.jump = Keyboard.isKeyDown(NoSlow.mc.gameSettings.keyBindJump.getKeyCode());
        }
        if (this.noSlow.getValue() && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            final MovementInput movementInput = event.getMovementInput();
            movementInput.moveStrafe *= 5.0f;
            final MovementInput movementInput2 = event.getMovementInput();
            movementInput2.moveForward *= 5.0f;
        }
    }
    
    static {
        NoSlow.INSTANCE = new NoSlow();
    }
}
