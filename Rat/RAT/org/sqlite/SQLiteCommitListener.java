//Raddon On Top!

package org.sqlite;

public interface SQLiteCommitListener
{
    void onCommit();
    
    void onRollback();
}
