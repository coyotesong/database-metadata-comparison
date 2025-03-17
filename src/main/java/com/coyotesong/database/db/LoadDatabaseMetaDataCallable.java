package com.coyotesong.database.db;

import com.coyotesong.database.Database;
import com.coyotesong.database.erd.ErdDotFileGenerator;
import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.output.MigrateResult;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.extensibility.MigrationType;
import org.flywaydb.core.extensibility.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
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

    class X implements MigrationResolver {

        @Override
        public Collection<ResolvedMigration> resolveMigrations(Context context) {
            return List.of();
        }

        @Override
        public String getPrefix(Configuration configuration) {
            return MigrationResolver.super.getPrefix(configuration);
        }

        @Override
        public MigrationType getDefaultMigrationType() {
            return MigrationResolver.super.getDefaultMigrationType();
        }

        @Override
        public boolean isLicensed(Configuration configuration) {
            return MigrationResolver.super.isLicensed(configuration);
        }

        @Override
        public boolean isEnabled() {
            return MigrationResolver.super.isEnabled();
        }

        @Override
        public String getName() {
            return MigrationResolver.super.getName();
        }

        @Override
        public String getPluginVersion(Configuration config) {
            return MigrationResolver.super.getPluginVersion(config);
        }

        @Override
        public int getPriority() {
            return MigrationResolver.super.getPriority();
        }

        @Override
        public int compareTo(Plugin o) {
            return MigrationResolver.super.compareTo(o);
        }

        @Override
        public Plugin copy() {
            return MigrationResolver.super.copy();
        }
    }

    void initializeDatabase(JdbcDatabaseContainer<?> db) throws SQLException {
        final FluentConfiguration conf = Flyway.configure().driver(db.getDriverClassName()).dataSource(db.getJdbcUrl(), db.getUsername(), db.getPassword());

        // conf.resolvers(MigrationResolver);
        final Flyway flyway = conf.load();
        final MigrateResult results = flyway.migrate();
        // note: if we see 'no database' check for 'flyway-database-x' dependency

        if (!results.success) {
            results.warnings.forEach(LOG::warn);
        }
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
            try (MDC.MDCCloseable mdc2 = MDC.putCloseable("containerId", db.getContainerName())) {

                metadata.setDriverClassName(db.getDriverClassName());
                metadata.setDockerImageName(db.getDockerImageName());

                if ("POSTGRESQL".equals(database.name())) {
                    initializeDatabase(db);
                    new ErdDotFileGenerator().writeERD(db, "/tmp/erd.dot");
                }

                // There is a 'db.createConnection(String)' method but there's no standard
                // safe connection string and it's not a property associated with the testcontainer.
                // So we need to use the driver insteadd.
                final Properties connInfo = new Properties();
                connInfo.put("user", db.getUsername());
                connInfo.put("password", db.getPassword());
                try (Connection conn = db.getJdbcDriverInstance().connect(db.getJdbcUrl(), connInfo)) {
                    metadata.loadMetadata(conn);
                } finally {
                    db.stop();
                }
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