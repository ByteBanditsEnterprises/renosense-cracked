//Raddon On Top!

package org.sqlite.core;

import java.sql.*;
import java.nio.*;
import java.nio.charset.*;
import org.sqlite.*;

public final class NativeDB extends DB
{
    private static final int DEFAULT_BACKUP_BUSY_SLEEP_TIME_MILLIS = 100;
    private static final int DEFAULT_BACKUP_NUM_BUSY_BEFORE_FAIL = 3;
    private static final int DEFAULT_PAGES_PER_BACKUP_STEP = 100;
    private long pointer;
    private static boolean isLoaded;
    private static boolean loadSucceeded;
    private long busyHandler;
    private long commitListener;
    private long updateListener;
    private long progressHandler;
    
    public NativeDB(final String url, final String fileName, final SQLiteConfig config) throws SQLException {
        super(url, fileName, config);
        this.pointer = 0L;
        this.busyHandler = 0L;
        this.commitListener = 0L;
        this.updateListener = 0L;
    }
    
    public static boolean load() throws Exception {
        if (NativeDB.isLoaded) {
            return NativeDB.loadSucceeded;
        }
        try {
            NativeDB.loadSucceeded = SQLiteJDBCLoader.initialize();
        }
        finally {
            NativeDB.isLoaded = true;
        }
        return NativeDB.loadSucceeded;
    }
    
    protected synchronized void _open(final String file, final int openFlags) throws SQLException {
        this._open_utf8(stringToUtf8ByteArray(file), openFlags);
    }
    
    synchronized native void _open_utf8(final byte[] p0, final int p1) throws SQLException;
    
    protected synchronized native void _close() throws SQLException;
    
    public synchronized int _exec(final String sql) throws SQLException {
        DriverManager.println("DriverManager [" + Thread.currentThread().getName() + "] [SQLite EXEC] " + sql);
        return this._exec_utf8(stringToUtf8ByteArray(sql));
    }
    
    synchronized native int _exec_utf8(final byte[] p0) throws SQLException;
    
    public synchronized native int shared_cache(final boolean p0);
    
    public synchronized native int enable_load_extension(final boolean p0);
    
    public native void interrupt();
    
    public synchronized native void busy_timeout(final int p0);
    
    public synchronized native void busy_handler(final BusyHandler p0);
    
    protected synchronized SafeStmtPtr prepare(final String sql) throws SQLException {
        DriverManager.println("DriverManager [" + Thread.currentThread().getName() + "] [SQLite PREP] " + sql);
        return new SafeStmtPtr(this, this.prepare_utf8(stringToUtf8ByteArray(sql)));
    }
    
    synchronized native long prepare_utf8(final byte[] p0) throws SQLException;
    
    synchronized String errmsg() {
        return utf8ByteBufferToString(this.errmsg_utf8());
    }
    
    synchronized native ByteBuffer errmsg_utf8();
    
    public synchronized String libversion() {
        return utf8ByteBufferToString(this.libversion_utf8());
    }
    
    native ByteBuffer libversion_utf8();
    
    public synchronized native long changes();
    
    public synchronized native long total_changes();
    
    protected synchronized native int finalize(final long p0);
    
    public synchronized native int step(final long p0);
    
    public synchronized native int reset(final long p0);
    
    public synchronized native int clear_bindings(final long p0);
    
    synchronized native int bind_parameter_count(final long p0);
    
    public synchronized native int column_count(final long p0);
    
    public synchronized native int column_type(final long p0, final int p1);
    
    public synchronized String column_decltype(final long stmt, final int col) {
        return utf8ByteBufferToString(this.column_decltype_utf8(stmt, col));
    }
    
    synchronized native ByteBuffer column_decltype_utf8(final long p0, final int p1);
    
    public synchronized String column_table_name(final long stmt, final int col) {
        return utf8ByteBufferToString(this.column_table_name_utf8(stmt, col));
    }
    
    synchronized native ByteBuffer column_table_name_utf8(final long p0, final int p1);
    
    public synchronized String column_name(final long stmt, final int col) {
        return utf8ByteBufferToString(this.column_name_utf8(stmt, col));
    }
    
    synchronized native ByteBuffer column_name_utf8(final long p0, final int p1);
    
    public synchronized String column_text(final long stmt, final int col) {
        return utf8ByteBufferToString(this.column_text_utf8(stmt, col));
    }
    
    synchronized native ByteBuffer column_text_utf8(final long p0, final int p1);
    
    public synchronized native byte[] column_blob(final long p0, final int p1);
    
    public synchronized native double column_double(final long p0, final int p1);
    
    public synchronized native long column_long(final long p0, final int p1);
    
    public synchronized native int column_int(final long p0, final int p1);
    
    synchronized native int bind_null(final long p0, final int p1);
    
    synchronized native int bind_int(final long p0, final int p1, final int p2);
    
    synchronized native int bind_long(final long p0, final int p1, final long p2);
    
    synchronized native int bind_double(final long p0, final int p1, final double p2);
    
    synchronized int bind_text(final long stmt, final int pos, final String v) {
        return this.bind_text_utf8(stmt, pos, stringToUtf8ByteArray(v));
    }
    
    synchronized native int bind_text_utf8(final long p0, final int p1, final byte[] p2);
    
    synchronized native int bind_blob(final long p0, final int p1, final byte[] p2);
    
    public synchronized native void result_null(final long p0);
    
    public synchronized void result_text(final long context, final String val) {
        this.result_text_utf8(context, stringToUtf8ByteArray(val));
    }
    
