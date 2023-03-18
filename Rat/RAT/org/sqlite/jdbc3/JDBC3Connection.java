//Raddon On Top!

package org.sqlite.jdbc3;

import java.util.concurrent.atomic.*;
import java.util.*;
import org.sqlite.*;
import java.sql.*;

public abstract class JDBC3Connection extends SQLiteConnection
{
    private final AtomicInteger savePoint;
    private Map<String, Class<?>> typeMap;
    private boolean readOnly;
    
    protected JDBC3Connection(final String url, final String fileName, final Properties prop) throws SQLException {
        super(url, fileName, prop);
        this.savePoint = new AtomicInteger(0);
        this.readOnly = false;
    }
    
    public void tryEnforceTransactionMode() throws SQLException {
        if (this.getDatabase().getConfig().isExplicitReadOnly() && !this.getAutoCommit() && this.getCurrentTransactionMode() != null) {
            if (this.isReadOnly()) {
                this.getDatabase()._exec("PRAGMA query_only = true;");
            }
            else if (this.getCurrentTransactionMode() == SQLiteConfig.TransactionMode.DEFERRED || this.getCurrentTransactionMode() == SQLiteConfig.TransactionMode.DEFFERED) {
                if (this.isFirstStatementExecuted()) {
                    throw new SQLException("A statement has already been executed on this connection; cannot upgrade to write transaction");
                }
                this.getDatabase()._exec("commit; /* need to explicitly upgrade transaction */");
                this.getDatabase()._exec("PRAGMA query_only = false;");
                this.getDatabase()._exec("BEGIN IMMEDIATE; /* explicitly upgrade transaction */");
                this.setCurrentTransactionMode(SQLiteConfig.TransactionMode.IMMEDIATE);
            }
        }
    }
    
    @Override
    public String getCatalog() throws SQLException {
        this.checkOpen();
        return null;
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.checkOpen();
    }
    
    @Override
    public int getHoldability() throws SQLException {
        this.checkOpen();
        return 2;
    }
    
    @Override
    public void setHoldability(final int h) throws SQLException {
        this.checkOpen();
        if (h != 2) {
            throw new SQLException("SQLite only supports CLOSE_CURSORS_AT_COMMIT");
        }
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        synchronized (this) {
            if (this.typeMap == null) {
                this.typeMap = new HashMap<String, Class<?>>();
            }
            return this.typeMap;
        }
    }
    
    @Override
    public void setTypeMap(final Map map) throws SQLException {
        synchronized (this) {
            this.typeMap = (Map<String, Class<?>>)map;
        }
    }
    
    @Override
    public boolean isReadOnly() {
        final SQLiteConfig config = this.getDatabase().getConfig();
        return (config.getOpenModeFlags() & SQLiteOpenMode.READONLY.flag) != 0x0 || (config.isExplicitReadOnly() && this.readOnly);
    }
    
    @Override
    public void setReadOnly(final boolean ro) throws SQLException {
        if (this.getDatabase().getConfig().isExplicitReadOnly()) {
            if (ro != this.readOnly && this.isFirstStatementExecuted()) {
                throw new SQLException("Cannot change Read-Only status of this connection: the first statement was already executed and the transaction is open.");
            }
        }
        else if (ro != this.isReadOnly()) {
            throw new SQLException("Cannot change read-only flag after establishing a connection. Use SQLiteConfig#setReadOnly and SQLiteConfig.createConnection().");
        }
        this.readOnly = ro;
    }
    
    @Override
    public String nativeSQL(final String sql) {
        return sql;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        return this.createStatement(1003, 1007, 2);
    }
    
    @Override
    public Statement createStatement(final int rsType, final int rsConcurr) throws SQLException {
        return this.createStatement(rsType, rsConcurr, 2);
    }
    
    @Override
    public abstract Statement createStatement(final int p0, final int p1, final int p2) throws SQLException;
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return this.prepareCall(sql, 1003, 1007, 2);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int rst, final int rsc) throws SQLException {
        return this.prepareCall(sql, rst, rsc, 2);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int rst, final int rsc, final int rsh) throws SQLException {
        throw new SQLException("SQLite does not support Stored Procedures");
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return this.prepareStatement(sql, 1003, 1007);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoC) throws SQLException {
        return this.prepareStatement(sql);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] colInds) throws SQLException {
        return this.prepareStatement(sql);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] colNames) throws SQLException {
        return this.prepareStatement(sql);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int rst, final int rsc) throws SQLException {
        return this.prepareStatement(sql, rst, rsc, 2);
    }
    
    @Override
    public abstract PreparedStatement prepareStatement(final String p0, final int p1, final int p2, final int p3) throws SQLException;
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.checkOpen();
        if (this.getAutoCommit()) {
            this.getConnectionConfig().setAutoCommit(false);
        }
        final Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet());
        this.getDatabase().exec(String.format("SAVEPOINT %s", sp.getSavepointName()), false);
        return sp;
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        this.checkOpen();
        if (this.getAutoCommit()) {
            this.getConnectionConfig().setAutoCommit(false);
        }
        final Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet(), name);
        this.getDatabase().exec(String.format("SAVEPOINT %s", sp.getSavepointName()), false);
        return sp;
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        if (this.getAutoCommit()) {
            throw new SQLException("database in auto-commit mode");
        }
        this.getDatabase().exec(String.format("RELEASE SAVEPOINT %s", savepoint.getSavepointName()), false);
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        if (this.getAutoCommit()) {
            throw new SQLException("database in auto-commit mode");
        }
        this.getDatabase().exec(String.format("ROLLBACK TO SAVEPOINT %s", savepoint.getSavepointName()), this.getAutoCommit());
    }
    
    @Override
    public Struct createStruct(final String t, final Object[] attr) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented by SQLite JDBC driver");
    }
}
