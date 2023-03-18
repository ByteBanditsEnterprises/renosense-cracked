//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.combat;

import me.sjnez.renosense.features.modules.*;
import net.minecraft.util.math.*;
import me.sjnez.renosense.features.setting.*;
import java.awt.*;
import net.minecraft.network.play.server.*;
import net.minecraft.init.*;
import net.minecraft.entity.player.*;
import com.mojang.realmsclient.gui.*;
import me.sjnez.renosense.features.command.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.sjnez.renosense.event.events.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;

public class ChorusPredict extends Module
{
    private final Timer renderTimer;
    private BlockPos pos;
    private final Setting<Integer> renderDelay;
    private Setting<Boolean> rainbow;
    private Setting<Integer> hue;
    private Setting<Integer> sat;
    private Setting<Integer> bright;
    private Setting<Integer> alpha;
    private Setting<Integer> outlineAlpha;
    private Setting<Boolean> rotateToPos;
    private Setting<Integer> rotateRange;
    public Setting<Boolean> colorSync;
    
    public ChorusPredict() {
        super("ChorusPredict", "Predicts the chorus, and renders where they will teleport to.", Category.COMBAT, true, false, false);
        this.renderTimer = new Timer();
        this.renderDelay = (Setting<Integer>)this.register(new Setting("RenderDelay", (T)4000, (T)0, (T)4000, "Delay of render."));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (T)false, "Rainbow color."));
        this.hue = (Setting<Integer>)this.register(new Setting("Hue", (T)300, (T)0, (T)360, v -> !this.rainbow.getValue(), "Rainbow hue."));
        this.sat = (Setting<Integer>)this.register(new Setting("Saturation", (T)100, (T)0, (T)100, v -> !this.rainbow.getValue(), "Rainbow sat."));
        this.bright = (Setting<Integer>)this.register(new Setting("Brightness", (T)33, (T)0, (T)100, v -> !this.rainbow.getValue(), "Rainbow bright."));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue(), "Alpha."));
        this.outlineAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (T)0, (T)0, (T)255, v -> !this.rainbow.getValue(), "Outline alpha."));
        this.rotateToPos = (Setting<Boolean>)this.register(new Setting("RotateToChorus", (T)false, "Rotates to players chorus location."));
        this.rotateRange = (Setting<Integer>)this.register(new Setting("RotateToRange", (T)20, (T)5, (T)50, v -> this.rotateToPos.getValue(), "Range of chorus rotation."));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Color Sync", (T)false, "Syncs color."));
    }
    
    public int getRed() {
        return new Color(this.getColor()).getRed();
    }
    
    public int getGreen() {
        return new Color(this.getColor()).getGreen();
    }
    
    public int getBlue() {
        return new Color(this.getColor()).getBlue();
    }
    
    public int getColor() {
        return Color.HSBtoRGB(this.hue.getValue() / 360.0f, this.sat.getValue() / 100.0f, this.bright.getValue() / 100.0f);
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (packet.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT || packet.getSound() == SoundEvents.ENTITY_ENDERMEN_TELEPORT) {
                this.renderTimer.reset2();
                this.pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                if (this.rotateToPos.getValue() && Math.sqrt(ChorusPredict.mc.player.getDistanceSq(this.pos)) < this.rotateRange.getValue()) {
                    ChorusPredict.mc.player.rotationYaw = (float)RotationUtil.calculateLookAt(packet.getX(), packet.getY(), packet.getZ(), (EntityPlayer)ChorusPredict.mc.player)[0];
                    ChorusPredict.mc.player.rotationPitch = (float)RotationUtil.calculateLookAt(packet.getX(), packet.getY(), packet.getZ(), (EntityPlayer)ChorusPredict.mc.player)[1];
                }
                if (this.debug.getValue()) {
                    Command.sendDebugMessage("Player Chorused To: " + ChatFormatting.GOLD + "X: " + ChatFormatting.YELLOW + this.pos.getX() + ", " + ChatFormatting.GOLD + "Y: " + ChatFormatting.YELLOW + this.pos.getY() + ", " + ChatFormatting.GOLD + "Z: " + ChatFormatting.YELLOW + this.pos.getZ(), (Module)this);
                }
            }
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.pos != null) {
            if (this.renderTimer.passed(this.renderDelay.getValue())) {
                this.pos = null;
                return;
            }
            RenderUtil.drawBoxESP(this.pos, ((boolean)this.colorSync.getValue()) ? new Color(Colors.getInstance().getRed(), Colors.getInstance().getGreen(), Colors.getInstance().getBlue(), ClickGui.getInstance().hoverAlpha1.getValue()) : (this.rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.getRed(), this.getRed(), this.getGreen(), this.outlineAlpha.getValue())), 1.5f, true, true, this.alpha.getValue());
        }
    }
}
