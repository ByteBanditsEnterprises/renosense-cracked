//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.sjnez.renosense.util.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import me.sjnez.renosense.features.gui.menu.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.injection.*;
import java.util.*;
import me.sjnez.renosense.features.modules.misc.*;
import net.minecraft.item.*;

@Mixin({ GuiScreen.class })
public abstract class MixinGuiScreen extends Gui
{
    private float logoLerp;
    private float bgLerp;
    
    @Inject(method = { "drawBackground" }, at = { @At("RETURN") }, cancellable = true)
    public void drawBackground(final int tint, final CallbackInfo ci) {
        final GuiScreen current = Util.mc.currentScreen;
        if (current instanceof GuiDownloadTerrain || current instanceof GuiConnecting || current instanceof GuiScreenWorking) {
            System.out.println("rendering logo image");
            final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            GL11.glPushMatrix();
            RenoSenseLoad.renderBackgroundImage(0, 0);
            GlStateManager.enableAlpha();
            RenoSenseLoad.renderLogo((float)System.currentTimeMillis());
            RenoSenseLoad.renderLoadIcon(60.0f, 60.0f, System.currentTimeMillis() % 7200L / 20.0f);
            GlStateManager.disableAlpha();
            RenoSenseLoad.renderTips(scaledResolution.getScaledWidth() / 2, 10);
            GL11.glPopMatrix();
            ci.cancel();
        }
    }
    
    @Inject(method = { "onGuiClosed" }, at = { @At("HEAD") })
    private void onGuiClosed(final CallbackInfo ci) {
        final Random r = new Random();
        RenoSenseLoad.logoLerp = 0.0f;
        RenoSenseLoad.g = r.nextInt(3);
        RenoSenseLoad.t = r.nextInt(RenoSenseLoad.tAm);
    }
    
    @Inject(method = { "renderToolTip" }, at = { @At("HEAD") }, cancellable = true)
    public void renderToolTipHook(final ItemStack stack, final int x, final int y, final CallbackInfo info) {
        if (ToolTips.getInstance().isOn() && stack.getItem() instanceof ItemShulkerBox) {
            ToolTips.getInstance().renderShulkerToolTip(stack, x, y, (String)null);
            info.cancel();
        }
    }
}
