package com.coyotesong.database.formatters;

import com.coyotesong.database.CatalogSchemaSupport;
import com.coyotesong.database.DatabaseComparisons;
import com.coyotesong.database.MyDatabaseMetaData;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

/**
 * Implementation of OutputFormatter using Markdown
 * <p>
 * This is a stopgap solution - it will be replaced by jinja2 templates.
 */
public class MarkdownFormatter extends AbstractOutputFormatter {
    public MarkdownFormatter(DatabaseComparisons databases) {
        super(databases);
    }

    // for testing
    protected MarkdownFormatter() {
        super(new DatabaseComparisons());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatSummaryTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("| Name | Version | SQL Grammar | Isolation | Holdability | RowID Lifetime | SQL State Type |\n");
        sb.append("|---|:---:|:---:|:---:|:---:|:---:|:---:|\n");
        for (MyDatabaseMetaData md : databases.values()) {
            sb.append(String.format("| %s | %s | %s | %s | %s | %s | %s |\n",
                    md.getDatabaseProductName(), md.getDatabaseMajorVersion(),
                    md.getSqlGrammar(), md.get("getDefaultTransactionIsolation"),
                    md.get("getResultSetHoldability"), format(md.get("getRowIdLifetime")),
                    md.get("getSQLStateType")));
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatDockerImageTable() {
        final StringBuilder sb = new StringBuilder();

        sb.append("| Name | Version | Docker Image Name |\n");
        sb.append("|---|:---:|:---|\n");
        for (MyDatabaseMetaData md : databases.values()) {
            sb.append(String.format("| %s | %d | %s |\n", md.getDatabaseProductName(),
                    md.getDatabaseMajorVersion(), getDockerRepo(md.getDockerImageName())));
        }

        sb.append("\n");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatDriverTable() {
        final StringBuilder sb = new StringBuilder();

        sb.append("| Name | Version | Driver Classname | Maven Coordinates |\n");
        sb.append("|---|:---:|:---|:---|\n");
        for (MyDatabaseMetaData md : databases.values()) {
            sb.append(String.format("| %s | %d | %s | %s |\n", md.getDatabaseProductName(),
                    md.getDatabaseMajorVersion(), md.getDriverClassName(),
                    getMavenCoordinates(md.getDriverClassName(), md.getDriverVersion())));
        }

        sb.append("\n");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatSQLProperties() {
        final StringBuilder sb = new StringBuilder();

        sb.append("| Name | Version | Full Tablename | Procedure Term | Quote | Escape | Extra | Nulls Sort | Identifier | Quoted Identifier |\n");
        sb.append("|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|\n");
        for (MyDatabaseMetaData md : databases.values()) {
            String catSchemaTerm = "";
            switch (md.getCatalogSchemaSupport().getSupport(CatalogSchemaSupport.Operation.TABLE_DEFINITIONS)) {
                case CATALOGS_ONLY:
                    catSchemaTerm = String.format("%s%stable", md.getCatalogTerm(), md.get("getCatalogSeparator"));
                    break;
                case SCHEMAS_ONLY:
                    catSchemaTerm = String.format("%s%stable", md.getSchemaTerm(), md.get("getCatalogSeparator"));
                    break;
                case BOTH:
                    catSchemaTerm = String.format("%s%s%s%stable", md.getCatalogTerm(), md.get("getCatalogSeparator"),
                            md.getSchemaTerm(), md.get("getCatalogSeparator"));
                    break;
                case NONE:
            }
            sb.append(String.format("| %s | %d | %s | %s | %s | %s | %s | %s | %s | %s |\n",
                    md.getDatabaseProductName(), md.getDatabaseMajorVersion(), catSchemaTerm,
                    md.get("getProcedureTerm"), md.get("getIdentifierQuoteString"), md.get("getSearchStringEscape"),
                    md.get("getExtraNameCharacters"), md.getNullSortPosition().getLabel(),
                    md.getIdentifierStorage(), md.getQuotedIdentifierStorage()));
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatCatalogSchemaSupport() {
        StringBuilder sb = new StringBuilder(formatPropertyHeader());
        sb.append("\n");
        for (CatalogSchemaSupport.Operation operation : CatalogSchemaSupport.Operation.values()) {
            sb.append("| ");
            sb.append(operation.getLabel());
            for (MyDatabaseMetaData md : databases.values()) {
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
    @Override
    public String formatPropertyHeader() {
        StringBuilder sb = new StringBuilder();

        // list databases
        sb.append("| ");
        for (MyDatabaseMetaData md : databases.values()) {
            sb.append(" | ");
            sb.append(md.getDatabaseProductName());
        }
        sb.append(" |\n");

        // add column definitions
        sb.append("|---");
        for (MyDatabaseMetaData md : databases.values()) {
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
    @Override
    public String formatPropertyLine(String propertyName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("| ");
        sb.append(simplify(propertyName));

        for (MyDatabaseMetaData md : databases.values()) {
            sb.append(" | ");
            if (md.containsKey(propertyName)) {
                sb.append(format(md.get(propertyName)));
            }
        }
        sb.append(" |");

        return sb.toString();
    }

    /**
     * Get docker repo from docker image name
     *
     * @param name docker image name
     * @return docker repo
     */
    protected String getDockerRepo(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        } else if ("alpine:latest".equals(name)) {
            return "n/a";
        }

        final DockerImageName dockerImageName = DockerImageName.parse(name);
        final String unversionedPart = dockerImageName.getUnversionedPart();
        if (DOCKER_REPOS.containsKey(unversionedPart)) {
            return String.format("[%s](%s):%s", unversionedPart, DOCKER_REPOS.get(unversionedPart),
                    dockerImageName.getVersionPart());
        }
        return dockerImageName.asCanonicalNameString();
    }

    /**
     * Get Maven Coordinates from driver classname and version
     *
     * @param driverClassName driver classname
     * @param driverVersion driver version
     * @return maven coordinates
     */
    protected String getMavenCoordinates(String driverClassName, String driverVersion) {
        if (StringUtils.isBlank(driverClassName)) {
            return "";
        }

        if (!MAVEN_REPOS.containsKey(driverClassName)) {
            return "unknown";
        }

        // there should only be a single entry, but...
        for (Map.Entry<String, String> entry : MAVEN_REPOS.get(driverClassName).entrySet()) {
            return MAVEN_REPO_FORMAT.formatted(
                    entry.getKey(), entry.getValue(), driverVersion,
                    entry.getKey(), entry.getValue(), driverVersion);
        }

        return "unknown";
    }
}
