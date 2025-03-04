package com.coyotesong.database;

import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TableTypesPivot {
    private static final Logger LOG = LoggerFactory.getLogger(TableTypesPivot.class);

    private final Map<Database, List<String>> cache = new HashMap<>();
    private final Set<String> types = new HashSet<>();

    public void addDatabase(ExtendedDatabaseMetaData md) {
        cache.put(md.getDatabase(), new ArrayList<>(md.getTableTypes()));
        types.addAll(md.getTableTypes());
    }

    public List<String> getTypes() {
        final List<String> results = new ArrayList<>(types);
        Collections.sort(results);
        return results;
    }

    public boolean isSupported(ExtendedDatabaseMetaData md, String type) {
        return cache.get(md.getDatabase()).contains(type);
    }
}
