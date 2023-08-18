package com.coyotesong.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.images.ImagePullPolicy;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.*;

/**
 * Customized PostgreSQL Container
 * <p>
 * This container will only load local images. If you want the default behavior add
 * <code>.with(PullPolicy.defaultPolicy())</code>
 * </p>
 * @param <S>
 */
public class MyDatabaseContainer<S extends JdbcDatabaseContainer<S>> {
    private static final Logger LOG = LoggerFactory.getLogger(MyDatabaseContainer.class);

    private MyLogConsumer logConsumer;

    private final S db;

    private static <S extends JdbcDatabaseContainer<S>> S ctor(Class<S> clz, String imageName) {
        try {
            final Constructor<S> ctor = clz.getConstructor(String.class);
            return ctor.newInstance(imageName);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("unable to call constructor!", e);
        }
    }

    private static <S extends JdbcDatabaseContainer<S>> S ctor(Class<S> clz, DockerImageName imageName) {
        try {
            final Constructor<S> ctor = clz.getConstructor(DockerImageName.class);
            return ctor.newInstance(imageName);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("unable to call constructor!", e);
        }
    }

    public MyDatabaseContainer(Class<S> clz, String imageName) {
        db = (S) ctor(clz, imageName);
        this.logConsumer = new MyLogConsumer(imageName);
    }

    public MyDatabaseContainer(Class<S> clz, DockerImageName imageName) {
        db = (S) ctor(clz, imageName);
        this.logConsumer = new MyLogConsumer(imageName.asCanonicalNameString());
    }

    /**
     * <p>
     * Specify a different ImagePullPolicy.
     * </p><p>
     * To pull from non-local repository either replace the lambda function
     * with 'PullPolicy.defaultPolicy()' or remove it entirely.
     * </p>
     *
     * @param pullPolicy
     * @return
     */
    public MyDatabaseContainer<S> withPullPolicy(ImagePullPolicy pullPolicy) {
        this.withPullPolicy(pullPolicy);
        return this;
    }

    /**
     * Capture everything from the container
     *
     * @return
     */
    public MyDatabaseContainer<S> withLogEverything() {
        logConsumer.setLoggingLevel(MyLogConsumer.LoggingLevel.ALL);
        return this;
    }

    /**
     * Capture nothing from the container
     *
     * @return
     */
    public MyDatabaseContainer<S> withLogNothing() {
        logConsumer.setLoggingLevel(MyLogConsumer.LoggingLevel.NONE);
        return this;
    }

    /**
     * Capture some (not all) things from the container
     *
     * @return
     */
    public MyDatabaseContainer<S> withLogSomeThings() {
        logConsumer.setLoggingLevel(MyLogConsumer.LoggingLevel.SOME);
        return this;
    }

    // @Override
    public void configure() {
        try {
            final Method m = db.getClass().getMethod("configure");
            m.invoke(db);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // this is okay... not all containers implement it
        }
    }

    public void start() {
        db.start();
    }

    public void stop() {
        db.stop();
    }

    public String getDriverClassName() {
        return db.getDriverClassName();
    }

    public String getTestQueryString() {
        try {
            final Method m = db.getClass().getMethod("getTestQueryString");
            return (String) m.invoke(this);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.info("error when invoking 'getTestQueryString()'", e);
        }
        return "";
    }

    public String getPassword() {
        return db.getPassword();
    }

    public String getUsername() {
        return db.getUsername();
    }

    public String getJdbcUrl() {
        return db.getJdbcUrl();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword());
    }

    /**
     * Extract Connection metadata
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public MyDatabaseMetaData getMetadata(Connection conn) throws SQLException {
        final MyDatabaseMetaData results = new MyDatabaseMetaData();

        final DatabaseMetaData md = conn.getMetaData();

        final Class<? extends DatabaseMetaData> clz = md.getClass();
        final Method[] methods = clz.getMethods();

        for (Method m : methods) {
            final Class<?> returnType = m.getReturnType();
            final Parameter[] parameters = m.getParameters();
            if (Void.class.equals(returnType) || (parameters.length == 0)) {
                continue;
            }

            if (ResultSet.class.equals(returnType)) {
                LOG.info("not implemented yet ({})", m.getName());
                // getClientInfoProperties()
                // getFunctions(catalog, schemaPattern, functionMamePattern)
                // getProcedures(catalog, schemaPattern, procedureNamePattern);
                // getSchemas();
                // getTableTypes()
                // getTypeInfo()
                // getUDTs(catalog, schemaPattern, typeNamePattern, int[] types)
            } else {
                try {
                    results.put(m.getName(), m.invoke(md));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.info("unexpected exception on {}", m.getName(), e);
                }
            }
        }


        // reflection on:
        //   all*
        //   autoCommitFailure...
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
        return results;
    }
}

