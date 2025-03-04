package com.coyotesong.database.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public enum IdentifierStorage {
    UNKNOWN,
    LOWER_CASE,
    MIXED_CASE,
    UPPER_CASE;

    private static final Logger LOG = LoggerFactory.getLogger(IdentifierStorage.class);

    public static IdentifierStorage valueOf(DatabaseMetaData md) {
        try {
            if (md.storesLowerCaseIdentifiers()) {
                return LOWER_CASE;
            } else if (md.storesMixedCaseIdentifiers()) {
                return MIXED_CASE;
            } else if (md.storesUpperCaseIdentifiers()) {
                return UPPER_CASE;
            }
        } catch (SQLException e) {
            LOG.info("{}: {}", e.getClass().getName(), e.getMessage(), e);
        }

        return UNKNOWN;
    }

    public static IdentifierStorage quotedValueOf(DatabaseMetaData md) {
        try {
            if (md.storesLowerCaseQuotedIdentifiers()) {
                return LOWER_CASE;
            } else if (md.storesMixedCaseQuotedIdentifiers()) {
                return MIXED_CASE;
            } else if (md.storesUpperCaseQuotedIdentifiers()) {
                return UPPER_CASE;
            }
        } catch (SQLException e) {
            LOG.info("{}: {}", e.getClass().getName(), e.getMessage(), e);
        }

        return UNKNOWN;
    }
}
