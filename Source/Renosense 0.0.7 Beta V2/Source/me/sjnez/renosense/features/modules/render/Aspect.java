//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.modules.render;

import me.sjnez.renosense.features.modules.*;
import me.sjnez.renosense.features.setting.*;
import me.sjnez.renosense.event.events.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Aspect extends Module
{
    public Setting<Double> aspect;
    
    public Aspect() {
        super("AspectRatio", "Stretched res like fortnite.", Module.Category.RENDER, true, false, false);
        this.aspect = (Setting<Double>)this.register(new Setting("aspect", (T)(Aspect.mc.displayWidth / Aspect.mc.displayHeight + 0.0), (T)0.0, (T)3.0));
    }
    
    @SubscribeEvent
    public void onPerspectiveEvent(final PerspectiveEvent event) {
        event.setAspect(this.aspect.getValue().floatValue());
    }
}
