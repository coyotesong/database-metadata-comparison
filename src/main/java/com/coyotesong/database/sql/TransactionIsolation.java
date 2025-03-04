package com.coyotesong.database.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Possible transaction isolation
 * <p>
 * Implementation note: the Connection.* values are powers of two which suggests the value
 * could be a bitset, not a single value, but I haven't seen that in the field. Yet.
 * </p>
 */
public enum TransactionIsolation {
    UNKNOWN,
    NONE,
    READ_COMMITTED,
    READ_UNCOMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE
    ;

    private static final Map<Integer, TransactionIsolation> LOOKUP = new HashMap<>();

    public static TransactionIsolation valueOf(int level) {
        if (LOOKUP.containsKey(level)) {
            return LOOKUP.get(level);
        }

        return UNKNOWN;
    }

    static {
        LOOKUP.put(Connection.TRANSACTION_NONE, NONE);
        LOOKUP.put(Connection.TRANSACTION_READ_COMMITTED, READ_COMMITTED);
        LOOKUP.put(Connection.TRANSACTION_READ_UNCOMMITTED, READ_UNCOMMITTED);
        LOOKUP.put(Connection.TRANSACTION_REPEATABLE_READ, REPEATABLE_READ);
        LOOKUP.put(Connection.TRANSACTION_SERIALIZABLE, SERIALIZABLE);
    }
}
