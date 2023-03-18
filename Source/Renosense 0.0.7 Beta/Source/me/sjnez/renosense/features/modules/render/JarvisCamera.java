//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import net.minecraft.client.entity.*;
import me.sjnez.renosense.features.gui.font.*;
import me.sjnez.renosense.features.setting.*;
import net.minecraft.entity.player.*;
import me.sjnez.renosense.*;
import net.minecraft.entity.*;
import java.awt.*;
import me.sjnez.renosense.event.events.*;
import me.sjnez.renosense.features.command.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import me.sjnez.renosense.util.*;
import me.sjnez.renosense.features.gui.components.items.buttons.*;
import java.util.*;
import com.mojang.authlib.*;
import net.minecraft.world.*;

public class JarvisCamera extends Module
{
    public static EntityOtherPlayerMP renderer;
    public static EntityOtherPlayerMP old;
    private float oldFov;
    private float yawLerp;
    private float pitchLerp;
    private float start;
    private float fovLerp;
    private CustomFont customFont;
    public Setting<Boolean> ironman;
    
    public JarvisCamera() {
        super("JarvisCamera", "Camera for jarvis testing", Module.Category.RENDER, true, false, false);
        this.oldFov = JarvisCamera.mc.gameSettings.fovSetting;
        this.customFont = new CustomFont(new Font("Verdana", 0, 30), true, false);
        this.ironman = (Setting<Boolean>)this.register(new Setting("IronManMode", (T)false, "Blue"));
    }
    
