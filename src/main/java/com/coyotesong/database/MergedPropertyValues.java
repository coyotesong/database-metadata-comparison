package com.coyotesong.database;

import com.coyotesong.database.sql.ExtendedDatabaseMetaData;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Merged property values
 */
public class MergedPropertyValues {
    final String propertyName;
    final Map<Database, Object> values = new LinkedHashMap<>();

    /**
     * Constructor
     *
     * @param propertyName property name
     * @param databases scanned databases
     */
    public MergedPropertyValues(final String propertyName, Map<Database, ExtendedDatabaseMetaData> databases) {
        this.propertyName = propertyName;

        // this ensures the values are always provided in the same order.
        for (Database database : Database.values()) {
            if (databases.containsKey(database)) {
                values.put(database, databases.get(database).get(propertyName));
            } else {
                // should this be replaced with an explicit sentinel value?
                values.put(database, null);
            }
        }
    }

    /**
     * Returns property name
     *
     * @return property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns collection of merged values (database -> value)
     *
     * @return collection of merged values
     */
    public Map<Database, Object> getValues() {
        return values;
    }
}
