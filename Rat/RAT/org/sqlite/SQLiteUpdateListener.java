//Raddon On Top!

package org.sqlite;

public interface SQLiteUpdateListener
{
    void onUpdate(final Type p0, final String p1, final String p2, final long p3);
    
    public enum Type
    {
        INSERT, 
        DELETE, 
        UPDATE;
    }
}
