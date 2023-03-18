//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util;

import net.minecraft.util.math.*;

public enum Animation
{
    LINEAR {
        @Override
        public float run(final float time, final float initial, final float changed, final float duration) {
            return (time >= duration) ? (initial + changed) : (changed * time / duration + initial);
        }
    }, 
    EASE_IN {
        @Override
        public float run(final float time, final float initial, final float changed, final float duration) {
            return (time >= duration) ? (initial + changed) : (changed * (float)Math.pow(time / duration, 2.0) + initial);
        }
    }, 
    EASE_OUT {
        @Override
        public float run(final float time, final float initial, final float changed, final float duration) {
            return (time >= duration) ? (initial + changed) : (-changed * (float)Math.pow(time / duration - 1.0f, 2.0) + initial);
        }
    }, 
    EXPONENTIAL {
        @Override
        public float run(final float time, final float initial, final float changed, final float duration) {
            return (time >= duration) ? (initial + changed) : (changed * ((float)(-Math.pow(2.0, -10.0f * time / duration)) + 1.0f) + initial);
        }
    }, 
    ELASTIC {
        @Override
        public float run(float time, final float initial, final float changed, final float duration) {
            if (time == 0.0f) {
                return initial;
            }
            if ((time /= duration / 2.0f) == 2.0f) {
                return initial + changed;
            }
            float a = 1.0f;
            float s;
            if (a < Math.abs(changed)) {
                a = changed;
                s = 0.25f;
            }
            else {
                s = 0.15915494f * (float)Math.asin(changed / a);
            }
            if (time < 1.0f) {
                return -0.5f * (a * (float)Math.pow(2.0, 10.0f * --time) * MathHelper.sin((float)((time * duration - s) * 6.283185307179586))) + initial;
            }
            return a * (float)Math.pow(2.0, -10.0f * --time) * MathHelper.sin((float)((time * duration - s) * 6.283185307179586)) * 0.5f + changed + initial;
        }
    }, 
    BOUNCE {
        @Override
        public float run(float time, final float initial, final float changed, final float duration) {
            final float s = 1.70158f;
            if (time > duration) {
                return initial + changed;
            }
            return changed * ((time = time / duration - 1.0f) * time * (2.70158f * time + 1.70158f) + 1.0f) + initial;
        }
    }, 
    INOUT {
        @Override
        public float run(final float time, final float initial, final float changed, final float duration) {
            final double amt = time / duration;
            return (time >= duration) ? (initial + changed) : ((float)(changed * ((amt < 0.5) ? (8.0 * amt * amt * amt * amt) : (1.0 - Math.pow(-2.0 * amt + 2.0, 4.0) / 2.0)) + initial));
        }
    }, 
    DOUBLEBOUNCE {
        @Override
        public float run(final float time, final float initial, final float changed, final float duration) {
            return (time >= duration) ? (initial + changed) : (changed * this.getAMT(time / duration) + initial);
        }
        
        float getAMT(float x) {
            final float n1 = 7.5625f;
            final float d1 = 2.75f;
            if (x < 0.36363637f) {
                return 7.5625f * x * x;
            }
            if (x < 0.72727275f) {
                return (float)(7.5625f * (x -= 0.54545456f) * x + 0.75);
            }
            if (x < 0.9090909090909091) {
                return (float)(7.5625f * (x -= 0.8181818f) * x + 0.9375);
            }
            return (float)(7.5625f * (x -= 0.95454544f) * x + 0.984375);
        }
    }, 
    INOUTSINE {
        @Override
        public float run(final float time, final float initial, final float changed, final float duration) {
            return -changed * MathHelper.cos((float)(time / duration * 1.5707963267948966)) + changed + initial;
        }
    };
    
    public static String[] getAnimationsString() {
        final String[] strings = new String[values().length];
        int i = 0;
        for (final Animation a : values()) {
            strings[i] = a.name();
            ++i;
        }
        return strings;
    }
    
    public abstract float run(final float p0, final float p1, final float p2, final float p3);
}
