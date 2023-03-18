//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util.shaders;

import net.minecraft.client.shader.*;
import me.sjnez.renosense.features.modules.render.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import org.lwjgl.opengl.*;

public class PhantomStarShader implements Util
{
    private static final Shader shader;
    private static Framebuffer framebuffer;
    
    public static void setupUniforms(final float speed) {
        PhantomStarShader.shader.setUniformf("time", new float[] { (float)(System.currentTimeMillis() * (double)speed % (PhantomStarShader.mc.displayWidth * PhantomStarShader.mc.displayHeight) / 10.0) });
        PhantomStarShader.shader.setUniformf("resolution", new float[] { (float)PhantomStarShader.mc.displayWidth, (float)PhantomStarShader.mc.displayHeight });
        PhantomStarShader.shader.setUniformf("opacity", new float[] { (int)ShaderChams.getInstance().opacity.getValue() / 10.0f });
    }
    
    public static void setup(final float speed) {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        PhantomStarShader.framebuffer = RenderUtil.createFrameBuffer(PhantomStarShader.framebuffer);
        PhantomStarShader.mc.getFramebuffer().bindFramebuffer(true);
        PhantomStarShader.shader.init();
        setupUniforms(speed);
        GL11.glBindTexture(3553, PhantomStarShader.framebuffer.framebufferTexture);
    }
    
    public static void finish() {
        PhantomStarShader.shader.unload();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
        GL11.glEnable(3042);
    }
    
    static {
        shader = new Shader("textures/shader/poly.frag");
        PhantomStarShader.framebuffer = new Framebuffer(1, 1, false);
    }
}
