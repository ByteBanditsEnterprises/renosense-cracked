//Raddon On Top!

package org.sqlite.core;

import org.sqlite.jdbc4.*;
import org.sqlite.jdbc3.*;
import java.util.*;
import org.sqlite.date.*;
import java.sql.*;
import org.sqlite.*;

public abstract class CorePreparedStatement extends JDBC4Statement
{
    protected int columnCount;
    protected int paramCount;
    protected int batchQueryCount;
    
    protected CorePreparedStatement(final SQLiteConnection conn, final String sql) throws SQLException {
        super(conn);
        this.sql = sql;
        final DB db = conn.getDatabase();
        db.prepare(this);
        this.rs.colsMeta = this.pointer.safeRun(DB::column_names);
        this.columnCount = this.pointer.safeRunInt(DB::column_count);
        this.paramCount = this.pointer.safeRunInt(DB::bind_parameter_count);
        this.batchQueryCount = 0;
        this.batch = null;
        this.batchPos = 0;
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        return Arrays.stream(this.executeLargeBatch()).mapToInt(l -> (int)l).toArray();
    }
    
    @Override
    public long[] executeLargeBatch() throws SQLException {
        if (this.batchQueryCount == 0) {
            return new long[0];
        }
        if (this.conn instanceof JDBC3Connection) {
            ((JDBC3Connection)this.conn).tryEnforceTransactionMode();
        }
        return this.withConnectionTimeout(() -> {
            try {
                return this.conn.getDatabase().executeBatch(this.pointer, this.batchQueryCount, this.batch, this.conn.getAutoCommit());
            }
            finally {
                this.clearBatch();
            }
        });
    }
    
    @Override
    public void clearBatch() throws SQLException {
        super.clearBatch();
        this.batchQueryCount = 0;
    }
    
    protected void batch(final int pos, final Object value) throws SQLException {
        this.checkOpen();
        if (this.batch == null) {
            this.batch = new Object[this.paramCount];
        }
        this.batch[this.batchPos + pos - 1] = value;
    }
    
    protected void setDateByMilliseconds(final int pos, final Long value, final Calendar calendar) throws SQLException {
        final SQLiteConnectionConfig config = this.conn.getConnectionConfig();
        switch (config.getDateClass()) {
            case TEXT: {
                this.batch(pos, FastDateFormat.getInstance(config.getDateStringFormat(), calendar.getTimeZone()).format(new Date(value)));
                break;
            }
            case REAL: {
                this.batch(pos, new Double(value / 8.64E7 + 2440587.5));
                break;
            }
            default: {
                this.batch(pos, new Long(value / config.getDateMultiplier()));
                break;
            }
        }
    }
}
