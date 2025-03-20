package com.coyotesong.database;

import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
// import com.coyotesong.database.sql.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Pivot list of databases.
 * <p>
 * Due to improvements in the templating code this class no longer performs any
 * actual pivoting. It still creates a number of caches that simplify life later.
 * </p>
 *
 */
public class Pivots {  // implements Iterable<ExtendedDatabaseMetaData> {
    private static final Logger LOG = LoggerFactory.getLogger(Pivots.class);

    // | Name | Product Name |Version | SQL Grammar | Isolation | Holdability | RowID Lifetime | SQL State Type |

    // Name | Version | Full Tablename | Procedure Term | Quote | Escape | Extra | Nulls Sort | Identifier | Quoted Identifie
    // {# | {{ db.databaseMajorVersion }} | {{ db.catSchemaTerm }} | {{ db.procedureTerm }} | {{ db.identifierQuoteString }} | {{ db.searchStringEscape }} | {{ db.extraNameCharacters }} | {{ db.nullSortPosition }} |  {{ db.identifierStorage }} | {{ db.quotedIdentifierStorage }} | #}

    private static final List<String> SQL_PROPERTIES_LIST = List.of(
            //  "getCatalogTerm",
            // "getSchemaTerm",
            "getProcedureTerm",
            // quote
            "getSearchStringEscape",
            "getExtraNameCharacters",
            // nulls
            "getIdentifierString",
            "getIdentifierQuoteString"
    );

    private final Map<String, ExtendedDatabaseMetaData> cache = new LinkedHashMap<>();
    private final Map<String, Set<String>> functionCache = new LinkedHashMap<>();

    private final List<String> databaseLabels = new ArrayList<>();
    private final List<String> propertyNames = new ArrayList<>();

    // per-database caches.
    private final Map<String, List<String>> sqlKeywordsCache = new HashMap<>();
    private final Map<String, List<String>> tableTypesCache = new HashMap<>();
    /*
    private final Map<String, List<String>> numericFunctionsCache = new HashMap<>();
    private final Map<String, List<String>> stringFunctionsCache = new HashMap<>();
    private final Map<String, List<String>> systemFunctionsCache = new HashMap<>();
    private final Map<String, List<String>> temporalFunctionsCache = new HashMap<>();
     */
    // private final Map<String, List<TypeInfo>> dataTypeCache = new HashMap<>();

    private final List<String> sqlKeywords = new ArrayList<>();
    private final List<String> tableTypes = new ArrayList<>();
    // private final List<TypeInfo> dataTypes = new ArrayList<>();

    private final List<String> numericFunctions = new ArrayList<>();
    private final List<String> stringFunctions = new ArrayList<>();
    private final List<String> systemFunctions = new ArrayList<>();
    private final List<String> temporalFunctions = new ArrayList<>();

    /**
     * Initialize list of properties.
     */
    void initializeListOfProperties() {
        final Set<String> propertyNameSet = new HashSet<>();
        for (ExtendedDatabaseMetaData md : cache.values()) {
            propertyNameSet.addAll(md.keySet());
        }

        propertyNames.addAll(propertyNameSet);
        Collections.sort(propertyNames);
    }

    /**
     * initializee list of functions
     */
    void initializeListOfFunctions() {
        final Set<String> numericFunctionKeySet = new HashSet<>();
        final Set<String> stringFunctionKeySet = new HashSet<>();
        final Set<String> systemFunctionKeySet = new HashSet<>();
        final Set<String> temporalFunctionKeySet = new HashSet<>();

        for (ExtendedDatabaseMetaData md : cache.values()) {
            numericFunctionKeySet.addAll(md.getNumericFunctions());
            stringFunctionKeySet.addAll(md.getStringFunctions());
            systemFunctionKeySet.addAll(md.getSystemFunctions());
            temporalFunctionKeySet.addAll(md.getTemporalFunctions());
        }

        this.numericFunctions.addAll(numericFunctionKeySet);
        Collections.sort(this.numericFunctions);

        this.stringFunctions.addAll(stringFunctionKeySet);
        Collections.sort(this.stringFunctions);

        this.systemFunctions.addAll(systemFunctionKeySet);
        Collections.sort(this.systemFunctions);

        this.temporalFunctions.addAll(temporalFunctionKeySet);
        Collections.sort(this.temporalFunctions);
    }

    /**
     * Initialize cache of supported functions per database.
     */
    void initializeSupportedFunctionsCache() {
        for (Map.Entry<String, ExtendedDatabaseMetaData> entry : cache.entrySet()) {
            final Set<String> functions = new HashSet<>();
            functionCache.put(entry.getKey(), functions);

            final ExtendedDatabaseMetaData md = entry.getValue();
            functions.addAll(md.getNumericFunctions());
            functions.addAll(md.getStringFunctions());
            functions.addAll(md.getSystemFunctions());
            functions.addAll(md.getTemporalFunctions());
        }
    }

