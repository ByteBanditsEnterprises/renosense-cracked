//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;

public class ViewModel extends Module
{
    public final Setting<Integer> translateX;
    public final Setting<Integer> translateY;
    public final Setting<Integer> translateZ;
    public final Setting<Integer> rotateX;
    public final Setting<Integer> rotateY;
    public final Setting<Integer> rotateZ;
    public final Setting<Integer> scaleX;
    public final Setting<Integer> scaleY;
    public final Setting<Integer> scaleZ;
    public static ViewModel INSTANCE;
    
    public ViewModel() {
        super("ViewModel", "Change the models of your items you have.", Module.Category.RENDER, true, false, false);
        this.translateX = (Setting<Integer>)this.register(new Setting("TranslateX", (T)0, (T)(-200), (T)200));
        this.translateY = (Setting<Integer>)this.register(new Setting("TranslateY", (T)0, (T)(-200), (T)200));
        this.translateZ = (Setting<Integer>)this.register(new Setting("TranslateZ", (T)0, (T)(-200), (T)200));
        this.rotateX = (Setting<Integer>)this.register(new Setting("RotateX", (T)0, (T)(-200), (T)200));
        this.rotateY = (Setting<Integer>)this.register(new Setting("RotateY", (T)0, (T)(-200), (T)200));
        this.rotateZ = (Setting<Integer>)this.register(new Setting("RotateZ", (T)0, (T)(-200), (T)200));
        this.scaleX = (Setting<Integer>)this.register(new Setting("ScaleX", (T)100, (T)0, (T)200));
        this.scaleY = (Setting<Integer>)this.register(new Setting("ScaleY", (T)100, (T)0, (T)200));
        this.scaleZ = (Setting<Integer>)this.register(new Setting("ScaleZ", (T)100, (T)0, (T)200));
        ViewModel.INSTANCE = this;
    }
}
