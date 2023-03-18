//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.util.*;

public interface GDI32 extends StdCallLibrary
{
    public static final GDI32 INSTANCE = (GDI32)Native.load("gdi32", (Class)GDI32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    public static final int SRCCOPY = 13369376;
    
    WinDef.HRGN ExtCreateRegion(final Pointer p0, final int p1, final WinGDI.RGNDATA p2);
    
    int CombineRgn(final WinDef.HRGN p0, final WinDef.HRGN p1, final WinDef.HRGN p2, final int p3);
    
    WinDef.HRGN CreateRectRgn(final int p0, final int p1, final int p2, final int p3);
    
    WinDef.HRGN CreateRoundRectRgn(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    WinDef.HRGN CreatePolyPolygonRgn(final WinDef.POINT[] p0, final int[] p1, final int p2, final int p3);
    
    boolean SetRectRgn(final WinDef.HRGN p0, final int p1, final int p2, final int p3, final int p4);
    
    int SetPixel(final WinDef.HDC p0, final int p1, final int p2, final int p3);
    
    WinDef.HDC CreateCompatibleDC(final WinDef.HDC p0);
    
    boolean DeleteDC(final WinDef.HDC p0);
    
    WinDef.HBITMAP CreateDIBitmap(final WinDef.HDC p0, final WinGDI.BITMAPINFOHEADER p1, final int p2, final Pointer p3, final WinGDI.BITMAPINFO p4, final int p5);
    
    WinDef.HBITMAP CreateDIBSection(final WinDef.HDC p0, final WinGDI.BITMAPINFO p1, final int p2, final PointerByReference p3, final Pointer p4, final int p5);
    
    WinDef.HBITMAP CreateCompatibleBitmap(final WinDef.HDC p0, final int p1, final int p2);
    
    WinNT.HANDLE SelectObject(final WinDef.HDC p0, final WinNT.HANDLE p1);
    
    boolean DeleteObject(final WinNT.HANDLE p0);
    
    int GetDeviceCaps(final WinDef.HDC p0, final int p1);
    
    int GetDIBits(final WinDef.HDC p0, final WinDef.HBITMAP p1, final int p2, final int p3, final Pointer p4, final WinGDI.BITMAPINFO p5, final int p6);
    
    int ChoosePixelFormat(final WinDef.HDC p0, final WinGDI.PIXELFORMATDESCRIPTOR.ByReference p1);
    
    boolean SetPixelFormat(final WinDef.HDC p0, final int p1, final WinGDI.PIXELFORMATDESCRIPTOR.ByReference p2);
    
    int GetObject(final WinNT.HANDLE p0, final int p1, final Pointer p2);
    
    boolean BitBlt(final WinDef.HDC p0, final int p1, final int p2, final int p3, final int p4, final WinDef.HDC p5, final int p6, final int p7, final int p8);
}
