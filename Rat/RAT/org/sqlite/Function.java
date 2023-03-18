//Raddon On Top!

package org.sqlite;

import org.sqlite.core.*;
import java.sql.*;

public abstract class Function
{
    public static final int FLAG_DETERMINISTIC = 2048;
    private SQLiteConnection conn;
    private DB db;
    long context;
    long value;
    int args;
    
    public Function() {
        this.context = 0L;
        this.value = 0L;
        this.args = 0;
    }
    
    public static void create(final Connection conn, final String name, final Function f) throws SQLException {
        create(conn, name, f, 0);
    }
    
    public static void create(final Connection conn, final String name, final Function f, final int flags) throws SQLException {
        create(conn, name, f, -1, flags);
    }
    
    public static void create(final Connection conn, final String name, final Function f, final int nArgs, final int flags) throws SQLException {
        if (!(conn instanceof SQLiteConnection)) {
            throw new SQLException("connection must be to an SQLite db");
        }
        if (conn.isClosed()) {
            throw new SQLException("connection closed");
        }
        f.conn = (SQLiteConnection)conn;
        f.db = f.conn.getDatabase();
        if (nArgs < -1 || nArgs > 127) {
            throw new SQLException("invalid args provided: " + nArgs);
        }
        if (f.db.create_function(name, f, nArgs, flags) != 0) {
            throw new SQLException("error creating function");
        }
    }
    
    public static void destroy(final Connection conn, final String name, final int nArgs) throws SQLException {
        if (!(conn instanceof SQLiteConnection)) {
            throw new SQLException("connection must be to an SQLite db");
        }
        ((SQLiteConnection)conn).getDatabase().destroy_function(name);
    }
    
    public static void destroy(final Connection conn, final String name) throws SQLException {
        destroy(conn, name, -1);
    }
    
    protected abstract void xFunc() throws SQLException;
    
    protected final synchronized int args() throws SQLException {
        this.checkContext();
        return this.args;
    }
    
    protected final synchronized void result(final byte[] value) throws SQLException {
        this.checkContext();
        this.db.result_blob(this.context, value);
    }
    
    protected final synchronized void result(final double value) throws SQLException {
        this.checkContext();
        this.db.result_double(this.context, value);
    }
    
    protected final synchronized void result(final int value) throws SQLException {
        this.checkContext();
        this.db.result_int(this.context, value);
    }
    
    protected final synchronized void result(final long value) throws SQLException {
        this.checkContext();
        this.db.result_long(this.context, value);
    }
    
    protected final synchronized void result() throws SQLException {
        this.checkContext();
        this.db.result_null(this.context);
    }
    
    protected final synchronized void result(final String value) throws SQLException {
        this.checkContext();
        this.db.result_text(this.context, value);
    }
    
    protected final synchronized void error(final String err) throws SQLException {
        this.checkContext();
        this.db.result_error(this.context, err);
    }
    
    protected final synchronized String value_text(final int arg) throws SQLException {
        this.checkValue(arg);
        return this.db.value_text(this, arg);
    }
    
    protected final synchronized byte[] value_blob(final int arg) throws SQLException {
        this.checkValue(arg);
        return this.db.value_blob(this, arg);
    }
    
    protected final synchronized double value_double(final int arg) throws SQLException {
        this.checkValue(arg);
        return this.db.value_double(this, arg);
    }
    
    protected final synchronized int value_int(final int arg) throws SQLException {
        this.checkValue(arg);
        return this.db.value_int(this, arg);
    }
    
    protected final synchronized long value_long(final int arg) throws SQLException {
        this.checkValue(arg);
        return this.db.value_long(this, arg);
    }
    
    protected final synchronized int value_type(final int arg) throws SQLException {
        this.checkValue(arg);
        return this.db.value_type(this, arg);
    }
    
    private void checkContext() throws SQLException {
        if (this.conn == null || this.conn.getDatabase() == null || this.context == 0L) {
            throw new SQLException("no context, not allowed to read value");
        }
    }
    
    private void checkValue(final int arg) throws SQLException {
        if (this.conn == null || this.conn.getDatabase() == null || this.value == 0L) {
            throw new SQLException("not in value access state");
        }
        if (arg >= this.args) {
            throw new SQLException("arg " + arg + " out bounds [0," + this.args + ")");
        }
    }
    
    public abstract static class Aggregate extends Function implements Cloneable
    {
        @Override
        protected final void xFunc() {
        }
        
        protected abstract void xStep() throws SQLException;
        
        protected abstract void xFinal() throws SQLException;
        
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
    
    public abstract static class Window extends Aggregate
    {
        protected abstract void xInverse() throws SQLException;
        
        protected abstract void xValue() throws SQLException;
    }
}
