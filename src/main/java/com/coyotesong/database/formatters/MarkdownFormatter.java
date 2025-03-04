package com.coyotesong.database.formatters;

import com.coyotesong.database.CatalogSchemaSupport;
import com.coyotesong.database.DatabaseComparisons;
import com.coyotesong.database.SqlKeywordsPivot;
import com.coyotesong.database.TableTypesPivot;
import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import com.coyotesong.database.config.ExternalRepositories;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

/**
 * Implementation of OutputFormatter using Markdown
 * <p>
 * This is a stopgap solution - it will be replaced by jinja2 templates.
 */
public class MarkdownFormatter extends AbstractOutputFormatter {
    private static final String MAVEN_REPO_FORMAT = "[%s:%s:%s](https://central.sonatype.com/artifact/%s/%s/%s)";

    private final ExternalRepositories repos;

    public MarkdownFormatter(ExternalRepositories repos, DatabaseComparisons databases) {
        super(databases);
        this.repos = repos;
    }

    // for testing
    protected MarkdownFormatter() {
        super(new DatabaseComparisons());
        this.repos = new ExternalRepositories();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatSummaryTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("| Name | Version | SQL Grammar | Isolation | Holdability | RowID Lifetime | SQL State Type |\n");
        sb.append("|---|:---:|:---:|:---:|:---:|:---:|:---:|\n");
        for (ExtendedDatabaseMetaData md : databases.values()) {
            sb.append(String.format("| %s | %s | %s | %s | %s | %s | %s |\n",
                    md.getDatabaseProductName(), md.getDatabaseMajorVersion(),
                    md.getSqlGrammar(), md.getDefaultTransactionIsolation(),
                    md.getResultSetHoldability(), md.getRowIdLifetime(),
                    md.getSQLStateType()));
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
        for (ExtendedDatabaseMetaData md : databases.values()) {
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
        for (ExtendedDatabaseMetaData md : databases.values()) {
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
    public String formatClientInfoProperties() {
        final StringBuilder sb = new StringBuilder();

        sb.append("| Name | ClientInfo Properties |\n");
        sb.append("|---|---|\n");
        for (ExtendedDatabaseMetaData md : databases.values()) {
            sb.append(String.format("| %s | %s |\n", md.getDatabaseProductName(), String.join(", ", md.getClientInfoProperties())));
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
        for (ExtendedDatabaseMetaData md : databases.values()) {
            String catSchemaTerm = "";
            switch (md.getCatalogSchemaSupport().getSupport(CatalogSchemaSupport.Operation.TABLE_DEFINITIONS)) {
                case CATALOGS_ONLY:
                    catSchemaTerm = String.format("%s%stable", md.getCatalogTerm(), md.getCatalogSeparator());
                    break;
                case SCHEMAS_ONLY:
                    catSchemaTerm = String.format("%s%stable", md.getSchemaTerm(), md.getCatalogSeparator());
                    break;
                case BOTH:
                    catSchemaTerm = String.format("%s%s%s%stable", md.getCatalogTerm(), md.getCatalogSeparator(),
                            md.getSchemaTerm(), md.getCatalogSeparator());
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
    @Override
    public String formatTableTypes() {
        final TableTypesPivot types = databases.getTableTypes();

        final StringBuilder sb = new StringBuilder(formatPropertyHeader());
        sb.append("\n");
        for (String type : types.getTypes()) {
            sb.append("| " + type + " | ");
            for (ExtendedDatabaseMetaData md : databases.values()) {
                sb.append(" | " + format(types.isSupported(md, type)));
            }
            sb.append(" |\n");
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatSqlKeywords() {
        final SqlKeywordsPivot keywords = databases.getSqlKeywords();

        final StringBuilder sb = new StringBuilder(formatPropertyHeader());
        sb.append("\n");
        for (String keyword : keywords.getKeywords()) {
            sb.append("| " + keyword + " | ");
            for (ExtendedDatabaseMetaData md : databases.values()) {
                sb.append(" | " + format(keywords.isSupported(md, keyword)));
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
    @Override
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
        if (repos.getDockerRepos().containsKey(unversionedPart)) {
            return String.format("[%s](%s):%s", unversionedPart, repos.getDockerRepos().get(unversionedPart),
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
}
