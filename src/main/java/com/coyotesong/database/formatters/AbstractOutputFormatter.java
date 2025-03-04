package com.coyotesong.database.formatters;

import com.coyotesong.database.DatabaseComparisons;

import java.sql.SQLFeatureNotSupportedException;
import java.util.function.Predicate;

/**
 * Abstract class that formats the output - Markdown, HTML, xlsx, etc.
 */
abstract public class AbstractOutputFormatter {

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

    abstract public String formatClientInfoProperties();

    abstract public String formatCatalogSchemaSupport();

    abstract public String formatSQLProperties();

    abstract public String formatTableTypes();

    abstract public String formatSqlKeywords();

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
            long v1TiB = 1024L * v1GiB;
            long v1PiB = 1024L * v1TiB;
            long v1EiB = 1024L * v1PiB; // for Oracle LOB

            if (v == 0) {
                return "";
            } else if (v % v1EiB == 0) {
                return Long.toString(v / v1EiB) + " EiB";
            } else if ((v > v1EiB - 1024) && (v % v1EiB > (v1EiB - 1024L))) {
                long vv = 1L + v / v1EiB;
                return Long.toString(vv) + " EiB - " + (vv * v1EiB - v);

            } else if (v % v1PiB == 0) {
                return Long.toString(v / v1PiB) + " PiB";
            } else if ((v > v1PiB - 1024) && (v % v1PiB > (v1PiB - 1024L))) {
                long vv = 1L + v / v1PiB;
                return Long.toString(vv) + " PiB - " + (vv * v1PiB - v);

            } else if (v % v1TiB == 0) {
                return Long.toString(v / v1TiB) + " TiB";
            } else if ((v > v1TiB - 1024) && (v % v1TiB > (v1TiB - 1024L))) {
                long vv = 1L + v / v1TiB;
                return Long.toString(vv) + " TiB - " + (vv * v1TiB - v);

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
            } else if ((v > v1KiB - 256) && (v % v1KiB) > (v1KiB - 256L)) {
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
            return "[" + value.getClass().getSimpleName() + "]";
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
