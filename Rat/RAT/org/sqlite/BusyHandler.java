//Raddon On Top!

package org.sqlite;

import java.sql.*;

public abstract class BusyHandler
{
    private static void commitHandler(final Connection conn, final BusyHandler busyHandler) throws SQLException {
        if (!(conn instanceof SQLiteConnection)) {
            throw new SQLException("connection must be to an SQLite db");
        }
        if (conn.isClosed()) {
            throw new SQLException("connection closed");
        }
        final SQLiteConnection sqliteConnection = (SQLiteConnection)conn;
        sqliteConnection.getDatabase().busy_handler(busyHandler);
    }
    
    public static final void setHandler(final Connection conn, final BusyHandler busyHandler) throws SQLException {
        commitHandler(conn, busyHandler);
    }
    
    public static final void clearHandler(final Connection conn) throws SQLException {
        commitHandler(conn, null);
    }
    
    protected abstract int callback(final int p0) throws SQLException;
}
