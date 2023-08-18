package com.coyotesong.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.function.Predicate;

public class StandardDatabases {

    final Map<Database, MyDatabaseMetaData> databases = new LinkedHashMap<>();
    final List<String> propertyNames = new ArrayList<>();

    public Markdown getMarkdown() {
        return new Markdown(this);
    }

    public void initialize() throws SQLException {
        // old: "ibmcom/db2"  (https://hub.docker.com/r/ibmcom/db2)
        // new: "icr.io/db2_community/db2");
        // https://hub.docker.com/_/microsoft-mssql-server

        for (Database db : Database.values()) {
            if (db.getContainerClass() != null) {
                databases.put(db, MyDatabaseMetaData.getMetadataFor(db));
            }
        }

        databases.put(Database.H2, getH2MetaData());
        databases.put(Database.SQLITE, getSQLiteMetaData());

        final Set<String> keySet = new HashSet<>();
        for (MyDatabaseMetaData md : databases.values()) {
            keySet.addAll(md.keySet());
        }

        propertyNames.clear();
        propertyNames.addAll(keySet);
        Collections.sort(propertyNames);
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public Collection<MyDatabaseMetaData> values() {
        return databases.values();
    }

    public boolean isBasicProperty(String key) {
        return !key.startsWith("getMax") && !(databases.get(Database.MYSQL).get(key) instanceof Boolean);
    }

    public boolean isLimitProperty(String key) {
        return key.startsWith("getMax") && (databases.get(Database.MYSQL).get(key) instanceof Number);
    }

    public boolean isBooleanProperty(String key) {
        return databases.get(Database.MYSQL).get(key) instanceof Boolean;
    }

    public Set<Map.Entry<Database, MyDatabaseMetaData>> entrySet() {
        return databases.entrySet();
    }

    private MyDatabaseMetaData getH2MetaData() throws SQLException {
        final MyDatabaseMetaData results = new MyDatabaseMetaData("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:")) {
            results.loadMetadata(conn);
        }
        return results;
    }

    private MyDatabaseMetaData getSQLiteMetaData() throws SQLException {
        final MyDatabaseMetaData results = new MyDatabaseMetaData("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            results.loadMetadata(conn);
        }
        return results;
    }

    /**
     * Abstract class that formats the output - Markdown, HTML, xlsx, etc.
     */
    abstract public static class OutputFormatter {
        protected final StandardDatabases databases;

        protected OutputFormatter(StandardDatabases databases) {
            this.databases = databases;
        }

        abstract public String formatPropertyHeader();
        abstract public String formatPropertyLine(String propertyName);

        public String openTable() {
            return "";
        }

        public String closeTable() {
            return "";
        }

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
                    return "n/a";
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

        public String formatTable(Predicate<String> predicate) {
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

    public static class Markdown extends OutputFormatter {

        Markdown(StandardDatabases databases) {
            super(databases);
        }

        public String formatPropertyHeader() {
            StringBuilder sb = new StringBuilder();

            // list databases
            sb.append("| ");
            for (Database db : Database.values()) {
                sb.append(" | ");
                sb.append(db.getLabel());
            }
            sb.append(" |\n");

            // add column definitions
            sb.append("|---");
            for (int i = 0; i < Database.values().length; i++) {
                // TODO: add alignment
                sb.append("|---");
            }
            sb.append(" |");

            return sb.toString();
        }

        public String formatPropertyLine(String propertyName) {
            final StringBuilder sb = new StringBuilder();
            sb.append("| ");
            sb.append(propertyName);
            for (MyDatabaseMetaData value : databases.values()) {
                sb.append(" | ");
                if (value.containsKey(propertyName)) {
                    sb.append(format(value.get(propertyName)));
                }
            }
            sb.append(" |");

            return sb.toString();
        }

        public String formatDatabaseDetails() {
            final StringBuilder sb = new StringBuilder();

            /*
                        pw.printf("| Database Product | %s | %s | %s | %s | %s | %s | %s | %s |\n",
            db2.getDatabaseProductName(), mssql.getDatabaseProductName(), mysql8.getDatabaseProductName(),
            oracle.getDatabaseProductName(), postgresql.getDatabaseProductName(), yugabytedb.getDatabaseProductName(),
                h2.getDatabaseProductName(), sqlite.getDatabaseProductName());
        pw.printf("| | %s | %s | %s | %s | %s | %s | %s | %s |\n",
                db2.getDatabaseProductVersion(), mssql.getDatabaseProductVersion(), mysql8.getDatabaseProductVersion(),
                oracle.getDatabaseProductVersion(), postgresql.getDatabaseProductVersion(), yugabytedb.getDatabaseProductVersion(),
                h2.getDatabaseProductVersion(), sqlite.getDatabaseProductVersion());
        pw.printf("| JDBC Driver | %s | %s | %s | %s | %s | %s | %s | %s |\n",
                db2.getDriverClassName(), mssql.getDriverClassName(), mysql8.getDriverClassName(),
                oracle.getDriverClassName(), postgresql.getDriverClassName(), yugabytedb.getDriverClassName(),
                h2.getDriverClassName(), sqlite.getDriverClassName());
        pw.printf("| | %s | %s | %s | %s | %s | %s | %s | %s |\n",
                db2.getDriverVersion(), mssql.getDriverVersion(), mysql8.getDriverVersion(),
                oracle.getDriverVersion(), postgresql.getDriverVersion(), yugabytedb.getDriverVersion(),
                h2.getDriverVersion(), sqlite.getDriverVersion());
        pw.printf("| Docker Image | %s | %s | %s | %s | %s | %s | | |\n",
                db2.getDockerImageName(), mssql.getDockerImageName(), mysql8.getDockerImageName(),
                oracle.getDockerImageName(), postgresql.getDockerImageName(), yugabytedb.getDockerImageName());
             */

            return sb.toString();
        }
    }
}
