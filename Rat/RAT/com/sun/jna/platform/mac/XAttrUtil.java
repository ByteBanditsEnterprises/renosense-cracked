//Raddon On Top!

package com.sun.jna.platform.mac;

import java.util.*;
import com.sun.jna.*;
import java.nio.charset.*;
import java.nio.*;

public class XAttrUtil
{
    public static List<String> listXAttr(final String path) {
        final long bufferLength = XAttr.INSTANCE.listxattr(path, (Pointer)null, 0L, 0);
        if (bufferLength < 0L) {
            return null;
        }
        if (bufferLength == 0L) {
            return new ArrayList<String>(0);
        }
        final Memory valueBuffer = new Memory(bufferLength);
        final long valueLength = XAttr.INSTANCE.listxattr(path, (Pointer)valueBuffer, bufferLength, 0);
        if (valueLength < 0L) {
            return null;
        }
        return decodeStringSequence(valueBuffer.getByteBuffer(0L, valueLength));
    }
    
    public static String getXAttr(final String path, final String name) {
        final long bufferLength = XAttr.INSTANCE.getxattr(path, name, (Pointer)null, 0L, 0, 0);
        if (bufferLength < 0L) {
            return null;
        }
        if (bufferLength == 0L) {
            return "";
        }
        final Memory valueBuffer = new Memory(bufferLength);
        valueBuffer.clear();
        final long valueLength = XAttr.INSTANCE.getxattr(path, name, (Pointer)valueBuffer, bufferLength, 0, 0);
        if (valueLength < 0L) {
            return null;
        }
        return Native.toString(valueBuffer.getByteArray(0L, (int)bufferLength), "UTF-8");
    }
    
    public static int setXAttr(final String path, final String name, final String value) {
        final Memory valueBuffer = encodeString(value);
        return XAttr.INSTANCE.setxattr(path, name, (Pointer)valueBuffer, valueBuffer.size(), 0, 0);
    }
    
    public static int removeXAttr(final String path, final String name) {
        return XAttr.INSTANCE.removexattr(path, name, 0);
    }
    
    protected static Memory encodeString(final String s) {
        final byte[] bb = s.getBytes(Charset.forName("UTF-8"));
        final Memory valueBuffer = new Memory((long)bb.length);
        valueBuffer.write(0L, bb, 0, bb.length);
        return valueBuffer;
    }
    
    protected static String decodeString(final ByteBuffer bb) {
        return Charset.forName("UTF-8").decode(bb).toString();
    }
    
    protected static List<String> decodeStringSequence(final ByteBuffer bb) {
        final List<String> names = new ArrayList<String>();
        bb.mark();
        while (bb.hasRemaining()) {
            if (bb.get() == 0) {
                final ByteBuffer nameBuffer = (ByteBuffer)bb.duplicate().limit(bb.position() - 1).reset();
                if (nameBuffer.hasRemaining()) {
                    names.add(decodeString(nameBuffer));
                }
                bb.mark();
            }
        }
        return names;
    }
}
