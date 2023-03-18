//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util.shaders;

import net.minecraft.client.shader.*;
import me.sjnez.renosense.features.modules.render.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import org.lwjgl.opengl.*;

public class DarkMatterShader implements Util
{
    private static final Shader shader;
    private static Framebuffer framebuffer;
    
    public static void setupUniforms(final float speed) {
        DarkMatterShader.shader.setUniformf("time", new float[] { (float)(System.currentTimeMillis() * (double)speed % (DarkMatterShader.mc.displayWidth * DarkMatterShader.mc.displayHeight) / 10.0) });
        DarkMatterShader.shader.setUniformf("resolution", new float[] { (float)DarkMatterShader.mc.displayWidth, (float)DarkMatterShader.mc.displayHeight });
        DarkMatterShader.shader.setUniformf("opacity", new float[] { (int)ShaderChams.getInstance().opacity.getValue() / 10.0f });
    }
    
    public static void setup(final float speed) {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        DarkMatterShader.framebuffer = RenderUtil.createFrameBuffer(DarkMatterShader.framebuffer);
        DarkMatterShader.mc.getFramebuffer().bindFramebuffer(true);
        DarkMatterShader.shader.init();
        setupUniforms(speed);
        GL11.glBindTexture(3553, DarkMatterShader.framebuffer.framebufferTexture);
    }
    
    public static void finish() {
        DarkMatterShader.shader.unload();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
        GL11.glEnable(3042);
    }
    
    static {
        shader = new Shader("textures/shader/darkmatter.frag");
        DarkMatterShader.framebuffer = new Framebuffer(1, 1, false);
    }
}
