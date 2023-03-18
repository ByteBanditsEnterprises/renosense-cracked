//Raddon On Top!

package org.sqlite;

import org.sqlite.jdbc4.*;
import java.sql.*;
import java.util.concurrent.*;
import java.util.*;
import org.sqlite.core.*;
import java.net.*;
import java.nio.file.*;
import java.io.*;

public abstract class SQLiteConnection implements Connection
{
    private static final String RESOURCE_NAME_PREFIX = ":resource:";
    private final DB db;
    private CoreDatabaseMetaData meta;
    private final SQLiteConnectionConfig connectionConfig;
    private SQLiteConfig.TransactionMode currentTransactionMode;
    private boolean firstStatementExecuted;
    
    public SQLiteConnection(final DB db) {
        this.meta = null;
        this.firstStatementExecuted = false;
        this.db = db;
        this.connectionConfig = db.getConfig().newConnectionConfig();
    }
    
    public SQLiteConnection(final String url, final String fileName) throws SQLException {
        this(url, fileName, new Properties());
    }
    
    public SQLiteConnection(final String url, final String fileName, final Properties prop) throws SQLException {
        this.meta = null;
        this.firstStatementExecuted = false;
        DB newDB = null;
        try {
            newDB = (this.db = open(url, fileName, prop));
            final SQLiteConfig config = this.db.getConfig();
            this.connectionConfig = this.db.getConfig().newConnectionConfig();
            config.apply((Connection)this);
            this.currentTransactionMode = this.getDatabase().getConfig().getTransactionMode();
            this.firstStatementExecuted = false;
        }
        catch (Throwable t) {
            try {
                if (newDB != null) {
                    newDB.close();
                }
            }
            catch (Exception e) {
                t.addSuppressed(e);
            }
            throw t;
        }
    }
    
    public SQLiteConfig.TransactionMode getCurrentTransactionMode() {
        return this.currentTransactionMode;
    }
    
    public void setCurrentTransactionMode(final SQLiteConfig.TransactionMode currentTransactionMode) {
        this.currentTransactionMode = currentTransactionMode;
    }
    
    public void setFirstStatementExecuted(final boolean firstStatementExecuted) {
        this.firstStatementExecuted = firstStatementExecuted;
    }
    
    public boolean isFirstStatementExecuted() {
        return this.firstStatementExecuted;
    }
    
    public SQLiteConnectionConfig getConnectionConfig() {
        return this.connectionConfig;
    }
    
    public CoreDatabaseMetaData getSQLiteDatabaseMetaData() throws SQLException {
        this.checkOpen();
        if (this.meta == null) {
            this.meta = (CoreDatabaseMetaData)new JDBC4DatabaseMetaData(this);
        }
        return this.meta;
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return (DatabaseMetaData)this.getSQLiteDatabaseMetaData();
    }
    
    public String getUrl() {
        return this.db.getUrl();
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
    }
    
    @Override
    public String getSchema() throws SQLException {
        return null;
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }
    
    protected void checkCursor(final int rst, final int rsc, final int rsh) throws SQLException {
        if (rst != 1003) {
            throw new SQLException("SQLite only supports TYPE_FORWARD_ONLY cursors");
        }
        if (rsc != 1007) {
            throw new SQLException("SQLite only supports CONCUR_READ_ONLY cursors");
        }
        if (rsh != 2) {
            throw new SQLException("SQLite only supports closing cursors at commit");
        }
    }
    
    protected void setTransactionMode(final SQLiteConfig.TransactionMode mode) {
        this.connectionConfig.setTransactionMode(mode);
    }
    
