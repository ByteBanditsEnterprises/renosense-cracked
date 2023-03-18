//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.menu;

import me.sjnez.renosense.features.gui.font.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import java.util.*;
import me.sjnez.renosense.features.gui.components.items.buttons.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import me.sjnez.renosense.util.*;
import java.awt.*;
import java.nio.file.*;

public class RenoSenseLoad extends GuiScreen
{
    public static float x;
    private static CustomFont customFont;
    public static final ResourceLocation bg1;
    public static final ResourceLocation bg2;
    public static final ResourceLocation bg3;
    public static final ResourceLocation logo;
    public static final ResourceLocation loadIcon;
    public static float logoLerp;
    public static int g;
    public static int t;
    public static int tAm;
    public static Path BG_TEXTURE;
    private static ScaledResolution resolution;
    
    public static void renderTips(final int x, final int y) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        final List<String> tips = new ArrayList<String>() {
            {
                this.add("Use AutoCrystal to attack players you do not like!");
                this.add("That boy scott got no rizz!");
                this.add("Turn on JarvisCamera to find your enemies easily!");
                this.add("Use KillMode to see red and win with ease.");
                this.add("When it comes to clients, none come close to RenoSense!");
                this.add("Press the W key to walk!");
                this.add("To stay safe while Crystal PvPing use the Surround Module!");
                this.add("Burrow ESP shows you players who are in a burrow!");
                this.add("RenoSense on 2b2t is helpful as it's modules are superior!");
            }
        };
        RenoSenseLoad.customFont.drawCenteredStringWithShadow((String)tips.get(RenoSenseLoad.t), (float)x, (float)y, -1);
    }
    
    public static void renderLogo(final float currTime) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        final float deltaTime = 5.0f / Minecraft.getDebugFPS();
        RenoSenseLoad.logoLerp = MathUtil.lerp(RenoSenseLoad.logoLerp, 1.0f, deltaTime / 20.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(RenoSenseLoad.logo);
        ModuleButton.drawCompleteImage(RenoSenseLoad.logoLerp * 25.0f, (float)(scaledResolution.getScaledHeight() - 135), 150, 150);
    }
    
    public static void renderBackgroundImage(final int x, final int y) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        final List<ResourceLocation> fN = new ArrayList<ResourceLocation>() {
            {
                this.add(RenoSenseLoad.bg1);
                this.add(RenoSenseLoad.bg2);
                this.add(RenoSenseLoad.bg3);
            }
        };
        System.out.println(RenoSenseLoad.g);
        Minecraft.getMinecraft().getTextureManager().bindTexture((ResourceLocation)fN.get(RenoSenseLoad.g));
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), (float)scaledResolution.getScaledWidth(), (float)scaledResolution.getScaledHeight());
    }
    
    public static void renderLoadIcon(final float width, final float height, final float rotation) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        final float x = (float)(scaledResolution.getScaledWidth() - 75);
        final float y = (float)(scaledResolution.getScaledHeight() - 60);
        RenoSenseLoad.customFont.drawStringWithShadow("Loading RenoSense Multiplayer", (double)(scaledResolution.getScaledWidth() - 300), (double)(scaledResolution.getScaledHeight() - 34), -1);
        GL11.glPushMatrix();
        GlStateManager.translate(x + width / 2.0f, y + height / 2.0f, 0.0f);
        GlStateManager.rotate(rotation, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-width / 2.0f, -height / 2.0f, 0.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(RenoSenseLoad.loadIcon);
        ColorUtil.glColor(Color.WHITE);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0f, 0.0f, (int)width, (int)height, width, height);
        GL11.glPopMatrix();
    }
    
    public static ScaledResolution current() {
        return (RenoSenseLoad.resolution != null) ? RenoSenseLoad.resolution : (RenoSenseLoad.resolution = new ScaledResolution(Minecraft.getMinecraft()));
    }
    
    static {
        RenoSenseLoad.x = 50.0f;
        RenoSenseLoad.customFont = new CustomFont(new Font("Arial", 0, 30), true, false);
        bg1 = new ResourceLocation("textures/loading/screen_1.png");
        bg2 = new ResourceLocation("textures/loading/screen_2.png");
        bg3 = new ResourceLocation("textures/loading/screen_3.png");
        logo = new ResourceLocation("textures/loading/renosense.png");
        loadIcon = new ResourceLocation("textures/loading/load.png");
        RenoSenseLoad.tAm = 9;
        RenoSenseLoad.BG_TEXTURE = Paths.get(RenoSenseLoad.bg1.getPath(), new String[0]);
    }
}
