//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.win32.*;
import com.sun.jna.*;

public interface OpenGL32 extends StdCallLibrary
{
    public static final OpenGL32 INSTANCE = (OpenGL32)Native.load("opengl32", (Class)OpenGL32.class);
    
    String glGetString(final int p0);
    
    WinDef.HGLRC wglCreateContext(final WinDef.HDC p0);
    
    WinDef.HGLRC wglGetCurrentContext();
    
    boolean wglMakeCurrent(final WinDef.HDC p0, final WinDef.HGLRC p1);
    
    boolean wglDeleteContext(final WinDef.HGLRC p0);
    
    Pointer wglGetProcAddress(final String p0);
}
