//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package org.spongepowered.tools.obfuscation.service;

import java.util.*;

public interface IObfuscationService
{
    Set<String> getSupportedOptions();
    
    Collection<ObfuscationTypeDescriptor> getObfuscationTypes();
}
