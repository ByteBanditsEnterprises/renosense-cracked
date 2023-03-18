//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util.shaders;

import net.minecraft.client.shader.*;
import java.awt.*;
import me.sjnez.renosense.features.modules.render.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import org.lwjgl.opengl.*;

public class GradientShader implements Util
{
    private static final Shader shader;
    private static Framebuffer framebuffer;
    
    public static void setupUniforms(final float step, final float speed, final Color color, final Color color2, final float opacity) {
        GradientShader.shader.setUniformi("texture", new int[] { 0 });
        GradientShader.shader.setUniformf("rgb", new float[] { color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f });
        GradientShader.shader.setUniformf("rgb1", new float[] { color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f });
        GradientShader.shader.setUniformf("step", new float[] { 300.0f * step });
        GradientShader.shader.setUniformf("offset", new float[] { (float)(System.currentTimeMillis() * (double)speed % (GradientShader.mc.displayWidth * GradientShader.mc.displayHeight) / 10.0) });
        GradientShader.shader.setUniformf("mix", new float[] { opacity });
        GradientShader.shader.setUniformf("opacity", new float[] { (int)ShaderChams.getInstance().opacity.getValue() / 10.0f });
    }
    
    public static void setup(final float step, final float speed, final Color color, final Color color2) {
        setup(step, speed, color, color2, 1.0f);
    }
    
    public static void setup(final float step, final float speed, final Color color, final Color color2, final float opacity) {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GradientShader.framebuffer = RenderUtil.createFrameBuffer(GradientShader.framebuffer);
        GradientShader.mc.getFramebuffer().bindFramebuffer(true);
        GradientShader.shader.init();
        setupUniforms(step, speed, color, color2, opacity);
        GL11.glBindTexture(3553, GradientShader.framebuffer.framebufferTexture);
    }
    
    public static void finish() {
        GradientShader.shader.unload();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
        GL11.glEnable(3042);
    }
    
    static {
        shader = new Shader("textures/shader/gradient.frag");
        GradientShader.framebuffer = new Framebuffer(1, 1, false);
    }
}
