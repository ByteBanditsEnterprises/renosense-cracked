//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.platform.*;
import java.io.*;

public class W32FileUtils extends FileUtils
{
    public boolean hasTrash() {
        return true;
    }
    
    public void moveToTrash(final File... files) throws IOException {
        final Shell32 shell = Shell32.INSTANCE;
        final ShellAPI.SHFILEOPSTRUCT fileop = new ShellAPI.SHFILEOPSTRUCT();
        fileop.wFunc = 3;
        final String[] paths = new String[files.length];
        for (int i = 0; i < paths.length; ++i) {
            paths[i] = files[i].getAbsolutePath();
        }
        fileop.pFrom = fileop.encodePaths(paths);
        fileop.fFlags = 1620;
        final int ret = shell.SHFileOperation(fileop);
        if (ret != 0) {
            throw new IOException("Move to trash failed: " + fileop.pFrom + ": " + Kernel32Util.formatMessageFromLastErrorCode(ret));
        }
        if (fileop.fAnyOperationsAborted) {
            throw new IOException("Move to trash aborted");
        }
    }
}
