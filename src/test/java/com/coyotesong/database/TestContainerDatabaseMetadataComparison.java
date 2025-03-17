package com.coyotesong.database;

import com.coyotesong.database.config.ExternalRepositories;
import com.coyotesong.database.formatters.Jinja2Formatter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.*;
import java.sql.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Produce spreadsheet containing information about multiple databases
 */
@Testcontainers
public class TestContainerDatabaseMetadataComparison {
    private static final Logger LOG = LoggerFactory.getLogger(TestContainerDatabaseMetadataComparison.class);

    static {
        // from online examples...
        // Postgres JDBC driver uses JUL; disable it to avoid annoying, irrelevant, stderr logs during connection testing
        //
        //java.util.logging.LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.INFO);
    }

    // think of this as injected...
    private final ExternalRepositories repos = new ExternalRepositories();


    @Test
    public void test() throws SQLException, IOException, InterruptedException {
        final DatabaseComparisons databases = new DatabaseComparisons();
        databases.initialize();
        LOG.info("statistics:\n{}", MetadataMethods.INSTANCE.toString());

        // MarkdownFormatter md = new MarkdownFormatter(repos, databases);
        Jinja2Formatter md = new Jinja2Formatter(repos, databases);

        // try (StringWriter w = new StringWriter();
        try (Writer w = new FileWriter("/tmp/database-comparison.md");
             PrintWriter pw = new PrintWriter(w)) {

            md.formatGeneral(pw);
            pw.flush();
        } catch (IOException e) {
            LOG.error("{}: {}", e.getClass().getName(), e.getMessage(), e);
        }

            /*
            pw.println();
            pw.println("## Catalog and Schema Support");
            pw.println(md.formatCatalogSchemaSupport());

            pw.println();
            pw.println("## SQL Properties");
            pw.println(md.formatSQLProperties());

            pw.println();
            pw.println("## Limits");
            pw.println(md.formatPropertyTable(databases::isLimitProperty)); // right

            pw.println();
            pw.println("## Table Types");
            pw.println(md.formatTableTypes());

            pw.println();
            pw.println("## DDL Statements");
            pw.println(md.formatPropertyTable(databases::isDdlProperty));

            pw.println();
            pw.println("## DML Statements");
            pw.println(md.formatPropertyTable(databases::isDmlProperty));

            pw.println();
            pw.println("## Transactions");
            pw.println(md.formatPropertyTable(databases::isTransactionsProperty));

            pw.println();
            pw.println("## Stored Procedures");
            pw.println(md.formatPropertyTable(databases::isStoredProceduresProperty));

            pw.println();
            pw.println("## Other Boolean Properties");
            pw.println(md.formatPropertyTable(databases::isBooleanProperty)); // center

            pw.println();
            pw.println("## Other Properties");
            pw.println(md.formatPropertyTable(databases::isOtherProperty)); // center

            pw.println();
            pw.println("## SQL Keywords");
            pw.println(md.formatSqlKeywords());
             */
       /*

            pw.flush();
            // LOG.info(w.toString());
        */
    }
}
