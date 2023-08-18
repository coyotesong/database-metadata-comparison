package com.coyotesong.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>
 * Read properties from pom.xml file.
 * </p>
 * <p>
 * From: <a href="https://www.baeldung.com/java-accessing-maven-properties">Accessing Maven Properties in Java</a>
 * </p>
 */
public class PropertiesReader {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesReader.class);

    private final Properties properties = new Properties();
    private boolean checkEnvironmentFirst;

    /**
     * Constructor
     * <p>
     * Implementation note: the constructor swallows the IOException in order
     * to simplify its use as a 'private static final' value that can be used
     * when configuring other class fields..
     * </p>
     *
     * @param propertyFileName
     */
    public PropertiesReader(String propertyFileName) {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(propertyFileName)) {
            this.properties.load(is);
            this.checkEnvironmentFirst = Boolean.TRUE.equals(this.getProperty("checkEnvironmentFirst"));
        } catch (IOException e) {
            LOG.info("unable to load propertyFile: '{}': {}", propertyFileName, e.getMessage());
        }
    }

    public PropertiesReader withCheckEnvironmentFirst() {
        return withCheckEnvironmentFirst(true);
    }

    public PropertiesReader withCheckEnvironmentFirst(boolean checkEnvironmentFirst) {
        this.checkEnvironmentFirst = checkEnvironmentFirst;
        return this;
    }

    /**
     * Retrieve property, or null.
     *
     * @param propertyName
     * @return
     */
    public String getProperty(String propertyName) {
        return getProperty(propertyName, null);
    }

    /**
     * Retrieve property, or default value.
     *
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public String getProperty(String propertyName, String defaultValue) {
        if (checkEnvironmentFirst) {
            final String value = System.getenv(propertyName);
            if (value != null) {
                return value;
            }
        }

        return this.properties.getProperty(propertyName, defaultValue);
    }
}