    /**
     * Initialize the pivot tables with the provided databases.
     *
     * @param databases results of database scans
     */
    public void initialize(Map<Database, ExtendedDatabaseMetaData> databases) {
        // copy useful with template functions
        databases.entrySet().stream().forEach((entry) -> cache.put(entry.getKey().getLabel(), entry.getValue()));
        databases.entrySet().stream().forEach((entry) -> databaseLabels.add(entry.getKey().getLabel()));

        initializeListOfProperties();
        initializeListOfFunctions();
        initializeSupportedFunctionsCache();

        for (ExtendedDatabaseMetaData md : databases.values()) {
            final String label = md.getLabel();
            sqlKeywordsCache.put(label, new ArrayList<>(md.getSqlKeywords()));
            tableTypesCache.put(label, new ArrayList<>(md.getTableTypes()));
            // dataTypeCache.put(label, new ArrayList<>(md.getTypes()));
        }


        this.sqlKeywords.addAll(mergeListsOfStrings(sqlKeywordsCache.values()));
        this.tableTypes.addAll(mergeListsOfStrings(tableTypesCache.values()));
    }

    List<String> mergeListsOfStrings(Collection<List<String>> values) {
        final Set<String> merged = new HashSet<>();
        values.forEach(merged::addAll);
        final List<String> results = new ArrayList<>(merged);
        Collections.sort(results);
        return results;
    }

    /*
    public ExtendedDatabaseMetaData getMetadata(Database database) {
        return databases.get(database);
    }
    */

    public List<String> getDatabaseLabels() {
        return databaseLabels;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public List<String> getNumericFunctions() {
        return numericFunctions;
    }

    public List<String> getSqlKeywords() {
        return sqlKeywords;
    }

    public List<String> getSqlProperties() {
        return SQL_PROPERTIES_LIST;
    }

    public List<String> getStringFunctions() {
        return stringFunctions;
    }

    public List<String> getSystemFunctions() {
        return systemFunctions;
    }

    public List<String> getTableTypes() {
        return tableTypes;
    }

    public List<String> getTemporalFunctions() {
        return temporalFunctions;
    }

    /*
    public List<TypeInfo> getDataTypes() {
        final List<TypeInfo> results = new ArrayList<>(dataTypes);
        Collections.sort(results, (s, p) ->
        {
            if (s.getType() != p.getType()) {
                return s.getType() - p.getType();
            }
            return p.getName().compareToIgnoreCase(s.getName());
        });
        return results;
    }
     */

    boolean checkCache(Map<String, List<String>> cache, String database, String key) {
        if (!cache.containsKey(database)) {
            LOG.warn("unexpected database: {}", database);
            return false;
        }
        return cache.get(database).contains(key);
    }

    public boolean isSqlKeywordSupported(String sqlKeyword, String database) {
        return checkCache(sqlKeywordsCache, database, sqlKeyword);
    }

    /*
    public boolean isNumericFunctionSupported(String function, String database) {
        return checkCache(numericFunctionsCache, database, function);
    }

    public boolean isStringFunctionSupported(String function, String database) {
        return checkCache(stringFunctionsCache, database, function);
    }

    public boolean isSystemFunctionSupported(String function, String database) {
        return checkCache(systemFunctionsCache, database, function);
    }

    public boolean isTemporalFunctionSupported(String function, String database) {
        return checkCache(temporalFunctionsCache, database, function);
    }

     */

    public boolean isTableTypeSupported(String type, String database) {
        return checkCache(tableTypesCache, database, type);
    }

    /**
     * Get property.
     *
     * @param propertyKey
     * @param database
     * @return
     */
    public Object getProperty(String propertyKey, String database) {
        if (cache.get(database).containsKey(propertyKey)) {
            return cache.get(database).get(propertyKey);
        }

        return null;
    }

    /**
     * Check whether function is supported.
     *
     * @param functionName
     * @param database
     * @return
     */
    public boolean isFunctionSupported(String functionName, String database) {
        return functionCache.get(database).contains(functionName);
    }

    public String getDockerImage(String database) {
        return cache.get(database).get("dockerImage");
    }

    public Collection<String> getClientInfoProperties(String database) {
        return cache.get(database).getClientInfoProperties();
    }

    public String getDatabaseProductName(String database) {
        return cache.get(database).getDatabaseProductName();
    }

    public String getDatabaseProductVersion(String database) {
        return cache.get(database).getDatabaseProductVersion();
    }

    public String getDriverClassname(String database) {
        return cache.get(database).getDriverClassName();
    }

    public String getMavenCoordinates(String database) {
        return cache.get(database).getMavenCoordinates();
    }

    public String getDockerRepo(String database) {
        return cache.get(database).getDockerRepo();
    }

    /**
     * Iterator across databases
     *
     * @return
     */
    /*
    @NotNull
    public Iterator<ExtendedDatabaseMetaData> iterator() {
        return databases.values().iterator();
    }
     */
}
