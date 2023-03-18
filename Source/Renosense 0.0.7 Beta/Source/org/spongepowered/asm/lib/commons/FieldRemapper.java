//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.commons;

import org.spongepowered.asm.lib.*;

public class FieldRemapper extends FieldVisitor
{
    private final Remapper remapper;
    
    public FieldRemapper(final FieldVisitor fv, final Remapper remapper) {
        this(327680, fv, remapper);
    }
    
    protected FieldRemapper(final int api, final FieldVisitor fv, final Remapper remapper) {
        super(api, fv);
        this.remapper = remapper;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        final AnnotationVisitor av = this.fv.visitAnnotation(this.remapper.mapDesc(desc), visible);
        return (AnnotationVisitor)((av == null) ? null : new AnnotationRemapper(av, this.remapper));
    }
    
    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        final AnnotationVisitor av = super.visitTypeAnnotation(typeRef, typePath, this.remapper.mapDesc(desc), visible);
        return (AnnotationVisitor)((av == null) ? null : new AnnotationRemapper(av, this.remapper));
    }
}
