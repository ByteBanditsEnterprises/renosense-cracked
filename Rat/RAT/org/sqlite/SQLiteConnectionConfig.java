//Raddon On Top!

package org.sqlite;

import org.sqlite.date.*;
import java.util.*;

public class SQLiteConnectionConfig implements Cloneable
{
    private SQLiteConfig.DateClass dateClass;
    private SQLiteConfig.DatePrecision datePrecision;
    private String dateStringFormat;
    private FastDateFormat dateFormat;
    private int transactionIsolation;
    private SQLiteConfig.TransactionMode transactionMode;
    private boolean autoCommit;
    private static final Map<SQLiteConfig.TransactionMode, String> beginCommandMap;
    
    public static SQLiteConnectionConfig fromPragmaTable(final Properties pragmaTable) {
        return new SQLiteConnectionConfig(SQLiteConfig.DateClass.getDateClass(pragmaTable.getProperty(SQLiteConfig.Pragma.DATE_CLASS.pragmaName, SQLiteConfig.DateClass.INTEGER.name())), SQLiteConfig.DatePrecision.getPrecision(pragmaTable.getProperty(SQLiteConfig.Pragma.DATE_PRECISION.pragmaName, SQLiteConfig.DatePrecision.MILLISECONDS.name())), pragmaTable.getProperty(SQLiteConfig.Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss.SSS"), 8, SQLiteConfig.TransactionMode.getMode(pragmaTable.getProperty(SQLiteConfig.Pragma.TRANSACTION_MODE.pragmaName, SQLiteConfig.TransactionMode.DEFERRED.name())), true);
    }
    
    public SQLiteConnectionConfig(final SQLiteConfig.DateClass dateClass, final SQLiteConfig.DatePrecision datePrecision, final String dateStringFormat, final int transactionIsolation, final SQLiteConfig.TransactionMode transactionMode, final boolean autoCommit) {
        this.dateClass = SQLiteConfig.DateClass.INTEGER;
        this.datePrecision = SQLiteConfig.DatePrecision.MILLISECONDS;
        this.dateStringFormat = "yyyy-MM-dd HH:mm:ss.SSS";
        this.dateFormat = FastDateFormat.getInstance(this.dateStringFormat);
        this.transactionIsolation = 8;
        this.transactionMode = SQLiteConfig.TransactionMode.DEFERRED;
        this.autoCommit = true;
        this.setDateClass(dateClass);
        this.setDatePrecision(datePrecision);
        this.setDateStringFormat(dateStringFormat);
        this.setTransactionIsolation(transactionIsolation);
        this.setTransactionMode(transactionMode);
        this.setAutoCommit(autoCommit);
    }
    
    public SQLiteConnectionConfig copyConfig() {
        return new SQLiteConnectionConfig(this.dateClass, this.datePrecision, this.dateStringFormat, this.transactionIsolation, this.transactionMode, this.autoCommit);
    }
    
    public long getDateMultiplier() {
        return (this.datePrecision == SQLiteConfig.DatePrecision.MILLISECONDS) ? 1L : 1000L;
    }
    
    public SQLiteConfig.DateClass getDateClass() {
        return this.dateClass;
    }
    
    public void setDateClass(final SQLiteConfig.DateClass dateClass) {
        this.dateClass = dateClass;
    }
    
    public SQLiteConfig.DatePrecision getDatePrecision() {
        return this.datePrecision;
    }
    
    public void setDatePrecision(final SQLiteConfig.DatePrecision datePrecision) {
        this.datePrecision = datePrecision;
    }
    
    public String getDateStringFormat() {
        return this.dateStringFormat;
    }
    
    public void setDateStringFormat(final String dateStringFormat) {
        this.dateStringFormat = dateStringFormat;
        this.dateFormat = FastDateFormat.getInstance(dateStringFormat);
    }
    
    public FastDateFormat getDateFormat() {
        return this.dateFormat;
    }
    
    public boolean isAutoCommit() {
        return this.autoCommit;
    }
    
    public void setAutoCommit(final boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
    
    public int getTransactionIsolation() {
        return this.transactionIsolation;
    }
    
    public void setTransactionIsolation(final int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }
    
    public SQLiteConfig.TransactionMode getTransactionMode() {
        return this.transactionMode;
    }
    
    public void setTransactionMode(SQLiteConfig.TransactionMode transactionMode) {
        if (transactionMode == SQLiteConfig.TransactionMode.DEFFERED) {
            transactionMode = SQLiteConfig.TransactionMode.DEFERRED;
        }
        this.transactionMode = transactionMode;
    }
    
    String transactionPrefix() {
        return SQLiteConnectionConfig.beginCommandMap.get(this.transactionMode);
    }
    
    static {
        (beginCommandMap = new EnumMap<SQLiteConfig.TransactionMode, String>(SQLiteConfig.TransactionMode.class)).put(SQLiteConfig.TransactionMode.DEFERRED, "begin;");
        SQLiteConnectionConfig.beginCommandMap.put(SQLiteConfig.TransactionMode.IMMEDIATE, "begin immediate;");
        SQLiteConnectionConfig.beginCommandMap.put(SQLiteConfig.TransactionMode.EXCLUSIVE, "begin exclusive;");
    }
}
