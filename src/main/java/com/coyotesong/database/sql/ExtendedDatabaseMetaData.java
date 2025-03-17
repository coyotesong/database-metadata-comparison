package com.coyotesong.database.sql;

import com.coyotesong.database.CatalogSchemaSupport;
import com.coyotesong.database.Database;
import com.coyotesong.database.MetadataMethods;
import com.coyotesong.database.config.ExternalRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Parsed database metadata
 * <p>
 * Proper testing of this class would probably require {@code Proxy<DatabaseMetaData>} or
 * adding a hefty additional dependency.
 */
public class ExtendedDatabaseMetaData {
    private static final Logger LOG = LoggerFactory.getLogger(ExtendedDatabaseMetaData.class);

    private static final String MAVEN_REPO_FORMAT = "[%s:%s:%s](https://central.sonatype.com/artifact/%s/%s/%s)";

    private final ExternalRepositories repos;

    private final Database database;
    private Properties clientInfo;
    private final Map<String, Object> properties = new LinkedHashMap<>();
    private final CatalogSchemaSupport catalogSchemaSupport = new CatalogSchemaSupport();

    private String databaseProductName;
    private String databaseProductVersion;
    private int databaseMajorVersion;
    private String driverName;
    private String driverVersion;
    private String driverClassName;
    private String dockerImageName;
    private String jdbcUrl;
    private List<String> clientInfoProperties = new ArrayList<>();

    private TransactionIsolation defaultTransactionIsolation;
    private ResultSetHoldability resultSetHoldability;
    private RowIdLifetime rowIdLifetime;
    private SQLStateType sqlStateType;

    private List<String> tableTypes = new ArrayList<>();
    private List<String> numericFunctions;
    private List<String> sqlKeywords;
    private List<String> stringFunctions;
    private List<String> systemFunctions;
    private List<String> temporalFunctions;

    private List<TypeInfo> types = new ArrayList<>();

    private NullSortPosition nullSortPosition = NullSortPosition.UNKNOWN;
    private SQLGrammar sqlGrammar = SQLGrammar.UNKNOWN;
    private IdentifierStorage identifierStorage = IdentifierStorage.UNKNOWN;
    private IdentifierStorage quotedIdentifierStorage = IdentifierStorage.UNKNOWN;
    private String catalogTerm;
    private String catalogSeparator;
    private String extraNameCharacters;
    private String identifierQuoteString;
    private String procedureTerm;
    private String schemaTerm;
    private String searchStringEscape;

    public ExtendedDatabaseMetaData(Database database) {
        this.database = database;
        this.repos = new ExternalRepositories();
    }

    public ExtendedDatabaseMetaData(Database database, String driverClassName) {
        this(database);
        this.driverClassName = driverClassName;
    }

