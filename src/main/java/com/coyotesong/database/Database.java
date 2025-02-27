package com.coyotesong.database;

import com.coyotesong.database.containers.H2Container;
import com.coyotesong.database.containers.SQLiteContainer;
// import com.coyotesong.database.containers.SapHanaContainer;
// import com.coyotesong.database.containers.VerticaContainer;
import org.testcontainers.containers.*;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

/**
 * List of tested databases
 * <p>
 * Implementation details are kept in an augmented enumeration, not a conventional POJO object,
 * since the details are so integral to the enumerated value. Changing the properties can break
 * existing software so it should not be done lightly!
 */
public enum Database {
    // Azure?
    // Clickhouse
    // Cockroach
    // CrateDB
    // MariaDB
    // presto
    // questdb
    // TiDB
    // trino
    //
    DB2("IBM DB2", Db2Container.class,
            DockerImageName.parse("icr.io/db2_community/db2").asCompatibleSubstituteFor("ibmcom/db2").withTag("latest"),
            List.of("LICENSE=accept")), // ports 22, 55000, 60006-60007  (db at 50000)
    MSSQL("MSSQL", MSSQLServerContainer.class,
            DockerImageName.parse("mcr.microsoft.com/mssql/server").withTag("latest"),
            // DockerImageName.parse("mcr.microsoft.com/mssql/server").asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server").withTag("latest"),
            List.of("ACCEPT_EULA=y")), // MSSQL_PID=... Developer, Express, Evaluation // no extra ports, db at 1433

    // FIXME - why is this failing?
    // MYSQL("MySQL", MySQLContainer.class, DockerImageName.parse("mysql").withTag("latest")), // ports 33060, db at 3306

    ORACLE("Oracle", OracleContainer.class, DockerImageName.parse("gvenzl/oracle-xe").withTag("21-slim")), // no extra ports, db at 1521, something at 8080
    POSTGRESQL("PostgreSQL", PostgreSQLContainer.class, DockerImageName.parse("postgres").withTag("latest")), // ANY EXTRA PORTS?  db at 5432
    // VERTICA("Vertica CE", VerticaContainer.class, DockerImageName.parse("vertica/vertica-ce:latest")), // no extra ports, db at 5433, something at 5444
    // YUGABYTEDB("YugaByteDB", YugabyteDBYSQLContainer.class, DockerImageName.parse("yugabytedb/yugabyte:latest")), // ports 6379, 7100, 7200, 9042, 9100, 10100, 11000, 12000, db at 5433, 7000 or 9000 ?
    H2("H2", H2Container.class, DockerImageName.parse("alpine").withTag("latest")),  // no db port
    SQLITE("SQLite", SQLiteContainer.class, DockerImageName.parse("alpine").withTag("latest")) // db at 8082

    // SAPHANA("SAP HANA Express", SapHanaContainer.class, DockerImageName.parse("saplabs/hanaexpress:2.00.061.00.20220519.1"))
    // SAPHANAXSA("SAP HANA Express XSA", SapHanaContainer.class, DockerImageName.parse("saplabs/hanaexpressxsa:2.00.061.00.20220519.1")),

    // Teradata ?
    ;

    private final String label;
    private final Class<? extends JdbcDatabaseContainer<?>> containerClass;
    private final DockerImageName imageName;
    private final List<String> env = new ArrayList<>();

    private Database(String label) {
        this.label = label;
        this.containerClass = null;
        this.imageName = null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Database(String label, Class<? extends JdbcDatabaseContainer> containerClass) {
        this.label = label;
        this.containerClass = (Class<? extends JdbcDatabaseContainer<?>>) containerClass;
        this.imageName = null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Database(String label, Class<? extends JdbcDatabaseContainer> containerClass, DockerImageName imageName) {
        this.label = label;
        this.containerClass = (Class<? extends JdbcDatabaseContainer<?>>) containerClass;
        this.imageName = imageName;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Database(String label, Class<? extends JdbcDatabaseContainer> containerClass, DockerImageName imageName, List<String> env) {
        this.label = label;
        this.containerClass = (Class<? extends JdbcDatabaseContainer<?>>) containerClass;
        this.imageName = imageName;
        this.env.addAll(env);
    }

    public String getLabel() {
        return label;
    }

    public Class<? extends JdbcDatabaseContainer<?>> getContainerClass() {
        return containerClass;
    }

    public DockerImageName getImageName() {
        return imageName;
    }

    public List<String> getEnv() {
        return env;
    }
}
