package com.coyotesong.database.formatters;

import com.coyotesong.database.CatalogSchemaSupport;
import com.coyotesong.database.DatabaseComparisons;
import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import com.coyotesong.database.config.ExternalRepositories;

import java.util.List;

/**
 * Implementation of OutputFormatter using Markdown
 * <p>
 * This is a stopgap solution - it is being replaced by Jinja2Formatter with converted
 * portions already deleted.
 */
public class OldMarkdownFormatter extends AbstractOutputFormatter {
    private final ExternalRepositories repos;

    public OldMarkdownFormatter(ExternalRepositories repos, DatabaseComparisons databases) {
        super(databases);
        this.repos = repos;
    }

    // for testing
    protected OldMarkdownFormatter() {
        super(new DatabaseComparisons());
        this.repos = new ExternalRepositories();
    }

    /**
     * {@inheritDoc}
     */
    public String formatCatalogSchemaSupport() {
        StringBuilder sb = new StringBuilder(formatPropertyHeader());
        sb.append("\n");
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            sb.append("| ");
            sb.append(operation.getLabel());
            for (ExtendedDatabaseMetaData md : databases.values()) {
                sb.append(" | ");
                final CatalogSchemaSupport value = md.getCatalogSchemaSupport();
                if (value != null) {
                    sb.append(value.getSupport(operation).getLabel());
                }
            }
            sb.append(" |\n");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String formatTableTypes() {
        final List<String> types = databases.getTableTypes();

        final StringBuilder sb = new StringBuilder(formatPropertyHeader());
        sb.append("\n");
        for (String type : types) {
            sb.append("| " + type + " | ");
            for (ExtendedDatabaseMetaData md : databases.values()) {
                // sb.append(" | " + format(types.isSupported(md, type)));
            }
            sb.append(" |\n");
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String formatSqlKeywords() {
        final List<String> keywords = databases.getSqlKeywords();

        final StringBuilder sb = new StringBuilder(formatPropertyHeader());
        sb.append("\n");
        for (String keyword : keywords) {
            sb.append("| " + keyword + " | ");
            for (ExtendedDatabaseMetaData md : databases.values()) {
                // sb.append(" | " + format(keywords.isSupported(md, keyword)));
            }
            sb.append(" |\n");
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String formatPropertyHeader() {
        StringBuilder sb = new StringBuilder();

        // list databases
        sb.append("| ");
        for (ExtendedDatabaseMetaData md : databases.values()) {
            sb.append(" | ");
            sb.append(md.getDatabaseProductName());
        }
        sb.append(" |\n");

        // add column definitions
        sb.append("|---");
        for (ExtendedDatabaseMetaData md : databases.values()) {
            sb.append("|:---:");
        }
        sb.append(" |");

        return sb.toString();
    }

    String simplify(String propertyName) {
        // save some effort...
        if ("isResetRequiredForDB2eWLM".equals(propertyName)) {
            return "is reset required for DB2eWWLM";
        }

        int idx = 0;
        if (propertyName.startsWith("supports")) {
            idx = 8;
        } else if (propertyName.startsWith("get")) {
            idx = 3;
        }

        StringBuilder sb = new StringBuilder();
        for (char c : propertyName.substring(idx).toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append(" ");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        String s = sb.toString()
                .replace("s q l ", "SQL")
                .replace("i d s", "IDS")
                .replace("r d b", "RDB")
                .replace("s c n", "SCN")
                .replace("d b2", "DB2")
                .replace("j c c", "jcc")
                .replace("u r l", "URL");

        return s.trim();
    }

    /**
     * {@inheritDoc}
     */
    public String formatPropertyLine(String propertyName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("| ");
        sb.append(simplify(propertyName));

        for (ExtendedDatabaseMetaData md : databases.values()) {
            sb.append(" | ");
            if (md.containsKey(propertyName)) {
                sb.append(format(md.get(propertyName)));
            }
        }
        sb.append(" |");

        return sb.toString();
    }
}