    @Override
    public int getTransactionIsolation() {
        return this.connectionConfig.getTransactionIsolation();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.checkOpen();
        switch (level) {
            case 2:
            case 4:
            case 8: {
                this.getDatabase().exec("PRAGMA read_uncommitted = false;", this.getAutoCommit());
                break;
            }
            case 1: {
                this.getDatabase().exec("PRAGMA read_uncommitted = true;", this.getAutoCommit());
                break;
            }
            default: {
                throw new SQLException("Unsupported transaction isolation level: " + level + ". Must be one of TRANSACTION_READ_UNCOMMITTED, TRANSACTION_READ_COMMITTED, TRANSACTION_REPEATABLE_READ, or TRANSACTION_SERIALIZABLE in java.sql.Connection");
            }
        }
        this.connectionConfig.setTransactionIsolation(level);
    }
    
    private static DB open(final String url, final String origFileName, final Properties props) throws SQLException {
        final Properties newProps = new Properties();
        newProps.putAll(props);
        String fileName = extractPragmasFromFilename(url, origFileName, newProps);
        final SQLiteConfig config = new SQLiteConfig(newProps);
        if (!fileName.isEmpty() && !":memory:".equals(fileName) && !fileName.startsWith("file:") && !fileName.contains("mode=memory")) {
            if (fileName.startsWith(":resource:")) {
                final String resourceName = fileName.substring(":resource:".length());
                final ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
                URL resourceAddr = contextCL.getResource(resourceName);
                if (resourceAddr == null) {
                    try {
                        resourceAddr = new URL(resourceName);
                    }
                    catch (MalformedURLException e) {
                        throw new SQLException(String.format("resource %s not found: %s", resourceName, e));
                    }
                }
                try {
                    fileName = extractResource(resourceAddr).getAbsolutePath();
                }
                catch (IOException e2) {
                    throw new SQLException(String.format("failed to load %s: %s", resourceName, e2));
                }
            }
            else {
                final File file = new File(fileName).getAbsoluteFile();
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    for (File up = parent; up != null && !up.exists(); up = up.getParentFile()) {
                        parent = up;
                    }
                    throw new SQLException("path to '" + fileName + "': '" + parent + "' does not exist");
                }
                try {
                    if (!file.exists() && file.createNewFile()) {
                        file.delete();
                    }
                }
                catch (Exception e3) {
                    throw new SQLException("opening db: '" + fileName + "': " + e3.getMessage());
                }
                fileName = file.getAbsolutePath();
            }
        }
        DB db = null;
        try {
            NativeDB.load();
            db = (DB)new NativeDB(url, fileName, config);
        }
        catch (Exception e4) {
            final SQLException err = new SQLException("Error opening connection");
            err.initCause(e4);
            throw err;
        }
        db.open(fileName, config.getOpenModeFlags());
        return db;
    }
    
    private static File extractResource(final URL resourceAddr) throws IOException {
        if (resourceAddr.getProtocol().equals("file")) {
            try {
                return new File(resourceAddr.toURI());
            }
            catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        }
        final String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        final String dbFileName = String.format("sqlite-jdbc-tmp-%d.db", resourceAddr.hashCode());
        final File dbFile = new File(tempFolder, dbFileName);
        if (dbFile.exists()) {
            final long resourceLastModified = resourceAddr.openConnection().getLastModified();
            final long tmpFileLastModified = dbFile.lastModified();
            if (resourceLastModified < tmpFileLastModified) {
                return dbFile;
            }
            final boolean deletionSucceeded = dbFile.delete();
            if (!deletionSucceeded) {
                throw new IOException("failed to remove existing DB file: " + dbFile.getAbsolutePath());
            }
        }
        final InputStream reader = resourceAddr.openStream();
        try {
            Files.copy(reader, dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            final File file = dbFile;
            if (reader != null) {
                reader.close();
            }
            return file;
        }
        catch (Throwable t) {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Throwable t2) {
                    t.addSuppressed(t2);
                }
            }
            throw t;
        }
    }
    
    public DB getDatabase() {
        return this.db;
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        return this.connectionConfig.isAutoCommit();
    }
    
    @Override
    public void setAutoCommit(final boolean ac) throws SQLException {
        this.checkOpen();
        if (this.connectionConfig.isAutoCommit() == ac) {
            return;
        }
        this.connectionConfig.setAutoCommit(ac);
        if (this.getConnectionConfig().isAutoCommit()) {
            this.db.exec("commit;", ac);
            this.currentTransactionMode = null;
        }
        else {
            this.db.exec(this.transactionPrefix(), ac);
            this.currentTransactionMode = this.getConnectionConfig().getTransactionMode();
        }
    }
    
    public int getBusyTimeout() {
        return this.db.getConfig().getBusyTimeout();
    }
    
    public void setBusyTimeout(final int timeoutMillis) throws SQLException {
        this.db.getConfig().setBusyTimeout(timeoutMillis);
        this.db.busy_timeout(timeoutMillis);
    }
    
    public void setLimit(final SQLiteLimits limit, final int value) throws SQLException {
        if (value >= 0) {
            this.db.limit(limit.getId(), value);
        }
    }
    
    public void getLimit(final SQLiteLimits limit) throws SQLException {
        this.db.limit(limit.getId(), -1);
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.db.isClosed();
    }
    
    @Override
    public void close() throws SQLException {
        if (this.isClosed()) {
            return;
        }
        if (this.meta != null) {
            this.meta.close();
        }
        this.db.close();
    }
    
    protected void checkOpen() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException("database connection closed");
        }
    }
    
    public String libversion() throws SQLException {
        this.checkOpen();
        return this.db.libversion();
    }
    
    @Override
    public void commit() throws SQLException {
        this.checkOpen();
        if (this.connectionConfig.isAutoCommit()) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("commit;", this.getAutoCommit());
        this.db.exec(this.transactionPrefix(), this.getAutoCommit());
        this.firstStatementExecuted = false;
        this.setCurrentTransactionMode(this.getConnectionConfig().getTransactionMode());
    }
    
    @Override
    public void rollback() throws SQLException {
        this.checkOpen();
        if (this.connectionConfig.isAutoCommit()) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("rollback;", this.getAutoCommit());
        this.db.exec(this.transactionPrefix(), this.getAutoCommit());
        this.firstStatementExecuted = false;
        this.setCurrentTransactionMode(this.getConnectionConfig().getTransactionMode());
    }
    
    public void addUpdateListener(final SQLiteUpdateListener listener) {
        this.db.addUpdateListener(listener);
    }
    
    public void removeUpdateListener(final SQLiteUpdateListener listener) {
        this.db.removeUpdateListener(listener);
    }
    
    public void addCommitListener(final SQLiteCommitListener listener) {
        this.db.addCommitListener(listener);
    }
    
    public void removeCommitListener(final SQLiteCommitListener listener) {
        this.db.removeCommitListener(listener);
    }
    
    protected static String extractPragmasFromFilename(final String url, final String filename, final Properties prop) throws SQLException {
        final int parameterDelimiter = filename.indexOf(63);
        if (parameterDelimiter == -1) {
            return filename;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(filename.substring(0, parameterDelimiter));
        int nonPragmaCount = 0;
        final String[] parameters = filename.substring(parameterDelimiter + 1).split("&");
        for (int i = 0; i < parameters.length; ++i) {
            final String parameter = parameters[parameters.length - 1 - i].trim();
            if (!parameter.isEmpty()) {
                final String[] kvp = parameter.split("=");
                final String key = kvp[0].trim().toLowerCase();
                if (SQLiteConfig.pragmaSet.contains(key)) {
                    if (kvp.length == 1) {
                        throw new SQLException(String.format("Please specify a value for PRAGMA %s in URL %s", key, url));
                    }
                    final String value = kvp[1].trim();
                    if (!value.isEmpty()) {
                        if (!prop.containsKey(key)) {
                            prop.setProperty(key, value);
                        }
                    }
                }
                else {
                    sb.append((nonPragmaCount == 0) ? '?' : '&');
                    sb.append(parameter);
                    ++nonPragmaCount;
                }
            }
        }
        final String newFilename = sb.toString();
        return newFilename;
    }
    
    protected String transactionPrefix() {
        return this.connectionConfig.transactionPrefix();
    }
}
