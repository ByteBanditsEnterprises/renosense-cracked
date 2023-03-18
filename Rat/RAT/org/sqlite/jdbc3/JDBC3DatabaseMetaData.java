//Raddon On Top!

package org.sqlite.jdbc3;

import org.sqlite.*;
import org.sqlite.core.*;
import java.util.stream.*;
import org.sqlite.util.*;
import java.sql.*;
import java.io.*;
import java.util.regex.*;
import java.util.*;

public abstract class JDBC3DatabaseMetaData extends CoreDatabaseMetaData
{
    private static String driverName;
    private static String driverVersion;
    protected static final Pattern TYPE_INTEGER;
    protected static final Pattern TYPE_VARCHAR;
    protected static final Pattern TYPE_FLOAT;
    private static final Map<String, Integer> RULE_MAP;
    protected static final Pattern PK_UNNAMED_PATTERN;
    protected static final Pattern PK_NAMED_PATTERN;
    
    protected JDBC3DatabaseMetaData(final SQLiteConnection conn) {
        super(conn);
    }
    
    public Connection getConnection() {
        return this.conn;
    }
    
    public int getDatabaseMajorVersion() throws SQLException {
        return Integer.parseInt(this.conn.libversion().split("\\.")[0]);
    }
    
    public int getDatabaseMinorVersion() throws SQLException {
        return Integer.parseInt(this.conn.libversion().split("\\.")[1]);
    }
    
    public int getDriverMajorVersion() {
        return Integer.parseInt(JDBC3DatabaseMetaData.driverVersion.split("\\.")[0]);
    }
    
    public int getDriverMinorVersion() {
        return Integer.parseInt(JDBC3DatabaseMetaData.driverVersion.split("\\.")[1]);
    }
    
    public int getJDBCMajorVersion() {
        return 4;
    }
    
    public int getJDBCMinorVersion() {
        return 2;
    }
    
    public int getDefaultTransactionIsolation() {
        return 8;
    }
    
    public int getMaxBinaryLiteralLength() {
        return 0;
    }
    
    public int getMaxCatalogNameLength() {
        return 0;
    }
    
    public int getMaxCharLiteralLength() {
        return 0;
    }
    
    public int getMaxColumnNameLength() {
        return 0;
    }
    
    public int getMaxColumnsInGroupBy() {
        return 0;
    }
    
    public int getMaxColumnsInIndex() {
        return 0;
    }
    
    public int getMaxColumnsInOrderBy() {
        return 0;
    }
    
    public int getMaxColumnsInSelect() {
        return 0;
    }
    
    public int getMaxColumnsInTable() {
        return 0;
    }
    
    public int getMaxConnections() {
        return 0;
    }
    
    public int getMaxCursorNameLength() {
        return 0;
    }
    
    public int getMaxIndexLength() {
        return 0;
    }
    
    public int getMaxProcedureNameLength() {
        return 0;
    }
    
    public int getMaxRowSize() {
        return 0;
    }
    
    public int getMaxSchemaNameLength() {
        return 0;
    }
    
    public int getMaxStatementLength() {
        return 0;
    }
    
    public int getMaxStatements() {
        return 0;
    }
    
    public int getMaxTableNameLength() {
        return 0;
    }
    
    public int getMaxTablesInSelect() {
        return 0;
    }
    
    public int getMaxUserNameLength() {
        return 0;
    }
    
    public int getResultSetHoldability() {
        return 2;
    }
    
    public int getSQLStateType() {
        return 2;
    }
    
    public String getDatabaseProductName() {
        return "SQLite";
    }
    
    public String getDatabaseProductVersion() throws SQLException {
        return this.conn.libversion();
    }
    
    public String getDriverName() {
        return JDBC3DatabaseMetaData.driverName;
    }
    
    public String getDriverVersion() {
        return JDBC3DatabaseMetaData.driverVersion;
    }
    
    public String getExtraNameCharacters() {
        return "";
    }
    
    public String getCatalogSeparator() {
        return ".";
    }
    
    public String getCatalogTerm() {
        return "catalog";
    }
    
    public String getSchemaTerm() {
        return "schema";
    }
    
    public String getProcedureTerm() {
        return "not_implemented";
    }
    
    public String getSearchStringEscape() {
        return "\\";
    }
    
    public String getIdentifierQuoteString() {
        return "\"";
    }
    
    public String getSQLKeywords() {
        return "ABORT,ACTION,AFTER,ANALYZE,ATTACH,AUTOINCREMENT,BEFORE,CASCADE,CONFLICT,DATABASE,DEFERRABLE,DEFERRED,DESC,DETACH,EXCLUSIVE,EXPLAIN,FAIL,GLOB,IGNORE,INDEX,INDEXED,INITIALLY,INSTEAD,ISNULL,KEY,LIMIT,NOTNULL,OFFSET,PLAN,PRAGMA,QUERY,RAISE,REGEXP,REINDEX,RENAME,REPLACE,RESTRICT,TEMP,TEMPORARY,TRANSACTION,VACUUM,VIEW,VIRTUAL";
    }
    
    public String getNumericFunctions() {
        return "";
    }
    
    public String getStringFunctions() {
        return "";
    }
    
    public String getSystemFunctions() {
        return "";
    }
    
    public String getTimeDateFunctions() {
        return "DATE,TIME,DATETIME,JULIANDAY,STRFTIME";
    }
    
    public String getURL() {
        return this.conn.getUrl();
    }
    
    public String getUserName() {
        return null;
    }
    
    public boolean allProceduresAreCallable() {
        return false;
    }
    
    public boolean allTablesAreSelectable() {
        return true;
    }
    
