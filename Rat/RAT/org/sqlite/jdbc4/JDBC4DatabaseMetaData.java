//Raddon On Top!

package org.sqlite.jdbc4;

import org.sqlite.jdbc3.*;
import org.sqlite.*;
import java.sql.*;

public class JDBC4DatabaseMetaData extends JDBC3DatabaseMetaData
{
    public JDBC4DatabaseMetaData(final SQLiteConnection conn) {
        super(conn);
    }
    
    public <T> T unwrap(final Class<T> iface) throws ClassCastException {
        return iface.cast(this);
    }
    
    public boolean isWrapperFor(final Class<?> iface) {
        return iface.isInstance(this);
    }
    
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public ResultSet getClientInfoProperties() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
