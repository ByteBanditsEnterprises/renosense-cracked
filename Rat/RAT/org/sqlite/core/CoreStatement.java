//Raddon On Top!

package org.sqlite.core;

import org.sqlite.jdbc4.*;
import org.sqlite.*;
import org.sqlite.jdbc3.*;
import java.sql.*;

public abstract class CoreStatement implements Codes
{
    public final SQLiteConnection conn;
    protected final CoreResultSet rs;
    public SafeStmtPtr pointer;
    protected String sql;
    protected int batchPos;
    protected Object[] batch;
    protected boolean resultsWaiting;
    
    protected CoreStatement(final SQLiteConnection c) {
        this.sql = null;
        this.batch = null;
        this.resultsWaiting = false;
        this.conn = c;
        this.rs = new JDBC4ResultSet(this);
    }
    
    public DB getDatabase() {
        return this.conn.getDatabase();
    }
    
    public SQLiteConnectionConfig getConnectionConfig() {
        return this.conn.getConnectionConfig();
    }
    
    protected final void checkOpen() throws SQLException {
        if (this.pointer.isClosed()) {
            throw new SQLException("statement is not executing");
        }
    }
    
    boolean isOpen() throws SQLException {
        return !this.pointer.isClosed();
    }
    
    protected boolean exec() throws SQLException {
        if (this.sql == null) {
            throw new SQLException("SQLiteJDBC internal error: sql==null");
        }
        if (this.rs.isOpen()) {
            throw new SQLException("SQLite JDBC internal error: rs.isOpen() on exec.");
        }
        if (this.conn instanceof JDBC3Connection) {
            ((JDBC3Connection)this.conn).tryEnforceTransactionMode();
        }
        boolean success = false;
        boolean rc = false;
        try {
            rc = this.conn.getDatabase().execute(this, null);
            success = true;
        }
        finally {
            this.notifyFirstStatementExecuted();
            this.resultsWaiting = rc;
            if (!success) {
                this.pointer.close();
            }
        }
        return this.pointer.safeRunInt(DB::column_count) != 0;
    }
    
    protected boolean exec(final String sql) throws SQLException {
        if (sql == null) {
            throw new SQLException("SQLiteJDBC internal error: sql==null");
        }
        if (this.rs.isOpen()) {
            throw new SQLException("SQLite JDBC internal error: rs.isOpen() on exec.");
        }
        if (this.conn instanceof JDBC3Connection) {
            ((JDBC3Connection)this.conn).tryEnforceTransactionMode();
        }
        boolean rc = false;
        boolean success = false;
        try {
            rc = this.conn.getDatabase().execute(sql, this.conn.getAutoCommit());
            success = true;
        }
        finally {
            this.notifyFirstStatementExecuted();
            this.resultsWaiting = rc;
            if (!success && this.pointer != null) {
                this.pointer.close();
            }
        }
        return this.pointer.safeRunInt(DB::column_count) != 0;
    }
    
    protected void internalClose() throws SQLException {
        if (this.pointer != null && !this.pointer.isClosed()) {
            if (this.conn.isClosed()) {
                throw DB.newSQLException(1, "Connection is closed");
            }
            this.rs.close();
            this.batch = null;
            this.batchPos = 0;
            final int resp = this.pointer.close();
            if (resp != 0 && resp != 21) {
                this.conn.getDatabase().throwex(resp);
            }
        }
    }
    
    protected void notifyFirstStatementExecuted() {
        this.conn.setFirstStatementExecuted(true);
    }
    
    public abstract ResultSet executeQuery(final String p0, final boolean p1) throws SQLException;
}
