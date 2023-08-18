package com.coyotesong.database;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class MyDatabaseMetaData {
    private static final Logger LOG = LoggerFactory.getLogger(MyDatabaseMetaData.class);

    private Properties clientInfo;
    private final HashMap<String, Object> properties = new LinkedHashMap<>();
    private String databaseProductName;
    private String databaseProductVersion;
    private String driverName;
    private String driverVersion;
    private String driverClassName;
    private String dockerImageName;
/*
        this.numericFunctions = md.getNumericFunctions();
        this.SQLKeywords = md.getSQLKeywords();
        this.stringFunctions = md.getStringFunctions();
        this.systemFunctions = md.getSystemFunctions();
        this.timeDateFunctions = md.getTimeDateFunctions(); */

    private String numericFunctions;
    private String sqlKeywords;
    private String stringFunctions;
    private String systemFunctions;
    private String timeDateFunctions;

    public MyDatabaseMetaData() {

    }

    public MyDatabaseMetaData(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    void put(String key, Object value) {
        properties.put(key, value);
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getDockerImageName() {
        return dockerImageName;
    }

    public String getNumericFunctions() {
        return numericFunctions;
    }

    public String getSQLKeywords() {
        return sqlKeywords;
    }

    public String getStringFunctions() {
        return stringFunctions;
    }

    public String getSystemFunctions() {
        return systemFunctions;
    }

    public String getTimeDateFunctions() {
        return timeDateFunctions;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public Set<String> keySet() {
        return properties.keySet();
    }

    private void put(Method m, DatabaseMetaData md) {
        try {
            put(m.getName(), m.invoke(md));
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                if (SQLFeatureNotSupportedException.class.isAssignableFrom(e.getCause().getClass())) {
                    LOG.info(e.getCause().getMessage());
                    put(m.getName(), e.getCause());
                } else {
                    LOG.info("unexpected exception on {}", m.getName(), e.getCause());
                    put(m.getName(), e.getCause());
                }
            } else {
                LOG.info("unexpected exception on {}", m.getName(), e);
                put(m.getName(), e);
            }
        } catch (IllegalAccessException e) {
            LOG.info("unexpected exception on {}", m.getName(), e);
            put(m.getName(), e);
        }
    }

    /**
     * Extract Connection metadata
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public void loadMetadata(Connection conn) throws SQLException {
        this.clientInfo = conn.getClientInfo();

        final DatabaseMetaData md = conn.getMetaData();
        this.databaseProductName = md.getDatabaseProductName().replace("\n", " ");
        this.databaseProductVersion = md.getDatabaseProductVersion().replace("\n", " ");
        this.driverName = md.getDriverName();
        this.driverVersion = md.getDriverVersion();

        int idx = this.driverVersion.indexOf(" (Revision: ");
        if (idx > 0) {
            this.driverVersion = this.driverVersion.substring(0, idx);
        }

        this.numericFunctions = md.getNumericFunctions();
        //Arrays.stream(db2.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
        this.sqlKeywords = md.getSQLKeywords();
        this.stringFunctions = md.getStringFunctions();
        this.systemFunctions = md.getSystemFunctions();
        this.timeDateFunctions = md.getTimeDateFunctions();

        switch (md.getDefaultTransactionIsolation()) {
            case Connection.TRANSACTION_NONE:
                put("getDefaultTransactionIsolation", "NONE");
                break;
            case Connection.TRANSACTION_READ_COMMITTED:
                put("getDefaultTransactionIsolation", "READ_COMMITTED");
                break;
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                put("getDefaultTransactionIsolation", "READ_UNCOMMITTED");
                break;
            case Connection.TRANSACTION_REPEATABLE_READ:
                put("getDefaultTransactionIsolation", "REPEATABLE_READ");
                break;
            case Connection.TRANSACTION_SERIALIZABLE:
                put("getDefaultTransactionIsolation", "SERIALIZABLE");
                break;
            default:
                put("getDefaultTransactionIsolation", md.getDefaultTransactionIsolation());
        }

        switch (md.getResultSetHoldability()) {
            case ResultSet.HOLD_CURSORS_OVER_COMMIT:
                put("getResultSetHoldability", "HOLD_CURSORS_OVER_COMMIT");
                break;
            case ResultSet.CLOSE_CURSORS_AT_COMMIT:
                put("getResultSetHoldability", "CLOSE_CURSORS_AT_COMMIT");
                break;
            default:
                put("getResultSetHoldability", md.getResultSetHoldability());
        }

        final int sst = md.getSQLStateType();
        if (DatabaseMetaData.sqlStateSQL == sst) {
            put("getSQLStateType", "SQL");
        } else if (DatabaseMetaData.sqlStateXOpen == sst) {
            put("getSQLStateType", "X/Open");
        } else {
            put("getSQLStateType", sst);
        }

        //final Class<? extends DatabaseMetaData> clz = md.getClass();
        //final Method[] methods = clz.getMethods();
        final Method[] methods = DatabaseMetaData.class.getMethods();

        final List<Method> methodList = new ArrayList<>(Arrays.asList(methods));
        methodList.sort((p, q) -> p.getName().compareTo(q.getName()));

        for (Method m : methodList) {
            final Class<?> returnType = m.getReturnType();
            if ((Void.class.equals(returnType) || m.getParameterCount() > 0)) {
                continue;
            }

            final String key = m.getName();

            // if ("unwrap".equals(m.getName())) {
            //     continue;
            // }

            if (ResultSet.class.equals(returnType)) {
                switch (key) {
                    case "getCatalogs":     // TABLE_CAT (string)
                    case "getTableTypes":   // TABLE_TYPE (string) (TABLE, VIEW, etc.)
                    case "getTypeInfo":     // TYPE_NAME, DATA_TYPE, PRECISION, ....
                        LOG.info("extraction not implemented yet: ({})", key);
                        break;
                    case "getSchemas":      // requires 'catalog'
                    case "getClientInfoProperties":
                        // will not implement
                        break;
                    default:
                        LOG.info("unexpected method: ({})", key);
                }
                // getFunctions(catalog, schemaPattern, functionMamePattern)
                // getProcedures(catalog, schemaPattern, procedureNamePattern);
                // getUDTs(catalog, schemaPattern, typeNamePattern, int[] types)
            } else {
                switch (key) {
                    case "getConnection":
                    case "getURL":
                    case "getUserName":
                    case "isReadOnly":
                        // ignore entirely
                        break;

                    case "getDatabaseMajorVersion":
                    case "getDatabaseMinorVersion":
                    case "getDatabaseProductName":
                    case "getDatabaseProductVersion":
                    case "getDriverMajorVersion":
                    case "getDriverMinorVersion":
                    case "getDriverName":
                    case "getDriverVersion":
                        // captured above
                        break;

                    case "getJDBCMajorVersion":
                    case "getJDBCMinorVersion":
                        // do we want to capture this?
                        break;

                    case "getNumericFunctions":
                    case "getSQLKeywords":
                    case "getStringFunctions":
                    case "getSystemFunctions":
                    case "getTimeDateFunctions":
                    case "getDefaultTransactionIsolation":
                    case "getResultSetHoldability":
                    case "getSQLStateType":
                        // captured above
                        break;

                    default:
                        put(m, md);
                }
            }
        }

        //   types: ResultSet.TYPE_FORWARD, TYPE_SCROLL_INSENSITIVE, TYPE_SCROLL_SELECTIVE
        //   ? deletesArDetected(int type)
        //   ? insertsAreDetected(int type)
        //   ? updatesAreDetected(int type)
        //   ? others*AreVisible(int type)
        //   ? own*AreVisible(int type)
    }

    static private <S extends JdbcDatabaseContainer<?>> MyDatabaseMetaData capture(Database dbe, S db) throws SQLException {
        final MyDatabaseMetaData results = new MyDatabaseMetaData();
        for (String env : dbe.getEnv()) {
            final String[] s = env.split("=");
            db.addEnv(s[0], s[1]);
        }

        db.start();
        results.driverClassName = db.getDriverClassName();
        results.dockerImageName = db.getDockerImageName();
        try (Connection conn = db.createConnection("")) {
            results.loadMetadata(conn);
            return results;
        }
    }

    static <S extends JdbcDatabaseContainer<?>> MyDatabaseMetaData getMetadataFor(@NotNull Database dbe) throws SQLException {
        Constructor<S> ctor = null;
        try {
            if (dbe.getImageName() == null) {
                ctor = (Constructor<S>) dbe.getContainerClass().getConstructor();
            } else {
                ctor = (Constructor<S>) dbe.getContainerClass().getConstructor(DockerImageName.class);
            }
        } catch (NoSuchMethodException e) {
            LOG.info("unable to create container for {}: {}", ctor.getClass().getSimpleName(), e.getMessage(), e);
            throw new SQLException("unable to find constructor");
        }

        if (dbe.getImageName() == null) {
            try (S db = ctor.newInstance()) {
                return capture(dbe, db);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException e) {
                LOG.info("unable to create container for {}: {}", ctor.getClass().getSimpleName(), e.getMessage(), e);
                throw new SQLException("unable to create container");
            }
        } else {
            try (S db = ctor.newInstance(dbe.getImageName())) {
                return capture(dbe, db);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException e) {
                LOG.info("unable to create container for {}: {}", ctor.getClass().getSimpleName(), e.getMessage(), e);
                throw new SQLException("unable to create container");
            }
        }
    }
}
