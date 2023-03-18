//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui.components.items;

import java.util.*;
import java.awt.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;
import net.minecraft.client.gui.*;
import me.sjnez.renosense.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class DescBox extends Item
{
    public static float descX;
    public static float descY;
    public static float descH;
    public static int width;
    public static int height;
    private final ArrayList<Item> items;
    private int x2;
    private int y2;
    public boolean drag;
    public static boolean open;
    
    public DescBox(final String name, final int x, final int y, final boolean open) {
        super(name);
        this.items = new ArrayList<Item>();
        this.x = DescBox.descX;
        this.y = DescBox.descY;
        DescBox.open = open;
    }
    
    public void drawScreen(final int mouseX, final int mouseY) {
        this.drag(mouseX, mouseY);
        final int tRed = 0;
        final int tBlue = 0;
        final int tGreen = 0;
        final Color tColor = new Color(tRed, tGreen, tBlue, 119);
        final int color2 = ClickGui.getInstance().getTColor();
        if (DescBox.open) {
            RenderUtil.drawRectGradient(DescBox.descX, DescBox.descY - 4.0f, (float)DescBox.width, DescBox.descH, tColor, tColor, tColor, tColor);
        }
        Gui.drawRect((int)DescBox.descX, (int)DescBox.descY - 17, (int)DescBox.descX + DescBox.width, (int)DescBox.descY - 3, ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : color2);
        RenoSense.textManager.drawStringWithShadow("Description", DescBox.descX + 3.0f, DescBox.descY - 14.0f, -1);
    }
    
    public Color dColorz() {
        final int dRed = ClickGui.getInstance().getTRed();
        final int dBlue = ClickGui.getInstance().getTBlue();
        final int dGreen = ClickGui.getInstance().getTGreen();
        final Color dColor = new Color(dRed, dGreen, dBlue, 255);
        return ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : dColor;
    }
    
    public void drag(final int mouseX, final int mouseY) {
        if (!this.drag) {
            return;
        }
        DescBox.descX = (float)(this.x2 + mouseX);
        DescBox.descY = (float)(this.y2 + mouseY);
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = (int)(DescBox.descX - mouseX);
            this.y2 = (int)(DescBox.descY - mouseY);
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            DescBox.open = !DescBox.open;
            Item.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
    }
    
    public boolean isHovering(final int mouseX, final int mouseY) {
        return mouseX >= DescBox.descX && mouseX <= DescBox.descX + DescBox.width && mouseY <= DescBox.descY - 3.0f && mouseY >= DescBox.descY - 17.0f;
    }
    
    static {
        DescBox.descX = 690.0f;
        DescBox.descY = 20.0f;
        DescBox.descH = 50.0f;
        DescBox.width = 250;
        DescBox.height = 100;
    }
}
