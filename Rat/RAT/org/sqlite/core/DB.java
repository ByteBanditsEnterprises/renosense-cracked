//Raddon On Top!

package org.sqlite.core;

import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.sql.*;
import java.util.*;
import org.sqlite.*;

public abstract class DB implements Codes
{
    private final String url;
    private final String fileName;
    private final SQLiteConfig config;
    private final AtomicBoolean closed;
    volatile SafeStmtPtr begin;
    volatile SafeStmtPtr commit;
    private final Set<SafeStmtPtr> stmts;
    private final Set<SQLiteUpdateListener> updateListeners;
    private final Set<SQLiteCommitListener> commitListeners;
    
    public DB(final String url, final String fileName, final SQLiteConfig config) throws SQLException {
        this.closed = new AtomicBoolean(true);
        this.stmts = (Set<SafeStmtPtr>)ConcurrentHashMap.newKeySet();
        this.updateListeners = new HashSet<SQLiteUpdateListener>();
        this.commitListeners = new HashSet<SQLiteCommitListener>();
        this.url = url;
        this.fileName = fileName;
        this.config = config;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public boolean isClosed() {
        return this.closed.get();
    }
    
    public SQLiteConfig getConfig() {
        return this.config;
    }
    
    public abstract void interrupt() throws SQLException;
    
    public abstract void busy_timeout(final int p0) throws SQLException;
    
    public abstract void busy_handler(final BusyHandler p0) throws SQLException;
    
    abstract String errmsg() throws SQLException;
    
    public abstract String libversion() throws SQLException;
    
    public abstract long changes() throws SQLException;
    
    public abstract long total_changes() throws SQLException;
    
    public abstract int shared_cache(final boolean p0) throws SQLException;
    
    public abstract int enable_load_extension(final boolean p0) throws SQLException;
    
    public final synchronized void exec(final String sql, final boolean autoCommit) throws SQLException {
        final SafeStmtPtr pointer = this.prepare(sql);
        try {
            final int rc = pointer.safeRunInt(DB::step);
            switch (rc) {
                case 101: {
                    this.ensureAutoCommit(autoCommit);
                }
                case 100: {}
                default: {
                    this.throwex(rc);
                    break;
                }
            }
        }
        finally {
            pointer.close();
        }
    }
    
    public final synchronized void open(final String file, final int openFlags) throws SQLException {
        this._open(file, openFlags);
        this.closed.set(false);
        if (this.fileName.startsWith("file:") && !this.fileName.contains("cache=")) {
            this.shared_cache(this.config.isEnabledSharedCache());
        }
        this.enable_load_extension(this.config.isEnabledLoadExtension());
        this.busy_timeout(this.config.getBusyTimeout());
    }
    
    public final synchronized void close() throws SQLException {
        for (final SafeStmtPtr element : this.stmts) {
            element.close();
        }
        if (this.begin != null) {
            this.begin.close();
        }
        if (this.commit != null) {
            this.commit.close();
        }
        this.closed.set(true);
        this._close();
    }
    
    public final synchronized void prepare(final CoreStatement stmt) throws SQLException {
        if (stmt.sql == null) {
            throw new NullPointerException();
        }
        if (stmt.pointer != null) {
            stmt.pointer.close();
        }
        stmt.pointer = this.prepare(stmt.sql);
        final boolean added = this.stmts.add(stmt.pointer);
        if (!added) {
            throw new IllegalStateException("Already added pointer to statements set");
        }
    }
    
    public synchronized int finalize(final SafeStmtPtr safePtr, final long ptr) throws SQLException {
        try {
            return this.finalize(ptr);
        }
        finally {
            this.stmts.remove(safePtr);
        }
    }
    
    protected abstract void _open(final String p0, final int p1) throws SQLException;
    
    protected abstract void _close() throws SQLException;
    
    public abstract int _exec(final String p0) throws SQLException;
    
    protected abstract SafeStmtPtr prepare(final String p0) throws SQLException;
    
    protected abstract int finalize(final long p0) throws SQLException;
    
    public abstract int step(final long p0) throws SQLException;
    
    public abstract int reset(final long p0) throws SQLException;
    
    public abstract int clear_bindings(final long p0) throws SQLException;
    
    abstract int bind_parameter_count(final long p0) throws SQLException;
    
    public abstract int column_count(final long p0) throws SQLException;
    
    public abstract int column_type(final long p0, final int p1) throws SQLException;
    
    public abstract String column_decltype(final long p0, final int p1) throws SQLException;
    
    public abstract String column_table_name(final long p0, final int p1) throws SQLException;
    
    public abstract String column_name(final long p0, final int p1) throws SQLException;
    
    public abstract String column_text(final long p0, final int p1) throws SQLException;
    
    public abstract byte[] column_blob(final long p0, final int p1) throws SQLException;
    
    public abstract double column_double(final long p0, final int p1) throws SQLException;
    
    public abstract long column_long(final long p0, final int p1) throws SQLException;
    
    public abstract int column_int(final long p0, final int p1) throws SQLException;
    
    abstract int bind_null(final long p0, final int p1) throws SQLException;
    
    abstract int bind_int(final long p0, final int p1, final int p2) throws SQLException;
    
    abstract int bind_long(final long p0, final int p1, final long p2) throws SQLException;
    
    abstract int bind_double(final long p0, final int p1, final double p2) throws SQLException;
    
    abstract int bind_text(final long p0, final int p1, final String p2) throws SQLException;
    
    abstract int bind_blob(final long p0, final int p1, final byte[] p2) throws SQLException;
    
    public abstract void result_null(final long p0) throws SQLException;
    
    public abstract void result_text(final long p0, final String p1) throws SQLException;
    
    public abstract void result_blob(final long p0, final byte[] p1) throws SQLException;
    
    public abstract void result_double(final long p0, final double p1) throws SQLException;
    
    public abstract void result_long(final long p0, final long p1) throws SQLException;
    
    public abstract void result_int(final long p0, final int p1) throws SQLException;
    
    public abstract void result_error(final long p0, final String p1) throws SQLException;
    
    public abstract String value_text(final Function p0, final int p1) throws SQLException;
    
    public abstract byte[] value_blob(final Function p0, final int p1) throws SQLException;
    
    public abstract double value_double(final Function p0, final int p1) throws SQLException;
    
    public abstract long value_long(final Function p0, final int p1) throws SQLException;
    
    public abstract int value_int(final Function p0, final int p1) throws SQLException;
    
    public abstract int value_type(final Function p0, final int p1) throws SQLException;
    
    public abstract int create_function(final String p0, final Function p1, final int p2, final int p3) throws SQLException;
    
    public abstract int destroy_function(final String p0) throws SQLException;
    
    public abstract int create_collation(final String p0, final Collation p1) throws SQLException;
    
    public abstract int destroy_collation(final String p0) throws SQLException;
    
    public abstract int backup(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    public abstract int backup(final String p0, final String p1, final ProgressObserver p2, final int p3, final int p4, final int p5) throws SQLException;
    
    public abstract int restore(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    public abstract int restore(final String p0, final String p1, final ProgressObserver p2, final int p3, final int p4, final int p5) throws SQLException;
    
    public abstract int limit(final int p0, final int p1) throws SQLException;
    
    public abstract void register_progress_handler(final int p0, final ProgressHandler p1) throws SQLException;
    
    public abstract void clear_progress_handler() throws SQLException;
    
    abstract boolean[][] column_metadata(final long p0) throws SQLException;
    
    public final synchronized String[] column_names(final long stmt) throws SQLException {
        final String[] names = new String[this.column_count(stmt)];
        for (int i = 0; i < names.length; ++i) {
            names[i] = this.column_name(stmt, i);
        }
        return names;
    }
    
    final synchronized int sqlbind(final long stmt, int pos, final Object v) throws SQLException {
        ++pos;
        if (v == null) {
            return this.bind_null(stmt, pos);
        }
        if (v instanceof Integer) {
            return this.bind_int(stmt, pos, (int)v);
        }
        if (v instanceof Short) {
            return this.bind_int(stmt, pos, (int)v);
        }
        if (v instanceof Long) {
            return this.bind_long(stmt, pos, (long)v);
        }
        if (v instanceof Float) {
            return this.bind_double(stmt, pos, (double)v);
        }
        if (v instanceof Double) {
            return this.bind_double(stmt, pos, (double)v);
        }
        if (v instanceof String) {
            return this.bind_text(stmt, pos, (String)v);
        }
        if (v instanceof byte[]) {
            return this.bind_blob(stmt, pos, (byte[])v);
        }
        throw new SQLException("unexpected param type: " + v.getClass());
    }
    
    final synchronized long[] executeBatch(final SafeStmtPtr stmt, final int count, final Object[] vals, final boolean autoCommit) throws SQLException {
        return stmt.safeRun((db, ptr) -> this.executeBatch(ptr, count, vals, autoCommit));
    }
    
    private synchronized long[] executeBatch(final long stmt, final int count, final Object[] vals, final boolean autoCommit) throws SQLException {
        if (count < 1) {
            throw new SQLException("count (" + count + ") < 1");
        }
        final int params = this.bind_parameter_count(stmt);
        final long[] changes = new long[count];
        try {
            for (int i = 0; i < count; ++i) {
                this.reset(stmt);
                for (int j = 0; j < params; ++j) {
                    final int rc = this.sqlbind(stmt, j, vals[i * params + j]);
                    if (rc != 0) {
                        this.throwex(rc);
                    }
                }
                final int rc = this.step(stmt);
                if (rc != 101) {
                    this.reset(stmt);
                    if (rc == 100) {
                        throw new BatchUpdateException("batch entry " + i + ": query returns results", null, 0, changes, null);
                    }
                    this.throwex(rc);
                }
                changes[i] = this.changes();
            }
        }
        finally {
            this.ensureAutoCommit(autoCommit);
        }
        this.reset(stmt);
        return changes;
    }
    
    public final synchronized boolean execute(final CoreStatement stmt, final Object[] vals) throws SQLException {
        final int statusCode = stmt.pointer.safeRunInt((db, ptr) -> this.execute(ptr, vals));
        switch (statusCode & 0xFF) {
            case 101: {
                this.ensureAutoCommit(stmt.conn.getAutoCommit());
                return false;
            }
            case 100: {
                return true;
            }
            case 5:
            case 6:
            case 19:
            case 21: {
                throw this.newSQLException(statusCode);
            }
            default: {
                stmt.pointer.close();
                throw this.newSQLException(statusCode);
            }
        }
    }
    
    private synchronized int execute(final long ptr, final Object[] vals) throws SQLException {
        if (vals != null) {
            final int params = this.bind_parameter_count(ptr);
            if (params > vals.length) {
                throw new SQLException("assertion failure: param count (" + params + ") > value count (" + vals.length + ")");
            }
            for (int i = 0; i < params; ++i) {
                final int rc = this.sqlbind(ptr, i, vals[i]);
                if (rc != 0) {
                    this.throwex(rc);
                }
            }
        }
        final int statusCode = this.step(ptr);
        if ((statusCode & 0xFF) == 0x65) {
            this.reset(ptr);
        }
        return statusCode;
    }
    
    final synchronized boolean execute(final String sql, final boolean autoCommit) throws SQLException {
        final int statusCode = this._exec(sql);
        switch (statusCode) {
            case 0: {
                return false;
            }
            case 101: {
                this.ensureAutoCommit(autoCommit);
                return false;
            }
            case 100: {
                return true;
            }
            default: {
                throw this.newSQLException(statusCode);
            }
        }
    }
    
    public final synchronized long executeUpdate(final CoreStatement stmt, final Object[] vals) throws SQLException {
        try {
            if (this.execute(stmt, vals)) {
                throw new SQLException("query returns results");
            }
        }
        finally {
            if (!stmt.pointer.isClosed()) {
                stmt.pointer.safeRunInt(DB::reset);
            }
        }
        return this.changes();
    }
    
    abstract void set_commit_listener(final boolean p0);
    
    abstract void set_update_listener(final boolean p0);
    
    public synchronized void addUpdateListener(final SQLiteUpdateListener listener) {
        if (this.updateListeners.add(listener) && this.updateListeners.size() == 1) {
            this.set_update_listener(true);
        }
    }
    
    public synchronized void addCommitListener(final SQLiteCommitListener listener) {
        if (this.commitListeners.add(listener) && this.commitListeners.size() == 1) {
            this.set_commit_listener(true);
        }
    }
    
    public synchronized void removeUpdateListener(final SQLiteUpdateListener listener) {
        if (this.updateListeners.remove(listener) && this.updateListeners.isEmpty()) {
            this.set_update_listener(false);
        }
    }
    
    public synchronized void removeCommitListener(final SQLiteCommitListener listener) {
        if (this.commitListeners.remove(listener) && this.commitListeners.isEmpty()) {
            this.set_commit_listener(false);
        }
    }
    
    void onUpdate(final int type, final String database, final String table, final long rowId) {
        final Set<SQLiteUpdateListener> listeners;
        synchronized (this) {
            listeners = new HashSet<SQLiteUpdateListener>(this.updateListeners);
        }
        for (final SQLiteUpdateListener listener : listeners) {
            SQLiteUpdateListener.Type operationType = null;
            switch (type) {
                case 18: {
                    operationType = SQLiteUpdateListener.Type.INSERT;
                    break;
                }
                case 9: {
                    operationType = SQLiteUpdateListener.Type.DELETE;
                    break;
                }
                case 23: {
                    operationType = SQLiteUpdateListener.Type.UPDATE;
                    break;
                }
                default: {
                    throw new AssertionError((Object)("Unknown type: " + type));
                }
            }
            listener.onUpdate(operationType, database, table, rowId);
        }
    }
    
    void onCommit(final boolean commit) {
        final Set<SQLiteCommitListener> listeners;
        synchronized (this) {
            listeners = new HashSet<SQLiteCommitListener>(this.commitListeners);
        }
        for (final SQLiteCommitListener listener : listeners) {
            if (commit) {
                listener.onCommit();
            }
            else {
                listener.onRollback();
            }
        }
    }
    
    final void throwex() throws SQLException {
        throw new SQLException(this.errmsg());
    }
    
    public final void throwex(final int errorCode) throws SQLException {
        throw this.newSQLException(errorCode);
    }
    
    static void throwex(final int errorCode, final String errorMessage) throws SQLException {
        throw newSQLException(errorCode, errorMessage);
    }
    
    public static SQLiteException newSQLException(final int errorCode, final String errorMessage) {
        final SQLiteErrorCode code = SQLiteErrorCode.getErrorCode(errorCode);
        String msg;
        if (code == SQLiteErrorCode.UNKNOWN_ERROR) {
            msg = String.format("%s:%s (%s)", code, errorCode, errorMessage);
        }
        else {
            msg = String.format("%s (%s)", code, errorMessage);
        }
        return new SQLiteException(msg, code);
    }
    
    private SQLiteException newSQLException(final int errorCode) throws SQLException {
        return newSQLException(errorCode, this.errmsg());
    }
    
    final void ensureAutoCommit(final boolean autoCommit) throws SQLException {
        if (!autoCommit) {
            return;
        }
        this.ensureBeginAndCommit();
        this.begin.safeRunConsume((db, beginPtr) -> this.commit.safeRunConsume((db2, commitPtr) -> this.ensureAutocommit(beginPtr, commitPtr)));
    }
    
    private void ensureBeginAndCommit() throws SQLException {
        if (this.begin == null) {
            synchronized (this) {
                if (this.begin == null) {
                    this.begin = this.prepare("begin;");
                }
            }
        }
        if (this.commit == null) {
            synchronized (this) {
                if (this.commit == null) {
                    this.commit = this.prepare("commit;");
                }
            }
        }
    }
    
    private void ensureAutocommit(final long beginPtr, final long commitPtr) throws SQLException {
        try {
            if (this.step(beginPtr) != 101) {
                return;
            }
            final int rc = this.step(commitPtr);
            if (rc != 101) {
                this.reset(commitPtr);
                this.throwex(rc);
            }
        }
        finally {
            this.reset(beginPtr);
            this.reset(commitPtr);
        }
    }
    
    public interface ProgressObserver
    {
        void progress(final int p0, final int p1);
    }
}
