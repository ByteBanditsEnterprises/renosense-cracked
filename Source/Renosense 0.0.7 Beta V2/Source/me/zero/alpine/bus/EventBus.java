//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.zero.alpine.bus;

import me.zero.alpine.listener.*;
import java.util.*;
import java.util.function.*;

public interface EventBus
{
    void subscribe(final Listenable p0);
    
    void subscribe(final Listener p0);
    
    default void subscribeAll(final Listenable... listenables) {
        Arrays.stream(listenables).forEach(this::subscribe);
    }
    
    default void subscribeAll(final Iterable<Listenable> listenables) {
        listenables.forEach(this::subscribe);
    }
    
    default void subscribeAll(final Listener... listeners) {
        Arrays.stream(listeners).forEach(this::subscribe);
    }
    
    void unsubscribe(final Listenable p0);
    
    void unsubscribe(final Listener p0);
    
    default void unsubscribeAll(final Listenable... listenables) {
        Arrays.stream(listenables).forEach(this::unsubscribe);
    }
    
    default void unsubscribeAll(final Iterable<Listenable> listenables) {
        listenables.forEach(this::unsubscribe);
    }
    
    default void unsubscribeAll(final Listener... listeners) {
        Arrays.stream(listeners).forEach(this::unsubscribe);
    }
    
    void post(final Object p0);
}
