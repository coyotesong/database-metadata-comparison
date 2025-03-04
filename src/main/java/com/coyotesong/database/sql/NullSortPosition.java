package com.coyotesong.database.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Where null values appear in ordered ResultSets
 * <p>
 * The difference between "Always at" and "Sorts as" is that the former
 * is not affected by ORDER BY, while the latter is.
 */
public enum NullSortPosition {
    UNKNOWN("Unknown"),
    SORT_TO_START("to START"),
    SORT_TO_END("to END"),
    SORT_AS_HIGH("as HIGH"),
    SORT_AS_LOW("as LOW");

    private static final Logger LOG = LoggerFactory.getLogger(NullSortPosition.class);

    private final String label;

    NullSortPosition(String label) {
        this.label = label;
    }

    /**
     * Get human-friendly label
     *
     * @return human-friendly label
     */
    public String getLabel() {
        return label;
    }

    public static NullSortPosition valueOf(DatabaseMetaData md) {

        try {
            if (md.nullsAreSortedAtStart()) {
                return SORT_TO_START;
            } else if (md.nullsAreSortedAtEnd()) {
                return SORT_TO_END;
            } else if (md.nullsAreSortedHigh()) {
                return SORT_AS_HIGH;
            } else if (md.nullsAreSortedLow()) {
                return SORT_AS_LOW;
            }
        } catch (SQLException e) {
            LOG.info("{}: {}", e.getClass().getName(), e.getMessage(), e);
        }

        return UNKNOWN;
    }
}
