//Raddon On Top!

package org.sqlite.javax;

import org.sqlite.jdbc4.*;
import org.sqlite.*;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.lang.reflect.*;

public class SQLitePooledConnection extends JDBC4PooledConnection
{
    protected SQLiteConnection physicalConn;
    protected volatile Connection handleConn;
    protected List<ConnectionEventListener> listeners;
    
    protected SQLitePooledConnection(final SQLiteConnection physicalConn) {
        this.listeners = new ArrayList<ConnectionEventListener>();
        this.physicalConn = physicalConn;
    }
    
    public SQLiteConnection getPhysicalConn() {
        return this.physicalConn;
    }
    
    @Override
    public void close() throws SQLException {
        if (this.handleConn != null) {
            this.listeners.clear();
            this.handleConn.close();
        }
        if (this.physicalConn != null) {
            try {
                this.physicalConn.close();
            }
            finally {
                this.physicalConn = null;
            }
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (this.handleConn != null) {
            this.handleConn.close();
        }
        return this.handleConn = (Connection)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { Connection.class }, new InvocationHandler() {
            boolean isClosed;
            
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    final String name = method.getName();
                    if ("close".equals(name)) {
                        final ConnectionEvent event = new ConnectionEvent(SQLitePooledConnection.this);
                        for (int i = SQLitePooledConnection.this.listeners.size() - 1; i >= 0; --i) {
                            SQLitePooledConnection.this.listeners.get(i).connectionClosed(event);
                        }
                        if (!SQLitePooledConnection.this.physicalConn.getAutoCommit()) {
                            SQLitePooledConnection.this.physicalConn.rollback();
                        }
                        SQLitePooledConnection.this.physicalConn.setAutoCommit(true);
                        this.isClosed = true;
                        return null;
                    }
                    if ("isClosed".equals(name)) {
                        if (!this.isClosed) {
                            this.isClosed = (boolean)method.invoke(SQLitePooledConnection.this.physicalConn, args);
                        }
                        return this.isClosed;
                    }
                    if (this.isClosed) {
                        throw new SQLException("Connection is closed");
                    }
                    return method.invoke(SQLitePooledConnection.this.physicalConn, args);
                }
                catch (SQLException e) {
                    if ("database connection closed".equals(e.getMessage())) {
                        final ConnectionEvent event = new ConnectionEvent(SQLitePooledConnection.this, e);
                        for (int i = SQLitePooledConnection.this.listeners.size() - 1; i >= 0; --i) {
                            SQLitePooledConnection.this.listeners.get(i).connectionErrorOccurred(event);
                        }
                    }
                    throw e;
                }
                catch (InvocationTargetException ex) {
                    throw ex.getCause();
                }
            }
        });
    }
    
    @Override
    public void addConnectionEventListener(final ConnectionEventListener listener) {
        this.listeners.add(listener);
    }
    
    @Override
    public void removeConnectionEventListener(final ConnectionEventListener listener) {
        this.listeners.remove(listener);
    }
    
    public List<ConnectionEventListener> getListeners() {
        return this.listeners;
    }
}
