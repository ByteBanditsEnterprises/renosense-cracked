//Raddon On Top!

package org.sqlite;

import java.sql.*;
import java.util.*;

public class SQLiteConfig
{
    public static final String DEFAULT_DATE_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final int DEFAULT_MAX_LENGTH = 1000000000;
    private static final int DEFAULT_MAX_COLUMN = 2000;
    private static final int DEFAULT_MAX_SQL_LENGTH = 1000000;
    private static final int DEFAULT_MAX_FUNCTION_ARG = 100;
    private static final int DEFAULT_MAX_ATTACHED = 10;
    private static final int DEFAULT_MAX_PAGE_COUNT = 1073741823;
    private final Properties pragmaTable;
    private int openModeFlag;
    private final int busyTimeout;
    private boolean explicitReadOnly;
    private final SQLiteConnectionConfig defaultConnectionConfig;
    private static final String[] OnOff;
    static final Set<String> pragmaSet;
    
    public SQLiteConfig() {
        this(new Properties());
    }
    
    public SQLiteConfig(final Properties prop) {
        this.openModeFlag = 0;
        this.pragmaTable = prop;
        final String openMode = this.pragmaTable.getProperty(Pragma.OPEN_MODE.pragmaName);
        if (openMode != null) {
            this.openModeFlag = Integer.parseInt(openMode);
        }
        else {
            this.setOpenMode(SQLiteOpenMode.READWRITE);
            this.setOpenMode(SQLiteOpenMode.CREATE);
        }
        this.setSharedCache(Boolean.parseBoolean(this.pragmaTable.getProperty(Pragma.SHARED_CACHE.pragmaName, "false")));
        this.setOpenMode(SQLiteOpenMode.OPEN_URI);
        this.busyTimeout = Integer.parseInt(this.pragmaTable.getProperty(Pragma.BUSY_TIMEOUT.pragmaName, "3000"));
        this.defaultConnectionConfig = SQLiteConnectionConfig.fromPragmaTable(this.pragmaTable);
        this.explicitReadOnly = Boolean.parseBoolean(this.pragmaTable.getProperty(Pragma.JDBC_EXPLICIT_READONLY.pragmaName, "false"));
    }
    
    public SQLiteConnectionConfig newConnectionConfig() {
        return this.defaultConnectionConfig.copyConfig();
    }
    
    public Connection createConnection(final String url) throws SQLException {
        return JDBC.createConnection(url, this.toProperties());
    }
    
