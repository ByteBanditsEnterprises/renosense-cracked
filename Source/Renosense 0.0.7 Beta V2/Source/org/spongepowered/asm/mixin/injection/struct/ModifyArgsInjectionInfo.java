//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.mixin.injection.struct;

import org.spongepowered.asm.mixin.transformer.*;
import org.spongepowered.asm.lib.tree.*;
import org.spongepowered.asm.mixin.injection.code.*;
import org.spongepowered.asm.mixin.injection.invoke.*;

public class ModifyArgsInjectionInfo extends InjectionInfo
{
    public ModifyArgsInjectionInfo(final MixinTargetContext mixin, final MethodNode method, final AnnotationNode annotation) {
        super(mixin, method, annotation);
    }
    
    protected Injector parseInjector(final AnnotationNode injectAnnotation) {
        return (Injector)new ModifyArgsInjector((InjectionInfo)this);
    }
    
    protected String getDescription() {
        return "Multi-argument modifier method";
    }
}
