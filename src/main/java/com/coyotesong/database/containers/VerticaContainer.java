package com.coyotesong.database.containers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * TestContainer for Vertica CE databases
 *
 * See https://hub.docker.com/r/vertica/vertica-ce
 *
 * See https://docs.vertica.com/12.0.x/en/getting-started/introducing-vmart-example-db/
 *
 *     // https://hub.docker.com/r/vertica/vertica-ce
 *     // https://www.vertica.com/
 *     // https://www.microfocus.com/en-us/legal/software-licensing
 *
 * @param <SELF>
 */
public class VerticaContainer<SELF extends VerticaContainer<SELF>> extends JdbcDatabaseContainer<SELF> {
    private static final Logger LOG = LoggerFactory.getLogger(VerticaContainer.class);

    public static final String NAME = "Vertica";
    public static final String IMAGE = "vertica/vertica-ce";
    public static final String DEFAULT_TAG = "latest"; // 23.3.0-0
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE);
    private static final Integer[] VERTICA_PORTS = { 5433, 5444 };

    static final String DEFAULT_DATABASE = "vmart";
    static final String DEFAULT_USER = "dbadmin"; // "newdbadmin" ?
    static final String DEFAULT_PASSWORD = "vertica";

    private String databaseName = DEFAULT_DATABASE;
    private String username = DEFAULT_USER;
    private String password = DEFAULT_PASSWORD;
    private Integer loginTimeout;
    private String keyStorePath;
    private String keyStorePassword;
    private String trustStorePath;
    private String trustStorePassword;

    public VerticaContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    public VerticaContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        this.waitStrategy = new LogMessageWaitStrategy()
                .withRegEx(".*Vertica is now running.*\\s")
                .withStartupTimeout(Duration.ofMinutes(5));
        // this.setCommand("...");

        for (Integer port : VERTICA_PORTS) {
            this.addExposedPort(port);
        }
    }

    @Override
    protected void configure() {
        // no effect?...

        // addEnv("VERTICA_DB_USER", username);
        // if (StringUtils.isNotBlank(password)) {
        //     addEnv("VERTICA_DB_PASSWORD", password);
        // }

        // addEnv("APP_DB_USER", username);
        // if (StringUtils.isNotBlank(password)) {
        //     addEnv("APP_DB_PASSWORD", password);
        // }

        // addEnv("TZ", "Europe/Prague");

        urlParameters.put("user", username);
        if (StringUtils.isNotBlank(password)) {
            urlParameters.put("password", password);
        }

        if (loginTimeout != null) {
            urlParameters.put("loginTimeout", Integer.toString(loginTimeout));
        }

        if (StringUtils.isNotBlank(keyStorePath) && StringUtils.isNotBlank(keyStorePassword)) {
            urlParameters.put("KeyStorePath", keyStorePath);
            urlParameters.put("KeyStorePassword", keyStorePassword);
        }

        if (StringUtils.isNotBlank(trustStorePath) && StringUtils.isNotBlank(trustStorePassword)) {
            urlParameters.put("TrustStorePath", trustStorePath);
            urlParameters.put("TrustStorePassword", trustStorePassword);
        }
    }

    @Override
    public String getDriverClassName() {
        return "com.vertica.jdbc.Driver";
    }

    @Override
    public String getJdbcUrl() {
        final String additionalUrlParams = constructUrlParameters("?", "&");
        return "jdbc:vertica://" + getHost() + ":" + getMappedPort(VERTICA_PORTS[0]) + "/" + databaseName + additionalUrlParams;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Integer getLoginTimeout() {
        return loginTimeout;
    }

    // do not provide password?
    public String getKeystorePath() {
        return keyStorePath;
    }

    // do not provide password?
    public String getTrustStorePath() {
        return trustStorePath;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
        return self();
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }

    public SELF withLoginTimeout(final Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
        return self();
    }

    // JKS file
    public SELF withKeyStorePath(final String keyStorePath) {
        this.keyStorePath = keyStorePath;
        return self();
    }

    public SELF withKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return self();
    }

    // JKS file
    public SELF withTrustStorePath(final String trustStorePath) {
        this.trustStorePath = trustStorePath;
        return self();
    }

    public SELF withTrustStorePassword(final String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return self();
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}
