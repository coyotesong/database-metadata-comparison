package com.coyotesong.database;

import org.testcontainers.containers.*;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    // DB2("DB2", Db2Container.class, new:"icr.io/db2_community/db2:11.5.8.0"); // new
    DB2("DB2", Db2Container.class, DockerImageName.parse("ibmcom/db2:11.5.8.0"),
        Arrays.asList("LICENSE=accept")),
    MSSQL("MSSQL", MSSQLServerContainer.class, DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"),
            Arrays.asList("ACCEPT_EULA=y")), // MSSQL_PID=... Developer, Express, Evaluation
    MYSQL("MySQL", MySQLContainer.class, DockerImageName.parse("mysql:latest")),
    ORACLE("Oracle", OracleContainer.class, DockerImageName.parse("gvenzl/oracle-xe:18.4.0-slim")),
    POSTGRESQL("PostgreSQL", PostgreSQLContainer.class, DockerImageName.parse("postgres:latest")),
    YUGABYTEDB("YugaByteDB", YugabyteDBYSQLContainer.class, DockerImageName.parse("yugabytedb/yugabyte:latest")),
    H2("H2"),
    SQLITE("SQLite")
    // SAP HANA
    // Teradata
    // vertica ?
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
