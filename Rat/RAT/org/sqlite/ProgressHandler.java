//Raddon On Top!

package org.sqlite;

import java.sql.*;

public abstract class ProgressHandler
{
    public static final void setHandler(final Connection conn, final int vmCalls, final ProgressHandler progressHandler) throws SQLException {
        if (!(conn instanceof SQLiteConnection)) {
            throw new SQLException("connection must be to an SQLite db");
        }
        if (conn.isClosed()) {
            throw new SQLException("connection closed");
        }
        final SQLiteConnection sqliteConnection = (SQLiteConnection)conn;
        sqliteConnection.getDatabase().register_progress_handler(vmCalls, progressHandler);
    }
    
    public static final void clearHandler(final Connection conn) throws SQLException {
        final SQLiteConnection sqliteConnection = (SQLiteConnection)conn;
        sqliteConnection.getDatabase().clear_progress_handler();
    }
    
    protected abstract int progress() throws SQLException;
}
