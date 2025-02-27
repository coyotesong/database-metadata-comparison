package com.coyotesong.database.containers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * TestContainer for H2 databases
 * <p>
 * H2 can emulate multiple databases and was therefore widely used in tests before the
 * Docker-based test container library was available.
 * <p>
 * However it still has many benefits that merit treating it as a "real" database:
 * <p>
 * - it's easy to use as an embedded database within an application
 * - it has a robust implementation (e.g., triggers)
 * - it has native encryption
 * - it is easy to extend with java user-defined functions (UDF).
 * <p>
 * For more information see:
 * <p>
 * - https://www.h2database.com/html/cheatSheet.html
 * - https://www.h2database.com/html/tutorial.html
 * - https://www.h2database.com/html/features.html
 * - https://www.h2database.com/html/advanced.html
 * <p>
 * Important: the default H2 JDBC driver registers as a JDBC driver for multiple databases.
 * This is not a problem when the software uses an explicit classname but it's why the
 * code is organized so it can use a separate classloader if necessary.
 *
 * @param <SELF>
 */
public class H2Container<SELF extends H2Container<SELF>> extends JdbcDatabaseContainer<SELF> {
    private static final Logger LOG = LoggerFactory.getLogger(H2Container.class);

    public static final String NAME = "H2";
    public static final String IMAGE = "alpine";
    public static final String DEFAULT_TAG = "latest";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE);
    private static final Integer H2_PORT = 8082;

    static final String DEFAULT_DATABASE = "test";
    static final String DEFAULT_USER = "test";
    static final String DEFAULT_PASSWORD = "test";

    private boolean inMemoryDatabase = true;
    private String databaseName = DEFAULT_DATABASE;
    private String username = DEFAULT_USER;
    private String password = DEFAULT_PASSWORD;

    public H2Container() {
        super(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
        this.waitStrategy = new DurationWaitStrategy();
    }

    public H2Container(final DockerImageName dockerImageName) {
        super(dockerImageName);
        //dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        this.waitStrategy = new DurationWaitStrategy();
        // this.setCommand("...");

        // addExposedPort(H2_PORT);
    }

    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }

    @Override
    public String getJdbcUrl() {
        if (inMemoryDatabase) {
            return "jdbc:h2:mem:";
        }
        return "jdbc:h2:" + getDatabaseName();
    }

    @Override
    public Connection createConnection(String queryString, Properties info) throws SQLException, NoDriverFoundException {
        final Driver d = getJdbcDriverInstance();
        final String url = getJdbcUrl();
        LOG.info("driver: {}, url: {}", d.getClass().getName(), url);

        return d.connect(url, info);
    }

    @Override
    protected String constructUrlForConnection(String queryString) {
        return getJdbcUrl();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }

    public boolean isInMemoryDatabase() {
        return inMemoryDatabase;
    }

    @Override
    public String getDatabaseName() {
        if (isInMemoryDatabase()) {
            return "";
        }
        return databaseName;
    }

    public SELF withInMemoryDatabase() {
        this.inMemoryDatabase = true;
        return self();
    }

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.inMemoryDatabase = false;
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

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}
