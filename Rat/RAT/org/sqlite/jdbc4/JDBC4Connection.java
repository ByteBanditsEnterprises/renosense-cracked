//Raddon On Top!

package org.sqlite.jdbc4;

import org.sqlite.jdbc3.*;
import java.util.*;
import org.sqlite.*;
import java.sql.*;

public class JDBC4Connection extends JDBC3Connection
{
    public JDBC4Connection(final String url, final String fileName, final Properties prop) throws SQLException {
        super(url, fileName, prop);
    }
    
    public Statement createStatement(final int rst, final int rsc, final int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4Statement((SQLiteConnection)this);
    }
    
    public PreparedStatement prepareStatement(final String sql, final int rst, final int rsc, final int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4PreparedStatement((SQLiteConnection)this, sql);
    }
    
    public boolean isClosed() throws SQLException {
        return super.isClosed();
    }
    
    public <T> T unwrap(final Class<T> iface) throws ClassCastException {
        return iface.cast(this);
    }
    
    public boolean isWrapperFor(final Class<?> iface) {
        return iface.isInstance(this);
    }
    
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public boolean isValid(final int timeout) throws SQLException {
        if (this.isClosed()) {
            return false;
        }
        final Statement statement = this.createStatement();
        try {
            return statement.execute("select 1");
        }
        finally {
            statement.close();
        }
    }
    
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
    }
    
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
    }
    
    public String getClientInfo(final String name) throws SQLException {
        return null;
    }
    
    public Properties getClientInfo() throws SQLException {
        return null;
    }
    
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return null;
    }
}
