//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util.shaders;

import net.minecraft.client.shader.*;
import me.sjnez.renosense.features.modules.render.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import org.lwjgl.opengl.*;

public class ScottShader implements Util
{
    private static final Shader shader;
    private static Framebuffer framebuffer;
    
    public static void setupUniforms(final float speed) {
        ScottShader.shader.setUniformf("time", new float[] { (float)(System.currentTimeMillis() * (double)speed % (ScottShader.mc.displayWidth * ScottShader.mc.displayHeight) / 10.0) });
        ScottShader.shader.setUniformf("resolution", new float[] { (float)ScottShader.mc.displayWidth, (float)ScottShader.mc.displayHeight });
        ScottShader.shader.setUniformf("opacity", new float[] { (int)ShaderChams.getInstance().opacity.getValue() / 10.0f });
    }
    
    public static void setup(final float speed) {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        ScottShader.framebuffer = RenderUtil.createFrameBuffer(ScottShader.framebuffer);
        ScottShader.mc.getFramebuffer().bindFramebuffer(true);
        ScottShader.shader.init();
        setupUniforms(speed);
        GL11.glBindTexture(3553, ScottShader.framebuffer.framebufferTexture);
    }
    
    public static void finish() {
        ScottShader.shader.unload();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
        GL11.glEnable(3042);
    }
    
    static {
        shader = new Shader("textures/shader/scott.frag");
        ScottShader.framebuffer = new Framebuffer(1, 1, false);
    }
}
