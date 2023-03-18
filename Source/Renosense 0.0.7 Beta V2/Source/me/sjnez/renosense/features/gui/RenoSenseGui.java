//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.gui;

import me.sjnez.renosense.features.gui.components.*;
import me.sjnez.renosense.*;
import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.gui.components.items.buttons.*;
import me.sjnez.renosense.features.*;
import java.util.*;
import java.util.function.*;
import me.sjnez.renosense.features.gui.components.items.*;
import me.sjnez.renosense.features.modules.client.*;
import me.sjnez.renosense.util.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import java.io.*;

public class RenoSenseGui extends GuiScreen
{
    private static RenoSenseGui INSTANCE;
    private final ArrayList<Component> components;
    private final DescBox descBox;
    private ArrayList<Snow> _snowList;
    
    public RenoSenseGui() {
        this.components = new ArrayList<Component>();
        this.descBox = new DescBox("Description", 500, 20, true);
        this._snowList = new ArrayList<Snow>();
        this.setInstance();
        this.load();
    }
    
    public static RenoSenseGui getInstance() {
        if (RenoSenseGui.INSTANCE == null) {
            RenoSenseGui.INSTANCE = new RenoSenseGui();
        }
        return RenoSenseGui.INSTANCE;
    }
    
    public static RenoSenseGui getClickGui() {
        return getInstance();
    }
    
    private void setInstance() {
        RenoSenseGui.INSTANCE = this;
    }
    
    private void load() {
        int x = -80;
        final Random random = new Random();
        for (int i = 0; i < 100; ++i) {
            for (int y = 0; y < 3; ++y) {
                final Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                this._snowList.add(snow);
            }
        }
        for (final Module.Category category : RenoSense.moduleManager.getCategories()) {
            final ArrayList<Component> components2 = this.components;
            final String name = category.getName();
            x += 110;
            components2.add(new Component(name, x, 4, true) {
                public void setupItems() {
                    RenoSenseGui$1.counter1 = new int[] { 1 };
                    for (final Module module : RenoSense.moduleManager.getModulesByCategory(category)) {
                        if (!module.hidden) {
                            this.addButton((Button)new ModuleButton(module));
                        }
                    }
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing((Function<? super E, ? extends Comparable>)Feature::getName)));
    }
    
    public void updateModule(final Module module) {
        for (final Component component : this.components) {
            for (final Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) {
                    continue;
                }
                final ModuleButton button = (ModuleButton)item;
                final Module mod = button.getModule();
                if (module == null) {
                    continue;
                }
                if (!module.equals(mod)) {
                    continue;
                }
                button.initSettings();
            }
        }
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.checkMouseWheel();
        final int r = 20;
        final int g = 20;
        final int b = 20;
        final int a = 230 * ClickGui.getInstance().tint.getValue();
        final int color1 = ColorUtil.toARGB(r, g, b, a / 100);
        final int r2 = 0;
        final int g2 = 0;
        final int b2 = 0;
        final int color2 = ColorUtil.toARGB(r2, g2, b2, a / 100);
        this.drawGradientRect(0, 0, this.width, this.height, color1, color2);
        this.descBox.drag(mouseX, mouseY);
        this.descBox.drawScreen(mouseX, mouseY);
        for (final Component component : this.components) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
        final ScaledResolution res = new ScaledResolution(this.mc);
        if (!this._snowList.isEmpty() && ClickGui.getInstance().snowing.getValue()) {
            for (final Snow snow : this._snowList) {
                snow.Update(res);
            }
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int clickedButton) {
        this.descBox.mouseClicked(mouseX, mouseY, clickedButton);
        for (final Component component : this.components) {
            component.mouseClicked(mouseX, mouseY, clickedButton);
        }
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        this.descBox.mouseReleased(mouseX, mouseY, releaseButton);
        for (final Component component : this.components) {
            component.mouseReleased(mouseX, mouseY, releaseButton);
        }
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    public final ArrayList<Component> getComponents() {
        return this.components;
    }
    
    public void checkMouseWheel() {
        final int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            DescBox.descY -= 10.0f;
            for (final Component component : this.components) {
                component.setY(component.getY() - 10);
            }
        }
        else if (dWheel > 0) {
            DescBox.descY += 10.0f;
            for (final Component component : this.components) {
                component.setY(component.getY() + 10);
            }
        }
    }
    
    public int getTextOffset() {
        return -6;
    }
    
    public Component getComponentByName(final String name) {
        for (final Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) {
                continue;
            }
            return component;
        }
        return null;
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (final Component component : this.components) {
            component.onKeyTyped(typedChar, keyCode);
        }
    }
    
    static {
        RenoSenseGui.INSTANCE = new RenoSenseGui();
    }
}
