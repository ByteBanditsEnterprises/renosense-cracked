//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.points;

import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.struct.*;
import java.util.*;
import org.spongepowered.asm.lib.tree.*;

@InjectionPoint.AtCode("HEAD")
public class MethodHead extends InjectionPoint
{
    public MethodHead(final InjectionPointData data) {
        super(data);
    }
    
    public boolean checkPriority(final int targetPriority, final int ownerPriority) {
        return true;
    }
    
    public boolean find(final String desc, final InsnList insns, final Collection<AbstractInsnNode> nodes) {
        nodes.add(insns.getFirst());
        return true;
    }
}
