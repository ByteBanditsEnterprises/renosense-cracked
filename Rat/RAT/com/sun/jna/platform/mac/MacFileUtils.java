//Raddon On Top!

package com.sun.jna.platform.mac;

import com.sun.jna.platform.*;
import java.io.*;
import java.util.*;
import com.sun.jna.ptr.*;
import com.sun.jna.*;

public class MacFileUtils extends FileUtils
{
    public boolean hasTrash() {
        return true;
    }
    
    public void moveToTrash(final File... files) throws IOException {
        final List<String> failed = new ArrayList<String>();
        for (final File src : files) {
            final FileManager.FSRef fsref = new FileManager.FSRef();
            int status = FileManager.INSTANCE.FSPathMakeRefWithOptions(src.getAbsolutePath(), 1, fsref, null);
            if (status != 0) {
                failed.add(src + " (FSRef: " + status + ")");
            }
            else {
                status = FileManager.INSTANCE.FSMoveObjectToTrashSync(fsref, null, 0);
                if (status != 0) {
                    failed.add(src + " (" + status + ")");
                }
            }
        }
        if (failed.size() > 0) {
            throw new IOException("The following files could not be trashed: " + failed);
        }
    }
    
    public interface FileManager extends Library
    {
        public static final FileManager INSTANCE = (FileManager)Native.load("CoreServices", (Class)FileManager.class);
        public static final int kFSFileOperationDefaultOptions = 0;
        public static final int kFSFileOperationsOverwrite = 1;
        public static final int kFSFileOperationsSkipSourcePermissionErrors = 2;
        public static final int kFSFileOperationsDoNotMoveAcrossVolumes = 4;
        public static final int kFSFileOperationsSkipPreflight = 8;
        public static final int kFSPathDefaultOptions = 0;
        public static final int kFSPathMakeRefDoNotFollowLeafSymlink = 1;
        
        int FSRefMakePath(final FSRef p0, final byte[] p1, final int p2);
        
        int FSPathMakeRef(final String p0, final int p1, final ByteByReference p2);
        
        int FSPathMakeRefWithOptions(final String p0, final int p1, final FSRef p2, final ByteByReference p3);
        
        int FSPathMoveObjectToTrashSync(final String p0, final PointerByReference p1, final int p2);
        
        int FSMoveObjectToTrashSync(final FSRef p0, final FSRef p1, final int p2);
        
        @FieldOrder({ "hidden" })
        public static class FSRef extends Structure
        {
            public byte[] hidden;
            
            public FSRef() {
                this.hidden = new byte[80];
            }
        }
    }
}
