package com.coyotesong.database;

import com.coyotesong.database.db.LoadDatabaseMetaDataCallable;
import com.coyotesong.database.sql.ExtendedDatabaseMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Merged database information
 */
public class DatabaseScanner {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseScanner.class);

    private final Map<Database, ExtendedDatabaseMetaData> databases; //  = new LinkedHashMap<>();

    public DatabaseScanner(Map<Database, ExtendedDatabaseMetaData> databases) {
        this.databases = databases;
    }

    /**
     * Collect information about each database (multithreaded)
     */
    public void scanDatabases() {
        final int poolSize = 4;
        final int maxPoolSize = 6;
        final int keepAliveTime = 10;
        final TimeUnit unit = TimeUnit.SECONDS;
        final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(Database.values().length);
        // final RejectedExecutionHandler handler = ...

        try (ExecutorService executor = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, unit, workQueue)) {

            // schedule separate task/future for each database. This will significantly
            // improve performance.
            final List<Future<ExtendedDatabaseMetaData>> futures = new ArrayList<>();
            for (Database db : Database.values()) {
                if (db.getContainerClass() != null) {
                    futures.add(executor.submit(new LoadDatabaseMetaDataCallable(db)));
                    // placeholder...
                    databases.put(db, new ExtendedDatabaseMetaData(db));
                }
            }

            // wait for everything to be done...
            try {
                // wait for up to 30(!) minutes
                for (int i = 0; i < 3600; i++) {
                    prune(futures);

                    // sleep for a bit if anything is left.
                    if (futures.isEmpty()) {
                        break;
                    } else {
                        Thread.sleep(500L);
                    }
                }

                // cancel anything still running
                if (!futures.isEmpty()) {
                    for (Future<ExtendedDatabaseMetaData> future : futures) {
                        if (future.state() == Future.State.RUNNING) {
                            // TODO - isn't there a way to peek at the Thread/Runnable
                            LOG.warn("database {} is still running!", future.toString());
                            future.cancel(true);
                        }
                    }
                }

                // gracefully shut down the executor.
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                    LOG.warn("database termination requests timed out!");
                }

                // forcefully shut down the executor.
                if (!executor.isTerminated()) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                handleInterruptedException(futures);
            }
        }
    }

    /**
     * Prune list of futures
     * <p>
     * Prune list of futures - remove anything that has completed or been canceled.
     *
     * @param futures list of futures
     * @throws InterruptedException
     */
    void prune(final List<Future<ExtendedDatabaseMetaData>> futures) throws InterruptedException {
        // have any of the futures completed or been canceled?
        if (!futures.isEmpty()) {
            // count down to avoid problems due to values being deleted
            for (int j = futures.size() - 1; j >= 0; j--) {
                if (futures.get(j).isCancelled()) {
                    futures.remove(j);
                } else if (futures.get(j).isDone()) {
                    try {
                        final ExtendedDatabaseMetaData metadata = futures.remove(j).get();
                        databases.put(metadata.getDatabase(), metadata);
                    } catch (ExecutionException e) {
                        LOG.info("execution failed: {}", e.getCause().getMessage(), e.getCause());
                    }
                }
            }
        }
    }

    /**
     * Handle InterruptedException by cancelling all remaining futures
     *
     * @param futures list of futures running a TestContainer
     */
    void handleInterruptedException(final List<Future<ExtendedDatabaseMetaData>> futures) {
        // count down to avoid problems due to values being deleted
        for (int j = futures.size() - 1; j >= 0; j--) {
            // cancel anything that's still running
            if (futures.get(j).state() == Future.State.RUNNING) {
                futures.get(j).cancel(true);
            }
        }

        Thread.currentThread().interrupt();
    }
}
