//Raddon On Top!

package org.sqlite;

import java.util.logging.*;
import java.util.*;
import org.sqlite.jdbc4.*;
import java.sql.*;

public class JDBC implements Driver
{
    public static final String PREFIX = "jdbc:sqlite:";
    
    @Override
    public int getMajorVersion() {
        return SQLiteJDBCLoader.getMajorVersion();
    }
    
    @Override
    public int getMinorVersion() {
        return SQLiteJDBCLoader.getMinorVersion();
    }
    
    @Override
    public boolean jdbcCompliant() {
        return false;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
    
    @Override
    public boolean acceptsURL(final String url) {
        return isValidURL(url);
    }
    
    public static boolean isValidURL(final String url) {
        return url != null && url.toLowerCase().startsWith("jdbc:sqlite:");
    }
    
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        return SQLiteConfig.getDriverPropertyInfo();
    }
    
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        return createConnection(url, info);
    }
    
    static String extractAddress(final String url) {
        return url.substring("jdbc:sqlite:".length());
    }
    
    public static SQLiteConnection createConnection(String url, final Properties prop) throws SQLException {
        if (!isValidURL(url)) {
            return null;
        }
        url = url.trim();
        return new JDBC4Connection(url, extractAddress(url), prop);
    }
    
    static {
        try {
            DriverManager.registerDriver(new JDBC());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
