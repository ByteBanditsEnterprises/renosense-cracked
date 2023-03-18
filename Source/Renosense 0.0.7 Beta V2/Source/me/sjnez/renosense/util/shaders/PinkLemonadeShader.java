//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util.shaders;

import net.minecraft.client.shader.*;
import me.sjnez.renosense.features.modules.render.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import org.lwjgl.opengl.*;

public class PinkLemonadeShader implements Util
{
    private static final Shader shader;
    private static Framebuffer framebuffer;
    
    public static void setupUniforms(final float speed) {
        PinkLemonadeShader.shader.setUniformf("time", new float[] { (float)(System.currentTimeMillis() * (double)speed % (PinkLemonadeShader.mc.displayWidth * PinkLemonadeShader.mc.displayHeight) / 10.0) });
        PinkLemonadeShader.shader.setUniformf("resolution", new float[] { (float)PinkLemonadeShader.mc.displayWidth, (float)PinkLemonadeShader.mc.displayHeight });
        PinkLemonadeShader.shader.setUniformf("opacity", new float[] { (int)ShaderChams.getInstance().opacity.getValue() / 10.0f });
    }
    
    public static void setup(final float speed) {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        PinkLemonadeShader.framebuffer = RenderUtil.createFrameBuffer(PinkLemonadeShader.framebuffer);
        PinkLemonadeShader.mc.getFramebuffer().bindFramebuffer(true);
        PinkLemonadeShader.shader.init();
        setupUniforms(speed);
        GL11.glBindTexture(3553, PinkLemonadeShader.framebuffer.framebufferTexture);
    }
    
    public static void finish() {
        PinkLemonadeShader.shader.unload();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
        GL11.glEnable(3042);
    }
    
    static {
        shader = new Shader("textures/shader/pinklemonade.frag");
        PinkLemonadeShader.framebuffer = new Framebuffer(1, 1, false);
    }
}
