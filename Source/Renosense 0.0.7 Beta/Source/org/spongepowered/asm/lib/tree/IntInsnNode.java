//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.tree;

import org.spongepowered.asm.lib.*;
import java.util.*;

public class IntInsnNode extends AbstractInsnNode
{
    public int operand;
    
    public IntInsnNode(final int opcode, final int operand) {
        super(opcode);
        this.operand = operand;
    }
    
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }
    
    public int getType() {
        return 1;
    }
    
    public void accept(final MethodVisitor mv) {
        mv.visitIntInsn(this.opcode, this.operand);
        this.acceptAnnotations(mv);
    }
    
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return new IntInsnNode(this.opcode, this.operand).cloneAnnotations((AbstractInsnNode)this);
    }
}
