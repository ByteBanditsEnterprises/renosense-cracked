//Raddon On Top!

package org.sqlite.jdbc4;

import org.sqlite.jdbc3.*;
import java.util.*;
import org.sqlite.*;
import java.io.*;
import java.sql.*;

public class JDBC4PreparedStatement extends JDBC3PreparedStatement implements PreparedStatement, ParameterMetaData
{
    public String toString() {
        return this.sql + " \n parameters=" + Arrays.toString(this.batch);
    }
    
    public JDBC4PreparedStatement(final SQLiteConnection conn, final String sql) throws SQLException {
        super(conn, sql);
    }
    
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setNString(final int parameterIndex, final String value) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
