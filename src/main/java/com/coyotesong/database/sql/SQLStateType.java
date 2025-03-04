package com.coyotesong.database.sql;

import com.mysql.cj.jdbc.DatabaseMetaData;

import java.util.HashMap;
import java.util.Map;

/**
 * Possible SQL State values
 */
public enum SQLStateType {
    UNKNOWN,
    DEFAULT,
    SQL99,
    XOPEN
    ;

    private static final Map<Integer, SQLStateType> LOOKUP = new HashMap<>();

    public static SQLStateType valueOf(int level) {
        if (LOOKUP.containsKey(level)) {
            return LOOKUP.get(level);
        }

        return UNKNOWN;
    }

    static {
        LOOKUP.put(DatabaseMetaData.sqlStateSQL, SQLStateType.DEFAULT);
        LOOKUP.put(DatabaseMetaData.sqlStateSQL99, SQLStateType.SQL99);
        LOOKUP.put(DatabaseMetaData.sqlStateXOpen, SQLStateType.XOPEN);
    }
}