    synchronized native void result_text_utf8(final long p0, final byte[] p1);
    
    public synchronized native void result_blob(final long p0, final byte[] p1);
    
    public synchronized native void result_double(final long p0, final double p1);
    
    public synchronized native void result_long(final long p0, final long p1);
    
    public synchronized native void result_int(final long p0, final int p1);
    
    public synchronized void result_error(final long context, final String err) {
        this.result_error_utf8(context, stringToUtf8ByteArray(err));
    }
    
    synchronized native void result_error_utf8(final long p0, final byte[] p1);
    
    public synchronized String value_text(final Function f, final int arg) {
        return utf8ByteBufferToString(this.value_text_utf8(f, arg));
    }
    
    synchronized native ByteBuffer value_text_utf8(final Function p0, final int p1);
    
    public synchronized native byte[] value_blob(final Function p0, final int p1);
    
    public synchronized native double value_double(final Function p0, final int p1);
    
    public synchronized native long value_long(final Function p0, final int p1);
    
    public synchronized native int value_int(final Function p0, final int p1);
    
    public synchronized native int value_type(final Function p0, final int p1);
    
    public synchronized int create_function(final String name, final Function func, final int nArgs, final int flags) throws SQLException {
        return this.create_function_utf8(this.nameToUtf8ByteArray("function", name), func, nArgs, flags);
    }
    
    synchronized native int create_function_utf8(final byte[] p0, final Function p1, final int p2, final int p3);
    
    public synchronized int destroy_function(final String name) throws SQLException {
        return this.destroy_function_utf8(this.nameToUtf8ByteArray("function", name));
    }
    
    synchronized native int destroy_function_utf8(final byte[] p0);
    
    public synchronized int create_collation(final String name, final Collation coll) throws SQLException {
        return this.create_collation_utf8(this.nameToUtf8ByteArray("collation", name), coll);
    }
    
    synchronized native int create_collation_utf8(final byte[] p0, final Collation p1);
    
    public synchronized int destroy_collation(final String name) throws SQLException {
        return this.destroy_collation_utf8(this.nameToUtf8ByteArray("collation", name));
    }
    
    synchronized native int destroy_collation_utf8(final byte[] p0);
    
    public synchronized native int limit(final int p0, final int p1) throws SQLException;
    
    private byte[] nameToUtf8ByteArray(final String nameType, final String name) throws SQLException {
        final byte[] nameUtf8 = stringToUtf8ByteArray(name);
        if (name == null || "".equals(name) || nameUtf8.length > 255) {
            throw new SQLException("invalid " + nameType + " name: '" + name + "'");
        }
        return nameUtf8;
    }
    
    public int backup(final String dbName, final String destFileName, final DB.ProgressObserver observer) throws SQLException {
        return this.backup(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(destFileName), observer, 100, 3, 100);
    }
    
    public int backup(final String dbName, final String destFileName, final DB.ProgressObserver observer, final int sleepTimeMillis, final int nTimeouts, final int pagesPerStep) throws SQLException {
        return this.backup(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(destFileName), observer, sleepTimeMillis, nTimeouts, pagesPerStep);
    }
    
    synchronized native int backup(final byte[] p0, final byte[] p1, final DB.ProgressObserver p2, final int p3, final int p4, final int p5) throws SQLException;
    
    public synchronized int restore(final String dbName, final String sourceFileName, final DB.ProgressObserver observer) throws SQLException {
        return this.restore(dbName, sourceFileName, observer, 100, 3, 100);
    }
    
    public synchronized int restore(final String dbName, final String sourceFileName, final DB.ProgressObserver observer, final int sleepTimeMillis, final int nTimeouts, final int pagesPerStep) throws SQLException {
        return this.restore(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(sourceFileName), observer, sleepTimeMillis, nTimeouts, pagesPerStep);
    }
    
    synchronized native int restore(final byte[] p0, final byte[] p1, final DB.ProgressObserver p2, final int p3, final int p4, final int p5) throws SQLException;
    
    synchronized native boolean[][] column_metadata(final long p0);
    
    synchronized native void set_commit_listener(final boolean p0);
    
    synchronized native void set_update_listener(final boolean p0);
    
    static void throwex(final String msg) throws SQLException {
        throw new SQLException(msg);
    }
    
    static byte[] stringToUtf8ByteArray(final String str) {
        if (str == null) {
            return null;
        }
        return str.getBytes(StandardCharsets.UTF_8);
    }
    
    static String utf8ByteBufferToString(final ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        final byte[] buff = new byte[buffer.remaining()];
        buffer.get(buff);
        return new String(buff, StandardCharsets.UTF_8);
    }
    
    public synchronized native void register_progress_handler(final int p0, final ProgressHandler p1) throws SQLException;
    
    public synchronized native void clear_progress_handler() throws SQLException;
    
    long getBusyHandler() {
        return this.busyHandler;
    }
    
    long getCommitListener() {
        return this.commitListener;
    }
    
    long getUpdateListener() {
        return this.updateListener;
    }
    
    long getProgressHandler() {
        return this.progressHandler;
    }
    
    static {
        if ("The Android Project".equals(System.getProperty("java.vm.vendor"))) {
            System.loadLibrary("sqlitejdbc");
            NativeDB.isLoaded = true;
            NativeDB.loadSucceeded = true;
        }
        else {
            NativeDB.isLoaded = false;
            NativeDB.loadSucceeded = false;
        }
    }
}
