//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ FontRenderer.class })
public interface IFontRenderer
{
    @Invoker("renderSplitString")
    void invokeRenderSplitString(final String p0, final int p1, final int p2, final int p3, final boolean p4);
}
