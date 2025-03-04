package com.coyotesong.database;

import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SqlKeywordsPivot {
    private static final Logger LOG = LoggerFactory.getLogger(SqlKeywordsPivot.class);

    private final Map<Database, List<String>> cache = new HashMap<>();
    private final Set<String> keywords = new HashSet<>();

    public void addDatabase(ExtendedDatabaseMetaData md) {
        cache.put(md.getDatabase(), new ArrayList<>(md.getSqlKeywords()));
        keywords.addAll(md.getSqlKeywords());
    }

    public List<String> getKeywords() {
        final List<String> results = new ArrayList<>(keywords);
        Collections.sort(results);
        return results;
    }

    public boolean isSupported(ExtendedDatabaseMetaData md, String keyword) {
        return cache.get(md.getDatabase()).contains(keyword);
    }
}
