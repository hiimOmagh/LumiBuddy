package de.omagh.core_domain.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides application wide ExecutorServices for IO and single threaded work.
 */
public class AppExecutors {
    private static final ExecutorService IO = Executors.newCachedThreadPool();
    private static final ExecutorService SINGLE = Executors.newSingleThreadExecutor();

    /**
     * Returns a general IO executor.
     */
    public static ExecutorService io() {
        return IO;
    }

    /**
     * Returns a single threaded executor.
     */
    public static ExecutorService single() {
        return SINGLE;
    }

    /**
     * Shutdown executors when the app is terminating.
     */
    public static void shutdown() {
        IO.shutdown();
        SINGLE.shutdown();
    }
}
