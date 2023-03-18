//Raddon On Top!

package org.sqlite.javax;

import org.sqlite.*;
import javax.sql.*;
import java.sql.*;

public class SQLiteConnectionPoolDataSource extends SQLiteDataSource implements ConnectionPoolDataSource
{
    public SQLiteConnectionPoolDataSource() {
    }
    
    public SQLiteConnectionPoolDataSource(final SQLiteConfig config) {
        super(config);
    }
    
    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        return this.getPooledConnection(null, null);
    }
    
    @Override
    public PooledConnection getPooledConnection(final String user, final String password) throws SQLException {
        return new SQLitePooledConnection(this.getConnection(user, password));
    }
}
