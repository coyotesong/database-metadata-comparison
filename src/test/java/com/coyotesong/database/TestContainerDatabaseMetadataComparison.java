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
    public void test() throws IOException {
        final DatabaseComparisons databases = new DatabaseComparisons();
        Pivots pivots = databases.initialize();
        LOG.info("statistics:\n{}", MetadataMethods.INSTANCE.toString());

        Jinja2Formatter md = new Jinja2Formatter(pivots);

        try (Writer w = new FileWriter("/tmp/database-comparison.md")) {
            w.write(md.format("markdown/general.md.j2"));
        }

            /*
            pw.println();
            pw.println("## Catalog and Schema Support");
            pw.println(md.formatCatalogSchemaSupport());

        */
    }
}
