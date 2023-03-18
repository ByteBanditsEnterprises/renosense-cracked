//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.event.events;

import me.sjnez.renosense.event.*;
import net.minecraft.entity.*;

public class EntityInteractEvent extends EventStage
{
    public final Entity target;
    
    public EntityInteractEvent(final Entity target) {
        this.target = target;
    }
}
