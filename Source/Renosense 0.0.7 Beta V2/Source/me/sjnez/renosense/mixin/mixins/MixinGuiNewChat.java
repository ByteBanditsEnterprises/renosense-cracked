//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import java.util.*;
import org.spongepowered.asm.mixin.*;
import me.sjnez.renosense.features.modules.misc.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.gui.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.modules.client.*;
import java.awt.*;
import net.minecraft.client.*;

@Mixin({ GuiNewChat.class })
public class MixinGuiNewChat extends Gui
{
    @Shadow
    @Final
    public List<ChatLine> drawnChatLines;
    private ChatLine chatLine;
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void drawRectHook(final int left, final int top, final int right, final int bottom, final int color) {
        Gui.drawRect(left, top, right, bottom, (ChatModifier.getInstance().isOn() && (boolean)ChatModifier.getInstance().clean.getValue()) ? 0 : color);
    }
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(final FontRenderer fontRenderer, final String text, final float x, final float y, final int color) {
        if (text.contains("§+")) {
            RenoSense.textManager.drawRainbowString(text, x, y, Color.HSBtoRGB(HUD.getInstance().hue, 1.0f, 1.0f), 100.0f, true);
        }
        else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return 0;
    }
    
    @Redirect(method = { "setChatLine" }, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0, remap = false))
    public int drawnChatLinesSize(final List<ChatLine> list) {
        return (ChatModifier.getInstance().isOn() && (boolean)ChatModifier.getInstance().infinite.getValue()) ? -2147483647 : list.size();
    }
    
    @Redirect(method = { "setChatLine" }, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2, remap = false))
    public int chatLinesSize(final List<ChatLine> list) {
        return (ChatModifier.getInstance().isOn() && (boolean)ChatModifier.getInstance().infinite.getValue()) ? -2147483647 : list.size();
    }
}
