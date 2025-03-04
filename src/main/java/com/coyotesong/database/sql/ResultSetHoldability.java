package com.coyotesong.database.sql;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Possible ResultSet Holdability values
 */
public enum ResultSetHoldability {
    UNKNOWN,
    HOLDS,
    CLOSES
    ;

    private static final Map<Integer, ResultSetHoldability> LOOKUP = new HashMap<>();

    public static ResultSetHoldability valueOf(int level) {
        if (LOOKUP.containsKey(level)) {
            return LOOKUP.get(level);
        }

        return UNKNOWN;
    }

    static {
        LOOKUP.put(ResultSet.HOLD_CURSORS_OVER_COMMIT, HOLDS);
        LOOKUP.put(ResultSet.CLOSE_CURSORS_AT_COMMIT, CLOSES);
    }
}
