package com.coyotesong.database;

import com.coyotesong.database.formatters.MarkdownFormatter;
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

    @Test
    public void test() throws SQLException, InterruptedException {
        final DatabaseComparisons databases = new DatabaseComparisons();
        databases.initialize();
        LOG.info("statistics:\n{}", MetadataMethods.INSTANCE.toString());

        MarkdownFormatter md = new MarkdownFormatter(databases);

        // try (StringWriter w = new StringWriter();
        try (Writer w = new FileWriter("/tmp/database-comparison.md");
             PrintWriter pw = new PrintWriter(w)) {
            pw.println("## Product Summary");
            pw.println(md.formatSummaryTable());

            pw.println();
            pw.println("## Docker Images");
            pw.println(md.formatDockerImageTable());

            pw.println();
            pw.println("## Drivers");
            pw.println(md.formatDriverTable());

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

            pw.flush();
            // LOG.info(w.toString());
        } catch (IOException e) {
            LOG.error("{}: {}", e.getClass().getName(), e.getMessage(), e);
        }
    }
}
