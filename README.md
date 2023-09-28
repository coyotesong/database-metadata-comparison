# Database Metadata Comparison

**This is prelimary work - it's only public for discussions during my job search **

This repo contains a simple app that launches a large number of databases (via TestContainers),
performs database metadata queries, then produces tables containing the results.

The next big step is a comparison of the standard types. I know from prior work that that will be... interesting.

Here's the current output.

## Product Summary
| | Name | Version | Cat and Schema Terms | Procedure Term | Quote | Escape | Extra | Isolation | Holdability | RowID Lifetime | SQL State Type |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| IBM DB2 | DB2/LINUXX8664 | 11 | database.schema | stored procedure | " | \ | @# | READ_COMMITTED | HOLD_CURSORS_OVER_COMMIT | ROWID_UNSUPPORTED | SQL |
| MSSQL | Microsoft SQL Server | 16 | database.schema | stored procedure | " | \ | $#@ | READ_COMMITTED | HOLD_CURSORS_OVER_COMMIT | ROWID_UNSUPPORTED | SQL |
| MySQL | MySQL | 8 | database. | PROCEDURE | ` | \ | #@ | REPEATABLE_READ | HOLD_CURSORS_OVER_COMMIT | ROWID_UNSUPPORTED | SQL |
| Oracle | Oracle | 18 | schema | procedure | " | / | $# | READ_COMMITTED | HOLD_CURSORS_OVER_COMMIT | ROWID_VALID_FOREVER |  |
| PostgreSQL | PostgreSQL | 15 | database.schema | function | " | \ |  | READ_COMMITTED | HOLD_CURSORS_OVER_COMMIT | not supported | SQL |
| Vertica CE | Vertica Database | 23 | catalog.schema | function | " | \ |  | READ_COMMITTED | CLOSE_CURSORS_AT_COMMIT | ROWID_UNSUPPORTED | X/Open |
| YugaByteDB | PostgreSQL | 11 | database.schema | function | " | \ |  | READ_COMMITTED | HOLD_CURSORS_OVER_COMMIT | not supported | SQL |
| H2 | H2 | 2 | catalog.schema | procedure | " | \ |  | READ_COMMITTED | CLOSE_CURSORS_AT_COMMIT | ROWID_UNSUPPORTED | SQL |
| SQLite | SQLite | 3 | catalog.schema | not_implemented | " | \ |  | SERIALIZABLE | CLOSE_CURSORS_AT_COMMIT | not supported | SQL |



## Limits
|  | IBM DB2 | MSSQL | MySQL | Oracle | PostgreSQL | Vertica CE | YugaByteDB | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---: |
| getMaxBinaryLiteralLength | 4 kiB - 96 |  | 16 MiB - 8 | 1 kiB - 24 |  |  |  |  |  |
| getMaxCatalogNameLength | 8 | 128 | 32 |  | 63 |  | 63 |  |  |
| getMaxCharLiteralLength | 32 kiB - 96 |  | 16 MiB - 8 | 2 kiB - 48 |  |  |  |  |  |
| getMaxColumnNameLength | 128 | 128 | 64 | 128 | 63 | 128 | 63 |  |  |
| getMaxColumnsInGroupBy | 1 kiB - 12 |  | 64 |  |  |  |  |  |  |
| getMaxColumnsInIndex | 16 | 16 | 16 | 32 | 32 |  | 32 |  |  |
| getMaxColumnsInOrderBy | 1 kiB - 12 |  | 64 |  |  |  |  |  |  |
| getMaxColumnsInSelect | 1 kiB - 12 | 4 kiB | 256 |  |  |  |  |  |  |
| getMaxColumnsInTable | 1 kiB - 12 | 1 kiB | 512 | 1 kiB - 24 | 1600 |  | 1600 |  |  |
| getMaxConnections |  | 32 kiB - 1 |  |  | 8 kiB |  | 8 kiB |  |  |
| getMaxCursorNameLength | 128 |  | 64 |  | 63 |  | 63 |  |  |
| getMaxIndexLength | 1 kiB | 900 | 256 |  |  |  |  |  |  |
| getMaxLogicalLobSize | 2 GiB - 1 | 2 GiB - 1 |  | [Ex] |  |  |  |  |  |
| getMaxProcedureNameLength | 128 | 128 |  | 128 | 63 | 128 | 63 |  |  |
| getMaxRowSize | 32 kiB - 91 | 8060 | 2 GiB - 9 |  | 1 GiB | 31250 kiB | 1 GiB |  |  |
| getMaxSchemaNameLength | 128 | 128 |  | 128 | 63 | 128 | 63 |  |  |
| getMaxStatementLength | 2 MiB | 500 MiB | 64 kiB - 5 | 64 kiB - 1 |  |  |  |  |  |
| getMaxStatements |  |  |  |  |  |  |  |  |  |
| getMaxTableNameLength | 128 | 128 | 64 | 128 | 63 | 128 | 63 |  |  |
| getMaxTablesInSelect |  | 256 | 256 |  |  |  |  |  |  |
| getMaxUserNameLength | 30 | 128 | 16 | 128 | 63 | 128 | 63 |  |  |


## Boolean properties
|  | IBM DB2 | MSSQL | MySQL | Oracle | PostgreSQL | Vertica CE | YugaByteDB | H2 | SQLite |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---: |
| allProceduresAreCallable |  | ☑ |  |  | ☑ |  | ☑ | ☑ |  |
| allTablesAreSelectable |  | ☑ |  |  | ☑ | ☑ | ☑ | ☑ | ☑ |
| autoCommitFailureClosesAllResultSets |  |  |  |  |  | ☑ |  |  | not supported |
| dataDefinitionCausesTransactionCommit |  |  | ☑ | ☑ |  | ☑ |  | ☑ |  |
| dataDefinitionIgnoredInTransactions |  |  |  |  |  |  |  |  |  |
| doesMaxRowSizeIncludeBlobs |  |  | ☑ | ☑ |  | ☑ |  |  |  |
| generatedKeyAlwaysReturned | ☑ | ☑ | ☑ |  | ☑ |  | ☑ | ☑ | not supported |
| isCatalogAtStart | ☑ | ☑ | ☑ |  | ☑ | ☑ | ☑ | ☑ | ☑ |
| locatorsUpdateCopy | ☑ | ☑ | ☑ | ☑ | ☑ |  | ☑ |  |  |
| nullPlusNonNullIsNull | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| nullsAreSortedAtEnd |  |  |  |  |  |  |  |  |  |
| nullsAreSortedAtStart |  |  |  |  |  |  |  |  | ☑ |
| nullsAreSortedHigh | ☑ |  |  | ☑ | ☑ |  | ☑ |  | ☑ |
| nullsAreSortedLow |  | ☑ | ☑ |  |  | ☑ |  | ☑ |  |
| storesLowerCaseIdentifiers |  |  |  |  | ☑ |  | ☑ |  |  |
| storesLowerCaseQuotedIdentifiers |  |  |  |  |  |  |  |  |  |
| storesMixedCaseIdentifiers |  | ☑ | ☑ |  |  | ☑ |  |  | ☑ |
| storesMixedCaseQuotedIdentifiers |  | ☑ | ☑ | ☑ |  | ☑ |  |  |  |
| storesUpperCaseIdentifiers | ☑ |  |  | ☑ |  |  |  | ☑ |  |
| storesUpperCaseQuotedIdentifiers |  |  | ☑ |  |  |  |  |  |  |
| supportsANSI92EntryLevelSQL | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsANSI92FullSQL |  |  |  |  |  |  |  |  |  |
| supportsANSI92IntermediateSQL |  |  |  |  |  |  |  |  |  |
| supportsAlterTableWithAddColumn | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsAlterTableWithDropColumn |  | ☑ | ☑ |  | ☑ | ☑ | ☑ | ☑ |  |
| supportsBatchUpdates | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsCatalogsInDataManipulation |  | ☑ | ☑ |  |  | ☑ |  | ☑ |  |
| supportsCatalogsInIndexDefinitions |  | ☑ | ☑ |  |  |  |  | ☑ |  |
| supportsCatalogsInPrivilegeDefinitions |  | ☑ | ☑ |  |  | ☑ |  | ☑ |  |
| supportsCatalogsInProcedureCalls |  | ☑ | ☑ |  |  |  |  |  |  |
| supportsCatalogsInTableDefinitions |  | ☑ | ☑ |  |  | ☑ |  | ☑ |  |
| supportsColumnAliasing | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsConvert |  | ☑ |  |  |  | ☑ |  | ☑ |  |
| supportsCoreSQLGrammar | ☑ | ☑ | ☑ | ☑ |  | ☑ |  | ☑ | ☑ |
| supportsCorrelatedSubqueries | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsDataDefinitionAndDataManipulationTransactions | ☑ | ☑ |  | ☑ | ☑ |  | ☑ |  | ☑ |
| supportsDataManipulationTransactionsOnly |  |  |  | ☑ |  |  |  | ☑ |  |
| supportsDifferentTableCorrelationNames |  |  | ☑ | ☑ |  |  |  |  |  |
| supportsExpressionsInOrderBy | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsExtendedSQLGrammar | ☑ |  |  | ☑ |  |  |  |  |  |
| supportsFullOuterJoins | ☑ | ☑ |  | ☑ | ☑ | ☑ | ☑ |  | ☑ |
| supportsGetGeneratedKeys | ☑ | ☑ | ☑ | ☑ | ☑ |  | ☑ | ☑ | ☑ |
| supportsGroupBy | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsGroupByBeyondSelect | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsGroupByUnrelated | ☑ | ☑ | ☑ | ☑ | ☑ |  | ☑ | ☑ |  |
| supportsIntegrityEnhancementFacility | ☑ |  |  | ☑ | ☑ |  | ☑ | ☑ |  |
| supportsLikeEscapeClause | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsLimitedOuterJoins | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsMinimumSQLGrammar | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsMixedCaseIdentifiers |  | ☑ | ☑ |  |  |  |  |  | ☑ |
| supportsMixedCaseQuotedIdentifiers | ☑ | ☑ | ☑ | ☑ | ☑ |  | ☑ | ☑ |  |
| supportsMultipleOpenResults | ☑ |  | ☑ |  |  |  |  |  |  |
| supportsMultipleResultSets | ☑ | ☑ | ☑ |  | ☑ | ☑ | ☑ |  |  |
| supportsMultipleTransactions | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsNamedParameters | ☑ | ☑ |  | ☑ |  |  |  |  | ☑ |
| supportsNonNullableColumns | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsOpenCursorsAcrossCommit | ☑ |  |  |  |  |  |  |  |  |
| supportsOpenCursorsAcrossRollback |  |  |  |  |  |  |  |  |  |
| supportsOpenStatementsAcrossCommit | ☑ | ☑ |  |  | ☑ | ☑ | ☑ | ☑ |  |
| supportsOpenStatementsAcrossRollback | ☑ | ☑ |  |  | ☑ | ☑ | ☑ | ☑ |  |
| supportsOrderByUnrelated | ☑ | ☑ |  | ☑ | ☑ |  | ☑ | ☑ |  |
| supportsOuterJoins | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsPositionedDelete | ☑ | ☑ |  |  |  |  |  |  |  |
| supportsPositionedUpdate | ☑ | ☑ |  |  |  |  |  |  |  |
| supportsRefCursors | ☑ |  |  | ☑ | ☑ |  | ☑ |  |  |
| supportsSavepoints | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsSchemasInDataManipulation | ☑ | ☑ |  | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsSchemasInIndexDefinitions | ☑ | ☑ |  | ☑ | ☑ |  | ☑ | ☑ |  |
| supportsSchemasInPrivilegeDefinitions | ☑ | ☑ |  | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsSchemasInProcedureCalls | ☑ | ☑ |  | ☑ | ☑ |  | ☑ | ☑ |  |
| supportsSchemasInTableDefinitions | ☑ | ☑ |  | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsSelectForUpdate | ☑ |  | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsSharding |  |  |  | ☑ |  |  |  |  |  |
| supportsStatementPooling |  |  |  | ☑ |  |  |  |  |  |
| supportsStoredFunctionsUsingCallSyntax |  | ☑ | ☑ | ☑ | ☑ |  | ☑ | ☑ | not supported |
| supportsStoredProcedures | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |  |
| supportsSubqueriesInComparisons | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsSubqueriesInExists | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsSubqueriesInIns | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsSubqueriesInQuantifieds | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsTableCorrelationNames | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |  |
| supportsTransactions | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsUnion | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| supportsUnionAll | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ | ☑ |
| usesLocalFilePerTable |  |  |  |  |  |  |  |  |  |
| usesLocalFiles |  |  |  |  |  |  |  | ☑ | ☑ |
