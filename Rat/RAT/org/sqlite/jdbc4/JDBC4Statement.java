//Raddon On Top!

package org.sqlite.jdbc4;

import org.sqlite.jdbc3.*;
import org.sqlite.*;
import java.sql.*;

public class JDBC4Statement extends JDBC3Statement implements Statement
{
    private boolean closed;
    boolean closeOnCompletion;
    
    public JDBC4Statement(final SQLiteConnection conn) {
        super(conn);
        this.closed = false;
    }
    
    public <T> T unwrap(final Class<T> iface) throws ClassCastException {
        return iface.cast(this);
    }
    
    public boolean isWrapperFor(final Class<?> iface) {
        return iface.isInstance(this);
    }
    
    public void close() throws SQLException {
        super.close();
        this.closed = true;
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public void closeOnCompletion() throws SQLException {
        if (this.closed) {
            throw new SQLException("statement is closed");
        }
        this.closeOnCompletion = true;
    }
    
    public boolean isCloseOnCompletion() throws SQLException {
        if (this.closed) {
            throw new SQLException("statement is closed");
        }
        return this.closeOnCompletion;
    }
    
    public void setPoolable(final boolean poolable) throws SQLException {
    }
    
    public boolean isPoolable() throws SQLException {
        return false;
    }
}
