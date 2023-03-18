//Raddon On Top!

package org.sqlite.jdbc3;

import org.sqlite.*;
import org.sqlite.core.*;
import java.math.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public abstract class JDBC3PreparedStatement extends CorePreparedStatement
{
    protected JDBC3PreparedStatement(final SQLiteConnection conn, final String sql) throws SQLException {
        super(conn, sql);
    }
    
    public void clearParameters() throws SQLException {
        this.checkOpen();
        this.pointer.safeRunConsume(DB::clear_bindings);
        if (this.batch != null) {
            for (int i = this.batchPos; i < this.batchPos + this.paramCount; ++i) {
                this.batch[i] = null;
            }
        }
    }
    
    public boolean execute() throws SQLException {
        this.checkOpen();
        this.rs.close();
        this.pointer.safeRunConsume(DB::reset);
        if (this.conn instanceof JDBC3Connection) {
            ((JDBC3Connection)this.conn).tryEnforceTransactionMode();
        }
        boolean success;
        return (boolean)this.withConnectionTimeout(() -> {
            success = false;
            try {
                this.resultsWaiting = this.conn.getDatabase().execute((CoreStatement)this, this.batch);
                success = true;
                this.updateCount = this.getDatabase().changes();
                return 0 != this.columnCount;
            }
            finally {
                if (!success && !this.pointer.isClosed()) {
                    this.pointer.safeRunConsume(DB::reset);
                }
            }
        });
    }
    
    public ResultSet executeQuery() throws SQLException {
        this.checkOpen();
        if (this.columnCount == 0) {
            throw new SQLException("Query does not return results");
        }
        this.rs.close();
        this.pointer.safeRunConsume(DB::reset);
        if (this.conn instanceof JDBC3Connection) {
            ((JDBC3Connection)this.conn).tryEnforceTransactionMode();
        }
        boolean success;
        return (ResultSet)this.withConnectionTimeout(() -> {
            success = false;
            try {
                this.resultsWaiting = this.conn.getDatabase().execute((CoreStatement)this, this.batch);
                success = true;
            }
            finally {
                if (!success && !this.pointer.isClosed()) {
                    this.pointer.safeRunInt(DB::reset);
                }
            }
            return this.getResultSet();
        });
    }
    
    public int executeUpdate() throws SQLException {
        return (int)this.executeLargeUpdate();
    }
    
    public long executeLargeUpdate() throws SQLException {
        this.checkOpen();
        if (this.columnCount != 0) {
            throw new SQLException("Query returns results");
        }
        this.rs.close();
        this.pointer.safeRunConsume(DB::reset);
        if (this.conn instanceof JDBC3Connection) {
            ((JDBC3Connection)this.conn).tryEnforceTransactionMode();
        }
        return (long)this.withConnectionTimeout(() -> this.conn.getDatabase().executeUpdate((CoreStatement)this, this.batch));
    }
    
    public void addBatch() throws SQLException {
        this.checkOpen();
        this.batchPos += this.paramCount;
        ++this.batchQueryCount;
        if (this.batch == null) {
            this.batch = new Object[this.paramCount];
        }
        if (this.batchPos + this.paramCount > this.batch.length) {
            final Object[] nb = new Object[this.batch.length * 2];
            System.arraycopy(this.batch, 0, nb, 0, this.batch.length);
            this.batch = nb;
        }
        System.arraycopy(this.batch, this.batchPos - this.paramCount, this.batch, this.batchPos, this.paramCount);
    }
    
    public ParameterMetaData getParameterMetaData() {
        return (ParameterMetaData)this;
    }
    
    public int getParameterCount() throws SQLException {
        this.checkOpen();
        return this.paramCount;
    }
    
    public String getParameterClassName(final int param) throws SQLException {
        this.checkOpen();
        return "java.lang.String";
    }
    
    public String getParameterTypeName(final int pos) {
        return "VARCHAR";
    }
    
    public int getParameterType(final int pos) {
        return 12;
    }
    
    public int getParameterMode(final int pos) {
        return 1;
    }
    
    public int getPrecision(final int pos) {
        return 0;
    }
    
    public int getScale(final int pos) {
        return 0;
    }
    
    public int isNullable(final int pos) {
        return 1;
    }
    
    public boolean isSigned(final int pos) {
        return true;
    }
    
    public Statement getStatement() {
        return (Statement)this;
    }
    
    public void setBigDecimal(final int pos, final BigDecimal value) throws SQLException {
        this.batch(pos, (Object)((value == null) ? null : value.toString()));
    }
    
    private byte[] readBytes(final InputStream istream, final int length) throws SQLException {
        if (length < 0) {
            throw new SQLException("Error reading stream. Length should be non-negative");
        }
        final byte[] bytes = new byte[length];
        try {
            int bytesRead;
            for (int totalBytesRead = 0; totalBytesRead < length; totalBytesRead += bytesRead) {
                bytesRead = istream.read(bytes, totalBytesRead, length - totalBytesRead);
                if (bytesRead == -1) {
                    throw new IOException("End of stream has been reached");
                }
            }
            return bytes;
        }
        catch (IOException cause) {
            final SQLException exception = new SQLException("Error reading stream");
            exception.initCause(cause);
            throw exception;
        }
    }
    
    public void setBinaryStream(final int pos, final InputStream istream, final int length) throws SQLException {
        if (istream == null && length == 0) {
            this.setBytes(pos, null);
        }
        this.setBytes(pos, this.readBytes(istream, length));
    }
    
    public void setAsciiStream(final int pos, final InputStream istream, final int length) throws SQLException {
        this.setUnicodeStream(pos, istream, length);
    }
    
    public void setUnicodeStream(final int pos, final InputStream istream, final int length) throws SQLException {
        if (istream == null && length == 0) {
            this.setString(pos, null);
        }
        try {
            this.setString(pos, new String(this.readBytes(istream, length), "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            final SQLException exception = new SQLException("UTF-8 is not supported");
            exception.initCause(e);
            throw exception;
        }
    }
    
    public void setBoolean(final int pos, final boolean value) throws SQLException {
        this.setInt(pos, value ? 1 : 0);
    }
    
    public void setByte(final int pos, final byte value) throws SQLException {
        this.setInt(pos, value);
    }
    
    public void setBytes(final int pos, final byte[] value) throws SQLException {
        this.batch(pos, (Object)value);
    }
    
    public void setDouble(final int pos, final double value) throws SQLException {
        this.batch(pos, (Object)new Double(value));
    }
    
    public void setFloat(final int pos, final float value) throws SQLException {
        this.batch(pos, (Object)new Float(value));
    }
    
    public void setInt(final int pos, final int value) throws SQLException {
        this.batch(pos, (Object)new Integer(value));
    }
    
    public void setLong(final int pos, final long value) throws SQLException {
        this.batch(pos, (Object)new Long(value));
    }
    
    public void setNull(final int pos, final int u1) throws SQLException {
        this.setNull(pos, u1, null);
    }
    
    public void setNull(final int pos, final int u1, final String u2) throws SQLException {
        this.batch(pos, (Object)null);
    }
    
    public void setObject(final int pos, final Object value) throws SQLException {
        if (value == null) {
            this.batch(pos, (Object)null);
        }
        else if (value instanceof Date) {
            this.setDateByMilliseconds(pos, Long.valueOf(((Date)value).getTime()), Calendar.getInstance());
        }
        else if (value instanceof Long) {
            this.batch(pos, value);
        }
        else if (value instanceof Integer) {
            this.batch(pos, value);
        }
        else if (value instanceof Short) {
            this.batch(pos, (Object)new Integer((int)value));
        }
        else if (value instanceof Float) {
            this.batch(pos, value);
        }
        else if (value instanceof Double) {
            this.batch(pos, value);
        }
        else if (value instanceof Boolean) {
            this.setBoolean(pos, (boolean)value);
        }
        else if (value instanceof byte[]) {
            this.batch(pos, value);
        }
        else if (value instanceof BigDecimal) {
            this.setBigDecimal(pos, (BigDecimal)value);
        }
        else {
            this.batch(pos, (Object)value.toString());
        }
    }
    
    public void setObject(final int p, final Object v, final int t) throws SQLException {
        this.setObject(p, v);
    }
    
    public void setObject(final int p, final Object v, final int t, final int s) throws SQLException {
        this.setObject(p, v);
    }
    
    public void setShort(final int pos, final short value) throws SQLException {
        this.setInt(pos, value);
    }
    
    public void setString(final int pos, final String value) throws SQLException {
        this.batch(pos, (Object)value);
    }
    
    public void setCharacterStream(final int pos, final Reader reader, final int length) throws SQLException {
        try {
            final StringBuffer sb = new StringBuffer();
            final char[] cbuf = new char[8192];
            int cnt;
            while ((cnt = reader.read(cbuf)) > 0) {
                sb.append(cbuf, 0, cnt);
            }
            this.setString(pos, sb.toString());
        }
        catch (IOException e) {
            throw new SQLException("Cannot read from character stream, exception message: " + e.getMessage());
        }
    }
    
    public void setDate(final int pos, final java.sql.Date x) throws SQLException {
        this.setDate(pos, x, Calendar.getInstance());
    }
    
    public void setDate(final int pos, final java.sql.Date x, final Calendar cal) throws SQLException {
        if (x == null) {
            this.setObject(pos, null);
        }
        else {
            this.setDateByMilliseconds(pos, Long.valueOf(x.getTime()), cal);
        }
    }
    
    public void setTime(final int pos, final Time x) throws SQLException {
        this.setTime(pos, x, Calendar.getInstance());
    }
    
    public void setTime(final int pos, final Time x, final Calendar cal) throws SQLException {
        if (x == null) {
            this.setObject(pos, null);
        }
        else {
            this.setDateByMilliseconds(pos, Long.valueOf(x.getTime()), cal);
        }
    }
    
    public void setTimestamp(final int pos, final Timestamp x) throws SQLException {
        this.setTimestamp(pos, x, Calendar.getInstance());
    }
    
    public void setTimestamp(final int pos, final Timestamp x, final Calendar cal) throws SQLException {
        if (x == null) {
            this.setObject(pos, null);
        }
        else {
            this.setDateByMilliseconds(pos, Long.valueOf(x.getTime()), cal);
        }
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkOpen();
        return (ResultSetMetaData)this.rs;
    }
    
    protected SQLException unsupported() {
        return new SQLFeatureNotSupportedException("not implemented by SQLite JDBC driver");
    }
    
    protected SQLException invalid() {
        return new SQLException("method cannot be called on a PreparedStatement");
    }
    
    public void setArray(final int i, final Array x) throws SQLException {
        throw this.unsupported();
    }
    
    public void setBlob(final int i, final Blob x) throws SQLException {
        throw this.unsupported();
    }
    
    public void setClob(final int i, final Clob x) throws SQLException {
        throw this.unsupported();
    }
    
    public void setRef(final int i, final Ref x) throws SQLException {
        throw this.unsupported();
    }
    
    public void setURL(final int pos, final URL x) throws SQLException {
        throw this.unsupported();
    }
    
    public boolean execute(final String sql) throws SQLException {
        throw this.invalid();
    }
    
    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        throw this.invalid();
    }
    
    public boolean execute(final String sql, final int[] colinds) throws SQLException {
        throw this.invalid();
    }
    
    public boolean execute(final String sql, final String[] colnames) throws SQLException {
        throw this.invalid();
    }
    
    public int executeUpdate(final String sql) throws SQLException {
        throw this.invalid();
    }
    
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        throw this.invalid();
    }
    
    public int executeUpdate(final String sql, final int[] colinds) throws SQLException {
        throw this.invalid();
    }
    
    public int executeUpdate(final String sql, final String[] cols) throws SQLException {
        throw this.invalid();
    }
    
    public long executeLargeUpdate(final String sql) throws SQLException {
        throw this.invalid();
    }
    
    public long executeLargeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        throw this.invalid();
    }
    
    public long executeLargeUpdate(final String sql, final int[] colinds) throws SQLException {
        throw this.invalid();
    }
    
    public long executeLargeUpdate(final String sql, final String[] cols) throws SQLException {
        throw this.invalid();
    }
    
    public ResultSet executeQuery(final String sql) throws SQLException {
        throw this.invalid();
    }
    
    public void addBatch(final String sql) throws SQLException {
        throw this.invalid();
    }
}
