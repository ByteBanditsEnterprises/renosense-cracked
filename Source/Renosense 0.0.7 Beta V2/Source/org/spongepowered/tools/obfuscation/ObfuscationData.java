//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.tools.obfuscation;

import java.util.*;

public class ObfuscationData<T> implements Iterable<ObfuscationType>
{
    private final Map<ObfuscationType, T> data;
    private final T defaultValue;
    
    public ObfuscationData() {
        this(null);
    }
    
    public ObfuscationData(final T defaultValue) {
        this.data = new HashMap<ObfuscationType, T>();
        this.defaultValue = defaultValue;
    }
    
    @Deprecated
    public void add(final ObfuscationType type, final T value) {
        this.put(type, value);
    }
    
    public void put(final ObfuscationType type, final T value) {
        this.data.put(type, value);
    }
    
    public boolean isEmpty() {
        return this.data.isEmpty();
    }
    
    public T get(final ObfuscationType type) {
        final T value = this.data.get(type);
        return (value != null) ? value : this.defaultValue;
    }
    
    @Override
    public Iterator<ObfuscationType> iterator() {
        return this.data.keySet().iterator();
    }
    
    @Override
    public String toString() {
        return String.format("ObfuscationData[%s,DEFAULT=%s]", this.listValues(), this.defaultValue);
    }
    
    public String values() {
        return "[" + this.listValues() + "]";
    }
    
    private String listValues() {
        final StringBuilder sb = new StringBuilder();
        boolean delim = false;
        for (final ObfuscationType type : this.data.keySet()) {
            if (delim) {
                sb.append(',');
            }
            sb.append(type.getKey()).append('=').append(this.data.get(type));
            delim = true;
        }
        return sb.toString();
    }
}
