package com.coyotesong.database;

import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Merged database information
 */
public class DatabaseComparisons {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseComparisons.class);

    private final Map<Database, ExtendedDatabaseMetaData> databases = new LinkedHashMap<>();

    private final List<String> propertyNames = new ArrayList<>();

    private final Map<String, MergedPropertyValues> mergedProperties = new LinkedHashMap<>();

    private final TableTypesPivot tableTypes = new TableTypesPivot();
    private final SqlKeywordsPivot sqlKeywords = new SqlKeywordsPivot();


    /**
     * Initialize the cached data
     */
    public void initialize() {
        final DatabaseScanner databaseScanner = new DatabaseScanner(databases);
        databaseScanner.scanDatabases();

        for (ExtendedDatabaseMetaData md : databases.values()) {
            tableTypes.addDatabase(md);
            sqlKeywords.addDatabase(md);
        }

        final Set<String> keySet = new HashSet<>();
        for (ExtendedDatabaseMetaData md : databases.values()) {
            keySet.addAll(md.keySet());
        }

        // sort property keys (for convenience)
        // propertyNames.addAll(MetadataMethods.INSTANCE.getPropertyNames());
        propertyNames.clear();
        propertyNames.addAll(keySet);
        Collections.sort(propertyNames);

        // pivot the collected data
        for (String propertyName : propertyNames) {
            mergedProperties.put(propertyName, new MergedPropertyValues(propertyName, databases));
        }
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public ExtendedDatabaseMetaData getMetadata(Database database) {
        return databases.get(database);
    }

    public TableTypesPivot getTableTypes() {
        return tableTypes;
    }

    public SqlKeywordsPivot getSqlKeywords() {
        return sqlKeywords;
    }

    public Collection<ExtendedDatabaseMetaData> values() {
        return databases.values();
    }

    public boolean isDmlProperty(String key) {
        return MetadataMethods.INSTANCE.isDmlMethod(key);
    }

    public boolean isDdlProperty(String key) {
        return MetadataMethods.INSTANCE.isDdlMethod(key);
    }

    public boolean isTransactionsProperty(String key) {
        return MetadataMethods.INSTANCE.isTransactionsMethod(key);
    }

    public boolean isStoredProceduresProperty(String key) {
        return MetadataMethods.INSTANCE.isStoredProceduresMethod(key);
    }

    public boolean isLimitProperty(String key) {
        return MetadataMethods.INSTANCE.isLimitMethod(key);
    }

    public boolean isBooleanProperty(String key) {
        return MetadataMethods.INSTANCE.isBooleanMethod(key);
    }

    public boolean isOtherProperty(String key) {
        return MetadataMethods.INSTANCE.isOtherMethod(key);
    }

    public Set<Map.Entry<Database, ExtendedDatabaseMetaData>> entrySet() {
        return databases.entrySet();
    }
}
