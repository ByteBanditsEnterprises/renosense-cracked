//Raddon On Top!

package com.sun.jna.platform.win32.COM;

public interface IPersistStream extends IPersist
{
    boolean IsDirty();
    
    void Load(final IStream p0);
    
    void Save(final IStream p0);
    
    void GetSizeMax();
}