    private EntityPlayer findClosestTarget() {
        EntityPlayer lowest = null;
        for (final EntityPlayer entityPlayer : JarvisCamera.mc.world.playerEntities) {
            if (entityPlayer.equals((Object)JarvisCamera.mc.player)) {
                continue;
            }
            if (entityPlayer.equals((Object)JarvisCamera.renderer)) {
                continue;
            }
            if (entityPlayer.equals((Object)JarvisCamera.old)) {
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
            if (lowest != null && JarvisCamera.mc.player.getDistance((Entity)entityPlayer) >= JarvisCamera.mc.player.getDistance((Entity)lowest)) {
                continue;
            }
            lowest = entityPlayer;
        }
        return lowest;
    }
    
    public void onLogin() {
        JarvisCamera.mc.gameSettings.fovSetting = this.oldFov;
        if (this.isOn()) {
            this.disable();
        }
    }
    
    public void onRender2D(final Render2DEvent event) {
        if (this.ironman.getValue()) {
            RenderUtil.drawRect(0.0f, 0.0f, (float)event.getScreenWidth(), (float)event.getScreenHeight(), new Color(74, 92, 212, 50).getRGB());
        }
        if (this.debug.getValue()) {
            this.customFont.drawStringWithShadow("Health is " + JarvisCamera.mc.player.getHealth(), 25.0, 25.0, -1);
        }
    }
    
    public void onUpdate() {
        if (JarvisCamera.old != null) {
            JarvisCamera.old.setAbsorptionAmount(JarvisCamera.mc.player.getAbsorptionAmount());
            JarvisCamera.old.setHealth(JarvisCamera.mc.player.getHealth());
            JarvisCamera.old.inventory = JarvisCamera.mc.player.inventory;
            JarvisCamera.old.inventoryContainer = JarvisCamera.mc.player.inventoryContainer;
        }
    }
    
    public void onRender3D(final Render3DEvent event) {
        JarvisCamera.mc.gameSettings.thirdPersonView = 0;
        JarvisCamera.mc.gameSettings.keyBindForward.pressed = false;
        JarvisCamera.mc.gameSettings.keyBindBack.pressed = false;
        JarvisCamera.mc.gameSettings.keyBindRight.pressed = false;
        JarvisCamera.mc.gameSettings.keyBindLeft.pressed = false;
        JarvisCamera.mc.gameSettings.keyBindJump.pressed = false;
        final EntityPlayer entityPlayer = this.findClosestTarget();
        if (entityPlayer == null) {
            JarvisCamera.mc.gameSettings.fovSetting = this.oldFov;
            this.disable();
            Command.sendDebugMessage("Nobody in range, disabling.", (Module)this);
            return;
        }
        if (JarvisCamera.renderer == null) {
            return;
        }
        final float deltaTime = 5.0f / Minecraft.getDebugFPS();
        final float yaw = (float)RotationUtil.calculateLookAt(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, (EntityPlayer)JarvisCamera.renderer)[0];
        final float pitch = (float)RotationUtil.calculateLookAt(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, (EntityPlayer)JarvisCamera.renderer)[1];
        final float fov = 150.0f - Math.max(20.0f, JarvisCamera.mc.player.getDistance((Entity)entityPlayer) * 3.0f);
        this.yawLerp = MathUtil.lerp(this.yawLerp, yaw, deltaTime);
        this.pitchLerp = MathUtil.lerp(this.pitchLerp, pitch, deltaTime);
        this.start = MathUtil.lerp(this.start, 1.0f, deltaTime);
        this.fovLerp = MathUtil.lerp(this.fovLerp, fov, deltaTime);
        JarvisCamera.renderer.setPositionAndRotation(JarvisCamera.old.posX, JarvisCamera.old.posY + 5.0f * this.start, JarvisCamera.old.posZ, this.yawLerp, this.pitchLerp);
        JarvisCamera.mc.gameSettings.fovSetting = ((this.fovLerp <= 1.0f) ? 100.0f : this.fovLerp);
        JarvisCamera.mc.setRenderViewEntity((Entity)JarvisCamera.renderer);
        final float[] interpolated = RenderUtil.interpolateEntity((Entity)entityPlayer);
        final double[] renderOffsets = { -JarvisCamera.mc.getRenderManager().renderPosX, -JarvisCamera.mc.getRenderManager().renderPosY, -JarvisCamera.mc.getRenderManager().renderPosZ };
        GL11.glPushMatrix();
        GL11.glTranslated(interpolated[0] + 0.5f + renderOffsets[0], interpolated[1] + 0.75f + renderOffsets[1], interpolated[2] + 0.5f + renderOffsets[2]);
        GL11.glRotatef(-JarvisCamera.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(JarvisCamera.mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        final double scale = 6.0;
        GL11.glScaled(-scale, -scale, scale);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        GL11.glRotatef(System.currentTimeMillis() % 4680L / 13.0f, 0.0f, 0.0f, -1.0f);
        JarvisCamera.mc.getTextureManager().bindTexture(new ResourceLocation("textures/jarvis/ring_3.png"));
        ColorUtil.glColor(Color.CYAN);
        ModuleButton.drawCompleteImage(-0.5f, -0.5f, 1, 1);
        GL11.glRotatef(System.currentTimeMillis() % 5400L / 15.0f, 0.0f, 0.0f, 1.0f);
        JarvisCamera.mc.getTextureManager().bindTexture(new ResourceLocation("textures/jarvis/ring_2.png"));
        ColorUtil.glColor(Color.WHITE);
        ModuleButton.drawCompleteImage(-0.5f, -0.5f, 1, 1);
        GL11.glRotatef(System.currentTimeMillis() % 7200L / 20.0f, 0.0f, 0.0f, -1.0f);
        JarvisCamera.mc.getTextureManager().bindTexture(new ResourceLocation("textures/jarvis/ring_1.png"));
        ColorUtil.glColor(Color.WHITE);
        ModuleButton.drawCompleteImage(-0.5f, -0.5f, 1, 1);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }
    
    public void onEnable() {
        this.oldFov = JarvisCamera.mc.gameSettings.fovSetting;
        this.start = 0.0f;
        if (!fullNullCheck()) {
            JarvisCamera.renderer = new EntityOtherPlayerMP((World)JarvisCamera.mc.world, new GameProfile(UUID.fromString(String.valueOf(UUID.randomUUID())), "Jarvis"));
            (JarvisCamera.old = new EntityOtherPlayerMP((World)JarvisCamera.mc.world, JarvisCamera.mc.player.getGameProfile())).copyLocationAndAnglesFrom((Entity)JarvisCamera.mc.player);
            JarvisCamera.old.setAbsorptionAmount(JarvisCamera.mc.player.getAbsorptionAmount());
            JarvisCamera.old.setHealth(JarvisCamera.mc.player.getHealth());
            JarvisCamera.old.inventory = JarvisCamera.mc.player.inventory;
            JarvisCamera.old.inventoryContainer = JarvisCamera.mc.player.inventoryContainer;
            JarvisCamera.mc.world.addEntityToWorld(-6969, (Entity)JarvisCamera.renderer);
            JarvisCamera.mc.world.addEntityToWorld(-99353, (Entity)JarvisCamera.old);
        }
    }
    
    public void onDisable() {
        if (JarvisCamera.mc.world == null) {
            return;
        }
        if (JarvisCamera.mc.player != null) {
            JarvisCamera.mc.setRenderViewEntity((Entity)JarvisCamera.mc.player);
        }
        if (JarvisCamera.renderer != null) {
            JarvisCamera.mc.world.removeEntityFromWorld(-6969);
        }
        if (JarvisCamera.old != null) {
            JarvisCamera.mc.world.removeEntityFromWorld(-99353);
        }
        JarvisCamera.mc.gameSettings.fovSetting = this.oldFov;
    }
}
