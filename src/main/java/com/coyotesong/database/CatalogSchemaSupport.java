package com.coyotesong.database;

import com.coyotesong.database.sql.NullSortPosition;
import com.coyotesong.database.sql.SQLGrammar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class CatalogSchemaSupport {
    private static final Logger LOG = LoggerFactory.getLogger(CatalogSchemaSupport.class);

    public enum Operation {
        DATA_MANIPULATION("Data Manipulation", "supports%sInDataManipulation"),
        INDEX_DEFINITIONS("Index Definitions", "supports%sInIndexDefinitions"),
        PRIVILEGE_DEFINITIONS("Privilege Definitions", "supports%sInPrivilegeDefinitions"),
        PROCEDURE_CALLS("Procedure Calls", "supports%sInProcedureCalls"),
        TABLE_DEFINITIONS("Table Definitions", "supports%sInTableDefinitions");

        private String label;
        private String pattern;

        Operation(String label, String pattern) {
            this.label = label;
            this.pattern = pattern;

        }

        public String getLabel() {
            return label;
        }

        public String getPattern() {
            return pattern;
        }
    }

    public enum Support {
        NONE("None"),
        CATALOGS_ONLY("Catalogs"),
        SCHEMAS_ONLY("Schemas"),
        BOTH("Both")
        ;

        private final String label;

        Support(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    final static Map<String, Operation> LOOKUP = new LinkedHashMap<>();

    static {
        for (Operation operation : Operation.values()) {
            LOOKUP.put(operation.pattern.formatted("Catalogs"), operation);
            LOOKUP.put(operation.pattern.formatted("Schemas"), operation);
        }
    }

    final Map<Operation, Boolean> catalogSupports = new LinkedHashMap<>(Operation.values().length);
    final Map<Operation, Boolean> schemaSupports = new LinkedHashMap<>(Operation.values().length);

    public CatalogSchemaSupport() {
        for (Operation operation : Operation.values()) {
            catalogSupports.put(operation, false);
            schemaSupports.put(operation, false);
        }
    }

    /**
     * Execute 'getter' and update this object
     *
     * @param getter getter method
     * @param md DatabaseMetaData object
     */
    public void setValue(Method getter, DatabaseMetaData md) {
        final Operation operation = LOOKUP.get(getter.getName());
        if (operation != null) {
            try {
                setValue(operation, getter.getName(), (boolean) getter.invoke(md));
            } catch (InvocationTargetException e) {
                if (e.getCause() != null) {
                    final Throwable cause = e.getCause();
                    if (SQLFeatureNotSupportedException.class.isAssignableFrom(cause.getClass())) {
                        LOG.warn("{}: {}: {}", cause.getClass().getName(), getter.getName(), cause.getMessage());
                    } else if (SQLException.class.isAssignableFrom(cause.getClass())) {
                        LOG.warn("{}: {}: {}", cause.getClass().getName(), getter.getName(), cause.getMessage());
                    } else {
                        LOG.info("{}: {}: {}", cause.getClass().getName(), getter.getName(), cause.getMessage(), cause);
                    }
                }
            } catch (IllegalAccessException e) {
                LOG.info("unexpected exception on {}", getter.getName(), e);
            }
        } else {
            LOG.info("unrecognized method: {}", getter.getName());
        }
    }

    /**
     * Set values (for testing)
     *
     * @param operation operation
     * @param getter getter method
     * @param value value
     */
    void setValue(Operation operation, String getter, boolean value) {
        if (getter.contains("Catalogs")) {
            catalogSupports.put(operation, value);
        } else if (getter.contains("Schemas")) {
            schemaSupports.put(operation, value);
        } else {
            LOG.info("unrecognized method: {}", getter);
        }
    }

    /**
     * Get catalog and schema support for operation
     *
     * @param operation operation
     * @return catalog and schema support (enum)
     */
    public Support getSupport(Operation operation) {
        Support support;
        if (catalogSupports.get(operation) && schemaSupports.get(operation)) {
            support = Support.BOTH;
        } else if (catalogSupports.get(operation)) {
            support = Support.CATALOGS_ONLY;
        } else if (schemaSupports.get(operation)) {
            support = Support.SCHEMAS_ONLY;
        } else {
            support = Support.NONE;
        }

        return support;
    }

}
