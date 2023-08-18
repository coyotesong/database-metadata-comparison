package com.coyotesong.database;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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

    private static final DockerImageName myImage = DockerImageName.parse("postgres:15");

    static {
        // from online examples...
        // Postgres JDBC driver uses JUL; disable it to avoid annoying, irrelevant, stderr logs during connection testing
        //
        //java.util.logging.LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.INFO);
    }
    @Test
    public void test() throws SQLException, InterruptedException {
        final StandardDatabases databases = new StandardDatabases();
        databases.initialize();

        StandardDatabases.Markdown md = databases.getMarkdown();

        // try (StringWriter w = new StringWriter();
        try (Writer w = new FileWriter("/tmp/database-comparison.md");
             PrintWriter pw = new PrintWriter(w)) {
            pw.println("## Product Versions");
            pw.println(md.formatPropertyHeader());
            pw.println(md.formatDatabaseDetails());

            pw.println();
            pw.println("## Basic Properties");
            pw.println(md.formatTable(databases::isBasicProperty));

            pw.println();
            pw.println("## Limits");
            pw.println(md.formatTable(databases::isLimitProperty)); // right

            pw.println();
            pw.println("## Boolean properties");
            pw.println(md.formatTable(databases::isBooleanProperty)); // center

            /*
            List<String> db2l = Arrays.stream(db2.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
            List<String> mssqll = Arrays.stream(mssql.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
            List<String> mysqll = Arrays.stream(mysql8.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
            List<String> oraclel = Arrays.stream(oracle.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
            List<String> postgresqll = Arrays.stream(postgresql.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
            List<String> yugabytedbl = Arrays.stream(yugabytedb.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
            List<String> h2l = Arrays.stream(h2.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
            List<String> sqlitel = Arrays.stream(sqlite.getSQLKeywords().split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());

            keySet.clear();
            keySet.addAll(db2l);
            keySet.addAll(mssqll);
            keySet.addAll(mysqll);
            keySet.addAll(oraclel);
            keySet.addAll(postgresqll);
            keySet.addAll(yugabytedbl);
            keySet.addAll(h2l);
            keySet.addAll(sqlitel);

            keyList.clear();
            keyList.addAll(keySet);
            Collections.sort(keyList);

            pw.println();
            pw.println("## SQL Keywords");
            pw.println(md.formatHeader(databases));  // center...
            for (String keyword : keyList) {
                StringBuilder sb = new StringBuilder("| ");
                sb.append(keyword);
                sb.append(" | ");
                if (db2l.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" | ");
                if (mssqll.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" | ");
                if (mysqll.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" | ");
                if (oraclel.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" | ");
                if (postgresqll.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" | ");
                if (yugabytedbl.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" | ");
                if (h2l.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" | ");
                if (sqlitel.contains(keyword)) {
                    sb.append(Character.toString(0x2611)); // ballot box with check mark
                }
                sb.append(" |");
                pw.println(sb.toString());
            }

            /*
            pw.printf("| System Functions | %s | %s | %s | %s | %s | %s | %s | %s |\n",
                db2.getSystemFunctions(), mssql.getSystemFunctions(), mysql8.getSystemFunctions(),
                oracle.getSystemFunctions(), postgresql.getSystemFunctions(), yugabytedb.getSystemFunctions(),
                h2.getSystemFunctions(), sqlite.getSystemFunctions());
             */

            pw.flush();
            // LOG.info(w.toString());
        } catch (IOException e) {
            //
        }

        // for (String key : pg.keySet()) {
        //    LOG.info(String.format("%-40.40s  %s", key, pg.get(key)));
        // }

            // reflection on:
            //   all*
            //   dataDef*
            //   doesMaxRowSizeIncludeBlobs()
            //   generatedKeyAlwaysReturned()
            //   get*()
            //   is*()
            //   locatorsUpdateCopy()
            //   null*()
            //   stores*()
            //   supports*()
            //   uses*()
            // 
            //   types: ResultSet.TYPE_FORWARD, TYPE_SCROLL_INSENSITIVE, TYPE_SCROLL_SELECTIVE
            //   ? deletesArDetected(int type)
            //   ? insertsAreDetected(int type)
            //   ? updatesAreDetected(int type)
            //   ? others*AreVisible(int type)
            //   ? own*AreVisible(int type)
    }
}