    public void apply(final Connection conn) throws SQLException {
        final HashSet<String> pragmaParams = new HashSet<String>();
        for (final Pragma each : Pragma.values()) {
            pragmaParams.add(each.pragmaName);
        }
        if (conn instanceof SQLiteConnection) {
            final SQLiteConnection sqliteConn = (SQLiteConnection)conn;
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_ATTACHED, this.parseLimitPragma(Pragma.LIMIT_ATTACHED, 10));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_COLUMN, this.parseLimitPragma(Pragma.LIMIT_COLUMN, 2000));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_COMPOUND_SELECT, this.parseLimitPragma(Pragma.LIMIT_COMPOUND_SELECT, -1));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_EXPR_DEPTH, this.parseLimitPragma(Pragma.LIMIT_EXPR_DEPTH, -1));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_FUNCTION_ARG, this.parseLimitPragma(Pragma.LIMIT_FUNCTION_ARG, 100));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_LENGTH, this.parseLimitPragma(Pragma.LIMIT_LENGTH, 1000000000));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_LIKE_PATTERN_LENGTH, this.parseLimitPragma(Pragma.LIMIT_LIKE_PATTERN_LENGTH, -1));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_SQL_LENGTH, this.parseLimitPragma(Pragma.LIMIT_SQL_LENGTH, 1000000));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_TRIGGER_DEPTH, this.parseLimitPragma(Pragma.LIMIT_TRIGGER_DEPTH, -1));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_VARIABLE_NUMBER, this.parseLimitPragma(Pragma.LIMIT_VARIABLE_NUMBER, -1));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_VDBE_OP, this.parseLimitPragma(Pragma.LIMIT_VDBE_OP, -1));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_WORKER_THREADS, this.parseLimitPragma(Pragma.LIMIT_WORKER_THREADS, -1));
            sqliteConn.setLimit(SQLiteLimits.SQLITE_LIMIT_PAGE_COUNT, this.parseLimitPragma(Pragma.LIMIT_PAGE_COUNT, 1073741823));
        }
        pragmaParams.remove(Pragma.OPEN_MODE.pragmaName);
        pragmaParams.remove(Pragma.SHARED_CACHE.pragmaName);
        pragmaParams.remove(Pragma.LOAD_EXTENSION.pragmaName);
        pragmaParams.remove(Pragma.DATE_PRECISION.pragmaName);
        pragmaParams.remove(Pragma.DATE_CLASS.pragmaName);
        pragmaParams.remove(Pragma.DATE_STRING_FORMAT.pragmaName);
        pragmaParams.remove(Pragma.PASSWORD.pragmaName);
        pragmaParams.remove(Pragma.HEXKEY_MODE.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_ATTACHED.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_COLUMN.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_COMPOUND_SELECT.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_EXPR_DEPTH.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_FUNCTION_ARG.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_LENGTH.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_LIKE_PATTERN_LENGTH.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_SQL_LENGTH.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_TRIGGER_DEPTH.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_VARIABLE_NUMBER.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_VDBE_OP.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_WORKER_THREADS.pragmaName);
        pragmaParams.remove(Pragma.LIMIT_PAGE_COUNT.pragmaName);
        pragmaParams.remove(Pragma.JDBC_EXPLICIT_READONLY.pragmaName);
        final Statement stat = conn.createStatement();
        try {
            if (this.pragmaTable.containsKey(Pragma.PASSWORD.pragmaName)) {
                final String password = this.pragmaTable.getProperty(Pragma.PASSWORD.pragmaName);
                if (password != null && !password.isEmpty()) {
                    final String hexkeyMode = this.pragmaTable.getProperty(Pragma.HEXKEY_MODE.pragmaName);
                    String passwordPragma;
                    if (HexKeyMode.SSE.name().equalsIgnoreCase(hexkeyMode)) {
                        passwordPragma = "pragma hexkey = '%s'";
                    }
                    else if (HexKeyMode.SQLCIPHER.name().equalsIgnoreCase(hexkeyMode)) {
                        passwordPragma = "pragma key = \"x'%s'\"";
                    }
                    else {
                        passwordPragma = "pragma key = '%s'";
                    }
                    stat.execute(String.format(passwordPragma, password.replace("'", "''")));
                    stat.execute("select 1 from sqlite_schema");
                }
            }
            for (final Object each2 : ((Hashtable<Object, V>)this.pragmaTable).keySet()) {
                final String key = each2.toString();
                if (!pragmaParams.contains(key)) {
                    continue;
                }
                final String value = this.pragmaTable.getProperty(key);
                if (value == null) {
                    continue;
                }
                stat.execute(String.format("pragma %s=%s", key, value));
            }
        }
        finally {
            if (stat != null) {
                stat.close();
            }
        }
    }
    
    private void set(final Pragma pragma, final boolean flag) {
        this.setPragma(pragma, Boolean.toString(flag));
    }
    
    private void set(final Pragma pragma, final int num) {
        this.setPragma(pragma, Integer.toString(num));
    }
    
    private boolean getBoolean(final Pragma pragma, final String defaultValue) {
        return Boolean.parseBoolean(this.pragmaTable.getProperty(pragma.pragmaName, defaultValue));
    }
    
    private int parseLimitPragma(final Pragma pragma, final int defaultValue) {
        if (!this.pragmaTable.containsKey(pragma.pragmaName)) {
            return defaultValue;
        }
        final String valueString = this.pragmaTable.getProperty(pragma.pragmaName);
        try {
            return Integer.parseInt(valueString);
        }
        catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
    
    public boolean isEnabledSharedCache() {
        return this.getBoolean(Pragma.SHARED_CACHE, "false");
    }
    
    public boolean isEnabledLoadExtension() {
        return this.getBoolean(Pragma.LOAD_EXTENSION, "false");
    }
    
    public int getOpenModeFlags() {
        return this.openModeFlag;
    }
    
    public void setPragma(final Pragma pragma, final String value) {
        ((Hashtable<String, String>)this.pragmaTable).put(pragma.pragmaName, value);
    }
    
    public Properties toProperties() {
        this.pragmaTable.setProperty(Pragma.OPEN_MODE.pragmaName, Integer.toString(this.openModeFlag));
        this.pragmaTable.setProperty(Pragma.TRANSACTION_MODE.pragmaName, this.defaultConnectionConfig.getTransactionMode().getValue());
        this.pragmaTable.setProperty(Pragma.DATE_CLASS.pragmaName, this.defaultConnectionConfig.getDateClass().getValue());
        this.pragmaTable.setProperty(Pragma.DATE_PRECISION.pragmaName, this.defaultConnectionConfig.getDatePrecision().getValue());
        this.pragmaTable.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, this.defaultConnectionConfig.getDateStringFormat());
        this.pragmaTable.setProperty(Pragma.JDBC_EXPLICIT_READONLY.pragmaName, this.explicitReadOnly ? "true" : "false");
        return this.pragmaTable;
    }
    
    static DriverPropertyInfo[] getDriverPropertyInfo() {
        final Pragma[] pragma = Pragma.values();
        final DriverPropertyInfo[] result = new DriverPropertyInfo[pragma.length];
        int index = 0;
        for (final Pragma p : Pragma.values()) {
            final DriverPropertyInfo di = new DriverPropertyInfo(p.pragmaName, null);
            di.choices = p.choices;
            di.description = p.description;
            di.required = false;
            result[index++] = di;
        }
        return result;
    }
    
    public boolean isExplicitReadOnly() {
        return this.explicitReadOnly;
    }
    
    public void setExplicitReadOnly(final boolean readOnly) {
        this.explicitReadOnly = readOnly;
    }
    
    public void setOpenMode(final SQLiteOpenMode mode) {
        this.openModeFlag |= mode.flag;
    }
    
    public void resetOpenMode(final SQLiteOpenMode mode) {
        this.openModeFlag &= ~mode.flag;
    }
    
    public void setSharedCache(final boolean enable) {
        this.set(Pragma.SHARED_CACHE, enable);
    }
    
    public void enableLoadExtension(final boolean enable) {
        this.set(Pragma.LOAD_EXTENSION, enable);
    }
    
    public void setReadOnly(final boolean readOnly) {
        if (readOnly) {
            this.setOpenMode(SQLiteOpenMode.READONLY);
            this.resetOpenMode(SQLiteOpenMode.CREATE);
            this.resetOpenMode(SQLiteOpenMode.READWRITE);
        }
        else {
            this.setOpenMode(SQLiteOpenMode.READWRITE);
            this.setOpenMode(SQLiteOpenMode.CREATE);
            this.resetOpenMode(SQLiteOpenMode.READONLY);
        }
    }
    
    public void setCacheSize(final int numberOfPages) {
        this.set(Pragma.CACHE_SIZE, numberOfPages);
    }
    
    public void enableCaseSensitiveLike(final boolean enable) {
        this.set(Pragma.CASE_SENSITIVE_LIKE, enable);
    }
    
    @Deprecated
    public void enableCountChanges(final boolean enable) {
        this.set(Pragma.COUNT_CHANGES, enable);
    }
    
    public void setDefaultCacheSize(final int numberOfPages) {
        this.set(Pragma.DEFAULT_CACHE_SIZE, numberOfPages);
    }
    
    public void deferForeignKeys(final boolean enable) {
        this.set(Pragma.DEFER_FOREIGN_KEYS, enable);
    }
    
    @Deprecated
    public void enableEmptyResultCallBacks(final boolean enable) {
        this.set(Pragma.EMPTY_RESULT_CALLBACKS, enable);
    }
    
    private static String[] toStringArray(final PragmaValue[] list) {
        final String[] result = new String[list.length];
        for (int i = 0; i < list.length; ++i) {
            result[i] = list[i].getValue();
        }
        return result;
    }
    
    public void setEncoding(final Encoding encoding) {
        this.setPragma(Pragma.ENCODING, encoding.typeName);
    }
    
    public void enforceForeignKeys(final boolean enforce) {
        this.set(Pragma.FOREIGN_KEYS, enforce);
    }
    
    @Deprecated
    public void enableFullColumnNames(final boolean enable) {
        this.set(Pragma.FULL_COLUMN_NAMES, enable);
    }
    
    public void enableFullSync(final boolean enable) {
        this.set(Pragma.FULL_SYNC, enable);
    }
    
    public void incrementalVacuum(final int numberOfPagesToBeRemoved) {
        this.set(Pragma.INCREMENTAL_VACUUM, numberOfPagesToBeRemoved);
    }
    
    public void setJournalMode(final JournalMode mode) {
        this.setPragma(Pragma.JOURNAL_MODE, mode.name());
    }
    
    public void setJounalSizeLimit(final int limit) {
        this.set(Pragma.JOURNAL_SIZE_LIMIT, limit);
    }
    
    public void useLegacyFileFormat(final boolean use) {
        this.set(Pragma.LEGACY_FILE_FORMAT, use);
    }
    
    public void setLockingMode(final LockingMode mode) {
        this.setPragma(Pragma.LOCKING_MODE, mode.name());
    }
    
    public void setPageSize(final int numBytes) {
        this.set(Pragma.PAGE_SIZE, numBytes);
    }
    
    public void setMaxPageCount(final int numPages) {
        this.set(Pragma.MAX_PAGE_COUNT, numPages);
    }
    
    public void setReadUncommited(final boolean useReadUncommitedIsolationMode) {
        this.set(Pragma.READ_UNCOMMITTED, useReadUncommitedIsolationMode);
    }
    
    public void enableRecursiveTriggers(final boolean enable) {
        this.set(Pragma.RECURSIVE_TRIGGERS, enable);
    }
    
    public void enableReverseUnorderedSelects(final boolean enable) {
        this.set(Pragma.REVERSE_UNORDERED_SELECTS, enable);
    }
    
    public void enableShortColumnNames(final boolean enable) {
        this.set(Pragma.SHORT_COLUMN_NAMES, enable);
    }
    
    public void setSynchronous(final SynchronousMode mode) {
        this.setPragma(Pragma.SYNCHRONOUS, mode.name());
    }
    
    public void setHexKeyMode(final HexKeyMode mode) {
        this.setPragma(Pragma.HEXKEY_MODE, mode.name());
    }
    
    public void setTempStore(final TempStore storeType) {
        this.setPragma(Pragma.TEMP_STORE, storeType.name());
    }
    
    public void setTempStoreDirectory(final String directoryName) {
        this.setPragma(Pragma.TEMP_STORE_DIRECTORY, String.format("'%s'", directoryName));
    }
    
    public void setUserVersion(final int version) {
        this.set(Pragma.USER_VERSION, version);
    }
    
    public void setApplicationId(final int id) {
        this.set(Pragma.APPLICATION_ID, id);
    }
    
    public void setTransactionMode(final TransactionMode transactionMode) {
        this.defaultConnectionConfig.setTransactionMode(transactionMode);
    }
    
    public void setTransactionMode(final String transactionMode) {
        this.setTransactionMode(TransactionMode.getMode(transactionMode));
    }
    
    public TransactionMode getTransactionMode() {
        return this.defaultConnectionConfig.getTransactionMode();
    }
    
    public void setDatePrecision(final String datePrecision) {
        this.defaultConnectionConfig.setDatePrecision(DatePrecision.getPrecision(datePrecision));
    }
    
    public void setDateClass(final String dateClass) {
        this.defaultConnectionConfig.setDateClass(DateClass.getDateClass(dateClass));
    }
    
    public void setDateStringFormat(final String dateStringFormat) {
        this.defaultConnectionConfig.setDateStringFormat(dateStringFormat);
    }
    
    public void setBusyTimeout(final int milliseconds) {
        this.setPragma(Pragma.BUSY_TIMEOUT, Integer.toString(milliseconds));
    }
    
    public int getBusyTimeout() {
        return this.busyTimeout;
    }
    
    static {
        OnOff = new String[] { "true", "false" };
        pragmaSet = new TreeSet<String>();
        for (final Pragma pragma : Pragma.values()) {
            SQLiteConfig.pragmaSet.add(pragma.pragmaName);
        }
    }
    
    public enum Pragma
    {
        OPEN_MODE("open_mode", "Database open-mode flag", (String[])null), 
        SHARED_CACHE("shared_cache", "Enable SQLite Shared-Cache mode, native driver only", SQLiteConfig.OnOff), 
        LOAD_EXTENSION("enable_load_extension", "Enable SQLite load_extension() function, native driver only", SQLiteConfig.OnOff), 
        CACHE_SIZE("cache_size", "Maximum number of database disk pages that SQLite will hold in memory at once per open database file", (String[])null), 
        MMAP_SIZE("mmap_size", "Maximum number of bytes that are set aside for memory-mapped I/O on a single database", (String[])null), 
        CASE_SENSITIVE_LIKE("case_sensitive_like", "Installs a new application-defined LIKE function that is either case sensitive or insensitive depending on the value", SQLiteConfig.OnOff), 
        COUNT_CHANGES("count_changes", "Deprecated", SQLiteConfig.OnOff), 
        DEFAULT_CACHE_SIZE("default_cache_size", "Deprecated", (String[])null), 
        DEFER_FOREIGN_KEYS("defer_foreign_keys", "When the defer_foreign_keys PRAGMA is on, enforcement of all foreign key constraints is delayed until the outermost transaction is committed. The defer_foreign_keys pragma defaults to OFF so that foreign key constraints are only deferred if they are created as \"DEFERRABLE INITIALLY DEFERRED\". The defer_foreign_keys pragma is automatically switched off at each COMMIT or ROLLBACK. Hence, the defer_foreign_keys pragma must be separately enabled for each transaction. This pragma is only meaningful if foreign key constraints are enabled, of course.", SQLiteConfig.OnOff), 
        EMPTY_RESULT_CALLBACKS("empty_result_callback", "Deprecated", SQLiteConfig.OnOff), 
        ENCODING("encoding", "Set the encoding that the main database will be created with if it is created by this session", toStringArray(Encoding.values())), 
        FOREIGN_KEYS("foreign_keys", "Set the enforcement of foreign key constraints", SQLiteConfig.OnOff), 
        FULL_COLUMN_NAMES("full_column_names", "Deprecated", SQLiteConfig.OnOff), 
        FULL_SYNC("fullsync", "Whether or not the F_FULLFSYNC syncing method is used on systems that support it. Only Mac OS X supports F_FULLFSYNC.", SQLiteConfig.OnOff), 
        INCREMENTAL_VACUUM("incremental_vacuum", "Causes up to N pages to be removed from the freelist. The database file is truncated by the same amount. The incremental_vacuum pragma has no effect if the database is not in auto_vacuum=incremental mode or if there are no pages on the freelist. If there are fewer than N pages on the freelist, or if N is less than 1, or if the \"(N)\" argument is omitted, then the entire freelist is cleared.", (String[])null), 
        JOURNAL_MODE("journal_mode", "Set the journal mode for databases associated with the current database connection", toStringArray(JournalMode.values())), 
        JOURNAL_SIZE_LIMIT("journal_size_limit", "Limit the size of rollback-journal and WAL files left in the file-system after transactions or checkpoints", (String[])null), 
        LEGACY_FILE_FORMAT("legacy_file_format", "No-op", SQLiteConfig.OnOff), 
        LOCKING_MODE("locking_mode", "Set the database connection locking-mode", toStringArray(LockingMode.values())), 
        PAGE_SIZE("page_size", "Set the page size of the database. The page size must be a power of two between 512 and 65536 inclusive.", (String[])null), 
        MAX_PAGE_COUNT("max_page_count", "Set the maximum number of pages in the database file", (String[])null), 
        READ_UNCOMMITTED("read_uncommitted", "Set READ UNCOMMITTED isolation", SQLiteConfig.OnOff), 
        RECURSIVE_TRIGGERS("recursive_triggers", "Set the recursive trigger capability", SQLiteConfig.OnOff), 
        REVERSE_UNORDERED_SELECTS("reverse_unordered_selects", "When enabled, this PRAGMA causes many SELECT statements without an ORDER BY clause to emit their results in the reverse order from what they normally would", SQLiteConfig.OnOff), 
        SECURE_DELETE("secure_delete", "When secure_delete is on, SQLite overwrites deleted content with zeros", new String[] { "true", "false", "fast" }), 
        SHORT_COLUMN_NAMES("short_column_names", "Deprecated", SQLiteConfig.OnOff), 
        SYNCHRONOUS("synchronous", "Set the \"synchronous\" flag", toStringArray(SynchronousMode.values())), 
        TEMP_STORE("temp_store", "When temp_store is DEFAULT (0), the compile-time C preprocessor macro SQLITE_TEMP_STORE is used to determine where temporary tables and indices are stored. When temp_store is MEMORY (2) temporary tables and indices are kept as if they were in pure in-memory databases. When temp_store is FILE (1) temporary tables and indices are stored in a file. The temp_store_directory pragma can be used to specify the directory containing temporary files when FILE is specified. When the temp_store setting is changed, all existing temporary tables, indices, triggers, and views are immediately deleted.", toStringArray(TempStore.values())), 
        TEMP_STORE_DIRECTORY("temp_store_directory", "Deprecated", (String[])null), 
        USER_VERSION("user_version", "Set the value of the user-version integer at offset 60 in the database header. The user-version is an integer that is available to applications to use however they want. SQLite makes no use of the user-version itself.", (String[])null), 
        APPLICATION_ID("application_id", "Set the 32-bit signed big-endian \"Application ID\" integer located at offset 68 into the database header. Applications that use SQLite as their application file-format should set the Application ID integer to a unique integer so that utilities such as file(1) can determine the specific file type rather than just reporting \"SQLite3 Database\"", (String[])null), 
        LIMIT_LENGTH("limit_length", "The maximum size of any string or BLOB or table row, in bytes.", (String[])null), 
        LIMIT_SQL_LENGTH("limit_sql_length", "The maximum length of an SQL statement, in bytes.", (String[])null), 
        LIMIT_COLUMN("limit_column", "The maximum number of columns in a table definition or in the result set of a SELECT or the maximum number of columns in an index or in an ORDER BY or GROUP BY clause.", (String[])null), 
        LIMIT_EXPR_DEPTH("limit_expr_depth", "The maximum depth of the parse tree on any expression.", (String[])null), 
        LIMIT_COMPOUND_SELECT("limit_compound_select", "The maximum number of terms in a compound SELECT statement.", (String[])null), 
        LIMIT_VDBE_OP("limit_vdbe_op", "The maximum number of instructions in a virtual machine program used to implement an SQL statement. If sqlite3_prepare_v2() or the equivalent tries to allocate space for more than this many opcodes in a single prepared statement, an SQLITE_NOMEM error is returned.", (String[])null), 
        LIMIT_FUNCTION_ARG("limit_function_arg", "The maximum number of arguments on a function.", (String[])null), 
        LIMIT_ATTACHED("limit_attached", "The maximum number of attached databases.", (String[])null), 
        LIMIT_LIKE_PATTERN_LENGTH("limit_like_pattern_length", "The maximum length of the pattern argument to the LIKE or GLOB operators.", (String[])null), 
        LIMIT_VARIABLE_NUMBER("limit_variable_number", "The maximum index number of any parameter in an SQL statement.", (String[])null), 
        LIMIT_TRIGGER_DEPTH("limit_trigger_depth", "The maximum depth of recursion for triggers.", (String[])null), 
        LIMIT_WORKER_THREADS("limit_worker_threads", "The maximum number of auxiliary worker threads that a single prepared statement may start.", (String[])null), 
        LIMIT_PAGE_COUNT("limit_page_count", "The maximum number of pages allowed in a single database file.", (String[])null), 
        TRANSACTION_MODE("transaction_mode", "Set the transaction mode", toStringArray(TransactionMode.values())), 
        DATE_PRECISION("date_precision", "\"seconds\": Read and store integer dates as seconds from the Unix Epoch (SQLite standard).\n\"milliseconds\": (DEFAULT) Read and store integer dates as milliseconds from the Unix Epoch (Java standard).", toStringArray(DatePrecision.values())), 
        DATE_CLASS("date_class", "\"integer\": (Default) store dates as number of seconds or milliseconds from the Unix Epoch\n\"text\": store dates as a string of text\n\"real\": store dates as Julian Dates", toStringArray(DateClass.values())), 
        DATE_STRING_FORMAT("date_string_format", "Format to store and retrieve dates stored as text. Defaults to \"yyyy-MM-dd HH:mm:ss.SSS\"", (String[])null), 
        BUSY_TIMEOUT("busy_timeout", "Sets a busy handler that sleeps for a specified amount of time when a table is locked", (String[])null), 
        HEXKEY_MODE("hexkey_mode", "Mode of the secret key", toStringArray(HexKeyMode.values())), 
        PASSWORD("password", "Database password", (String[])null), 
        JDBC_EXPLICIT_READONLY("jdbc.explicit_readonly", "Set explicit read only transactions", (String[])null);
        
        public final String pragmaName;
        public final String[] choices;
        public final String description;
        
        private Pragma(final String pragmaName) {
            this(pragmaName, null);
        }
        
        private Pragma(final String pragmaName, final String[] choices) {
            this(pragmaName, null, choices);
        }
        
        private Pragma(final String pragmaName, final String description, final String[] choices) {
            this.pragmaName = pragmaName;
            this.description = description;
            this.choices = choices;
        }
        
        public final String getPragmaName() {
            return this.pragmaName;
        }
    }
    
    public enum Encoding implements PragmaValue
    {
        UTF8("'UTF-8'"), 
        UTF16("'UTF-16'"), 
        UTF16_LITTLE_ENDIAN("'UTF-16le'"), 
        UTF16_BIG_ENDIAN("'UTF-16be'"), 
        UTF_8(Encoding.UTF8), 
        UTF_16(Encoding.UTF16), 
        UTF_16LE(Encoding.UTF16_LITTLE_ENDIAN), 
        UTF_16BE(Encoding.UTF16_BIG_ENDIAN);
        
        public final String typeName;
        
        private Encoding(final String typeName) {
            this.typeName = typeName;
        }
        
        private Encoding(final Encoding encoding) {
            this.typeName = encoding.getValue();
        }
        
        @Override
        public String getValue() {
            return this.typeName;
        }
        
        public static Encoding getEncoding(final String value) {
            return valueOf(value.replaceAll("-", "_").toUpperCase());
        }
    }
    
    public enum JournalMode implements PragmaValue
    {
        DELETE, 
        TRUNCATE, 
        PERSIST, 
        MEMORY, 
        WAL, 
        OFF;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum LockingMode implements PragmaValue
    {
        NORMAL, 
        EXCLUSIVE;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum SynchronousMode implements PragmaValue
    {
        OFF, 
        NORMAL, 
        FULL;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum TempStore implements PragmaValue
    {
        DEFAULT, 
        FILE, 
        MEMORY;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum HexKeyMode implements PragmaValue
    {
        NONE, 
        SSE, 
        SQLCIPHER;
        
        @Override
        public String getValue() {
            return this.name();
        }
    }
    
    public enum TransactionMode implements PragmaValue
    {
        @Deprecated
        DEFFERED, 
        DEFERRED, 
        IMMEDIATE, 
        EXCLUSIVE;
        
        @Override
        public String getValue() {
            return this.name();
        }
        
        public static TransactionMode getMode(final String mode) {
            if ("DEFFERED".equalsIgnoreCase(mode)) {
                return TransactionMode.DEFERRED;
            }
            return valueOf(mode.toUpperCase());
        }
    }
    
    public enum DatePrecision implements PragmaValue
    {
        SECONDS, 
        MILLISECONDS;
        
        @Override
        public String getValue() {
            return this.name();
        }
        
        public static DatePrecision getPrecision(final String precision) {
            return valueOf(precision.toUpperCase());
        }
    }
    
    public enum DateClass implements PragmaValue
    {
        INTEGER, 
        TEXT, 
        REAL;
        
        @Override
        public String getValue() {
            return this.name();
        }
        
        public static DateClass getDateClass(final String dateClass) {
            return valueOf(dateClass.toUpperCase());
        }
    }
    
    private interface PragmaValue
    {
        String getValue();
    }
}
