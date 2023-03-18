//Raddon On Top!

package org.sqlite;

import javax.sql.*;
import java.io.*;
import java.util.logging.*;
import java.sql.*;
import java.util.*;

public class SQLiteDataSource implements DataSource
{
    private SQLiteConfig config;
    private transient PrintWriter logger;
    private int loginTimeout;
    private String url;
    private String databaseName;
    
    public SQLiteDataSource() {
        this.loginTimeout = 1;
        this.url = "jdbc:sqlite:";
        this.databaseName = "";
        this.config = new SQLiteConfig();
    }
    
    public SQLiteDataSource(final SQLiteConfig config) {
        this.loginTimeout = 1;
        this.url = "jdbc:sqlite:";
        this.databaseName = "";
        this.config = config;
    }
    
    public void setConfig(final SQLiteConfig config) {
        this.config = config;
    }
    
    public SQLiteConfig getConfig() {
        return this.config;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public void setSharedCache(final boolean enable) {
        this.config.setSharedCache(enable);
    }
    
    public void setLoadExtension(final boolean enable) {
        this.config.enableLoadExtension(enable);
    }
    
    public void setReadOnly(final boolean readOnly) {
        this.config.setReadOnly(readOnly);
    }
    
    public void setCacheSize(final int numberOfPages) {
        this.config.setCacheSize(numberOfPages);
    }
    
    public void setCaseSensitiveLike(final boolean enable) {
        this.config.enableCaseSensitiveLike(enable);
    }
    
    public void setCountChanges(final boolean enable) {
        this.config.enableCountChanges(enable);
    }
    
    public void setDefaultCacheSize(final int numberOfPages) {
        this.config.setDefaultCacheSize(numberOfPages);
    }
    
    public void setEncoding(final String encoding) {
        this.config.setEncoding(SQLiteConfig.Encoding.getEncoding(encoding));
    }
    
    public void setEnforceForeignKeys(final boolean enforce) {
        this.config.enforceForeignKeys(enforce);
    }
    
    public void setFullColumnNames(final boolean enable) {
        this.config.enableFullColumnNames(enable);
    }
    
    public void setFullSync(final boolean enable) {
        this.config.enableFullSync(enable);
    }
    
    public void setIncrementalVacuum(final int numberOfPagesToBeRemoved) {
        this.config.incrementalVacuum(numberOfPagesToBeRemoved);
    }
    
    public void setJournalMode(final String mode) {
        this.config.setJournalMode(SQLiteConfig.JournalMode.valueOf(mode));
    }
    
    public void setJournalSizeLimit(final int limit) {
        this.config.setJounalSizeLimit(limit);
    }
    
    public void setLegacyFileFormat(final boolean use) {
        this.config.useLegacyFileFormat(use);
    }
    
    public void setLockingMode(final String mode) {
        this.config.setLockingMode(SQLiteConfig.LockingMode.valueOf(mode));
    }
    
    public void setPageSize(final int numBytes) {
        this.config.setPageSize(numBytes);
    }
    
    public void setMaxPageCount(final int numPages) {
        this.config.setMaxPageCount(numPages);
    }
    
    public void setReadUncommited(final boolean useReadUncommitedIsolationMode) {
        this.config.setReadUncommited(useReadUncommitedIsolationMode);
    }
    
    public void setRecursiveTriggers(final boolean enable) {
        this.config.enableRecursiveTriggers(enable);
    }
    
    public void setReverseUnorderedSelects(final boolean enable) {
        this.config.enableReverseUnorderedSelects(enable);
    }
    
    public void setShortColumnNames(final boolean enable) {
        this.config.enableShortColumnNames(enable);
    }
    
    public void setSynchronous(final String mode) {
        this.config.setSynchronous(SQLiteConfig.SynchronousMode.valueOf(mode));
    }
    
    public void setTempStore(final String storeType) {
        this.config.setTempStore(SQLiteConfig.TempStore.valueOf(storeType));
    }
    
    public void setTempStoreDirectory(final String directoryName) {
        this.config.setTempStoreDirectory(directoryName);
    }
    
    public void setTransactionMode(final String transactionMode) {
        this.config.setTransactionMode(transactionMode);
    }
    
    public void setUserVersion(final int version) {
        this.config.setUserVersion(version);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return (Connection)this.getConnection((String)null, (String)null);
    }
    
    public SQLiteConnection getConnection(final String username, final String password) throws SQLException {
        final Properties p = this.config.toProperties();
        if (username != null) {
            ((Hashtable<String, String>)p).put("user", username);
        }
        if (password != null) {
            ((Hashtable<String, String>)p).put("pass", password);
        }
        return JDBC.createConnection(this.url, p);
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.logger;
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return this.loginTimeout;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger");
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        this.logger = out;
    }
    
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        this.loginTimeout = seconds;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return (T)this;
    }
}
