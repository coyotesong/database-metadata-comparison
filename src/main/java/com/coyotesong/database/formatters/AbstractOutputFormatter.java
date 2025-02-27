package com.coyotesong.database.formatters;

import com.coyotesong.database.DatabaseComparisons;
import com.coyotesong.database.MetadataMethods;

import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Abstract class that formats the output - Markdown, HTML, xlsx, etc.
 */
abstract public class AbstractOutputFormatter {

    protected static final Map<String, String> DOCKER_REPOS = new LinkedHashMap<>();
    protected static final Map<String, Map<String, String>> MAVEN_REPOS = new LinkedHashMap<>();

    static {
        DOCKER_REPOS.put("icr.io/db2_community/db2", "https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker");
        DOCKER_REPOS.put("mysql", "https://hub.docker.com/_/mysql");
        DOCKER_REPOS.put("gvenzl/oracle-xe", "https://hub.docker.com/r/gvenzl/oracle-xe");
        DOCKER_REPOS.put("postgres", "https://hub.docker.com/_/postgres");
        DOCKER_REPOS.put("mcr.microsoft.com/mssql/server", "https://mcr.microsoft.com/en-us/product/mssql/server");
        DOCKER_REPOS.put("mcr.microsoft.com/mssql/rhel/server", "https://mcr.microsoft.com/en-us/product/mssql/rhel/server");

        MAVEN_REPOS.put("com.ibm.db2.jcc.DB2Driver", Collections.singletonMap("com.ibm.db2", "jcc")); // 11.5.9.0
        MAVEN_REPOS.put("com.mysql.cj.jdbc.Driver", Collections.singletonMap("com.mysql", "mysql-connector-j")); // 9.0.0
        MAVEN_REPOS.put("oracle.jdbc.driver.OracleDriver", Collections.singletonMap("com.oracle.database.jdbc", "ojdbc11")); // 23.5.0.24.07
        MAVEN_REPOS.put("org.postgresql.Driver", Collections.singletonMap("org.postgresql", "postgresql")); // 42.7.4
        MAVEN_REPOS.put("org.h2.Driver", Collections.singletonMap("com.h2database", "h2")); // 2.3.232
        MAVEN_REPOS.put("org.sqlite.JDBC", Collections.singletonMap("org.xerial", "sqlite-jdbc")); // 3.46.1.0
        MAVEN_REPOS.put("com.microsoft.sqlserver.jdbc.SQLServerDriver", Collections.singletonMap("com.microsoft.sqlserver", "mssql-jdbc")); // 12.8.1.jre11
    }

    protected static final String MAVEN_REPO_FORMAT = "[%s:%s:%s](https://central.sonatype.com/artifact/%s/%s/%s)";

    protected final DatabaseComparisons databases;

    /**
     * Constructor
     * @param databases
     */
    protected AbstractOutputFormatter(DatabaseComparisons databases) {
        this.databases = databases;
    }

    /**
     * Create a table containing top-level summaries
     *
     * @return
     */
    abstract public String formatSummaryTable();

    /**
     * Create a table containing information about the docker images
     *
     * @return
     */
    abstract public String formatDockerImageTable();

    /**
     * Create a table containing information about the database driver classes.
     *
     * @return
     */
    abstract public String formatDriverTable();

    abstract public String formatCatalogSchemaSupport();

    abstract public String formatSQLProperties();

    /**
     * Create a table header
     *
     * @return
     */
    abstract public String formatPropertyHeader();

    /**
     * Create a table line
     */
    abstract public String formatPropertyLine(String propertyName);

    /**
     * "Open" a table
     *
     * @return
     */
    public String openTable() {
        return "";
    }

    /**
     * "Close" a table
     *
     * @return
     */
    public String closeTable() {
        return "";
    }

    /**
     * General formatter
     * <p>
     * This method is smart enough to recognize values that are "just a little off"
     * from milestones like multiples of KiB, MiB, or GiB.
     *
     * @param value
     * @return
     */
    protected String format(Object value) {
        if (value == null) {
            return "[null]";
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Boolean) {
            if ((Boolean) value) {
                // return Character.toString(0x1F5F8); // check mark
                // return Character.toString(0x1F5F9); // ballot box with bold check mark
                return Character.toString(0x2611); // ballot box with check mark
            } else {
                return "";
                //      return Character.toString(0x10102); // hash (X) mark
            }
        } else if (value instanceof Number) {
            // should be a bit more clever here...
            long v = ((Number) value).longValue();
            long v1KiB = 1024L;
            long v1MiB = 1024L * v1KiB;
            long v1GiB = 1024L * v1MiB;

            if (v == 0) {
                return "";
            } else if (v % v1GiB == 0) {
                return Long.toString(v / v1GiB) + " GiB";
            } else if ((v > v1GiB - 1024) && (v % v1GiB > (v1GiB - 1024L))) {
                long vv = 1L + v / v1GiB;
                return Long.toString(vv) + " GiB - " + (vv * v1GiB - v);

            } else if ((v % v1MiB) == 0L) {
                return Long.toString(v / v1MiB) + " MiB";
            } else if ((v > v1MiB - 1024) && (v % v1MiB) > (v1MiB - 1024L)) {
                long vv = 1L + (v / v1MiB);
                return Long.toString(vv) + " MiB - " + (vv * v1MiB - v);

            } else if ((v % v1KiB) == 0L) {
                return Long.toString(v / v1KiB) + " kiB";
            } else if ((v > v1KiB - 100) && (v % v1KiB) > (v1KiB - 100L)) {
                long vv = 1L + (v / v1KiB);
                return Long.toString(vv) + " kiB - " + (vv * v1KiB - v);

            } else {
                return String.valueOf(v);
            }
        } else if (value instanceof SQLFeatureNotSupportedException) {
            // return "[Method not yet implemented]";
            return "_n/a_";
        } else if (value instanceof Exception) {
            return "[Ex]";
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * Create a table containing values that satisfy the predicate
     *
     * @param predicate
     * @return
     */
    public String formatPropertyTable(Predicate<String> predicate) {
        final StringBuilder sb = new StringBuilder();

        sb.append(openTable());
        sb.append(formatPropertyHeader());
        sb.append("\n");
        for (String key : databases.getPropertyNames()) {
            if (predicate.test(key)) {
                sb.append(formatPropertyLine(key));
                sb.append("\n");
            }
        }
        sb.append(closeTable());

        return sb.toString();
    }
}
