//Raddon On Top!

package org.sqlite;

import org.sqlite.core.*;
import java.sql.*;

public abstract class Collation
{
    private SQLiteConnection conn;
    private DB db;
    
    public static final void create(final Connection conn, final String name, final Collation f) throws SQLException {
        if (conn == null || !(conn instanceof SQLiteConnection)) {
            throw new SQLException("connection must be to an SQLite db");
        }
        if (conn.isClosed()) {
            throw new SQLException("connection closed");
        }
        f.conn = (SQLiteConnection)conn;
        f.db = f.conn.getDatabase();
        if (f.db.create_collation(name, f) != 0) {
            throw new SQLException("error creating collation");
        }
    }
    
    public static final void destroy(final Connection conn, final String name) throws SQLException {
        if (conn == null || !(conn instanceof SQLiteConnection)) {
            throw new SQLException("connection must be to an SQLite db");
        }
        ((SQLiteConnection)conn).getDatabase().destroy_collation(name);
    }
    
    protected abstract int xCompare(final String p0, final String p1);
}
