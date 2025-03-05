package com.coyotesong.database.db;

import com.coyotesong.database.Database;
import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Thread to launch a docker container and capture the database's metadata
 */
public class LoadDatabaseMetaDataCallable implements Callable<ExtendedDatabaseMetaData> {
    private static final Logger LOG = LoggerFactory.getLogger(LoadDatabaseMetaDataCallable.class);

    private final Database database;
    private final ExtendedDatabaseMetaData metadata;

    public LoadDatabaseMetaDataCallable(Database database) {
        this.database = database;
        this.metadata = new ExtendedDatabaseMetaData(database);
    }

    /**
     * {@inheritDoc}
     */
    public ExtendedDatabaseMetaData call() throws SQLException {
        MDC.MDCCloseable mdc0 = MDC.putCloseable("database", database.name());
        MDC.MDCCloseable mdc1 = MDC.putCloseable("dockerImageName", database.getImageName().asCanonicalNameString());
        try (JdbcDatabaseContainer<?> db = newContainer()) {

            // start server
            db.start();
            mdc1 = MDC.putCloseable("containerId", db.getContainerName());

            metadata.setDriverClassName(db.getDriverClassName());
            metadata.setDockerImageName(db.getDockerImageName());

            // load metadata then shut down server
            try (Connection conn = db.createConnection("")) {
                metadata.loadMetadata(conn);
            } finally {
                db.stop();
            }

            return metadata;
        } catch (SQLException e) {
            trace(e);
            throw e;
        } finally {
            mdc1.close();
            mdc0.close();
        }
    }

    /**
     * Convenience method that prints a recursive stack trace
     * <p>
     * SQLException includes both 'next' and 'suppress' exceptions. We only care about the first.
     *
     * @param e SQLException
     */
    private void trace(Throwable e) {
        LOG.info("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        if (e.getCause() != null) {
            trace(e.getCause());
        }

        if (e instanceof SQLException sqle) {
            if (sqle.getNextException() != null) {
                trace(sqle.getNextException());
            }
        }
    }

    /**
     * Find the appropriate constructor
     *
     * @return appropriate JdbcDatabaseContainer constructor
     * @throws SQLException unable to find the constructor
     */
    private JdbcDatabaseContainer<?> newContainer() throws SQLException {
        try {
            JdbcDatabaseContainer<?> db;
            if (database.getImageName() == null) {
                final Constructor<?> ctor = database.getContainerClass().getConstructor();
                db = (JdbcDatabaseContainer<?>) ctor.newInstance();
            } else {
                final Constructor<?> ctor = database.getContainerClass().getConstructor(DockerImageName.class);
                db = (JdbcDatabaseContainer<?>) ctor.newInstance(database.getImageName());
            }

            for (String env : database.getEnv()) {
                final String[] s = env.split("=");
                db.addEnv(s[0], s[1]);
            }

            return db;
        } catch (NoSuchMethodException e) {
            LOG.info("unable to create container for {}: {}", database.getContainerClass().getSimpleName(), e.getMessage(), e);
            throw new SQLException("unable to find constructor");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOG.info("unable to create container for {}: {}", database.getContainerClass().getSimpleName(), e.getMessage(), e);
            throw new SQLException(e);
        }
    }
}
