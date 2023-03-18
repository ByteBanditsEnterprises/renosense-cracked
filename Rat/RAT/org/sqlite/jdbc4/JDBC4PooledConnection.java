//Raddon On Top!

package org.sqlite.jdbc4;

import javax.sql.*;

public abstract class JDBC4PooledConnection implements PooledConnection
{
    @Override
    public void addStatementEventListener(final StatementEventListener listener) {
    }
    
    @Override
    public void removeStatementEventListener(final StatementEventListener listener) {
    }
}
