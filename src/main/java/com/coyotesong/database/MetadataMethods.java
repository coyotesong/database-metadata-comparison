package com.coyotesong.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Available metadata
 * <p>
 * This is a union of the metadata 'gettter' methods for all covered DatabaseMetaData
 * instances. It provides modest charaacterization of the metadata (DDL, DML, etc.)
 * </p>
 * <p>
 * This information is primarily used when generating reports.
 * </p>
 */
public class MetadataMethods {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataMethods.class);

    public static final MetadataMethods INSTANCE = new MetadataMethods();

    public static final List<String> IGNORE_LIST = List.of(
            "getClass",
            "hashCode",
            "toString",

            "acquireCloseableLock",
            "getACProxy",
            "getMonitorLock",

            "getURL",
            "getConnection",
            "getUserName",
            "getDatabaseMajorVersion",
            "getDatabaseMinorVersion",
            "getDatabaseProductName",
            "getDatabaseProductVersion",
            "getDriverMajorVersion",
            "getDriverMinorVersion",
            "getDriverName",
            "getDriverVersion",
            "getJDBCMajorVersion",
            "getJDBCMinorVersion",
            "getNumericFunctions",
            "getSQLKeywords",
            "getStringFunctions",
            "getSystemFunctions",
            "getTimeDateFunctions",
            "getDefaultTransactionIsolation",
            "getResultSetHoldability",
            "getSQLStateType",
            "nullsAreSortedAtEnd",
            "nullsAreSortedAtStart",
            "nullsAreSortedHigh",
            "nullsAreSortedLow",
            "supportsCoreSQLGrammar",
            "supportsExtendedSQLGrammar",
            "supportsMinimumSQLGrammar",
            "isReadOnly",
            "storesLowerCaseIdentifiers",
            "storesMixedCaseIdentifiers",
            "storesUpperCaseIdentifiers",
            "storesLowerCaseQuotedIdentifiers",
            "storesMixedCaseQuotedIdentifiers",
            "storesUpperCaseQuotedIdentifiers",
            "supportsANSI92EntryLevelSQL",
            "supportsANSI92FullSQL",
            "supportsANSI92IntermediateSQL",
            "supportsCatalogsDataManipulation",
            "supportsCatalogsIndexDefinitions",
            "supportsCatalogsPrivilegeDefinitions",
            "supportsCatalogsProcedureCalls",
            "supportsCatalogsTableDefinitions",
            "supportsSchemasDataManipulation",
            "supportsSchemasIndexDefinitions",
            "supportsSchemasPrivilegeDefinitions",
            "supportsSchemasProcedureCalls",
            "supportsSchemasTableDefinitions",

            // non-standard
            "getDriverDB2ConnectLevel",
            "getDriverMajorVersionInfo",
            "getDriverMinorVersionInfo",
            "getDriverNameInfo",
            "getDriverVersionInfo",
            "getURL_"
    );

    private static final List<String> CATALOG_METHODS_LIST = List.of(
            "getCatalogSeparator",
            "getCatalogTerm",
            "getExtraNameCharacters",
            "getIdentifierQuoteString",
            "getProcedureTerm",
            "getRowIdLifetime",
            "getSchemaTerm",
            "getSearchStringEscape"
    );

    private static final List<String> DDL_METHODS_LIST = List.of(
            "dataDefinitionCausesTransactionCommit",
            "dataDefinitionIgnoredInTransactions",
            "supportsGetGeneratedKeys",
            "supportsAlterTableWithAddColumn",
            "supportsAlterTableWithDropColumn",
            "supportsDataDefinitionAndDataManipulationTransactions",
            "supportsDataManipulationTransactionsOnly",
            "supportsNonNullableColumns",
            "supportsSharding",
            "supportsMixedCaseIdentifiers",
            "supportsMixedCaseQuotedIdentifiers"
    );

    private static final List<String> DML_METHODS_LIST = List.of(
            "allProceduresAreCallable",
            "allTablesAreSelectable",
            "generatedKeyAlwaysReturned",
            "nullPlusNonNullIsNull",
            "supportsBatchUpdates",
            "supportsCorrelatedSubqueries",
            "supportsExpressionsInOrderBy",
            "supportsFullOuterJoins",
            "supportsGroupBy",
            "supportsGroupByBeyondSelect",
            "supportsGroupByUnrelated",
            "supportsLikeEscapeClause",
            "supportsLimitedOuterJoins",
            "supportsMultipleOpenResults",
            "supportsMultipleResultSets",
            "supportsOrderByUnrelated",
            "supportsOuterJoins",
            "supportsSelectForUpdate",
            "supportsSubqueriesInComparisons",
            "supportsSubqueriesInExists",
            "supportsSubqueriesInIns",
            "supportsSubqueriesInQuantifieds",
            "supportsUnion",
            "supportsUnionAll"
    );

    private static final List<String> TRANSACTIONS_METHODS_LIST = List.of(
            "autoCommitFailureClosesAllResultSets",
            "supportsMultipleTransactions",
            "supportsOpenCursorsAcrossCommit",
            "supportsOpenCursorsAcrossRollback",
            "supportsOpenStatementsAcrossCommit",
            "supportsOpenStatementsAcrossRollback",
            "supportsSavepoints",
            "supportsTransactions"
    );

    private static final List<String> STORED_PROCEDURE_METHODS_LIST = List.of(
            "supportsNamedParameters",
            "supportsPositionedDelete",
            "supportsPositionedUpdate",
            "supportsRefCursors",
            "supportsStoredProcedures",
            "supportsStoredFunctionsUsingCallSyntax"
    );


    static {
        // Initialize with standard methods
        INSTANCE.add(DatabaseMetaData.class);
    }

    private final CatalogSchemaSupport catalogSchemaSupport = new CatalogSchemaSupport();
    private final Set<String> booleanMethods = new HashSet<>();
    private final Set<String> limitMethods = new HashSet<>();
    private final Set<String> resultSetMethods = new HashSet<>();
    private final Set<String> catalogSchemaSupportMethods = new HashSet<>();
    private final Set<String> ddlMethods = new HashSet<>();
    private final Set<String> dmlMethods = new HashSet<>();
    private final Set<String> otherMethods = new HashSet<>();
    private final Set<String> transactionsMethods = new HashSet<>();
    private final Set<String> storedProceduresMethods = new HashSet<>();

    private final Set<String> numericFunctions = new HashSet<>();
    private final Set<String> sqlKeywords = new HashSet<>();
    private final Set<String> stringFunctions = new HashSet<>();
    private final Set<String> systemFunctions = new HashSet<>();
    private final Set<String> temporalFunctions = new HashSet<>();

    private MetadataMethods() {
    }

    public void add(Class<? extends DatabaseMetaData> clz) {

        for (Method m : listMethods(clz)) {
            final Class<?> returnType = m.getReturnType();
            final String name = m.getName();

            // is it something we ignore or explicitly handle?
            if (IGNORE_LIST.contains(name) || CATALOG_METHODS_LIST.contains(name)) {
                continue;
            }

            if (name.startsWith("getMax")) {
                limitMethods.add(name);
            } else if (ResultSet.class.isAssignableFrom(returnType)) {
                resultSetMethods.add(name);
            } else if (name.startsWith("supportsCatalogs") || name.startsWith("supportsSchemas")) {
                catalogSchemaSupportMethods.add(name);
            } else if (DDL_METHODS_LIST.contains(name)) {
                ddlMethods.add(name);
            } else if (DML_METHODS_LIST.contains(name)) {
                dmlMethods.add(name);
            } else if (TRANSACTIONS_METHODS_LIST.contains(name)) {
                transactionsMethods.add(name);
            } else if (STORED_PROCEDURE_METHODS_LIST.contains(name)) {
                storedProceduresMethods.add(name);
            } else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
                booleanMethods.add(name);
            } else {
                otherMethods.add(name);
            }
        }
    }

    public <T extends DatabaseMetaData> void add(T md) {
        add(md.getClass());

        try {
            Arrays.stream(md.getNumericFunctions().split(",")).map(String::trim).map(String::toUpperCase).forEach(this.numericFunctions::add);
            Arrays.stream(md.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).forEach(this.sqlKeywords::add);
            Arrays.stream(md.getStringFunctions().split(",")).map(String::trim).map(String::toUpperCase).forEach(this.stringFunctions::add);
            Arrays.stream(md.getSystemFunctions().split(",")).map(String::trim).map(String::toUpperCase).forEach(this.systemFunctions::add);
            Arrays.stream(md.getTimeDateFunctions().split(",")).map(String::trim).map(String::toUpperCase).forEach(this.temporalFunctions::add);
        } catch (SQLException e) {
            LOG.warn("{}: {}", md.getClass().getName(), e.getMessage());
        }
    }

    public List<String> getPropertyNames() {
        final List<String> names = new ArrayList<>();
        names.addAll(limitMethods);
        names.addAll(ddlMethods);
        names.addAll(dmlMethods);
        names.addAll(transactionsMethods);
        names.addAll(storedProceduresMethods);
        names.addAll(booleanMethods);
        names.addAll(otherMethods);
        Collections.sort(names);
        return names;
    }

    public boolean isBooleanMethod(String name) {
        return booleanMethods.contains(name);
    }

    public boolean isOtherMethod(String name) {
        return otherMethods.contains(name);
    }

    public boolean isResultSetMethod(String name) {
        return resultSetMethods.contains(name);
    }

    public boolean isLimitMethod(String name) {
        return limitMethods.contains(name);
    }

    public boolean isSupportCatalogSchemaMethod(String name) {
        return catalogSchemaSupportMethods.contains(name);
    }

    public boolean isDdlMethod(String name) {
        return ddlMethods.contains(name);
    }

    public boolean isDmlMethod(String name) {
        return dmlMethods.contains(name);
    }

    public boolean isTransactionsMethod(String name) {
        return transactionsMethods.contains(name);
    }

    public boolean isStoredProceduresMethod(String name) {
        return storedProceduresMethods.contains(name);
    }

    public String toString() {
        try (StringWriter w = new StringWriter();
             PrintWriter pw = new PrintWriter(w)) {

            pw.printf("MetadataMethods stats\n");
            pw.printf(" - limits:  %4d\n", limitMethods.size());
            pw.printf(" - ddl:     %4d\n", ddlMethods.size());
            pw.printf(" - dml:     %4d\n", dmlMethods.size());
            pw.printf(" - tx:      %4d\n", transactionsMethods.size());
            pw.printf(" - sp:      %4d\n", storedProceduresMethods.size());
            pw.printf(" - boolean: %4d\n", booleanMethods.size());
            pw.printf(" - other:   %4d\n", otherMethods.size());
            pw.flush();
            return w.toString();
        } catch (IOException e) {
            LOG.warn("{}: {}", e.getClass().getName(), e.getMessage());
        }
        return "(null)";
    }

    public static List<Method> listMethods(Class<? extends DatabaseMetaData> clz) {
        // implementation note: we can't test for Void.class due to multiple classloaders
        return Arrays.stream(clz.getMethods())
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> !"void".equals(m.getReturnType().getName()))
                .sorted((p, q) -> p.getName().compareTo(q.getName()))
                .toList();
    }
}