    public void setDockerImageName(String dockerImageName) {
        this.dockerImageName = dockerImageName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Database getDatabase() {
        return database;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public int getDatabaseMajorVersion() {
        return databaseMajorVersion;
    }

    public String getDockerImageName() {
        return dockerImageName;
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

    public List<String> getClientInfoProperties() {
        return clientInfoProperties;
    }

    public TransactionIsolation getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public ResultSetHoldability getResultSetHoldability() {
        return resultSetHoldability;
    }

    public RowIdLifetime getRowIdLifetime() {
        return rowIdLifetime;
    }

    public SQLGrammar getSqlGrammar() {
        return sqlGrammar;
    }

    public SQLStateType getSqlStateType() {
        return sqlStateType;
    }

    public List<String> getTableTypes() {
        return tableTypes;
    }

    public List<String> getSqlKeywords() {
        return sqlKeywords;
    }

    public List<String> getNumericFunctions() {
        return numericFunctions;
    }

    public List<String> getStringFunctions() {
        return stringFunctions;
    }

    public List<String> getSystemFunctions() {
        return systemFunctions;
    }

    public List<TypeInfo> getTypes() {
        return types;
    }

    public IdentifierStorage getIdentifierStorage() {
        return identifierStorage;
    }

    public IdentifierStorage getQuotedIdentifierStorage() {
        return quotedIdentifierStorage;
    }

    public NullSortPosition getNullSortPosition() {
        return nullSortPosition;
    }

    public CatalogSchemaSupport getCatalogSchemaSupport() {
        return catalogSchemaSupport;
    }

    public List<String> getTemporalFunctions() {
        return temporalFunctions;
    }

    public String getCatalogSeparator() {
        return catalogSeparator;
    }

    public String getCatalogTerm() {
        return catalogTerm;
    }

    public String getExtraNameCharacters() {
        return extraNameCharacters;
    }

    public String getIdentifierQuoteString() {
        return identifierQuoteString;
    }

    public String getProcedureTerm() {
        return procedureTerm;
    }

    public String getSchemaTerm() {
        return schemaTerm;
    }

    public String getSearchStringEscape() {
        return searchStringEscape;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Set<String> keySet() {
        return properties.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) properties.get(key);
    }

    private void put(Method m, DatabaseMetaData md) {
        final Object obj = invoke(m, md);

        // error handling may have already put a value into the properties map.
        if (!properties.containsKey(m.getName())) {
            properties.put(m.getName(), obj);
        }
    }

    private void put(Method m, Object obj) {
        properties.put(m.getName(), obj);
    }

    private <T> T invoke(Method m, DatabaseMetaData md) {
        try {
            return (T) m.invoke(md);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                final Throwable cause = e.getCause();
                String url = null;
                try {
                    url = md.getDatabaseProductName();
                } catch (SQLException sqle) {
                    LOG.warn("{}: {}", sqle.getClass().getName(), sqle.getMessage());
                    put(m, cause);
                }

                if (SQLFeatureNotSupportedException.class.isAssignableFrom(cause.getClass())) {
                    LOG.warn("{}: {}: {}", cause.getClass().getName(), m.getName(), cause.getMessage());
                    put(m, "[not supported]");
                } else if (SQLException.class.isAssignableFrom(cause.getClass())) {
                    LOG.warn("{}: {}: {}", cause.getClass().getName(), m.getName(), cause.getMessage());
                    put(m, cause);
                } else if (NullPointerException.class.isAssignableFrom(cause.getClass())) {
                    LOG.warn("{} when calling '{}.{}': {}", e.getClass().getSimpleName(), url, m.getName(), cause.getMessage(), e);
                    put(m, cause);
                } else if (IllegalMonitorStateException.class.isAssignableFrom(cause.getClass())) {
                    LOG.warn("{} when calling '{}.{}': {}", e.getClass().getSimpleName(), url, m.getName(), cause.getMessage(), e);
                    put(m, cause);
                } else {
                    LOG.warn("{}: {}: {}", cause.getClass().getName(), m.getName(), cause.getMessage(), cause);
                    put(m, cause);
                }
            } else {
                LOG.info("unexpected exception on {}", m.getName(), e);
                put(m, e);
            }
        } catch (IllegalAccessException e) {
            LOG.warn("Unable to access {}#{}: {}", m.getDeclaringClass().getName(), m.getName(), e.getMessage());
            put(m, "[no access]");
        }

        return null;
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
        MetadataMethods.INSTANCE.add(md);

        this.databaseProductName = md.getDatabaseProductName().replace("\n", " ");
        this.databaseProductVersion = md.getDatabaseProductVersion().replace("\n", " ");
        this.databaseMajorVersion = md.getDatabaseMajorVersion();
        this.driverName = md.getDriverName();

        // see org.h2:  2.2.220 (2023-07-04)
        String driverVersion = md.getDriverVersion();
        if (driverVersion.indexOf(" ") > 0) {
            this.driverVersion = driverVersion.substring(0, driverVersion.indexOf(" "));
        } else {
            this.driverVersion = driverVersion;
        }

        int idx = this.driverVersion.indexOf(" (Revision: ");
        if (idx > 0) {
            this.driverVersion = this.driverVersion.substring(0, idx);
        }

        this.jdbcUrl = md.getURL();

        try (ResultSet rs = md.getClientInfoProperties()) {
            while (rs.next()) {
                clientInfoProperties.add(rs.getString(1));
            }
            Collections.sort(clientInfoProperties);
        } catch (SQLFeatureNotSupportedException e) {
            LOG.info("{}: clientInfoProperties not supported", databaseProductName);
        } catch (SQLException e) {
            LOG.warn("{}: {}", e.getClass().getName(), e.getMessage());
        }

        try (ResultSet rs = md.getTableTypes()) {
            while (rs.next()) {
                tableTypes.add(rs.getString(1));
            }
        } catch (SQLException e) {
            LOG.warn("{}: {}", e.getClass().getName(), e.getMessage());
        }

        try (ResultSet rs = md.getTypeInfo()) {
            while (rs.next()) {
                types.add(new TypeInfo(rs.getString("TYPE_NAME"), rs.getInt("DATA_TYPE"), rs.getInt("PRECISION"), rs.getBoolean("AUTO_INCREMENT")));
            }
        }
        this.numericFunctions = new ArrayList<>(Arrays.stream(md.getNumericFunctions().split(",")).map(String::trim).map(String::toUpperCase).toList());
        Collections.sort(this.numericFunctions);

        this.sqlKeywords = new ArrayList<>(Arrays.stream(md.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).toList());
        Collections.sort(this.sqlKeywords);

        this.stringFunctions = new ArrayList<>(Arrays.stream(md.getStringFunctions().split(",")).map(String::trim).map(String::toUpperCase).toList());
        Collections.sort(this.stringFunctions);

        this.systemFunctions = new ArrayList<>(Arrays.stream(md.getSystemFunctions().split(",")).map(String::trim).map(String::toUpperCase).toList());
        Collections.sort(this.systemFunctions);

        this.temporalFunctions = new ArrayList<>(Arrays.stream(md.getTimeDateFunctions().split(",")).map(String::trim).map(String::toUpperCase).toList());
        Collections.sort(this.temporalFunctions);

        // put("getDefaultTransactionIsolation", getDefaultTransactionIsolation(md));
        // put("getResultSetHoldability", getResultSetHoldability(md));
        // put("getSQLStateType", getSQLStateType(md));

        this.nullSortPosition = NullSortPosition.valueOf(md);
        this.sqlGrammar = SQLGrammar.valueOf(md);
        this.identifierStorage = IdentifierStorage.valueOf(md);
        this.quotedIdentifierStorage = IdentifierStorage.quotedValueOf(md);

        // this provides a bit of isolation...
        this.defaultTransactionIsolation = TransactionIsolation.valueOf(md.getDefaultTransactionIsolation());
        this.resultSetHoldability = ResultSetHoldability.valueOf(md.getResultSetHoldability());
        this.rowIdLifetime = RowIdLifetime.valueOf(md);
        this.sqlStateType = SQLStateType.valueOf(md.getSQLStateType());

        this.catalogSeparator = md.getCatalogSeparator();
        this.catalogTerm = md.getCatalogTerm();
        this.extraNameCharacters = md.getExtraNameCharacters();
        this.identifierQuoteString = md.getIdentifierQuoteString();
        this.procedureTerm = md.getProcedureTerm();
        this.schemaTerm = md.getSchemaTerm();
        this.searchStringEscape = md.getSearchStringEscape();

        final Class<? extends DatabaseMetaData> clz = md.getClass();
        final Method[] methods = clz.getMethods();
        // final Method[] methods = DatabaseMetaData.class.getMethods();

        // implementation note: we can't test for Void.class due to multiple classloaders
        final List<Method> methodList = Arrays.stream(methods)
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> !"void".equals(m.getReturnType().getName()))
                .filter(m -> !MetadataMethods.IGNORE_LIST.contains(m.getName()))
                .sorted((p, q) -> p.getName().compareTo(q.getName()))
                .toList();

        for (Method m : methodList) {

            final String name = m.getName();
            if (MetadataMethods.INSTANCE.isSupportCatalogSchemaMethod(name)) {
                catalogSchemaSupport.setValue(m, md);
            } else {
                put(m, md);
            }
        }

        // small tweak for clarity
        if (properties.containsKey("getExtraNameCharacters")) {
            String value = (String) properties.get("getExtraNameCharacters");
            if (!value.isBlank()) {
                List<String> chars = new ArrayList(value.length());
                for (int i = 0; i < value.length(); i++) {
                    chars.add(Character.toString(value.charAt(i)));
                }
                Collections.sort(chars);
                properties.put("getExtraNameCharacters", "'" + String.join("', '", chars) + "'");
            }
        }

        //   types: ResultSet.TYPE_FORWARD, TYPE_SCROLL_INSENSITIVE, TYPE_SCROLL_SELECTIVE
        //   ? deletesArDetected(int type)
        //   ? insertsAreDetected(int type)
        //   ? updatesAreDetected(int type)
        //   ? others*AreVisible(int type)
        //   ? own*AreVisible(int type)


        StringBuilder sb = new StringBuilder();
        this.sqlKeywords.forEach(k -> sb.append("\n  - " + k));
        // LOG.info("SQL Keywords for '{}': {}\n", conn.getSchema(), sb.toString());

    }

    /**
     * Get default transaction isolation
     *
     * @param md
     * @return
     */
    String getDefaultTransactionIsolation(DatabaseMetaData md) {
        final List<String> flags = new ArrayList<>();
        try {
            final int bits = md.getDefaultTransactionIsolation();

            if ((bits & Connection.TRANSACTION_READ_UNCOMMITTED) == Connection.TRANSACTION_READ_UNCOMMITTED) {
                flags.add("UNCOMMITTED");
            }

            if ((bits & Connection.TRANSACTION_READ_COMMITTED) == Connection.TRANSACTION_READ_COMMITTED) {
                flags.add("COMMITTED");
            }

            if ((bits & Connection.TRANSACTION_REPEATABLE_READ) == Connection.TRANSACTION_REPEATABLE_READ) {
                flags.add("REPEATABLE");
            }

            if ((bits & Connection.TRANSACTION_SERIALIZABLE) == Connection.TRANSACTION_SERIALIZABLE) {
                flags.add("REPEATABLE");
            }
        } catch (SQLException e) {
            LOG.info("{}: {}", e.getClass().getName(), e.getMessage(), e);
            return null;
        }

        if (flags.isEmpty()) {
            flags.add("NONE");
        }

        return String.join(", ", flags);
    }

    String getResultSetHoldability(DatabaseMetaData md) {
        try {
            int holdability = md.getResultSetHoldability();
            switch (holdability) {
                case 0:
                    return null;
                case ResultSet.HOLD_CURSORS_OVER_COMMIT:
                    return "HOLD_CURSORS_OVER_COMMIT";
                case ResultSet.CLOSE_CURSORS_AT_COMMIT:
                    return "CLOSE_CURSORS_AT_COMMIT";
                default:
                    return "n/a";
            }
        } catch (SQLException e) {
            LOG.info("{}: {}", e.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }

    String getSQLStateType(DatabaseMetaData md) {
        try {
            final int type = md.getSQLStateType();
            switch (type) {
                case DatabaseMetaData.sqlStateSQL:
                    return "SQL";
                case DatabaseMetaData.sqlStateXOpen:
                    return "X/Open";
                case 0:
                    return null;
                default:
                    return "[ " + Integer.toString(type) + " ]";
            }
        } catch (SQLException e) {
            LOG.info("{}: {}", e.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get Maven Coordinates from driver classname and version
     *
     * @return maven coordinates
     */
    public String getMavenCoordinates() {
        if (StringUtils.isBlank(driverClassName)) {
            return "";
        }

        if (!repos.getMavenRepos().containsKey(driverClassName)) {
            return "unknown";
        }

        final Map.Entry<String, String> entry = repos.getMavenRepos().get(driverClassName).entrySet().iterator().next();
        final String groupId = entry.getKey();
        final String artifactId = entry.getKey();
        return (MAVEN_REPO_FORMAT.formatted(
                groupId, artifactId, driverVersion,
                groupId, artifactId, driverVersion));
    }

    public String getDockerRepo() {
        if (dockerImageName == null) {
            return "alpine:latest";
        }

        final DockerImageName actual = DockerImageName.parse(dockerImageName);
        final String unversionedPart = actual.getUnversionedPart();
        if (repos.getDockerRepos().containsKey(unversionedPart)) {
            return String.format("[%s:%s](%s:%s)", unversionedPart, actual.getVersionPart(),
                    repos.getDockerRepos().get(unversionedPart), actual.getVersionPart());
        }
        return actual.asCanonicalNameString();
    }

    public String getCatSchemaTerm() {
        switch (catalogSchemaSupport.getSupport(CatalogSchemaSupport.Operation.TABLE_DEFINITIONS)) {
            case CATALOGS_ONLY:
                return String.format("%s%stable", catalogTerm, catalogSeparator);
            case SCHEMAS_ONLY:
                return String.format("%s%stable", schemaTerm, catalogSeparator);
            case BOTH:
                return String.format("%s%s%s%stable", catalogTerm, catalogSeparator, schemaTerm, catalogSeparator);
        }
        return null;
    }
}
