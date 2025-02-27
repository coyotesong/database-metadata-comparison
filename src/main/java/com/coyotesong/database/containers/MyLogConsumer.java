package com.coyotesong.database.containers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Consumer;

class MyLogConsumer implements Consumer<OutputFrame> {
    private static final Logger LOG = LoggerFactory.getLogger(MyLogConsumer.class);

    public enum LoggingLevel {
        ALL,
        SOME,
        NONE
    }

    ;

    private final String name;
    private LoggingLevel level = LoggingLevel.NONE;

    public MyLogConsumer(String name, LoggingLevel level) {
        this.name = name;
        this.level = level;
    }

    public MyLogConsumer(String name) {
        this(name, LoggingLevel.SOME);
    }

    public MyLogConsumer(DockerImageName name, LoggingLevel level) {
        this(name.asCanonicalNameString(), level);
    }

    public LoggingLevel getLoggingLevel() {
        return level;
    }

    public void setLoggingLevel(LoggingLevel level) {
        this.level = level;
    }

    public void accept(OutputFrame frame) {
        // should message prepend image name? Or rely on user and MDC?
        final String s = frame.getUtf8StringWithoutLineEnding();

        switch (level) {
            case ALL:
                switch (frame.getType()) {
                    case STDOUT:
                        LOG.info(s);
                        break;
                    case STDERR:
                        LOG.warn(s);
                }
                break;

            case SOME:
                // strip out embedded log statements
                // we could be more intelligent and use a regex that considers timestamps, etc.
                final boolean ignore = s.contains(": DEBUG:  ")
                        | s.contains(": NOTICE:  ")
                        | s.contains(": INFO:  ")
                        | s.contains(" LOG: ");
                switch (frame.getType()) {
                    case STDOUT:
                        LOG.info(frame.getUtf8StringWithoutLineEnding());
                        break;
                    case STDERR:
                        if (ignore) {
                            LOG.debug(s);
                        } else {
                            LOG.warn(s);
                        }
                }
                break;

            case NONE:
                // do nothing
        }
    }
}
