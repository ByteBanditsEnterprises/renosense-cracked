//Raddon On Top!

package org.sqlite.jdbc4;

import org.sqlite.jdbc3.*;
import org.sqlite.core.*;
import java.math.*;
import java.util.*;
import java.sql.*;
import java.net.*;
import java.io.*;

public class JDBC4ResultSet extends JDBC3ResultSet implements ResultSet, ResultSetMetaData
{
    public JDBC4ResultSet(final CoreStatement stmt) {
        super(stmt);
    }
    
    public void close() throws SQLException {
        final boolean wasOpen = this.isOpen();
        super.close();
        if (wasOpen && this.stmt instanceof JDBC4Statement) {
            final JDBC4Statement stat = (JDBC4Statement)this.stmt;
            if (stat.closeOnCompletion && !stat.isClosed()) {
                stat.close();
            }
        }
    }
    
    public <T> T unwrap(final Class<T> iface) throws ClassCastException {
        return iface.cast(this);
    }
    
    public boolean isWrapperFor(final Class<?> iface) {
        return iface.isInstance(this);
    }
    
    public RowId getRowId(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public RowId getRowId(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public int getHoldability() throws SQLException {
        return 0;
    }
    
    public boolean isClosed() throws SQLException {
        return !this.isOpen();
    }
    
    public void updateNString(final int columnIndex, final String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNString(final String columnLabel, final String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public NClob getNClob(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public NClob getNClob(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public String getNString(final int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public String getNString(final String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public Reader getNCharacterStream(final int col) throws SQLException {
        final String data = this.getString(col);
        return this.getNCharacterStreamInternal(data);
    }
    
    private Reader getNCharacterStreamInternal(final String data) {
        if (data == null) {
            return null;
        }
        final Reader reader = new StringReader(data);
        return reader;
    }
    
    public Reader getNCharacterStream(final String col) throws SQLException {
        final String data = this.getString(col);
        return this.getNCharacterStreamInternal(data);
    }
    
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        if (type == null) {
            throw new SQLException("requested type cannot be null");
        }
        if (type == String.class) {
            return type.cast(this.getString(columnIndex));
        }
        if (type == Boolean.class) {
            return type.cast(this.getBoolean(columnIndex));
        }
        if (type == BigDecimal.class) {
            return type.cast(this.getBigDecimal(columnIndex));
        }
        if (type == byte[].class) {
            return type.cast(this.getBytes(columnIndex));
        }
        if (type == Date.class) {
            return type.cast(this.getDate(columnIndex));
        }
        if (type == Time.class) {
            return type.cast(this.getTime(columnIndex));
        }
        if (type == Timestamp.class) {
            return type.cast(this.getTimestamp(columnIndex));
        }
        final int columnType = this.safeGetColumnType(this.markCol(columnIndex));
        if (type == Double.class) {
            if (columnType == 1 || columnType == 2) {
                return type.cast(this.getDouble(columnIndex));
            }
            throw new SQLException("Bad value for type Double");
        }
        else if (type == Long.class) {
            if (columnType == 1 || columnType == 2) {
                return type.cast(this.getLong(columnIndex));
            }
            throw new SQLException("Bad value for type Long");
        }
        else if (type == Float.class) {
            if (columnType == 1 || columnType == 2) {
                return type.cast(this.getFloat(columnIndex));
            }
            throw new SQLException("Bad value for type Float");
        }
        else {
            if (type != Integer.class) {
                throw this.unsupported();
            }
            if (columnType == 1 || columnType == 2) {
                return type.cast(this.getInt(columnIndex));
            }
            throw new SQLException("Bad value for type Integer");
        }
    }
    
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        return this.getObject(this.findColumn(columnLabel), type);
    }
    
    protected SQLException unsupported() {
        return new SQLFeatureNotSupportedException("not implemented by SQLite JDBC driver");
    }
    
    public Array getArray(final int i) throws SQLException {
        throw this.unsupported();
    }
    
    public Array getArray(final String col) throws SQLException {
        throw this.unsupported();
    }
    
    public InputStream getAsciiStream(final int col) throws SQLException {
        final String data = this.getString(col);
        return this.getAsciiStreamInternal(data);
    }
    
    public InputStream getAsciiStream(final String col) throws SQLException {
        final String data = this.getString(col);
        return this.getAsciiStreamInternal(data);
    }
    
    private InputStream getAsciiStreamInternal(final String data) {
        if (data == null) {
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = new ByteArrayInputStream(data.getBytes("ASCII"));
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return inputStream;
    }
    
    @Deprecated
    public BigDecimal getBigDecimal(final int col, final int s) throws SQLException {
        throw this.unsupported();
    }
    
    @Deprecated
    public BigDecimal getBigDecimal(final String col, final int s) throws SQLException {
        throw this.unsupported();
    }
    
    public Blob getBlob(final int col) throws SQLException {
        throw this.unsupported();
    }
    
    public Blob getBlob(final String col) throws SQLException {
        throw this.unsupported();
    }
    
    public Clob getClob(final int col) throws SQLException {
        final String clob = this.getString(col);
        return (clob == null) ? null : new SqliteClob(clob);
    }
    
    public Clob getClob(final String col) throws SQLException {
        final String clob = this.getString(col);
        return (clob == null) ? null : new SqliteClob(clob);
    }
    
    public Object getObject(final int col, final Map map) throws SQLException {
        throw this.unsupported();
    }
    
    public Object getObject(final String col, final Map map) throws SQLException {
        throw this.unsupported();
    }
    
    public Ref getRef(final int i) throws SQLException {
        throw this.unsupported();
    }
    
    public Ref getRef(final String col) throws SQLException {
        throw this.unsupported();
    }
    
    public InputStream getUnicodeStream(final int col) throws SQLException {
        return this.getAsciiStream(col);
    }
    
    public InputStream getUnicodeStream(final String col) throws SQLException {
        return this.getAsciiStream(col);
    }
    
    public URL getURL(final int col) throws SQLException {
        throw this.unsupported();
    }
    
    public URL getURL(final String col) throws SQLException {
        throw this.unsupported();
    }
    
    public void insertRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public void moveToInsertRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public boolean last() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public boolean previous() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public boolean relative(final int rows) throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public boolean absolute(final int row) throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public void afterLast() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public void beforeFirst() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public boolean first() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    public void cancelRowUpdates() throws SQLException {
        throw this.unsupported();
    }
    
    public void deleteRow() throws SQLException {
        throw this.unsupported();
    }
    
    public void updateArray(final int col, final Array x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateArray(final String col, final Array x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateAsciiStream(final int col, final InputStream x, final int l) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateAsciiStream(final String col, final InputStream x, final int l) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBigDecimal(final int col, final BigDecimal x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBigDecimal(final String col, final BigDecimal x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBinaryStream(final int c, final InputStream x, final int l) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBinaryStream(final String c, final InputStream x, final int l) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBlob(final int col, final Blob x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBlob(final String col, final Blob x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBoolean(final int col, final boolean x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBoolean(final String col, final boolean x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateByte(final int col, final byte x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateByte(final String col, final byte x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBytes(final int col, final byte[] x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateBytes(final String col, final byte[] x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateCharacterStream(final int c, final Reader x, final int l) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateCharacterStream(final String c, final Reader r, final int l) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateClob(final int col, final Clob x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateClob(final String col, final Clob x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateDate(final int col, final Date x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateDate(final String col, final Date x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateDouble(final int col, final double x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateDouble(final String col, final double x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateFloat(final int col, final float x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateFloat(final String col, final float x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateInt(final int col, final int x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateInt(final String col, final int x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateLong(final int col, final long x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateLong(final String col, final long x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateNull(final int col) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateNull(final String col) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateObject(final int c, final Object x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateObject(final int c, final Object x, final int s) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateObject(final String col, final Object x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateObject(final String c, final Object x, final int s) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateRef(final int col, final Ref x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateRef(final String c, final Ref x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateRow() throws SQLException {
        throw this.unsupported();
    }
    
    public void updateShort(final int c, final short x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateShort(final String c, final short x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateString(final int c, final String x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateString(final String c, final String x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateTime(final int c, final Time x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateTime(final String c, final Time x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateTimestamp(final int c, final Timestamp x) throws SQLException {
        throw this.unsupported();
    }
    
    public void updateTimestamp(final String c, final Timestamp x) throws SQLException {
        throw this.unsupported();
    }
    
    public void refreshRow() throws SQLException {
        throw this.unsupported();
    }
    
    class SqliteClob implements NClob
    {
        private String data;
        
        protected SqliteClob(final String data) {
            this.data = data;
        }
        
        @Override
        public void free() throws SQLException {
            this.data = null;
        }
        
        @Override
        public InputStream getAsciiStream() throws SQLException {
            return JDBC4ResultSet.this.getAsciiStreamInternal(this.data);
        }
        
        @Override
        public Reader getCharacterStream() throws SQLException {
            return JDBC4ResultSet.this.getNCharacterStreamInternal(this.data);
        }
        
        @Override
        public Reader getCharacterStream(final long arg0, final long arg1) throws SQLException {
            return JDBC4ResultSet.this.getNCharacterStreamInternal(this.data);
        }
        
        @Override
        public String getSubString(final long position, final int length) throws SQLException {
            if (this.data == null) {
                throw new SQLException("no data");
            }
            if (position < 1L) {
                throw new SQLException("Position must be greater than or equal to 1");
            }
            if (length < 0) {
                throw new SQLException("Length must be greater than or equal to 0");
            }
            final int start = (int)position - 1;
            return this.data.substring(start, Math.min(start + length, this.data.length()));
        }
        
        @Override
        public long length() throws SQLException {
            if (this.data == null) {
                throw new SQLException("no data");
            }
            return this.data.length();
        }
        
        @Override
        public long position(final String arg0, final long arg1) throws SQLException {
            JDBC4ResultSet.this.unsupported();
            return -1L;
        }
        
        @Override
        public long position(final Clob arg0, final long arg1) throws SQLException {
            JDBC4ResultSet.this.unsupported();
            return -1L;
        }
        
        @Override
        public OutputStream setAsciiStream(final long arg0) throws SQLException {
            JDBC4ResultSet.this.unsupported();
            return null;
        }
        
        @Override
        public Writer setCharacterStream(final long arg0) throws SQLException {
            JDBC4ResultSet.this.unsupported();
            return null;
        }
        
        @Override
        public int setString(final long arg0, final String arg1) throws SQLException {
            JDBC4ResultSet.this.unsupported();
            return -1;
        }
        
        @Override
        public int setString(final long arg0, final String arg1, final int arg2, final int arg3) throws SQLException {
            JDBC4ResultSet.this.unsupported();
            return -1;
        }
        
        @Override
        public void truncate(final long arg0) throws SQLException {
            JDBC4ResultSet.this.unsupported();
        }
    }
}
