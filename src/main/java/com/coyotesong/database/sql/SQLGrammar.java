package com.coyotesong.database.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Declared SQL grammar
 */
public enum SQLGrammar {
    UNKNOWN,
    EXTENDED,
    CORE,
    MINIMUM;

    private static final Logger LOG = LoggerFactory.getLogger(SQLGrammar.class);

    static SQLGrammar valueOf(DatabaseMetaData md) {
        try {
            // order matters since there may be more than one, but we don't need
            // a bitmap since they're inclusive.
            if (md.supportsExtendedSQLGrammar()) {
                return EXTENDED;
            } else if (md.supportsCoreSQLGrammar()) {
                return CORE;
            } else if (md.supportsMinimumSQLGrammar()) {
                return MINIMUM;
            }
        } catch (SQLException e) {
            LOG.info("{}: {}", e.getClass().getName(), e.getMessage(), e);
        }

        return UNKNOWN;
    }
}
