//Raddon On Top!

package org.sqlite;

import java.sql.*;

public class SQLiteException extends SQLException
{
    private SQLiteErrorCode resultCode;
    
    public SQLiteException(final String message, final SQLiteErrorCode resultCode) {
        super(message, null, resultCode.code & 0xFF);
        this.resultCode = resultCode;
    }
    
    public SQLiteErrorCode getResultCode() {
        return this.resultCode;
    }
}
