package com.coyotesong.database.sql;

import com.coyotesong.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Pivots {
    private static final Logger LOG = LoggerFactory.getLogger(Pivots.class);

    private final Map<Database, List<String>> numericFunctionsCache = new HashMap<>();
    private final Map<Database, List<String>> sqlKeywordsCache = new HashMap<>();
    private final Map<Database, List<String>> stringFunctionsCache = new HashMap<>();
    private final Map<Database, List<String>> systemFunctionsCache = new HashMap<>();
    private final Map<Database, List<String>> tableTypesCache = new HashMap<>();
    private final Map<Database, List<String>> temporalFunctionsCache = new HashMap<>();
    private final Map<Database, List<TypeInfo>> typeInfoCache = new HashMap<>();
    private final Set<String> numericFunctions = new HashSet<>();
    private final Set<String> sqlKeywords = new HashSet<>();
    private final Set<String> stringFunctions = new HashSet<>();
    private final Set<String> systemFunctions = new HashSet<>();
    private final Set<String> tableTypes = new HashSet<>();
    private final Set<String> temporalFunctions = new HashSet<>();
    private final Set<TypeInfo> types = new HashSet<>();

    public void addDatabase(ExtendedDatabaseMetaData md) {
        if (md.getNumericFunctions() == null) {
            numericFunctionsCache.put(md.getDatabase(), new ArrayList<>());
        } else {
            numericFunctionsCache.put(md.getDatabase(), new ArrayList<>(md.getNumericFunctions()));
            numericFunctions.addAll(md.getNumericFunctions());
        }

        if (md.getSqlKeywords() == null) {
            sqlKeywordsCache.put(md.getDatabase(), new ArrayList<>());
        } else {
            sqlKeywordsCache.put(md.getDatabase(), new ArrayList<>(md.getSqlKeywords()));
            sqlKeywords.addAll(md.getSqlKeywords());
        }

        if (md.getStringFunctions() == null) {
            stringFunctionsCache.put(md.getDatabase(), new ArrayList<>());
        } else {
            stringFunctionsCache.put(md.getDatabase(), new ArrayList<>(md.getStringFunctions()));
            stringFunctions.addAll(md.getStringFunctions());
        }

        if (md.getSystemFunctions() == null) {
            systemFunctionsCache.put(md.getDatabase(), new ArrayList<>());
        } else {
            systemFunctionsCache.put(md.getDatabase(), new ArrayList<>(md.getSystemFunctions()));
            systemFunctions.addAll(md.getSystemFunctions());
        }

        if (md.getTableTypes() == null) {
            tableTypesCache.put(md.getDatabase(), new ArrayList<>());
        } else {
            tableTypesCache.put(md.getDatabase(), new ArrayList<>(md.getTableTypes()));
            tableTypes.addAll(md.getTableTypes());
        }

        if (md.getTemporalFunctions() == null) {
            temporalFunctionsCache.put(md.getDatabase(), new ArrayList<>());
        } else {
            temporalFunctionsCache.put(md.getDatabase(), new ArrayList<>(md.getTemporalFunctions()));
            temporalFunctions.addAll(md.getTemporalFunctions());
        }

        if (md.getTypes() == null) {
            typeInfoCache.put(md.getDatabase(), new ArrayList<>());
        } else {
            typeInfoCache.put(md.getDatabase(), new ArrayList<>(md.getTypes()));
            types.addAll(md.getTypes());
        }
    }

    public List<String> getNumericFunctions() {
        final List<String> results = new ArrayList<>(numericFunctions);
        Collections.sort(results);
        return results;
    }

    public List<String> getSqlKeywords() {
        final List<String> results = new ArrayList<>(sqlKeywords);
        Collections.sort(results);
        return results;
    }

    public List<String> getStringFunctions() {
        final List<String> results = new ArrayList<>(stringFunctions);
        Collections.sort(results);
        return results;
    }

    public List<String> getSystemFunctions() {
        final List<String> results = new ArrayList<>(systemFunctions);
        Collections.sort(results);
        return results;
    }

    public List<String> getTableTypes() {
        final List<String> results = new ArrayList<>(tableTypes);
        Collections.sort(results);
        return results;
    }

    public List<String> getTemporalFunctions() {
        final List<String> results = new ArrayList<>(temporalFunctions);
        Collections.sort(results);
        return results;
    }

    public List<TypeInfo> getTypes() {
        final List<TypeInfo> results = new ArrayList<>(types);
        Collections.sort(results, (s, p) ->
                {
                    if (s.getType() != p.getType()) {
                        return s.getType() - p.getType();
                    }
                    return p.getName().compareToIgnoreCase(s.getName());
                });
        return results;
    }


    public boolean isNumericFunctionSupported(ExtendedDatabaseMetaData md, String key) {
        return numericFunctionsCache.get(md.getDatabase()).contains(key);
    }
}
