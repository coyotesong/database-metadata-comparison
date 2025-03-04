package com.coyotesong.database.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Externalized repositories.
 * <p>
 * Docker and maven repositories. This should be further externalized into configuration files.
 * </p>
 * <p>
 * This can be mapped to a Spring @Configuration that provides two beans
 * </p>
 */
public class ExternalRepositories {
    private static final String MAVEN_REPO_FORMAT = "[%s:%s:%s](https://central.sonatype.com/artifact/%s/%s/%s)";

    private final Map<String, String> dockerRepos = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> mavenRepos = new LinkedHashMap<>();

    public ExternalRepositories() {
        // format: docker image name -> url
        dockerRepos.put("icr.io/db2_community/db2", "https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker");
        dockerRepos.put("mysql", "https://hub.docker.com/_/mysql");
        dockerRepos.put("gvenzl/oracle-xe", "https://hub.docker.com/r/gvenzl/oracle-xe");
        dockerRepos.put("postgres", "https://hub.docker.com/_/postgres");
        dockerRepos.put("mcr.microsoft.com/mssql/server", "https://mcr.microsoft.com/en-us/product/mssql/server");
        dockerRepos.put("mcr.microsoft.com/mssql/rhel/server", "https://mcr.microsoft.com/en-us/product/mssql/rhel/server");

        // format: driverClassName -> (groupId, versionId). Actual version determined elsewhere.
        mavenRepos.put("com.ibm.db2.jcc.DB2Driver", Collections.singletonMap("com.ibm.db2", "jcc")); // 11.5.9.0
        mavenRepos.put("com.mysql.cj.jdbc.Driver", Collections.singletonMap("com.mysql", "mysql-connector-j")); // 9.0.0
        mavenRepos.put("oracle.jdbc.driver.OracleDriver", Collections.singletonMap("com.oracle.database.jdbc", "ojdbc11")); // 23.5.0.24.07
        mavenRepos.put("org.postgresql.Driver", Collections.singletonMap("org.postgresql", "postgresql")); // 42.7.4
        mavenRepos.put("org.h2.Driver", Collections.singletonMap("com.h2database", "h2")); // 2.3.232
        mavenRepos.put("org.sqlite.JDBC", Collections.singletonMap("org.xerial", "sqlite-jdbc")); // 3.46.1.0
        mavenRepos.put("com.microsoft.sqlserver.jdbc.SQLServerDriver", Collections.singletonMap("com.microsoft.sqlserver", "mssql-jdbc")); // 12.8.1.jre11
    }

    public Map<String, String> getDockerRepos() {
        return dockerRepos;
    }

    public Map<String, Map<String, String>> getMavenRepos() {
        return mavenRepos;
    }
}
