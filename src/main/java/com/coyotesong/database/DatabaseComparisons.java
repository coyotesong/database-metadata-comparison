package com.coyotesong.database;

import com.coyotesong.database.db.LoadDatabaseMetaDataCallable;
import com.coyotesong.database.formatters.MarkdownFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class DatabaseComparisons {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseComparisons.class);

    final Map<Database, MyDatabaseMetaData> databases = new LinkedHashMap<>();

    final List<String> propertyNames = new ArrayList<>();

    final Map<String, MergedPropertyValues> mergedProperties = new LinkedHashMap<>();

    /**
     * Collect information about each database (multithreaded)
     */
    public void collectData() {
        final int poolSize = 4;
        final int maxPoolSize = 6;
        final int keepAliveTime = 10;
        final TimeUnit unit = TimeUnit.SECONDS;
        final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(Database.values().length);
        // final RejectedExecutionHandler handler = ...

        try (ExecutorService executor = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, unit, workQueue)) {

            // schedule separate task/future for each database. This will significantly
            // improve performance.
            final List<Future<MyDatabaseMetaData>> futures = new ArrayList<>();
            for (Database db : Database.values()) {
                if (db.getContainerClass() != null) {
                    futures.add(executor.submit(new LoadDatabaseMetaDataCallable(db)));
                    // placeholder...
                    databases.put(db, new MyDatabaseMetaData(db));
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
                    for (Future<MyDatabaseMetaData> future : futures) {
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
    void prune(final List<Future<MyDatabaseMetaData>> futures) throws InterruptedException {
        // have any of the futures completed or been canceled?
        if (!futures.isEmpty()) {
            // count down to avoid problems due to values being deleted
            for (int j = futures.size() - 1; j >= 0; j--) {
                if (futures.get(j).isCancelled()) {
                    futures.remove(j);
                } else if (futures.get(j).isDone()) {
                    try {
                        final MyDatabaseMetaData metadata = futures.remove(j).get();
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
    void handleInterruptedException(final List<Future<MyDatabaseMetaData>> futures) {
        // count down to avoid problems due to values being deleted
        for (int j = futures.size() - 1; j >= 0; j--) {
            // cancel anything that's still running
            if (futures.get(j).state() == Future.State.RUNNING) {
                futures.get(j).cancel(true);
            }
        }

        Thread.currentThread().interrupt();
    }

    /**
     * Initialize the cached data
     */
    public void initialize() {
        collectData();

        final Set<String> keySet = new HashSet<>();
        for (MyDatabaseMetaData md : databases.values()) {
            keySet.addAll(md.keySet());
        }

        // sort property keys (for convenience)
        // propertyNames.addAll(MetadataMethods.INSTANCE.getPropertyNames());
        propertyNames.clear();
        propertyNames.addAll(keySet);
        Collections.sort(propertyNames);

        // pivot the collected data
        for (String propertyName : propertyNames) {
            mergedProperties.put(propertyName, new MergedPropertyValues(propertyName, databases));
        }
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public MyDatabaseMetaData getMetadata(Database database) {
        return databases.get(database);
    }

    public Collection<MyDatabaseMetaData> values() {
        return databases.values();
    }

    public boolean isDmlProperty(String key) {
        return MetadataMethods.INSTANCE.isDmlMethod(key);
    }

    public boolean isDdlProperty(String key) {
        return MetadataMethods.INSTANCE.isDdlMethod(key);
    }

    public boolean isTransactionsProperty(String key) {
        return MetadataMethods.INSTANCE.isTransactionsMethod(key);
    }

    public boolean isStoredProceduresProperty(String key) {
        return MetadataMethods.INSTANCE.isStoredProceduresMethod(key);
    }

    public boolean isLimitProperty(String key) {
        return MetadataMethods.INSTANCE.isLimitMethod(key);
    }

    public boolean isBooleanProperty(String key) {
        return MetadataMethods.INSTANCE.isBooleanMethod(key);
    }

    public boolean isOtherProperty(String key) {
        return MetadataMethods.INSTANCE.isOtherMethod(key);
    }

    public Set<Map.Entry<Database, MyDatabaseMetaData>> entrySet() {
        return databases.entrySet();
    }
}
