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
 * TestContainer for SQLite databases
 * <p>
 * Important: SQLite is not a traditional client/server database - it is a library that provides
 * a fairly robust SQL API via an external library.
 * <p>
 * Debian packages:
 * <p>
 * - sqlite3
 * - sqlite3-tools
 * - sqlite3-doc
 * <p>
 * JDBC driver: org.xerial:sqlite-jdbc:3.42.0.0
 *
 * @param <SELF>
 */
public class SQLiteContainer<SELF extends SQLiteContainer<SELF>> extends JdbcDatabaseContainer<SELF> {
    private static final Logger LOG = LoggerFactory.getLogger(SQLiteContainer.class);

    public static final String NAME = "SQLite";
    public static final String IMAGE = "alpine";
    public static final String DEFAULT_TAG = "latest";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE);

    // FIXME - use property java.io_tmpdir ?
    static final String DEFAULT_DATABASE = "/tmp/test.db";
    static final String DEFAULT_USER = "test";
    static final String DEFAULT_PASSWORD = "test";

    private boolean inMemoryDatabase = true;
    private String filename = DEFAULT_DATABASE;
    private String username = DEFAULT_USER;
    private String password = DEFAULT_PASSWORD;

    public SQLiteContainer() {
        super(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
        this.waitStrategy = new DurationWaitStrategy();
    }

    public SQLiteContainer(final DockerImageName imageName) {
        this();
    }

    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    public String getJdbcUrl() {
        if (inMemoryDatabase) {
            return "jdbc:sqlite::memory:";
        }
        return "jdbc:sqlite:" + getDatabaseName();
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
        return filename;
    }

    public SELF withInMemoryDatabase() {
        this.inMemoryDatabase = true;
        return self();
    }

    @Override
    public SELF withDatabaseName(final String filename) {
        this.inMemoryDatabase = false;
        this.filename = filename;
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
