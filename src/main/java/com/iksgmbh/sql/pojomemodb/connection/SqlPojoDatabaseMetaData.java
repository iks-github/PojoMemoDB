package com.iksgmbh.sql.pojomemodb.connection;

import java.sql.*;

/**
 * This class represents an incomplete implementation of java.sql.DatabaseMetaData class.
 * It implements the some basic methods that are needed by current requirements.
 * Other methods may be implemented later on as needed.
 *
 * @author Reik Oberrath
 */
@SuppressWarnings("unused")
public class SqlPojoDatabaseMetaData implements DatabaseMetaData
{
    private static final String DATABASE_NAME = "SqlPojoMemoDb";

    // Must be idendical to the version in the pom file (without -SNAPSHOT)
    private static final String DATABASE_VERSION = "0.0.4";

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return DATABASE_VERSION;
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return DATABASE_NAME;
    }


    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }


    // ###########################################################################
    //                 not   implemented    dummy    methods
    // ###########################################################################


	@Override
    public boolean allProceduresAreCallable() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public String getURL() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getUserName() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }


    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public String getDriverName() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getDriverVersion() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public int getDriverMajorVersion() {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getDriverMinorVersion() {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException
    {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return 0;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    // @Override not before Java 1.7
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    // @Override not before Java 1.7
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (true) throw new RuntimeException("Not yet implemented!");
        return false;
    }
}
