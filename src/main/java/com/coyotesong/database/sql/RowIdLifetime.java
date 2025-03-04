package com.coyotesong.database.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Possible RowId Lifetime values
 */
public enum RowIdLifetime {
    UNKNOWN,
    UNSUPPORTED,
    OTHER,
    TRANSACTION,
    SESSION,
    FOREVER
    ;

    private static final Logger LOG = LoggerFactory.getLogger(RowIdLifetime.class);

    private static final Map<java.sql.RowIdLifetime, RowIdLifetime> LOOKUP = new HashMap<>();

    public static RowIdLifetime valueOf(DatabaseMetaData md) {
        try {
            java.sql.RowIdLifetime lifetime = md.getRowIdLifetime();
            if (LOOKUP.containsKey(lifetime)) {
                return LOOKUP.get(lifetime);
            }
        } catch (java.sql.SQLFeatureNotSupportedException e) {
            LOG.warn("RowIDLifetime not supported");
            return UNSUPPORTED;
        } catch (java.sql.SQLException e) {
            LOG.warn("{}: {}", e.getClass().getName(), e.getMessage());
            return UNKNOWN;
        }

        return UNKNOWN;
    }

    static {
        LOOKUP.put(java.sql.RowIdLifetime.ROWID_UNSUPPORTED, UNSUPPORTED);
        LOOKUP.put(java.sql.RowIdLifetime.ROWID_VALID_OTHER, OTHER);
        LOOKUP.put(java.sql.RowIdLifetime.ROWID_VALID_TRANSACTION, TRANSACTION);
        LOOKUP.put(java.sql.RowIdLifetime.ROWID_VALID_SESSION, SESSION);
        LOOKUP.put(java.sql.RowIdLifetime.ROWID_VALID_FOREVER, FOREVER);
    }
}
