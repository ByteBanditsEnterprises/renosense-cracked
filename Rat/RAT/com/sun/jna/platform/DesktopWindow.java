//Raddon On Top!

package com.sun.jna.platform;

import com.sun.jna.platform.win32.*;
import java.awt.*;

public class DesktopWindow
{
    private WinDef.HWND hwnd;
    private String title;
    private String filePath;
    private Rectangle locAndSize;
    
    public DesktopWindow(final WinDef.HWND hwnd, final String title, final String filePath, final Rectangle locAndSize) {
        this.hwnd = hwnd;
        this.title = title;
        this.filePath = filePath;
        this.locAndSize = locAndSize;
    }
    
    public WinDef.HWND getHWND() {
        return this.hwnd;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    public Rectangle getLocAndSize() {
        return this.locAndSize;
    }
}
