//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.features.setting;

import com.google.common.base.*;
import java.util.*;
import com.google.gson.*;

public class EnumConverter extends Converter<Enum, JsonElement>
{
    private final Class<? extends Enum> clazz;
    
    public EnumConverter(final Class<? extends Enum> clazz) {
        this.clazz = clazz;
    }
    
    public static int currentEnum(final Enum clazz) {
        for (int i = 0; i < ((Enum[])clazz.getClass().getEnumConstants()).length; ++i) {
            final Enum e = ((Enum[])clazz.getClass().getEnumConstants())[i];
            if (e.name().equalsIgnoreCase(clazz.name())) {
                return i;
            }
        }
        return -1;
    }
    
    public static int enumCount(final Enum clazz) {
        return (int)Arrays.stream(clazz.getClass().getEnumConstants()).count();
    }
    
    public static Enum decreaseEnum(final Enum clazz) {
        final int index = currentEnum(clazz);
        for (int i = 0; i < ((Enum[])clazz.getClass().getEnumConstants()).length; ++i) {
            final Enum e = ((Enum[])clazz.getClass().getEnumConstants())[i];
            if (i == index - 1) {
                return e;
            }
        }
        final int c = ((Enum[])clazz.getClass().getEnumConstants()).length;
        return ((Enum[])clazz.getClass().getEnumConstants())[c - 1];
    }
    
    public static Enum increaseEnum(final Enum clazz) {
        final int index = currentEnum(clazz);
        for (int i = 0; i < ((Enum[])clazz.getClass().getEnumConstants()).length; ++i) {
            final Enum e = ((Enum[])clazz.getClass().getEnumConstants())[i];
            if (i == index + 1) {
                return e;
            }
        }
        return ((Enum[])clazz.getClass().getEnumConstants())[0];
    }
    
    public static String getProperName(final Enum clazz) {
        return Character.toUpperCase(clazz.name().charAt(0)) + clazz.name().toLowerCase().substring(1);
    }
    
    public JsonElement doForward(final Enum anEnum) {
        return (JsonElement)new JsonPrimitive(anEnum.toString());
    }
    
    public Enum doBackward(final JsonElement jsonElement) {
        try {
            return (Enum)Enum.valueOf(this.clazz, jsonElement.getAsString());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
