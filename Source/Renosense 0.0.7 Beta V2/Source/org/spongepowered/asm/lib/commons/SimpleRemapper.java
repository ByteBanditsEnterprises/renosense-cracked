//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.asm.lib.commons;

import java.util.*;

public class SimpleRemapper extends Remapper
{
    private final Map<String, String> mapping;
    
    public SimpleRemapper(final Map<String, String> mapping) {
        this.mapping = mapping;
    }
    
    public SimpleRemapper(final String oldName, final String newName) {
        this.mapping = Collections.singletonMap(oldName, newName);
    }
    
    public String mapMethodName(final String owner, final String name, final String desc) {
        final String s = this.map(owner + '.' + name + desc);
        return (s == null) ? name : s;
    }
    
    public String mapInvokeDynamicMethodName(final String name, final String desc) {
        final String s = this.map('.' + name + desc);
        return (s == null) ? name : s;
    }
    
    public String mapFieldName(final String owner, final String name, final String desc) {
        final String s = this.map(owner + '.' + name);
        return (s == null) ? name : s;
    }
    
    public String map(final String key) {
        return this.mapping.get(key);
    }
}
