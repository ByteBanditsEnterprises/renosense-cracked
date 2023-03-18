//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util.shaders;

import net.minecraft.client.shader.*;
import me.sjnez.renosense.features.modules.render.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import org.lwjgl.opengl.*;

public class HypnoShader implements Util
{
    private static final Shader shader;
    private static Framebuffer framebuffer;
    
    public static void setupUniforms(final float speed) {
        HypnoShader.shader.setUniformf("time", new float[] { (float)(System.currentTimeMillis() * (double)speed % (HypnoShader.mc.displayWidth * HypnoShader.mc.displayHeight) / 10.0) });
        HypnoShader.shader.setUniformf("resolution", new float[] { (float)HypnoShader.mc.displayWidth, (float)HypnoShader.mc.displayHeight });
        HypnoShader.shader.setUniformf("opacity", new float[] { (int)ShaderChams.getInstance().opacity.getValue() / 10.0f });
    }
    
    public static void setup(final float speed) {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        HypnoShader.framebuffer = RenderUtil.createFrameBuffer(HypnoShader.framebuffer);
        HypnoShader.mc.getFramebuffer().bindFramebuffer(true);
        HypnoShader.shader.init();
        setupUniforms(speed);
        GL11.glBindTexture(3553, HypnoShader.framebuffer.framebufferTexture);
    }
    
    public static void finish() {
        HypnoShader.shader.unload();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
        GL11.glEnable(3042);
    }
    
    static {
        shader = new Shader("textures/shader/hypno.frag");
        HypnoShader.framebuffer = new Framebuffer(1, 1, false);
    }
}
