//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\xeonl\OneDrive\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package me.sjnez.renosense.util;

import java.nio.*;
import javax.imageio.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;

public class IconUtil
{
    public static final IconUtil INSTANCE;
    
    public ByteBuffer readImageToBuffer(final InputStream inputStream) throws IOException {
        final BufferedImage bufferedimage = ImageIO.read(inputStream);
        final int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        final ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        Arrays.stream(aint).map(i -> i << 8 | (i >> 24 & 0xFF)).forEach(bytebuffer::putInt);
        bytebuffer.flip();
        return bytebuffer;
    }
    
    static {
        INSTANCE = new IconUtil();
    }
}
