package com.coyotesong.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Merged database information
 */
public class DatabaseComparisons { // implements Iterable<ExtendedDatabaseMetaData> {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseComparisons.class);

    private final Pivots pivots = new Pivots();

    /**
     * Initialize the cached data
     */
    public Pivots initialize() {
        final DatabaseScanner databaseScanner = new DatabaseScanner();
        pivots.initialize(databaseScanner.scanDatabases());
        return pivots;
    }

    /*
    public List<String> getPropertyNames() {
        return pivots.getPropertyNames();
    }
     */

    /*
    public List<String> getTableTypes() {
        return pivots.getTableTypes();
    }

    public List<String> getSqlKeywords() {
        return pivots.getSqlKeywords();
    }
    *
     */

    /*
    public List<String> getNumericFunctions() {
        return pivots.getNumericFunctions();
    }
     */

    /*
    public List<String> getStringFunctions() {
        return pivots.getStringFunctions();
    }

    public List<String> getSystemFunctions() {
        return pivots.getSystemFunctions();
    }

    public List<String> getTemporalFunctions() {
        return pivots.getTemporalFunctions();
    }
     */

    /*
    public List<TypeInfo> getTypes() {
        return pivots.getDataTypes();
    }
    */

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
        return MetadataMethods.INSTANCE.isLimitProperty(key);
    }

    public boolean isBooleanProperty(String key) {
        return MetadataMethods.INSTANCE.isBooleanProperty(key);
    }

    public boolean isOtherProperty(String key) {
        return MetadataMethods.INSTANCE.isOtherMethod(key);
    }

    // public Set<Map.Entry<Database, ExtendedDatabaseMetaData>> entrySet() {
    //    return databases.entrySet();
    //}


    /*
    @NotNull
    public Iterator<ExtendedDatabaseMetaData> iterator() {
        return pivots.iterator();
    }
     */
}