    public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }
    
    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }
    
    public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }
    
    public boolean deletesAreDetected(final int type) {
        return false;
    }
    
    public boolean insertsAreDetected(final int type) {
        return false;
    }
    
    public boolean isCatalogAtStart() {
        return true;
    }
    
    public boolean locatorsUpdateCopy() {
        return false;
    }
    
    public boolean nullPlusNonNullIsNull() {
        return true;
    }
    
    public boolean nullsAreSortedAtEnd() {
        return !this.nullsAreSortedAtStart();
    }
    
    public boolean nullsAreSortedAtStart() {
        return true;
    }
    
    public boolean nullsAreSortedHigh() {
        return true;
    }
    
    public boolean nullsAreSortedLow() {
        return !this.nullsAreSortedHigh();
    }
    
    public boolean othersDeletesAreVisible(final int type) {
        return false;
    }
    
    public boolean othersInsertsAreVisible(final int type) {
        return false;
    }
    
    public boolean othersUpdatesAreVisible(final int type) {
        return false;
    }
    
    public boolean ownDeletesAreVisible(final int type) {
        return false;
    }
    
    public boolean ownInsertsAreVisible(final int type) {
        return false;
    }
    
    public boolean ownUpdatesAreVisible(final int type) {
        return false;
    }
    
    public boolean storesLowerCaseIdentifiers() {
        return false;
    }
    
    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }
    
    public boolean storesMixedCaseIdentifiers() {
        return true;
    }
    
    public boolean storesMixedCaseQuotedIdentifiers() {
        return false;
    }
    
    public boolean storesUpperCaseIdentifiers() {
        return false;
    }
    
    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }
    
    public boolean supportsAlterTableWithAddColumn() {
        return false;
    }
    
    public boolean supportsAlterTableWithDropColumn() {
        return false;
    }
    
    public boolean supportsANSI92EntryLevelSQL() {
        return false;
    }
    
    public boolean supportsANSI92FullSQL() {
        return false;
    }
    
    public boolean supportsANSI92IntermediateSQL() {
        return false;
    }
    
    public boolean supportsBatchUpdates() {
        return true;
    }
    
    public boolean supportsCatalogsInDataManipulation() {
        return false;
    }
    
    public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }
    
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }
    
    public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }
    
    public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }
    
    public boolean supportsColumnAliasing() {
        return true;
    }
    
    public boolean supportsConvert() {
        return false;
    }
    
    public boolean supportsConvert(final int fromType, final int toType) {
        return false;
    }
    
    public boolean supportsCorrelatedSubqueries() {
        return false;
    }
    
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return true;
    }
    
    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }
    
    public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }
    
    public boolean supportsExpressionsInOrderBy() {
        return true;
    }
    
    public boolean supportsMinimumSQLGrammar() {
        return true;
    }
    
    public boolean supportsCoreSQLGrammar() {
        return true;
    }
    
    public boolean supportsExtendedSQLGrammar() {
        return false;
    }
    
    public boolean supportsLimitedOuterJoins() {
        return true;
    }
    
    public boolean supportsFullOuterJoins() throws SQLException {
        final String[] version = this.conn.libversion().split("\\.");
        return Integer.parseInt(version[0]) >= 3 && Integer.parseInt(version[1]) >= 39;
    }
    
    public boolean supportsGetGeneratedKeys() {
        return true;
    }
    
    public boolean supportsGroupBy() {
        return true;
    }
    
    public boolean supportsGroupByBeyondSelect() {
        return false;
    }
    
    public boolean supportsGroupByUnrelated() {
        return false;
    }
    
    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }
    
    public boolean supportsLikeEscapeClause() {
        return false;
    }
    
    public boolean supportsMixedCaseIdentifiers() {
        return true;
    }
    
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }
    
    public boolean supportsMultipleOpenResults() {
        return false;
    }
    
    public boolean supportsMultipleResultSets() {
        return false;
    }
    
    public boolean supportsMultipleTransactions() {
        return true;
    }
    
    public boolean supportsNamedParameters() {
        return true;
    }
    
    public boolean supportsNonNullableColumns() {
        return true;
    }
    
    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }
    
    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }
    
    public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }
    
    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }
    
    public boolean supportsOrderByUnrelated() {
        return false;
    }
    
    public boolean supportsOuterJoins() {
        return true;
    }
    
    public boolean supportsPositionedDelete() {
        return false;
    }
    
    public boolean supportsPositionedUpdate() {
        return false;
    }
    
    public boolean supportsResultSetConcurrency(final int t, final int c) {
        return t == 1003 && c == 1007;
    }
    
    public boolean supportsResultSetHoldability(final int h) {
        return h == 2;
    }
    
    public boolean supportsResultSetType(final int t) {
        return t == 1003;
    }
    
    public boolean supportsSavepoints() {
        return true;
    }
    
    public boolean supportsSchemasInDataManipulation() {
        return false;
    }
    
    public boolean supportsSchemasInIndexDefinitions() {
        return false;
    }
    
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return false;
    }
    
    public boolean supportsSchemasInProcedureCalls() {
        return false;
    }
    
    public boolean supportsSchemasInTableDefinitions() {
        return false;
    }
    
    public boolean supportsSelectForUpdate() {
        return false;
    }
    
    public boolean supportsStatementPooling() {
        return false;
    }
    
    public boolean supportsStoredProcedures() {
        return false;
    }
    
    public boolean supportsSubqueriesInComparisons() {
        return false;
    }
    
    public boolean supportsSubqueriesInExists() {
        return true;
    }
    
    public boolean supportsSubqueriesInIns() {
        return true;
    }
    
    public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }
    
    public boolean supportsTableCorrelationNames() {
        return false;
    }
    
    public boolean supportsTransactionIsolationLevel(final int level) {
        return level == 8;
    }
    
    public boolean supportsTransactions() {
        return true;
    }
    
    public boolean supportsUnion() {
        return true;
    }
    
    public boolean supportsUnionAll() {
        return true;
    }
    
    public boolean updatesAreDetected(final int type) {
        return false;
    }
    
    public boolean usesLocalFilePerTable() {
        return false;
    }
    
    public boolean usesLocalFiles() {
        return true;
    }
    
    public boolean isReadOnly() throws SQLException {
        return this.conn.isReadOnly();
    }
    
    public ResultSet getAttributes(final String c, final String s, final String t, final String a) throws SQLException {
        if (this.getAttributes == null) {
            this.getAttributes = this.conn.prepareStatement("select null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME, null as ATTR_NAME, null as DATA_TYPE, null as ATTR_TYPE_NAME, null as ATTR_SIZE, null as DECIMAL_DIGITS, null as NUM_PREC_RADIX, null as NULLABLE, null as REMARKS, null as ATTR_DEF, null as SQL_DATA_TYPE, null as SQL_DATETIME_SUB, null as CHAR_OCTET_LENGTH, null as ORDINAL_POSITION, null as IS_NULLABLE, null as SCOPE_CATALOG, null as SCOPE_SCHEMA, null as SCOPE_TABLE, null as SOURCE_DATA_TYPE limit 0;");
        }
        return this.getAttributes.executeQuery();
    }
    
    public ResultSet getBestRowIdentifier(final String c, final String s, final String t, final int scope, final boolean n) throws SQLException {
        if (this.getBestRowIdentifier == null) {
            this.getBestRowIdentifier = this.conn.prepareStatement("select null as SCOPE, null as COLUMN_NAME, null as DATA_TYPE, null as TYPE_NAME, null as COLUMN_SIZE, null as BUFFER_LENGTH, null as DECIMAL_DIGITS, null as PSEUDO_COLUMN limit 0;");
        }
        return this.getBestRowIdentifier.executeQuery();
    }
    
    public ResultSet getColumnPrivileges(final String c, final String s, final String t, final String colPat) throws SQLException {
        if (this.getColumnPrivileges == null) {
            this.getColumnPrivileges = this.conn.prepareStatement("select null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as COLUMN_NAME, null as GRANTOR, null as GRANTEE, null as PRIVILEGE, null as IS_GRANTABLE limit 0;");
        }
        return this.getColumnPrivileges.executeQuery();
    }
    
    public ResultSet getColumns(final String c, final String s, final String tblNamePattern, final String colNamePattern) throws SQLException {
        this.checkOpen();
        final StringBuilder sql = new StringBuilder(700);
        sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, tblname as TABLE_NAME, ").append("cn as COLUMN_NAME, ct as DATA_TYPE, tn as TYPE_NAME, colSize as COLUMN_SIZE, ").append("2000000000 as BUFFER_LENGTH, colDecimalDigits as DECIMAL_DIGITS, 10   as NUM_PREC_RADIX, ").append("colnullable as NULLABLE, null as REMARKS, colDefault as COLUMN_DEF, ").append("0    as SQL_DATA_TYPE, 0    as SQL_DATETIME_SUB, 2000000000 as CHAR_OCTET_LENGTH, ").append("ordpos as ORDINAL_POSITION, (case colnullable when 0 then 'NO' when 1 then 'YES' else '' end)").append("    as IS_NULLABLE, null as SCOPE_CATALOG, null as SCOPE_SCHEMA, ").append("null as SCOPE_TABLE, null as SOURCE_DATA_TYPE, ").append("(case colautoincrement when 0 then 'NO' when 1 then 'YES' else '' end) as IS_AUTOINCREMENT, ").append("(case colgenerated when 0 then 'NO' when 1 then 'YES' else '' end) as IS_GENERATEDCOLUMN from (");
        boolean colFound = false;
        ResultSet rs = null;
        try {
            rs = this.getTables(c, s, tblNamePattern, null);
            while (rs.next()) {
                final String tableName = rs.getString(3);
                Statement statColAutoinc = this.conn.createStatement();
                ResultSet rsColAutoinc = null;
                boolean isAutoIncrement;
                try {
                    statColAutoinc = this.conn.createStatement();
                    rsColAutoinc = statColAutoinc.executeQuery("SELECT LIKE('%autoincrement%', LOWER(sql)) FROM sqlite_schema WHERE LOWER(name) = LOWER('" + this.escape(tableName) + "') AND TYPE IN ('table', 'view')");
                    rsColAutoinc.next();
                    isAutoIncrement = (rsColAutoinc.getInt(1) == 1);
                }
                finally {
                    if (statColAutoinc != null) {
                        try {
                            statColAutoinc.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                final String pragmaStatement = "PRAGMA table_xinfo('" + this.escape(tableName) + "')";
                final Statement colstat = this.conn.createStatement();
                try {
                    final ResultSet rscol = colstat.executeQuery(pragmaStatement);
                    try {
                        int i = 0;
                        while (rscol.next()) {
                            final String colName = rscol.getString(2);
                            String colType = rscol.getString(3);
                            final String colNotNull = rscol.getString(4);
                            final String colDefault = rscol.getString(5);
                            final boolean isPk = "1".equals(rscol.getString(6));
                            final String colHidden = rscol.getString(7);
                            int colNullable = 2;
                            if (colNotNull != null) {
                                colNullable = (colNotNull.equals("0") ? 1 : 0);
                            }
                            if (colFound) {
                                sql.append(" union all ");
                            }
                            colFound = true;
                            int iColumnSize = 2000000000;
                            int iDecimalDigits = 10;
                            colType = ((colType == null) ? "TEXT" : colType.toUpperCase());
                            int colAutoIncrement = 0;
                            if (isPk && isAutoIncrement) {
                                colAutoIncrement = 1;
                            }
                            int colJavaType;
                            if (JDBC3DatabaseMetaData.TYPE_INTEGER.matcher(colType).find()) {
                                colJavaType = 4;
                                iDecimalDigits = 0;
                            }
                            else if (JDBC3DatabaseMetaData.TYPE_VARCHAR.matcher(colType).find()) {
                                colJavaType = 12;
                                iDecimalDigits = 0;
                            }
                            else if (JDBC3DatabaseMetaData.TYPE_FLOAT.matcher(colType).find()) {
                                colJavaType = 6;
                            }
                            else {
                                colJavaType = 12;
                            }
                            final int iStartOfDimension = colType.indexOf(40);
                            if (iStartOfDimension > 0) {
                                final int iEndOfDimension = colType.indexOf(41, iStartOfDimension);
                                if (iEndOfDimension > 0) {
                                    final int iDimensionSeparator = colType.indexOf(44, iStartOfDimension);
                                    String sInteger;
                                    String sDecimal;
                                    if (iDimensionSeparator > 0) {
                                        sInteger = colType.substring(iStartOfDimension + 1, iDimensionSeparator);
                                        sDecimal = colType.substring(iDimensionSeparator + 1, iEndOfDimension);
                                    }
                                    else {
                                        sInteger = colType.substring(iStartOfDimension + 1, iEndOfDimension);
                                        sDecimal = null;
                                    }
                                    try {
                                        final int iInteger = Integer.parseUnsignedInt(sInteger);
                                        if (sDecimal != null) {
                                            iDecimalDigits = Integer.parseUnsignedInt(sDecimal);
                                            iColumnSize = iInteger + iDecimalDigits;
                                        }
                                        else {
                                            iDecimalDigits = 0;
                                            iColumnSize = iInteger;
                                        }
                                    }
                                    catch (NumberFormatException ex) {}
                                }
                            }
                            final int colGenerated = "2".equals(colHidden) ? 1 : 0;
                            sql.append("select ").append(i + 1).append(" as ordpos, ").append(colNullable).append(" as colnullable,").append("'").append(colJavaType).append("' as ct, ").append(iColumnSize).append(" as colSize, ").append(iDecimalDigits).append(" as colDecimalDigits, ").append("'").append(tableName).append("' as tblname, ").append("'").append(this.escape(colName)).append("' as cn, ").append("'").append(this.escape(colType)).append("' as tn, ").append(quote((colDefault == null) ? null : this.escape(colDefault))).append(" as colDefault,").append(colAutoIncrement).append(" as colautoincrement,").append(colGenerated).append(" as colgenerated");
                            if (colNamePattern != null) {
                                sql.append(" where upper(cn) like upper('").append(this.escape(colNamePattern)).append("') ESCAPE '").append(this.getSearchStringEscape()).append("'");
                            }
                            ++i;
                        }
                        if (rscol != null) {
                            rscol.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rscol != null) {
                            try {
                                rscol.close();
                            }
                            catch (Throwable t2) {
                                t.addSuppressed(t2);
                            }
                        }
                        throw t;
                    }
                    if (colstat == null) {
                        continue;
                    }
                    colstat.close();
                }
                catch (Throwable t3) {
                    if (colstat != null) {
                        try {
                            colstat.close();
                        }
                        catch (Throwable t4) {
                            t3.addSuppressed(t4);
                        }
                    }
                    throw t3;
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (colFound) {
            sql.append(") order by TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION;");
        }
        else {
            sql.append("select null as ordpos, null as colnullable, null as ct, null as colsize, null as colDecimalDigits, null as tblname, null as cn, null as tn, null as colDefault, null as colautoincrement, null as colgenerated) limit 0;");
        }
        final Statement stat = this.conn.createStatement();
        return ((CoreStatement)stat).executeQuery(sql.toString(), true);
    }
    
    public ResultSet getCrossReference(final String pc, final String ps, final String pt, final String fc, final String fs, final String ft) throws SQLException {
        if (pt == null) {
            return this.getExportedKeys(fc, fs, ft);
        }
        if (ft == null) {
            return this.getImportedKeys(pc, ps, pt);
        }
        final String query = "select " + quote(pc) + " as PKTABLE_CAT, " + quote(ps) + " as PKTABLE_SCHEM, " + quote(pt) + " as PKTABLE_NAME, '' as PKCOLUMN_NAME, " + quote(fc) + " as FKTABLE_CAT, " + quote(fs) + " as FKTABLE_SCHEM, " + quote(ft) + " as FKTABLE_NAME, '' as FKCOLUMN_NAME, -1 as KEY_SEQ, 3 as UPDATE_RULE, 3 as DELETE_RULE, '' as FK_NAME, '' as PK_NAME, " + 5 + " as DEFERRABILITY limit 0 ";
        return ((CoreStatement)this.conn.createStatement()).executeQuery(query, true);
    }
    
    public ResultSet getSchemas() throws SQLException {
        if (this.getSchemas == null) {
            this.getSchemas = this.conn.prepareStatement("select null as TABLE_SCHEM, null as TABLE_CATALOG limit 0;");
        }
        return this.getSchemas.executeQuery();
    }
    
    public ResultSet getCatalogs() throws SQLException {
        if (this.getCatalogs == null) {
            this.getCatalogs = this.conn.prepareStatement("select null as TABLE_CAT limit 0;");
        }
        return this.getCatalogs.executeQuery();
    }
    
    public ResultSet getPrimaryKeys(final String c, final String s, final String table) throws SQLException {
        final PrimaryKeyFinder pkFinder = new PrimaryKeyFinder(table);
        final String[] columns = pkFinder.getColumns();
        final Statement stat = this.conn.createStatement();
        final StringBuilder sql = new StringBuilder(512);
        sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, '").append(this.escape(table)).append("' as TABLE_NAME, cn as COLUMN_NAME, ks as KEY_SEQ, pk as PK_NAME from (");
        if (columns == null) {
            sql.append("select null as cn, null as pk, 0 as ks) limit 0;");
            return ((CoreStatement)stat).executeQuery(sql.toString(), true);
        }
        String pkName = pkFinder.getName();
        if (pkName != null) {
            pkName = "'" + pkName + "'";
        }
        for (int i = 0; i < columns.length; ++i) {
            if (i > 0) {
                sql.append(" union ");
            }
            sql.append("select ").append(pkName).append(" as pk, '").append(this.escape(this.unquoteIdentifier(columns[i]))).append("' as cn, ").append(i + 1).append(" as ks");
        }
        return ((CoreStatement)stat).executeQuery(sql.append(") order by cn;").toString(), true);
    }
    
    public ResultSet getExportedKeys(String catalog, String schema, final String table) throws SQLException {
        final PrimaryKeyFinder pkFinder = new PrimaryKeyFinder(table);
        final String[] pkColumns = pkFinder.getColumns();
        final Statement stat = this.conn.createStatement();
        catalog = ((catalog != null) ? quote(catalog) : null);
        schema = ((schema != null) ? quote(schema) : null);
        final StringBuilder exportedKeysQuery = new StringBuilder(512);
        String target = null;
        int count = 0;
        if (pkColumns != null) {
            final ResultSet rs = stat.executeQuery("select name from sqlite_schema where type = 'table'");
            ArrayList<String> tableList;
            try {
                tableList = new ArrayList<String>();
                while (rs.next()) {
                    final String tblname = rs.getString(1);
                    tableList.add(tblname);
                    if (tblname.equalsIgnoreCase(table)) {
                        target = tblname;
                    }
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (Throwable t) {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                throw t;
            }
            for (final String tbl : tableList) {
                final ImportedKeyFinder impFkFinder = new ImportedKeyFinder(tbl);
                final List<ImportedKeyFinder.ForeignKey> fkNames = impFkFinder.getFkList();
                for (final ImportedKeyFinder.ForeignKey foreignKey : fkNames) {
                    final String PKTabName = foreignKey.getPkTableName();
                    if (PKTabName != null) {
                        if (!PKTabName.equalsIgnoreCase(target)) {
                            continue;
                        }
                        for (int j = 0; j < foreignKey.getColumnMappingCount(); ++j) {
                            final int keySeq = j + 1;
                            final String[] columnMapping = foreignKey.getColumnMapping(j);
                            String PKColName = columnMapping[1];
                            PKColName = ((PKColName == null) ? "" : PKColName);
                            String FKColName = columnMapping[0];
                            FKColName = ((FKColName == null) ? "" : FKColName);
                            boolean usePkName = false;
                            for (final String pkColumn : pkColumns) {
                                if (pkColumn != null && pkColumn.equalsIgnoreCase(PKColName)) {
                                    usePkName = true;
                                    break;
                                }
                            }
                            final String pkName = (usePkName && pkFinder.getName() != null) ? pkFinder.getName() : "";
                            exportedKeysQuery.append((count > 0) ? " union all select " : "select ").append(keySeq).append(" as ks, '").append(this.escape(tbl)).append("' as fkt, '").append(this.escape(FKColName)).append("' as fcn, '").append(this.escape(PKColName)).append("' as pcn, '").append(this.escape(pkName)).append("' as pkn, ").append(JDBC3DatabaseMetaData.RULE_MAP.get(foreignKey.getOnUpdate())).append(" as ur, ").append(JDBC3DatabaseMetaData.RULE_MAP.get(foreignKey.getOnDelete())).append(" as dr, ");
                            final String fkName = foreignKey.getFkName();
                            if (fkName != null) {
                                exportedKeysQuery.append("'").append(this.escape(fkName)).append("' as fkn");
                            }
                            else {
                                exportedKeysQuery.append("'' as fkn");
                            }
                            ++count;
                        }
                    }
                }
            }
        }
        final boolean hasImportedKey = count > 0;
        final StringBuilder sql = new StringBuilder(512);
        sql.append("select ").append(catalog).append(" as PKTABLE_CAT, ").append(schema).append(" as PKTABLE_SCHEM, ").append(quote(target)).append(" as PKTABLE_NAME, ").append(hasImportedKey ? "pcn" : "''").append(" as PKCOLUMN_NAME, ").append(catalog).append(" as FKTABLE_CAT, ").append(schema).append(" as FKTABLE_SCHEM, ").append(hasImportedKey ? "fkt" : "''").append(" as FKTABLE_NAME, ").append(hasImportedKey ? "fcn" : "''").append(" as FKCOLUMN_NAME, ").append(hasImportedKey ? "ks" : "-1").append(" as KEY_SEQ, ").append(hasImportedKey ? "ur" : "3").append(" as UPDATE_RULE, ").append(hasImportedKey ? "dr" : "3").append(" as DELETE_RULE, ").append(hasImportedKey ? "fkn" : "''").append(" as FK_NAME, ").append(hasImportedKey ? "pkn" : "''").append(" as PK_NAME, ").append(5).append(" as DEFERRABILITY ");
        if (hasImportedKey) {
            sql.append("from (").append((CharSequence)exportedKeysQuery).append(") ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, KEY_SEQ");
        }
        else {
            sql.append("limit 0");
        }
        return ((CoreStatement)stat).executeQuery(sql.toString(), true);
    }
    
    private StringBuilder appendDummyForeignKeyList(final StringBuilder sql) {
        sql.append("select -1 as ks, '' as ptn, '' as fcn, '' as pcn, ").append(3).append(" as ur, ").append(3).append(" as dr, ").append(" '' as fkn, ").append(" '' as pkn ").append(") limit 0;");
        return sql;
    }
    
    public ResultSet getImportedKeys(final String catalog, final String schema, final String table) throws SQLException {
        final Statement stat = this.conn.createStatement();
        StringBuilder sql = new StringBuilder(700);
        sql.append("select ").append(quote(catalog)).append(" as PKTABLE_CAT, ").append(quote(schema)).append(" as PKTABLE_SCHEM, ").append("ptn as PKTABLE_NAME, pcn as PKCOLUMN_NAME, ").append(quote(catalog)).append(" as FKTABLE_CAT, ").append(quote(schema)).append(" as FKTABLE_SCHEM, ").append(quote(table)).append(" as FKTABLE_NAME, ").append("fcn as FKCOLUMN_NAME, ks as KEY_SEQ, ur as UPDATE_RULE, dr as DELETE_RULE, fkn as FK_NAME, pkn as PK_NAME, ").append(5).append(" as DEFERRABILITY from (");
        ResultSet rs;
        try {
            rs = stat.executeQuery("pragma foreign_key_list('" + this.escape(table) + "');");
        }
        catch (SQLException e) {
            sql = this.appendDummyForeignKeyList(sql);
            return ((CoreStatement)stat).executeQuery(sql.toString(), true);
        }
        final ImportedKeyFinder impFkFinder = new ImportedKeyFinder(table);
        final List<ImportedKeyFinder.ForeignKey> fkNames = impFkFinder.getFkList();
        int i = 0;
        while (rs.next()) {
            final int keySeq = rs.getInt(2) + 1;
            final int keyId = rs.getInt(1);
            final String PKTabName = rs.getString(3);
            final String FKColName = rs.getString(4);
            String PKColName = rs.getString(5);
            String pkName = null;
            try {
                final PrimaryKeyFinder pkFinder = new PrimaryKeyFinder(PKTabName);
                pkName = pkFinder.getName();
                if (PKColName == null) {
                    PKColName = pkFinder.getColumns()[0];
                }
            }
            catch (SQLException ex) {}
            final String updateRule = rs.getString(6);
            final String deleteRule = rs.getString(7);
            if (i > 0) {
                sql.append(" union all ");
            }
            String fkName = null;
            if (fkNames.size() > keyId) {
                fkName = fkNames.get(keyId).getFkName();
            }
            sql.append("select ").append(keySeq).append(" as ks,").append("'").append(this.escape(PKTabName)).append("' as ptn, '").append(this.escape(FKColName)).append("' as fcn, '").append(this.escape(PKColName)).append("' as pcn,").append("case '").append(this.escape(updateRule)).append("'").append(" when 'NO ACTION' then ").append(3).append(" when 'CASCADE' then ").append(0).append(" when 'RESTRICT' then ").append(1).append(" when 'SET NULL' then ").append(2).append(" when 'SET DEFAULT' then ").append(4).append(" end as ur, ").append("case '").append(this.escape(deleteRule)).append("'").append(" when 'NO ACTION' then ").append(3).append(" when 'CASCADE' then ").append(0).append(" when 'RESTRICT' then ").append(1).append(" when 'SET NULL' then ").append(2).append(" when 'SET DEFAULT' then ").append(4).append(" end as dr, ").append((fkName == null) ? "''" : quote(fkName)).append(" as fkn, ").append((pkName == null) ? "''" : quote(pkName)).append(" as pkn");
            ++i;
        }
        rs.close();
        if (i == 0) {
            sql = this.appendDummyForeignKeyList(sql);
        }
        else {
            sql.append(") ORDER BY PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, KEY_SEQ;");
        }
        return ((CoreStatement)stat).executeQuery(sql.toString(), true);
    }
    
    public ResultSet getIndexInfo(final String c, final String s, final String table, final boolean u, final boolean approximate) throws SQLException {
        final Statement stat = this.conn.createStatement();
        final StringBuilder sql = new StringBuilder(500);
        sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, '").append(this.escape(table)).append("' as TABLE_NAME, un as NON_UNIQUE, null as INDEX_QUALIFIER, n as INDEX_NAME, ").append(Integer.toString(3)).append(" as TYPE, op as ORDINAL_POSITION, ").append("cn as COLUMN_NAME, null as ASC_OR_DESC, 0 as CARDINALITY, 0 as PAGES, null as FILTER_CONDITION from (");
        ResultSet rs = stat.executeQuery("pragma index_list('" + this.escape(table) + "');");
        final ArrayList<ArrayList<Object>> indexList = new ArrayList<ArrayList<Object>>();
        while (rs.next()) {
            indexList.add(new ArrayList<Object>());
            indexList.get(indexList.size() - 1).add(rs.getString(2));
            indexList.get(indexList.size() - 1).add(rs.getInt(3));
        }
        rs.close();
        if (indexList.size() == 0) {
            sql.append("select null as un, null as n, null as op, null as cn) limit 0;");
            return ((CoreStatement)stat).executeQuery(sql.toString(), true);
        }
        final Iterator<ArrayList<Object>> indexIterator = indexList.iterator();
        final ArrayList<String> unionAll = new ArrayList<String>();
        while (indexIterator.hasNext()) {
            final ArrayList<Object> currentIndex = indexIterator.next();
            final String indexName = currentIndex.get(0).toString();
            rs = stat.executeQuery("pragma index_info('" + this.escape(indexName) + "');");
            while (rs.next()) {
                final StringBuilder sqlRow = new StringBuilder();
                final String colName = rs.getString(3);
                sqlRow.append("select ").append(1 - currentIndex.get(1)).append(" as un,'").append(this.escape(indexName)).append("' as n,").append(rs.getInt(1) + 1).append(" as op,");
                if (colName == null) {
                    sqlRow.append("null");
                }
                else {
                    sqlRow.append("'").append(this.escape(colName)).append("'");
                }
                sqlRow.append(" as cn");
                unionAll.add(sqlRow.toString());
            }
            rs.close();
        }
        final String sqlBlock = StringUtils.join(unionAll, " union all ");
        return ((CoreStatement)stat).executeQuery(sql.append(sqlBlock).append(");").toString(), true);
    }
    
    public ResultSet getProcedureColumns(final String c, final String s, final String p, final String colPat) throws SQLException {
        if (this.getProcedureColumns == null) {
            this.getProcedureColumns = this.conn.prepareStatement("select null as PROCEDURE_CAT, null as PROCEDURE_SCHEM, null as PROCEDURE_NAME, null as COLUMN_NAME, null as COLUMN_TYPE, null as DATA_TYPE, null as TYPE_NAME, null as PRECISION, null as LENGTH, null as SCALE, null as RADIX, null as NULLABLE, null as REMARKS limit 0;");
        }
        return this.getProcedureColumns.executeQuery();
    }
    
    public ResultSet getProcedures(final String c, final String s, final String p) throws SQLException {
        if (this.getProcedures == null) {
            this.getProcedures = this.conn.prepareStatement("select null as PROCEDURE_CAT, null as PROCEDURE_SCHEM, null as PROCEDURE_NAME, null as UNDEF1, null as UNDEF2, null as UNDEF3, null as REMARKS, null as PROCEDURE_TYPE limit 0;");
        }
        return this.getProcedures.executeQuery();
    }
    
    public ResultSet getSuperTables(final String c, final String s, final String t) throws SQLException {
        if (this.getSuperTables == null) {
            this.getSuperTables = this.conn.prepareStatement("select null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as SUPERTABLE_NAME limit 0;");
        }
        return this.getSuperTables.executeQuery();
    }
    
    public ResultSet getSuperTypes(final String c, final String s, final String t) throws SQLException {
        if (this.getSuperTypes == null) {
            this.getSuperTypes = this.conn.prepareStatement("select null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME, null as SUPERTYPE_CAT, null as SUPERTYPE_SCHEM, null as SUPERTYPE_NAME limit 0;");
        }
        return this.getSuperTypes.executeQuery();
    }
    
    public ResultSet getTablePrivileges(final String c, final String s, final String t) throws SQLException {
        if (this.getTablePrivileges == null) {
            this.getTablePrivileges = this.conn.prepareStatement("select  null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as GRANTOR, null GRANTEE,  null as PRIVILEGE, null as IS_GRANTABLE limit 0;");
        }
        return this.getTablePrivileges.executeQuery();
    }
    
    public synchronized ResultSet getTables(final String c, final String s, String tblNamePattern, final String[] types) throws SQLException {
        this.checkOpen();
        tblNamePattern = ((tblNamePattern == null || "".equals(tblNamePattern)) ? "%" : this.escape(tblNamePattern));
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT").append("\n");
        sql.append("  NULL AS TABLE_CAT,").append("\n");
        sql.append("  NULL AS TABLE_SCHEM,").append("\n");
        sql.append("  NAME AS TABLE_NAME,").append("\n");
        sql.append("  TYPE AS TABLE_TYPE,").append("\n");
        sql.append("  NULL AS REMARKS,").append("\n");
        sql.append("  NULL AS TYPE_CAT,").append("\n");
        sql.append("  NULL AS TYPE_SCHEM,").append("\n");
        sql.append("  NULL AS TYPE_NAME,").append("\n");
        sql.append("  NULL AS SELF_REFERENCING_COL_NAME,").append("\n");
        sql.append("  NULL AS REF_GENERATION").append("\n");
        sql.append("FROM").append("\n");
        sql.append("  (").append("\n");
        sql.append("    SELECT\n");
        sql.append("      'sqlite_schema' AS NAME,\n");
        sql.append("      'SYSTEM TABLE' AS TYPE");
        sql.append("    UNION ALL").append("\n");
        sql.append("    SELECT").append("\n");
        sql.append("      NAME,").append("\n");
        sql.append("      UPPER(TYPE) AS TYPE").append("\n");
        sql.append("    FROM").append("\n");
        sql.append("      sqlite_schema").append("\n");
        sql.append("    WHERE").append("\n");
        sql.append("      NAME NOT LIKE 'sqlite\\_%' ESCAPE '\\'").append("\n");
        sql.append("      AND UPPER(TYPE) IN ('TABLE', 'VIEW')").append("\n");
        sql.append("    UNION ALL").append("\n");
        sql.append("    SELECT").append("\n");
        sql.append("      NAME,").append("\n");
        sql.append("      'GLOBAL TEMPORARY' AS TYPE").append("\n");
        sql.append("    FROM").append("\n");
        sql.append("      sqlite_temp_master").append("\n");
        sql.append("    UNION ALL").append("\n");
        sql.append("    SELECT").append("\n");
        sql.append("      NAME,").append("\n");
        sql.append("      'SYSTEM TABLE' AS TYPE").append("\n");
        sql.append("    FROM").append("\n");
        sql.append("      sqlite_schema").append("\n");
        sql.append("    WHERE").append("\n");
        sql.append("      NAME LIKE 'sqlite\\_%' ESCAPE '\\'").append("\n");
        sql.append("  )").append("\n");
        sql.append(" WHERE TABLE_NAME LIKE '");
        sql.append(tblNamePattern);
        sql.append("' ESCAPE '");
        sql.append(this.getSearchStringEscape());
        sql.append("'");
        if (types != null && types.length != 0) {
            sql.append(" AND TABLE_TYPE IN (");
            sql.append(Arrays.stream(types).map(t -> "'" + t.toUpperCase() + "'").collect((Collector<? super Object, ?, String>)Collectors.joining(",")));
            sql.append(")");
        }
        sql.append(" ORDER BY TABLE_TYPE, TABLE_NAME;");
        return ((CoreStatement)this.conn.createStatement()).executeQuery(sql.toString(), true);
    }
    
    public ResultSet getTableTypes() throws SQLException {
        this.checkOpen();
        final String sql = "SELECT 'TABLE' AS TABLE_TYPE UNION SELECT 'VIEW' AS TABLE_TYPE UNION SELECT 'SYSTEM TABLE' AS TABLE_TYPE UNION SELECT 'GLOBAL TEMPORARY' AS TABLE_TYPE;";
        if (this.getTableTypes == null) {
            this.getTableTypes = this.conn.prepareStatement(sql);
        }
        this.getTableTypes.clearParameters();
        return this.getTableTypes.executeQuery();
    }
    
    public ResultSet getTypeInfo() throws SQLException {
        if (this.getTypeInfo == null) {
            final String sql = QueryUtils.valuesQuery(Arrays.asList("TYPE_NAME", "DATA_TYPE", "PRECISION", "LITERAL_PREFIX", "LITERAL_SUFFIX", "CREATE_PARAMS", "NULLABLE", "CASE_SENSITIVE", "SEARCHABLE", "UNSIGNED_ATTRIBUTE", "FIXED_PREC_SCALE", "AUTO_INCREMENT", "LOCAL_TYPE_NAME", "MINIMUM_SCALE", "MAXIMUM_SCALE", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "NUM_PREC_RADIX"), Arrays.asList(Arrays.asList("BLOB", 2004, 0, null, null, null, 1, 0, 3, 1, 0, 0, null, 0, 0, 0, 0, 10), Arrays.asList("INTEGER", 4, 0, null, null, null, 1, 0, 3, 0, 0, 1, null, 0, 0, 0, 0, 10), Arrays.asList("NULL", 0, 0, null, null, null, 1, 0, 3, 1, 0, 0, null, 0, 0, 0, 0, 10), Arrays.asList("REAL", 7, 0, null, null, null, 1, 0, 3, 0, 0, 0, null, 0, 0, 0, 0, 10), Arrays.asList("TEXT", 12, 0, null, null, null, 1, 1, 3, 1, 0, 0, null, 0, 0, 0, 0, 10))) + " order by DATA_TYPE";
            this.getTypeInfo = this.conn.prepareStatement(sql);
        }
        this.getTypeInfo.clearParameters();
        return this.getTypeInfo.executeQuery();
    }
    
    public ResultSet getUDTs(final String c, final String s, final String t, final int[] types) throws SQLException {
        if (this.getUDTs == null) {
            this.getUDTs = this.conn.prepareStatement("select  null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME,  null as CLASS_NAME,  null as DATA_TYPE, null as REMARKS, null as BASE_TYPE limit 0;");
        }
        this.getUDTs.clearParameters();
        return this.getUDTs.executeQuery();
    }
    
    public ResultSet getVersionColumns(final String c, final String s, final String t) throws SQLException {
        if (this.getVersionColumns == null) {
            this.getVersionColumns = this.conn.prepareStatement("select null as SCOPE, null as COLUMN_NAME, null as DATA_TYPE, null as TYPE_NAME, null as COLUMN_SIZE, null as BUFFER_LENGTH, null as DECIMAL_DIGITS, null as PSEUDO_COLUMN limit 0;");
        }
        return this.getVersionColumns.executeQuery();
    }
    
    public ResultSet getGeneratedKeys() throws SQLException {
        if (this.getGeneratedKeys == null) {
            this.getGeneratedKeys = this.conn.prepareStatement("select last_insert_rowid();");
        }
        return this.getGeneratedKeys.executeQuery();
    }
    
    public Struct createStruct(final String t, final Object[] attr) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not yet implemented by SQLite JDBC driver");
    }
    
    public ResultSet getFunctionColumns(final String a, final String b, final String c, final String d) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not yet implemented by SQLite JDBC driver");
    }
    
    protected void finalize() throws Throwable {
        this.close();
    }
    
    private String unquoteIdentifier(String name) {
        if (name == null) {
            return name;
        }
        name = name.trim();
        if (name.length() > 2 && ((name.startsWith("`") && name.endsWith("`")) || (name.startsWith("\"") && name.endsWith("\"")) || (name.startsWith("[") && name.endsWith("]")))) {
            name = name.substring(1, name.length() - 1);
        }
        return name;
    }
    
    static /* synthetic */ String access$100(final JDBC3DatabaseMetaData x0, final String x1) {
        return x0.escape(x1);
    }
    
    static /* synthetic */ String access$200(final JDBC3DatabaseMetaData x0, final String x1) {
        return x0.escape(x1);
    }
    
    static /* synthetic */ String access$400(final JDBC3DatabaseMetaData x0, final String x1) {
        return x0.escape(x1);
    }
    
    static /* synthetic */ String access$600(final JDBC3DatabaseMetaData x0, final String x1) {
        return x0.escape(x1);
    }
    
    static /* synthetic */ String access$800(final JDBC3DatabaseMetaData x0, final String x1) {
        return x0.escape(x1);
    }
    
    static {
        try {
            final InputStream sqliteJdbcPropStream = JDBC3DatabaseMetaData.class.getClassLoader().getResourceAsStream("sqlite-jdbc.properties");
            try {
                if (sqliteJdbcPropStream == null) {
                    throw new IOException("Cannot load sqlite-jdbc.properties from jar");
                }
                final Properties sqliteJdbcProp = new Properties();
                sqliteJdbcProp.load(sqliteJdbcPropStream);
                JDBC3DatabaseMetaData.driverName = sqliteJdbcProp.getProperty("name");
                JDBC3DatabaseMetaData.driverVersion = sqliteJdbcProp.getProperty("version");
                if (sqliteJdbcPropStream != null) {
                    sqliteJdbcPropStream.close();
                }
            }
            catch (Throwable t) {
                if (sqliteJdbcPropStream != null) {
                    try {
                        sqliteJdbcPropStream.close();
                    }
                    catch (Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                throw t;
            }
        }
        catch (Exception e) {
            JDBC3DatabaseMetaData.driverName = "SQLite JDBC";
            JDBC3DatabaseMetaData.driverVersion = "3.0.0-UNKNOWN";
        }
        TYPE_INTEGER = Pattern.compile(".*(INT|BOOL).*");
        TYPE_VARCHAR = Pattern.compile(".*(CHAR|CLOB|TEXT|BLOB).*");
        TYPE_FLOAT = Pattern.compile(".*(REAL|FLOA|DOUB|DEC|NUM).*");
        (RULE_MAP = new HashMap<String, Integer>()).put("NO ACTION", 3);
        JDBC3DatabaseMetaData.RULE_MAP.put("CASCADE", 0);
        JDBC3DatabaseMetaData.RULE_MAP.put("RESTRICT", 1);
        JDBC3DatabaseMetaData.RULE_MAP.put("SET NULL", 2);
        JDBC3DatabaseMetaData.RULE_MAP.put("SET DEFAULT", 4);
        PK_UNNAMED_PATTERN = Pattern.compile(".*PRIMARY\\s+KEY\\s*\\((.*?)\\).*", 34);
        PK_NAMED_PATTERN = Pattern.compile(".*CONSTRAINT\\s*(.*?)\\s*PRIMARY\\s+KEY\\s*\\((.*?)\\).*", 34);
    }
    
    class PrimaryKeyFinder
    {
        String table;
        String pkName;
        String[] pkColumns;
        
        public PrimaryKeyFinder(final String table) throws SQLException {
            this.pkName = null;
            this.pkColumns = null;
            this.table = table;
            if ("sqlite_schema".equals(table) || "sqlite_master".equals(table)) {
                return;
            }
            if (table == null || table.trim().length() == 0) {
                throw new SQLException("Invalid table name: '" + this.table + "'");
            }
            final Statement stat = JDBC3DatabaseMetaData.this.conn.createStatement();
            try {
                final ResultSet rs = stat.executeQuery("select sql from sqlite_schema where lower(name) = lower('" + JDBC3DatabaseMetaData.access$100(JDBC3DatabaseMetaData.this, table) + "') and type in ('table', 'view')");
                try {
                    if (!rs.next()) {
                        throw new SQLException("Table not found: '" + table + "'");
                    }
                    Matcher matcher = JDBC3DatabaseMetaData.PK_NAMED_PATTERN.matcher(rs.getString(1));
                    if (matcher.find()) {
                        this.pkName = JDBC3DatabaseMetaData.this.unquoteIdentifier(JDBC3DatabaseMetaData.access$200(JDBC3DatabaseMetaData.this, matcher.group(1)));
                        this.pkColumns = matcher.group(2).split(",");
                    }
                    else {
                        matcher = JDBC3DatabaseMetaData.PK_UNNAMED_PATTERN.matcher(rs.getString(1));
                        if (matcher.find()) {
                            this.pkColumns = matcher.group(1).split(",");
                        }
                    }
                    if (this.pkColumns == null) {
                        final ResultSet rs2 = stat.executeQuery("pragma table_info('" + JDBC3DatabaseMetaData.access$400(JDBC3DatabaseMetaData.this, table) + "');");
                        try {
                            while (rs2.next()) {
                                if (rs2.getBoolean(6)) {
                                    this.pkColumns = new String[] { rs2.getString(2) };
                                }
                            }
                            if (rs2 != null) {
                                rs2.close();
                            }
                        }
                        catch (Throwable t) {
                            if (rs2 != null) {
                                try {
                                    rs2.close();
                                }
                                catch (Throwable t2) {
                                    t.addSuppressed(t2);
                                }
                            }
                            throw t;
                        }
                    }
                    if (this.pkColumns != null) {
                        for (int i = 0; i < this.pkColumns.length; ++i) {
                            this.pkColumns[i] = JDBC3DatabaseMetaData.this.unquoteIdentifier(this.pkColumns[i]);
                        }
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (Throwable t3) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable t4) {
                            t3.addSuppressed(t4);
                        }
                    }
                    throw t3;
                }
                if (stat != null) {
                    stat.close();
                }
            }
            catch (Throwable t5) {
                if (stat != null) {
                    try {
                        stat.close();
                    }
                    catch (Throwable t6) {
                        t5.addSuppressed(t6);
                    }
                }
                throw t5;
            }
        }
        
        public String getName() {
            return this.pkName;
        }
        
        public String[] getColumns() {
            return this.pkColumns;
        }
    }
    
    class ImportedKeyFinder
    {
        private final Pattern FK_NAMED_PATTERN;
        private final String fkTableName;
        private final List<ForeignKey> fkList;
        
        public ImportedKeyFinder(final String table) throws SQLException {
            this.FK_NAMED_PATTERN = Pattern.compile("CONSTRAINT\\s*\"?([A-Za-z_][A-Za-z\\d_]*)?\"?\\s*FOREIGN\\s+KEY\\s*\\((.*?)\\)", 34);
            this.fkList = new ArrayList<ForeignKey>();
            if (table == null || table.trim().length() == 0) {
                throw new SQLException("Invalid table name: '" + table + "'");
            }
            this.fkTableName = table;
            final List<String> fkNames = this.getForeignKeyNames(this.fkTableName);
            final Statement stat = JDBC3DatabaseMetaData.this.conn.createStatement();
            try {
                final ResultSet rs = stat.executeQuery("pragma foreign_key_list('" + JDBC3DatabaseMetaData.access$600(JDBC3DatabaseMetaData.this, this.fkTableName.toLowerCase()) + "')");
                try {
                    int prevFkId = -1;
                    int count = 0;
                    ForeignKey fk = null;
                    while (rs.next()) {
                        final int fkId = rs.getInt(1);
                        final String pkTableName = rs.getString(3);
                        final String fkColName = rs.getString(4);
                        final String pkColName = rs.getString(5);
                        final String onUpdate = rs.getString(6);
                        final String onDelete = rs.getString(7);
                        final String match = rs.getString(8);
                        String fkName = null;
                        if (fkNames.size() > count) {
                            fkName = fkNames.get(count);
                        }
                        if (fkId != prevFkId) {
                            fk = new ForeignKey(fkName, pkTableName, this.fkTableName, onUpdate, onDelete, match);
                            this.fkList.add(fk);
                            prevFkId = fkId;
                            ++count;
                        }
                        if (fk != null) {
                            fk.addColumnMapping(fkColName, pkColName);
                        }
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (Throwable t) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                    }
                    throw t;
                }
                if (stat != null) {
                    stat.close();
                }
            }
            catch (Throwable t3) {
                if (stat != null) {
                    try {
                        stat.close();
                    }
                    catch (Throwable t4) {
                        t3.addSuppressed(t4);
                    }
                }
                throw t3;
            }
        }
        
        private List<String> getForeignKeyNames(final String tbl) throws SQLException {
            final List<String> fkNames = new ArrayList<String>();
            if (tbl == null) {
                return fkNames;
            }
            final Statement stat2 = JDBC3DatabaseMetaData.this.conn.createStatement();
            try {
                final ResultSet rs = stat2.executeQuery("select sql from sqlite_schema where lower(name) = lower('" + JDBC3DatabaseMetaData.access$800(JDBC3DatabaseMetaData.this, tbl) + "')");
                try {
                    if (rs.next()) {
                        final Matcher matcher = this.FK_NAMED_PATTERN.matcher(rs.getString(1));
                        while (matcher.find()) {
                            fkNames.add(matcher.group(1));
                        }
                    }
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (Throwable t) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable t2) {
                            t.addSuppressed(t2);
                        }
                    }
                    throw t;
                }
                if (stat2 != null) {
                    stat2.close();
                }
            }
            catch (Throwable t3) {
                if (stat2 != null) {
                    try {
                        stat2.close();
                    }
                    catch (Throwable t4) {
                        t3.addSuppressed(t4);
                    }
                }
                throw t3;
            }
            Collections.reverse(fkNames);
            return fkNames;
        }
        
        public String getFkTableName() {
            return this.fkTableName;
        }
        
        public List<ForeignKey> getFkList() {
            return this.fkList;
        }
        
        class ForeignKey
        {
            private final String fkName;
            private final String pkTableName;
            private final String fkTableName;
            private final List<String> fkColNames;
            private final List<String> pkColNames;
            private final String onUpdate;
            private final String onDelete;
            private final String match;
            
            ForeignKey(final String fkName, final String pkTableName, final String fkTableName, final String onUpdate, final String onDelete, final String match) {
                this.fkColNames = new ArrayList<String>();
                this.pkColNames = new ArrayList<String>();
                this.fkName = fkName;
                this.pkTableName = pkTableName;
                this.fkTableName = fkTableName;
                this.onUpdate = onUpdate;
                this.onDelete = onDelete;
                this.match = match;
            }
            
            public String getFkName() {
                return this.fkName;
            }
            
            void addColumnMapping(final String fkColName, final String pkColName) {
                this.fkColNames.add(fkColName);
                this.pkColNames.add(pkColName);
            }
            
            public String[] getColumnMapping(final int colSeq) {
                return new String[] { this.fkColNames.get(colSeq), this.pkColNames.get(colSeq) };
            }
            
            public int getColumnMappingCount() {
                return this.fkColNames.size();
            }
            
            public String getPkTableName() {
                return this.pkTableName;
            }
            
            public String getFkTableName() {
                return this.fkTableName;
            }
            
            public String getOnUpdate() {
                return this.onUpdate;
            }
            
            public String getOnDelete() {
                return this.onDelete;
            }
            
            public String getMatch() {
                return this.match;
            }
            
            @Override
            public String toString() {
                return "ForeignKey [fkName=" + this.fkName + ", pkTableName=" + this.pkTableName + ", fkTableName=" + this.fkTableName + ", pkColNames=" + this.pkColNames + ", fkColNames=" + this.fkColNames + "]";
            }
        }
    }
}
